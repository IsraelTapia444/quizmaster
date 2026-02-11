<?php
// partidas.php
// Script para guardar y obtener partidas en QuizMaster

// Headers CORS para permitir acceso desde Android
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=UTF-8');

// Manejar preflight request de CORS
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Incluir configuración de base de datos
require_once 'config.php';

try {
    $method = $_SERVER['REQUEST_METHOD'];
    
    switch ($method) {
        case 'POST':
            guardarPartida();
            break;
            
        case 'GET':
            obtenerPartidas();
            break;
            
        default:
            http_response_code(405);
            echo json_encode([
                'success' => false,
                'message' => 'Método no permitido'
            ]);
            break;
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error del servidor: ' . $e->getMessage()
    ]);
}

/**
 * POST - Guardar nueva partida y actualizar ranking
 */
function guardarPartida() {
    $conn = getDBConnection();
    
    // Obtener datos JSON del body
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);
    
    if (!$data) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'No se recibieron datos válidos'
        ]);
        $conn->close();
        return;
    }
    
    // Obtener y validar campos
    $usuario_id = isset($data['usuario_id']) ? intval($data['usuario_id']) : 0;
    $puntuacion = isset($data['puntuacion']) ? intval($data['puntuacion']) : 0;
    
    // Validaciones
    if ($usuario_id <= 0) {
        throw new Exception('ID de usuario inválido');
    }
    
    if ($puntuacion < 0 || $puntuacion > 100) {
        throw new Exception('Puntuación inválida (debe estar entre 0 y 100)');
    }
    
    // Verificar que el usuario existe
    $stmt = $conn->prepare("SELECT id FROM usuarios WHERE id = ?");
    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        throw new Exception('Usuario no encontrado');
    }
    $stmt->close();
    
    // Iniciar transacción
    $conn->begin_transaction();
    
    try {
        // Insertar partida
        $stmt = $conn->prepare("INSERT INTO partidas (usuario_id, puntuacion, fecha) VALUES (?, ?, NOW())");
        $stmt->bind_param("ii", $usuario_id, $puntuacion);
        
        if (!$stmt->execute()) {
            throw new Exception('Error al guardar partida');
        }
        
        $partida_id = $conn->insert_id;
        $stmt->close();
        
        // Actualizar ranking si es necesario
        actualizarRanking($conn, $usuario_id, $puntuacion);
        
        // Confirmar transacción
        $conn->commit();
        
        http_response_code(201);
        echo json_encode([
            'success' => true,
            'message' => 'Partida guardada exitosamente',
            'data' => [
                'partida_id' => $partida_id,
                'usuario_id' => $usuario_id,
                'puntuacion' => $puntuacion
            ]
        ]);
        
    } catch (Exception $e) {
        // Revertir transacción en caso de error
        $conn->rollback();
        throw $e;
    }
    
    $conn->close();
}

/**
 * Función para actualizar el ranking
 * Solo actualiza si la nueva puntuación es mejor que la anterior
 */
function actualizarRanking($conn, $usuario_id, $nueva_puntuacion) {
    // Verificar si el usuario ya está en el ranking
    $stmt = $conn->prepare("SELECT mejor_puntuacion FROM ranking WHERE usuario_id = ?");
    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        // Usuario ya está en ranking, verificar si debe actualizarse
        $row = $result->fetch_assoc();
        $mejor_puntuacion_actual = $row['mejor_puntuacion'];
        
        if ($nueva_puntuacion > $mejor_puntuacion_actual) {
            // Actualizar mejor puntuación
            $stmt->close();
            $stmt = $conn->prepare("UPDATE ranking SET mejor_puntuacion = ? WHERE usuario_id = ?");
            $stmt->bind_param("ii", $nueva_puntuacion, $usuario_id);
            $stmt->execute();
        }
    } else {
        // Usuario no está en ranking, insertarlo
        $stmt->close();
        $stmt = $conn->prepare("INSERT INTO ranking (usuario_id, mejor_puntuacion) VALUES (?, ?)");
        $stmt->bind_param("ii", $usuario_id, $nueva_puntuacion);
        $stmt->execute();
    }
    
    $stmt->close();
}

/**
 * GET - Obtener partidas de un usuario
 */
function obtenerPartidas() {
    $conn = getDBConnection();
    
    // Obtener usuario_id de la URL
    if (!isset($_GET['usuario_id'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'ID de usuario requerido'
        ]);
        $conn->close();
        return;
    }
    
    $usuario_id = intval($_GET['usuario_id']);
    
    // Obtener todas las partidas del usuario, ordenadas por fecha descendente
    $stmt = $conn->prepare("
        SELECT id, usuario_id, puntuacion, fecha 
        FROM partidas 
        WHERE usuario_id = ? 
        ORDER BY fecha DESC
    ");
    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $partidas = array();
    while ($row = $result->fetch_assoc()) {
        $partidas[] = [
            'id' => (int)$row['id'],
            'usuario_id' => (int)$row['usuario_id'],
            'puntuacion' => (int)$row['puntuacion'],
            'fecha' => $row['fecha']
        ];
    }
    
    http_response_code(200);
    echo json_encode($partidas);
    
    $stmt->close();
    $conn->close();
}
?>

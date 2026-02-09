<?php
// preguntas.php
// Script para manejar operaciones CRUD de preguntas en QuizMaster

// Headers CORS para permitir acceso desde Android
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, PUT, DELETE, OPTIONS');
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
        case 'GET':
            obtenerPreguntas();
            break;
            
        case 'POST':
            crearPregunta();
            break;
            
        case 'PUT':
            actualizarPregunta();
            break;
            
        case 'DELETE':
            eliminarPregunta();
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
 * GET - Obtener preguntas
 * Parámetros opcionales:
 * - id: obtener una pregunta específica
 * - dificultad: filtrar por dificultad (facil, media, dificil)
 */
function obtenerPreguntas() {
    $conn = getDBConnection();
    
    // Verificar si se solicita una pregunta específica
    if (isset($_GET['id'])) {
        $id = intval($_GET['id']);
        $stmt = $conn->prepare("SELECT * FROM preguntas WHERE id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            $pregunta = $result->fetch_assoc();
            http_response_code(200);
            echo json_encode($pregunta);
        } else {
            http_response_code(404);
            echo json_encode([
                'success' => false,
                'message' => 'Pregunta no encontrada'
            ]);
        }
        
        $stmt->close();
        $conn->close();
        return;
    }
    
    // Verificar si se filtra por dificultad
    if (isset($_GET['dificultad'])) {
        $dificultad = $_GET['dificultad'];
        
        // Validar dificultad
        if (!in_array($dificultad, ['facil', 'media', 'dificil'])) {
            http_response_code(400);
            echo json_encode([
                'success' => false,
                'message' => 'Dificultad inválida. Use: facil, media o dificil'
            ]);
            $conn->close();
            return;
        }
        
        $stmt = $conn->prepare("SELECT * FROM preguntas WHERE dificultad = ? ORDER BY id DESC");
        $stmt->bind_param("s", $dificultad);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $preguntas = array();
        while ($row = $result->fetch_assoc()) {
            $preguntas[] = $row;
        }
        
        http_response_code(200);
        echo json_encode($preguntas);
        
        $stmt->close();
        $conn->close();
        return;
    }
    
    // Obtener todas las preguntas
    $query = "SELECT * FROM preguntas ORDER BY id DESC";
    $result = $conn->query($query);
    
    $preguntas = array();
    while ($row = $result->fetch_assoc()) {
        $preguntas[] = $row;
    }
    
    http_response_code(200);
    echo json_encode($preguntas);
    
    $conn->close();
}

/**
 * POST - Crear nueva pregunta
 */
function crearPregunta() {
    $conn = getDBConnection();
    
    // Obtener datos JSON del body
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);
    
    // Validar que se recibieron datos
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
    $pregunta = isset($data['pregunta']) ? trim($data['pregunta']) : '';
    $opcion1 = isset($data['opcion1']) ? trim($data['opcion1']) : '';
    $opcion2 = isset($data['opcion2']) ? trim($data['opcion2']) : '';
    $opcion3 = isset($data['opcion3']) ? trim($data['opcion3']) : '';
    $opcion4 = isset($data['opcion4']) ? trim($data['opcion4']) : '';
    $correcta = isset($data['correcta']) ? intval($data['correcta']) : 0;
    $categoria = isset($data['categoria']) ? trim($data['categoria']) : null;
    $dificultad = isset($data['dificultad']) ? trim($data['dificultad']) : 'media';
    
    // Validaciones
    if (empty($pregunta)) {
        throw new Exception('La pregunta es obligatoria');
    }
    
    if (empty($opcion1) || empty($opcion2) || empty($opcion3) || empty($opcion4)) {
        throw new Exception('Las 4 opciones son obligatorias');
    }
    
    if ($correcta < 1 || $correcta > 4) {
        throw new Exception('La respuesta correcta debe ser entre 1 y 4');
    }
    
    if (!in_array($dificultad, ['facil', 'media', 'dificil'])) {
        throw new Exception('Dificultad inválida. Use: facil, media o dificil');
    }
    
    // Insertar pregunta
    $stmt = $conn->prepare(
        "INSERT INTO preguntas (pregunta, opcion1, opcion2, opcion3, opcion4, correcta, categoria, dificultad) 
         VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    );
    $stmt->bind_param("sssssiss", $pregunta, $opcion1, $opcion2, $opcion3, $opcion4, $correcta, $categoria, $dificultad);
    
    if ($stmt->execute()) {
        $pregunta_id = $conn->insert_id;
        
        http_response_code(201);
        echo json_encode([
            'success' => true,
            'message' => 'Pregunta creada exitosamente',
            'data' => [
                'id' => $pregunta_id
            ]
        ]);
    } else {
        throw new Exception('Error al crear pregunta: ' . $conn->error);
    }
    
    $stmt->close();
    $conn->close();
}

/**
 * PUT - Actualizar pregunta existente
 */
function actualizarPregunta() {
    $conn = getDBConnection();
    
    // Obtener ID de la URL
    if (!isset($_GET['id'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'ID de pregunta requerido'
        ]);
        $conn->close();
        return;
    }
    
    $id = intval($_GET['id']);
    
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
    
    // Obtener campos
    $pregunta = isset($data['pregunta']) ? trim($data['pregunta']) : '';
    $opcion1 = isset($data['opcion1']) ? trim($data['opcion1']) : '';
    $opcion2 = isset($data['opcion2']) ? trim($data['opcion2']) : '';
    $opcion3 = isset($data['opcion3']) ? trim($data['opcion3']) : '';
    $opcion4 = isset($data['opcion4']) ? trim($data['opcion4']) : '';
    $correcta = isset($data['correcta']) ? intval($data['correcta']) : 0;
    $categoria = isset($data['categoria']) ? trim($data['categoria']) : null;
    $dificultad = isset($data['dificultad']) ? trim($data['dificultad']) : 'media';
    
    // Validaciones
    if (empty($pregunta) || empty($opcion1) || empty($opcion2) || empty($opcion3) || empty($opcion4)) {
        throw new Exception('Todos los campos son obligatorios');
    }
    
    if ($correcta < 1 || $correcta > 4) {
        throw new Exception('La respuesta correcta debe ser entre 1 y 4');
    }
    
    // Actualizar pregunta
    $stmt = $conn->prepare(
        "UPDATE preguntas 
         SET pregunta = ?, opcion1 = ?, opcion2 = ?, opcion3 = ?, opcion4 = ?, correcta = ?, categoria = ?, dificultad = ?
         WHERE id = ?"
    );
    $stmt->bind_param("sssssissi", $pregunta, $opcion1, $opcion2, $opcion3, $opcion4, $correcta, $categoria, $dificultad, $id);
    
    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            http_response_code(200);
            echo json_encode([
                'success' => true,
                'message' => 'Pregunta actualizada exitosamente'
            ]);
        } else {
            http_response_code(404);
            echo json_encode([
                'success' => false,
                'message' => 'Pregunta no encontrada'
            ]);
        }
    } else {
        throw new Exception('Error al actualizar pregunta: ' . $conn->error);
    }
    
    $stmt->close();
    $conn->close();
}

/**
 * DELETE - Eliminar pregunta
 */
function eliminarPregunta() {
    $conn = getDBConnection();
    
    // Obtener ID de la URL
    if (!isset($_GET['id'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'ID de pregunta requerido'
        ]);
        $conn->close();
        return;
    }
    
    $id = intval($_GET['id']);
    
    // Eliminar pregunta
    $stmt = $conn->prepare("DELETE FROM preguntas WHERE id = ?");
    $stmt->bind_param("i", $id);
    
    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            http_response_code(200);
            echo json_encode([
                'success' => true,
                'message' => 'Pregunta eliminada exitosamente'
            ]);
        } else {
            http_response_code(404);
            echo json_encode([
                'success' => false,
                'message' => 'Pregunta no encontrada'
            ]);
        }
    } else {
        throw new Exception('Error al eliminar pregunta: ' . $conn->error);
    }
    
    $stmt->close();
    $conn->close();
}
?>

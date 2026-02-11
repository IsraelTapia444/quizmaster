<?php
// estadisticas.php
// Script para obtener estadísticas de un usuario en QuizMaster

// Headers CORS para permitir acceso desde Android
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=UTF-8');

// Manejar preflight request de CORS
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Incluir configuración de base de datos
require_once 'config.php';

// Solo aceptar método GET
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido. Use GET.'
    ]);
    exit();
}

try {
    // Obtener usuario_id de la URL
    if (!isset($_GET['usuario_id'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'ID de usuario requerido'
        ]);
        exit();
    }
    
    $usuario_id = intval($_GET['usuario_id']);
    
    // Conectar a la base de datos
    $conn = getDBConnection();
    
    // Verificar que el usuario existe
    $stmt = $conn->prepare("SELECT id, nombre FROM usuarios WHERE id = ?");
    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Usuario no encontrado'
        ]);
        $stmt->close();
        $conn->close();
        exit();
    }
    
    $usuario = $result->fetch_assoc();
    $stmt->close();
    
    // Obtener estadísticas de partidas
    $query = "
        SELECT 
            COUNT(*) as total_partidas,
            COALESCE(AVG(puntuacion), 0) as puntuacion_media,
            COALESCE(MAX(puntuacion), 0) as mejor_puntuacion,
            COALESCE(MIN(puntuacion), 0) as peor_puntuacion
        FROM partidas 
        WHERE usuario_id = ?
    ";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $stats = $result->fetch_assoc();
    $stmt->close();
    
    // Obtener puntuación de la última partida
    $query_ultima = "
        SELECT puntuacion, fecha
        FROM partidas 
        WHERE usuario_id = ? 
        ORDER BY fecha DESC 
        LIMIT 1
    ";
    
    $stmt = $conn->prepare($query_ultima);
    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $ultima_puntuacion = 0;
    $ultima_fecha = null;
    
    if ($result->num_rows > 0) {
        $ultima = $result->fetch_assoc();
        $ultima_puntuacion = (int)$ultima['puntuacion'];
        $ultima_fecha = $ultima['fecha'];
    }
    
    $stmt->close();
    
    // Si no hay partidas, peor_puntuacion debe ser 0
    if ($stats['total_partidas'] == 0) {
        $stats['peor_puntuacion'] = 0;
    }
    
    // Construir respuesta
    $estadisticas = [
        'usuario_id' => $usuario_id,
        'nombre_usuario' => $usuario['nombre'],
        'total_partidas' => (int)$stats['total_partidas'],
        'puntuacion_media' => round((float)$stats['puntuacion_media'], 2),
        'mejor_puntuacion' => (int)$stats['mejor_puntuacion'],
        'peor_puntuacion' => (int)$stats['peor_puntuacion'],
        'ultima_puntuacion' => $ultima_puntuacion,
        'ultima_fecha' => $ultima_fecha
    ];
    
    // Obtener ranking del usuario (posición)
    $query_ranking = "
        SELECT COUNT(*) + 1 as posicion
        FROM ranking
        WHERE mejor_puntuacion > (
            SELECT COALESCE(mejor_puntuacion, 0)
            FROM ranking
            WHERE usuario_id = ?
        )
    ";
    
    $stmt = $conn->prepare($query_ranking);
    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $ranking = $result->fetch_assoc();
        $estadisticas['posicion_ranking'] = (int)$ranking['posicion'];
    } else {
        $estadisticas['posicion_ranking'] = 0;
    }
    
    $stmt->close();
    
    // Obtener total de usuarios en el ranking (para mostrar "3 de 10")
    $query_total = "SELECT COUNT(*) as total FROM ranking";
    $result = $conn->query($query_total);
    $total = $result->fetch_assoc();
    $estadisticas['total_usuarios'] = (int)$total['total'];
    
    http_response_code(200);
    echo json_encode($estadisticas);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error del servidor: ' . $e->getMessage()
    ]);
}
?>

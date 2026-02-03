<?php
// config.php
// Configuración de la base de datos para QuizMaster

// Configuración de la base de datos
define('DB_HOST', 'localhost');      // Servidor (normalmente localhost)
define('DB_PORT', '3306');           // Puerto de MySQL
define('DB_NAME', 'quizmaster');     // Nombre de tu base de datos
define('DB_USER', 'root');           // Usuario de MySQL
define('DB_PASS', 'root');               // Contraseña de MySQL (vacío en XAMPP por defecto)
define('DB_CHARSET', 'utf8mb4');     // Charset

/**
 * Función para obtener conexión a la base de datos
 * @return mysqli Conexión a la base de datos
 * @throws Exception Si no se puede conectar
 */
function getDBConnection() {
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME, DB_PORT);

    // Verificar conexión
    if ($conn->connect_error) {
        throw new Exception('Error de conexión: ' . $conn->connect_error);
    }

    // Establecer charset
    $conn->set_charset(DB_CHARSET);

    return $conn;
}

/**
 * Función para enviar respuesta JSON
 */
function sendJSONResponse($success, $message, $data = null, $httpCode = 200) {
    http_response_code($httpCode);
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data
    ]);
    exit();
}

/**
 * Función para validar que el método HTTP sea el correcto
 */
function validateMethod($allowedMethod) {
    if ($_SERVER['REQUEST_METHOD'] !== $allowedMethod) {
        sendJSONResponse(false, "Método no permitido. Use $allowedMethod.", null, 405);
    }
}

/**
 * Función para obtener datos JSON del body
 */
function getJSONInput() {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);
    
    if (!$data) {
        sendJSONResponse(false, 'No se recibieron datos válidos', null, 400);
    }
    
    return $data;
}
?>
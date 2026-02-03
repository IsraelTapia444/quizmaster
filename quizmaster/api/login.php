<?php
// login.php
// Script para autenticar usuarios en QuizMaster

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

// Solo aceptar método POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido. Use POST.'
    ]);
    exit();
}

try {
    // Obtener datos JSON del body
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    // Validar que se recibieron datos
    if (!$data) {
        throw new Exception('No se recibieron datos válidos');
    }

    // Obtener y validar campos
    $email = isset($data['email']) ? trim($data['email']) : '';
    $password = isset($data['password']) ? trim($data['password']) : '';

    // Validaciones
    if (empty($email) || empty($password)) {
        throw new Exception('Email y contraseña son obligatorios');
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        throw new Exception('Email inválido');
    }

    // Conectar a la base de datos
    $conn = getDBConnection();

    // Buscar usuario por email
    $stmt = $conn->prepare("SELECT id, nombre, email, password FROM usuarios WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        // Usuario no encontrado
        throw new Exception('Email o contraseña incorrectos');
    }

    $usuario = $result->fetch_assoc();

    // Verificar contraseña
    if (!password_verify($password, $usuario['password'])) {
        // Contraseña incorrecta
        throw new Exception('Email o contraseña incorrectos');
    }

    // Login exitoso
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'message' => 'Login exitoso',
        'data' => [
            'id' => (int)$usuario['id'],
            'usuario_id' => (int)$usuario['id'], // Por compatibilidad
            'nombre' => $usuario['nombre'],
            'email' => $usuario['email']
        ]
    ]);

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}
?>
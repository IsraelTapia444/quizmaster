<?php
// registro.php
// Script para registrar nuevos usuarios en QuizMaster

// Permitir CORS (para desarrollo)
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');
header('Content-Type: application/json; charset=UTF-8');

// Si es una petición OPTIONS, terminar aquí
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
    $nombre = isset($data['nombre']) ? trim($data['nombre']) : '';
    $email = isset($data['email']) ? trim($data['email']) : '';
    $password = isset($data['password']) ? trim($data['password']) : '';

    // Validaciones
    if (empty($nombre) || empty($email) || empty($password)) {
        throw new Exception('Todos los campos son obligatorios');
    }

    if (strlen($nombre) < 3) {
        throw new Exception('El nombre debe tener al menos 3 caracteres');
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        throw new Exception('Email inválido');
    }

    if (strlen($password) < 6) {
        throw new Exception('La contraseña debe tener al menos 6 caracteres');
    }

    // Conectar a la base de datos
    $conn = getDBConnection();

    // Verificar si el email ya existe
    $stmt = $conn->prepare("SELECT id FROM usuarios WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        throw new Exception('El email ya está registrado');
    }

    // Hashear la contraseña
    $password_hash = password_hash($password, PASSWORD_DEFAULT);

    // Insertar nuevo usuario
    $stmt = $conn->prepare("INSERT INTO usuarios (nombre, email, password) VALUES (?, ?, ?)");
    $stmt->bind_param("sss", $nombre, $email, $password_hash);

    if ($stmt->execute()) {
        $usuario_id = $conn->insert_id;
        
        http_response_code(201);
        echo json_encode([
            'success' => true,
            'message' => 'Usuario registrado exitosamente',
            'data' => [
                'usuario_id' => $usuario_id,
                'nombre' => $nombre,
                'email' => $email
            ]
        ]);
    } else {
        throw new Exception('Error al registrar usuario: ' . $conn->error);
    }

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
package com.example.quizmaster.models

// Modelo para la respuesta del servidor
data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)

// Modelo para el registro de usuario
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

// Modelo para el login de usuario
data class LoginRequest(
    val email: String,
    val password: String
)

// Modelo de usuario completo
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val fecha_registro: String
)

// Modelo de pregunta (según tu BD)
data class Pregunta(
    val id: Int,
    val pregunta: String,
    val opcion1: String,
    val opcion2: String,
    val opcion3: String,
    val opcion4: String,
    val correcta: Int,
    val categoria: String?,
    val dificultad: String  // "facil", "media", "dificil"
)

// Modelo para crear/actualizar pregunta
data class PreguntaRequest(
    val pregunta: String,
    val opcion1: String,
    val opcion2: String,
    val opcion3: String,
    val opcion4: String,
    val correcta: Int,
    val categoria: String?,
    val dificultad: String
)

// Modelo de partida
data class Partida(
    val id: Int,
    val usuario_id: Int,
    val puntuacion: Int,
    val fecha: String
)

// Modelo para guardar partida
data class PartidaRequest(
    val usuario_id: Int,
    val puntuacion: Int
)

// Modelo de estadísticas ACTUALIZADO
data class Estadisticas(
    val usuario_id: Int,
    val nombre_usuario: String,
    val total_partidas: Int,
    val puntuacion_media: Double,
    val mejor_puntuacion: Int,
    val peor_puntuacion: Int,
    val ultima_puntuacion: Int,
    val ultima_fecha: String?,
    val posicion_ranking: Int,
    val total_usuarios: Int
)
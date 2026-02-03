package com.example.quizmaster.network

import com.example.quizmaster.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // ============================================
    // ENDPOINTS DE AUTENTICACIÓN
    // ============================================

    @POST("registro.php")
    fun registrarUsuario(@Body request: RegistroRequest): Call<ApiResponse>

    @POST("login.php")
    fun loginUsuario(@Body request: LoginRequest): Call<ApiResponse>

    // ============================================
    // ENDPOINTS DE PREGUNTAS
    // ============================================

    // Obtener todas las preguntas
    @GET("preguntas.php")
    fun obtenerTodasLasPreguntas(): Call<List<Pregunta>>

    // Obtener preguntas por dificultad
    @GET("preguntas.php")
    fun obtenerPreguntasPorDificultad(@Query("dificultad") dificultad: String): Call<List<Pregunta>>

    // Obtener una pregunta por ID
    @GET("preguntas.php")
    fun obtenerPreguntaPorId(@Query("id") id: Int): Call<Pregunta>

    // Crear nueva pregunta
    @POST("preguntas.php")
    fun crearPregunta(@Body pregunta: PreguntaRequest): Call<ApiResponse>

    // Actualizar pregunta
    @PUT("preguntas.php")
    fun actualizarPregunta(@Query("id") id: Int, @Body pregunta: PreguntaRequest): Call<ApiResponse>

    // Eliminar pregunta
    @DELETE("preguntas.php")
    fun eliminarPregunta(@Query("id") id: Int): Call<ApiResponse>

    // ============================================
    // ENDPOINTS DE PARTIDAS
    // ============================================

    // Guardar partida
    @POST("partidas.php")
    fun guardarPartida(@Body partida: PartidaRequest): Call<ApiResponse>

    // Obtener partidas de un usuario
    @GET("partidas.php")
    fun obtenerPartidasUsuario(@Query("usuario_id") usuarioId: Int): Call<List<Partida>>

    // ============================================
    // ENDPOINTS DE ESTADÍSTICAS
    // ============================================

    // Obtener estadísticas de un usuario
    @GET("estadisticas.php")
    fun obtenerEstadisticas(@Query("usuario_id") usuarioId: Int): Call<Estadisticas>

    // ============================================
    // ENDPOINTS DE USUARIO
    // ============================================

    // Obtener datos de usuario
    @GET("usuario.php")
    fun obtenerUsuario(@Query("id") id: Int): Call<Usuario>
}
package com.example.quizmaster.models

/**
 * Modelo de datos para la configuración de la aplicación
 * 
 * @property id ID único de la configuración (siempre será 1, ya que solo hay una configuración)
 * @property soundEnabled Estado de los sonidos (true = activado, false = desactivado)
 * @property vibrationEnabled Estado de las vibraciones (true = activado, false = desactivado)
 * @property notificationsEnabled Estado de las notificaciones (true = activado, false = desactivado)
 */
data class AppConfig(
    val id: Int = 1,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)

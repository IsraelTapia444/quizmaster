package com.example.quizmaster.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.quizmaster.R

class NotificacionesHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "quizmaster_notificaciones"
        private const val CHANNEL_NAME = "QuizMaster Notificaciones"
        private const val CHANNEL_DESCRIPTION = "Notificaciones de logros y acciones en QuizMaster"

        private const val NOTIFICATION_ID_MEJOR_PUNTUACION = 1001
        private const val NOTIFICATION_ID_PREGUNTA_ELIMINADA = 1002
    }

    init {
        crearCanalNotificaciones()
    }

    /**
     * Crear canal de notificaciones (necesario para Android 8.0+)
     */
    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Mostrar notificación de nueva mejor puntuación
     */
    fun mostrarNotificacionMejorPuntuacion(puntuacion: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Usa el icono de tu app
            .setContentTitle("🎉 Has conseguido superar tu mejor puntuación")
            .setContentText("Nueva mejor puntuación: $puntuacion puntos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Nueva mejor puntuación: $puntuacion puntos")
            )

        mostrarNotificacion(NOTIFICATION_ID_MEJOR_PUNTUACION, builder)
    }

    /**
     * Mostrar notificación de pregunta eliminada
     */
    fun mostrarNotificacionPreguntaEliminada(textoPregunta: String) {
        // Limitar el texto de la pregunta para la notificación
        val textoCorto = if (textoPregunta.length > 50) {
            "${textoPregunta.substring(0, 50)}..."
        } else {
            textoPregunta
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("🗑️ Pregunta eliminada con éxito")
            .setContentText(textoCorto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textoPregunta)
            )

        mostrarNotificacion(NOTIFICATION_ID_PREGUNTA_ELIMINADA, builder)
    }

    /**
     * Mostrar notificación
     */
    private fun mostrarNotificacion(notificationId: Int, builder: NotificationCompat.Builder) {
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            android.util.Log.e("NotificacionesHelper", "Permisos de notificación denegados: ${e.message}")
        }
    }

    /**
     * Cancelar todas las notificaciones
     */
    fun cancelarTodasLasNotificaciones() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
package com.example.quizmaster.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

/**
 * Helper para reproducir sonidos en la aplicación
 * respetando la configuración del usuario
 * 
 * Uso:
 * ```
 * // Reproducir sonido de respuesta correcta
 * SoundHelper.playSound(context, R.raw.correct_answer)
 * 
 * // Reproducir sonido de respuesta incorrecta
 * SoundHelper.playSound(context, R.raw.wrong_answer)
 * ```
 */
object SoundHelper {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Reproduce un sonido si los sonidos están activados en la configuración
     * 
     * @param context Contexto de la aplicación
     * @param soundResId ID del recurso de sonido (debe estar en res/raw/)
     * @param volume Volumen de reproducción (0.0 a 1.0), por defecto 1.0
     */
    fun playSound(context: Context, @RawRes soundResId: Int, volume: Float = 1.0f) {
        // Verificar si los sonidos están activados
        if (!ConfigManager.isSoundEnabled()) {
            return
        }

        try {
            // Liberar el MediaPlayer anterior si existe
            mediaPlayer?.release()

            // Crear nuevo MediaPlayer
            mediaPlayer = MediaPlayer.create(context, soundResId)
            mediaPlayer?.setVolume(volume, volume)
            
            // Configurar listener para liberar recursos cuando termine
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
            }

            // Reproducir el sonido
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Detiene la reproducción actual si hay alguna
     */
    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Libera los recursos del MediaPlayer
     * Debe llamarse cuando la Activity se destruya
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

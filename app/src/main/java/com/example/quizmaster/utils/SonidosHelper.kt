package com.example.quizmaster.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager

class SonidosHelper(private val context: Context) {

    private var sonidoAcierto: MediaPlayer? = null
    private var sonidoFallo: MediaPlayer? = null
    private var sonidoFinPartida: MediaPlayer? = null
    private var sonidoPreguntaGuardada: MediaPlayer? = null

    init {
        inicializarSonidos()
    }

    /**
     * Inicializar todos los sonidos del sistema
     */
    private fun inicializarSonidos() {
        try {
            // Sonido de acierto - Notificación
            sonidoAcierto = MediaPlayer.create(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            sonidoAcierto?.setVolume(0.7f, 0.7f)
            sonidoAcierto?.setOnCompletionListener {
                it.seekTo(0)
            }

            // Sonido de fallo - Notificación corta
            sonidoFallo = MediaPlayer.create(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            sonidoFallo?.setVolume(0.5f, 0.5f)
            sonidoFallo?.setOnCompletionListener {
                it.seekTo(0)
            }

            // Sonido de fin de partida - Ringtone
            sonidoFinPartida = MediaPlayer.create(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            )
            sonidoFinPartida?.setVolume(0.8f, 0.8f)
            sonidoFinPartida?.setOnCompletionListener {
                it.seekTo(0)
            }

            // Sonido de pregunta guardada - Notificación
            sonidoPreguntaGuardada = MediaPlayer.create(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            sonidoPreguntaGuardada?.setVolume(0.7f, 0.7f)
            sonidoPreguntaGuardada?.setOnCompletionListener {
                it.seekTo(0)
            }

        } catch (e: Exception) {
            android.util.Log.e("SonidosHelper", "Error inicializando sonidos: ${e.message}")
        }
    }

    /**
     * Reproducir sonido de acierto
     */
    fun reproducirAcierto() {
        reproducirSonido(sonidoAcierto)
    }

    /**
     * Reproducir sonido de fallo
     */
    fun reproducirFallo() {
        reproducirSonido(sonidoFallo)
    }

    /**
     * Reproducir sonido de fin de partida
     */
    fun reproducirFinPartida() {
        reproducirSonido(sonidoFinPartida)
    }

    /**
     * Reproducir sonido de pregunta guardada
     */
    fun reproducirPreguntaGuardada() {
        reproducirSonido(sonidoPreguntaGuardada)
    }

    /**
     * Reproducir un sonido
     */
    private fun reproducirSonido(mediaPlayer: MediaPlayer?) {
        try {
            mediaPlayer?.let {
                // Si ya está reproduciéndose, no hacer nada
                if (it.isPlaying) {
                    return
                }

                // Resetear al inicio
                it.seekTo(0)

                // Reproducir
                it.start()
            }
        } catch (e: Exception) {
            android.util.Log.e("SonidosHelper", "Error reproduciendo sonido: ${e.message}")
        }
    }

    /**
     * Liberar recursos
     */
    fun release() {
        sonidoAcierto?.release()
        sonidoFallo?.release()
        sonidoFinPartida?.release()
        sonidoPreguntaGuardada?.release()

        sonidoAcierto = null
        sonidoFallo = null
        sonidoFinPartida = null
        sonidoPreguntaGuardada = null
    }
}
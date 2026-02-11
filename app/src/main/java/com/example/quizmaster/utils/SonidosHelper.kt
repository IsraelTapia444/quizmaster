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

            // Sonido de fallo - Alarma (más corto)
            val uriAlarma = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            sonidoFallo = MediaPlayer.create(context, uriAlarma)
            sonidoFallo?.setVolume(0.5f, 0.5f)

            // Sonido de fin de partida - Ringtone
            sonidoFinPartida = MediaPlayer.create(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            )
            sonidoFinPartida?.setVolume(0.8f, 0.8f)

            // Sonido de pregunta guardada - Notificación
            sonidoPreguntaGuardada = MediaPlayer.create(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            sonidoPreguntaGuardada?.setVolume(0.7f, 0.7f)

            // Limitar duración a 2 segundos máximo
            configurarDuracionMaxima(sonidoAcierto, 1000)
            configurarDuracionMaxima(sonidoFallo, 1000)
            configurarDuracionMaxima(sonidoFinPartida, 2000)
            configurarDuracionMaxima(sonidoPreguntaGuardada, 1000)

        } catch (e: Exception) {
            android.util.Log.e("SonidosHelper", "Error inicializando sonidos: ${e.message}")
        }
    }

    /**
     * Configurar duración máxima de un sonido
     */
    private fun configurarDuracionMaxima(mediaPlayer: MediaPlayer?, maxDuration: Int) {
        mediaPlayer?.setOnPreparedListener {
            // Detener después de maxDuration milisegundos
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (it.isPlaying) {
                    it.pause()
                    it.seekTo(0)
                }
            }, maxDuration.toLong())
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
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer.seekTo(0)
            } else {
                mediaPlayer?.start()
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
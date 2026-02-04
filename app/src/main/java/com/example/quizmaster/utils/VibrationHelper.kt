package com.example.quizmaster.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Helper para gestionar vibraciones en la aplicación
 * respetando la configuración del usuario
 * 
 * Uso:
 * ```
 * // Vibración corta al tocar un botón
 * VibrationHelper.vibrate(context, VibrationHelper.VIBRATION_SHORT)
 * 
 * // Vibración para respuesta correcta
 * VibrationHelper.vibrateSuccess(context)
 * 
 * // Vibración para respuesta incorrecta
 * VibrationHelper.vibrateError(context)
 * ```
 */
object VibrationHelper {

    // Duraciones predefinidas (en milisegundos)
    const val VIBRATION_SHORT = 50L
    const val VIBRATION_MEDIUM = 100L
    const val VIBRATION_LONG = 200L

    /**
     * Obtiene el Vibrator del sistema de forma compatible con diferentes versiones de Android
     */
    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Realiza una vibración simple si las vibraciones están activadas
     * 
     * @param context Contexto de la aplicación
     * @param duration Duración de la vibración en milisegundos
     * @param amplitude Amplitud de la vibración (1-255), por defecto VibrationEffect.DEFAULT_AMPLITUDE
     */
    fun vibrate(
        context: Context, 
        duration: Long = VIBRATION_SHORT,
        amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE
    ) {
        // Verificar si las vibraciones están activadas
        if (!ConfigManager.isVibrationEnabled()) {
            return
        }

        try {
            val vibrator = getVibrator(context)
            
            // Verificar que el dispositivo tiene vibrador
            if (!vibrator.hasVibrator()) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26+: Usar VibrationEffect
                val effect = VibrationEffect.createOneShot(duration, amplitude)
                vibrator.vibrate(effect)
            } else {
                // API < 26: Usar método deprecado
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Realiza un patrón de vibración personalizado
     * 
     * @param context Contexto de la aplicación
     * @param pattern Array de tiempos alternando entre espera y vibración (en ms)
     *                Ejemplo: longArrayOf(0, 100, 50, 100) = espera 0ms, vibra 100ms, espera 50ms, vibra 100ms
     * @param repeat Índice desde donde repetir (-1 para no repetir)
     */
    fun vibratePattern(
        context: Context,
        pattern: LongArray,
        repeat: Int = -1
    ) {
        // Verificar si las vibraciones están activadas
        if (!ConfigManager.isVibrationEnabled()) {
            return
        }

        try {
            val vibrator = getVibrator(context)
            
            if (!vibrator.hasVibrator()) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amplitudes = IntArray(pattern.size) { VibrationEffect.DEFAULT_AMPLITUDE }
                val effect = VibrationEffect.createWaveform(pattern, amplitudes, repeat)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Vibración predefinida para indicar éxito/respuesta correcta
     */
    fun vibrateSuccess(context: Context) {
        // Patrón: vibración corta
        vibrate(context, VIBRATION_SHORT)
    }

    /**
     * Vibración predefinida para indicar error/respuesta incorrecta
     */
    fun vibrateError(context: Context) {
        // Patrón: dos vibraciones cortas
        vibratePattern(context, longArrayOf(0, 100, 50, 100))
    }

    /**
     * Vibración predefinida para botones/click
     */
    fun vibrateClick(context: Context) {
        vibrate(context, VIBRATION_SHORT)
    }

    /**
     * Cancela cualquier vibración en curso
     */
    fun cancel(context: Context) {
        try {
            val vibrator = getVibrator(context)
            vibrator.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

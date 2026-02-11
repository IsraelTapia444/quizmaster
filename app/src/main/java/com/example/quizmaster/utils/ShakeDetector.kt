package com.example.quizmaster.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(context: Context, private val onShake: () -> Unit) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Configuración para sensibilidad media
    private val shakeThreshold = 15.0f // Umbral de aceleración
    private val timeBetweenShakes = 1000L // Mínimo 1 segundo entre agitaciones
    private var lastShakeTime = 0L

    /**
     * Iniciar detección de agitación
     */
    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    /**
     * Detener detección de agitación
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calcular la aceleración total
            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Restar la gravedad (9.8 m/s²)
            val accelerationWithoutGravity = acceleration - SensorManager.GRAVITY_EARTH

            // Detectar agitación
            if (accelerationWithoutGravity > shakeThreshold) {
                val currentTime = System.currentTimeMillis()

                // Verificar que haya pasado suficiente tiempo desde la última agitación
                if (currentTime - lastShakeTime > timeBetweenShakes) {
                    lastShakeTime = currentTime
                    onShake.invoke()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesitamos implementar esto
    }
}
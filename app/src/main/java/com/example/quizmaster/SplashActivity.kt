package com.example.quizmaster

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_splash)

            // Reproducir sonido del sistema (notificación)
            val notificationUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(applicationContext, notificationUri)
            ringtone?.play()

            // Animación del logo
            val logo = findViewById<View>(R.id.splashLogo)

            ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }

            ObjectAnimator.ofFloat(logo, "scaleY", 0f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }

            ObjectAnimator.ofFloat(logo, "rotation", 0f, 360f).apply {
                duration = 1000
                start()
            }

            // Navegar a MainActivity después de 3 segundos
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    Log.d(TAG, "Intentando abrir LoginActivity...")
                    ringtone?.stop()
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    Log.d(TAG, "LoginActivity iniciada correctamente")
                } catch (e: Exception) {
                    Log.e(TAG, "Error al abrir LoginActivity: ${e.message}")
                    e.printStackTrace()
                }
            }, 3000)

        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
    }
}

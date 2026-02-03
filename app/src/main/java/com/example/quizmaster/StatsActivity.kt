package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class StatsActivity : AppCompatActivity() {

    // Componentes de estadísticas
    private lateinit var tvTotalPartidas: TextView
    private lateinit var tvMejorPuntuacion: TextView
    private lateinit var tvPeorPuntuacion: TextView
    private lateinit var tvUltimaPuntuacion: TextView

    // Icono de perfil
    private lateinit var btnIconoPerfil: android.widget.ImageView

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SOLUCIÓN 1: Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_stats)

        // Inicializar componentes de estadísticas
        tvTotalPartidas = findViewById(R.id.tvTotalPartidas)
        tvMejorPuntuacion = findViewById(R.id.tvMejorPuntuacion)
        tvPeorPuntuacion = findViewById(R.id.tvPeorPuntuacion)
        tvUltimaPuntuacion = findViewById(R.id.tvUltimaPuntuacion)

        // Inicializar botones del menú inferior
        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)

        // Inicializar icono de perfil
        btnIconoPerfil = findViewById(R.id.btnIconoPerfil)
        // Botón icono de perfil
        btnIconoPerfil.setOnClickListener {
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).duration = 100
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                }
        }

        // SOLUCIÓN 1: Aplicar WindowInsets al menú inferior
        aplicarWindowInsets()

        // Configurar listeners
        configurarMenuInferior()
        cargarEstadisticas()
    }

    private fun aplicarWindowInsets() {
        // Aplicar padding para los insets del sistema en el menú inferior
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun cargarEstadisticas() {
        // TODO: Implementar lógica para cargar estadísticas desde BD/SharedPreferences
        // Ejemplo:
        // tvTotalPartidas.text = obtenerTotalPartidas().toString()
        // tvMejorPuntuacion.text = obtenerMejorPuntuacion().toString()
        // tvPeorPuntuacion.text = obtenerPeorPuntuacion().toString()
        // tvUltimaPuntuacion.text = "${obtenerUltimaPuntuacion()}%"
    }

    private fun configurarMenuInferior() {
        // Botón Añadir Pregunta
        btnAddQuestion.setOnClickListener {
            val intent = Intent(this, PreguntaActivity::class.java)
            startActivity(intent)
        }

        // Botón Fácil - ir a MainActivity con dificultad fácil
        btnFacil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 1)
            startActivity(intent)
        }

        // Botón Medio - ir a MainActivity con dificultad media
        btnMedio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 2)
            startActivity(intent)
        }

        // Botón Difícil - ir a MainActivity con dificultad difícil
        btnDificil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 3)
            startActivity(intent)
        }

        // Botón Stats (esta misma actividad)
        btnStats.setOnClickListener {
            // Ya estamos en StatsActivity, no hacer nada
        }
    }
}
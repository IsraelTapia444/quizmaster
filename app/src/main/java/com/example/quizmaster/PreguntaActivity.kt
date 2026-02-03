package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class PreguntaActivity : AppCompatActivity() {

    // Componentes del formulario
    private lateinit var etPregunta: EditText
    private lateinit var etOpcion1: EditText
    private lateinit var etOpcion2: EditText
    private lateinit var etOpcion3: EditText
    private lateinit var etOpcion4: EditText
    private lateinit var etRespuestaCorrecta: EditText
    private lateinit var etDificultad: EditText

    // Botones principales
    private lateinit var btnEnviar: Button
    private lateinit var btnListar: Button

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

        setContentView(R.layout.activity_pregunta)

        // Inicializar componentes del formulario
        etPregunta = findViewById(R.id.etPregunta)
        etOpcion1 = findViewById(R.id.etOpcion1)
        etOpcion2 = findViewById(R.id.etOpcion2)
        etOpcion3 = findViewById(R.id.etOpcion3)
        etOpcion4 = findViewById(R.id.etOpcion4)
        etRespuestaCorrecta = findViewById(R.id.etRespuestaCorrecta)
        etDificultad = findViewById(R.id.etDificultad)

        // Inicializar botones principales
        btnEnviar = findViewById(R.id.btnEnviar)
        btnListar = findViewById(R.id.btnListar)

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
        configurarBotones()
        configurarMenuInferior()
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

    private fun configurarBotones() {
        // Botón ENVIAR - Aquí irá la lógica para insertar en BD
        btnEnviar.setOnClickListener {
            // TODO: Implementar lógica de inserción en BD
        }

        // Botón LISTAR
        btnListar.setOnClickListener {
            val intent = Intent(this, ListaActivity::class.java)
            // TODO: Cambia por tu Activity de listar si es diferente
            startActivity(intent)
        }
    }

    private fun configurarMenuInferior() {
        // Botón Añadir Pregunta (esta misma actividad)
        btnAddQuestion.setOnClickListener {
            // Ya estamos en PreguntaActivity, no hacer nada o recargar
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

        // Botón Stats
        btnStats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
    }
}
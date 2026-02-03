package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.quizmaster.utils.UserSession

class PerfilActivity : AppCompatActivity() {

    // Componentes UI
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtEmailUsuario: TextView
    private lateinit var txtTotalPartidas: TextView
    private lateinit var txtMejorPuntuacion: TextView
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnVolver: Button

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // UserSession
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_perfil)

        // Inicializar UserSession
        userSession = UserSession(this)

        // Verificar que el usuario esté logueado
        if (!userSession.isLoggedIn()) {
            // Si no está logueado, redirigir al Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Inicializar componentes
        inicializarComponentes()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Cargar datos del usuario
        cargarDatosUsuario()

        // Configurar botones
        configurarBotones()

        // Configurar menú inferior
        configurarMenuInferior()
    }

    private fun inicializarComponentes() {
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        txtEmailUsuario = findViewById(R.id.txtEmailUsuario)
        txtTotalPartidas = findViewById(R.id.txtTotalPartidas)
        txtMejorPuntuacion = findViewById(R.id.txtMejorPuntuacion)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnVolver = findViewById(R.id.btnVolver)

        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)
    }

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun cargarDatosUsuario() {
        // Obtener datos de UserSession
        val nombreUsuario = userSession.getUserName() ?: "Usuario"
        val email = userSession.getUserEmail() ?: "email@example.com"

        txtNombreUsuario.text = nombreUsuario.uppercase()
        txtEmailUsuario.text = email

        // TODO: Cargar estadísticas del usuario desde la API
        // Por ahora valores por defecto
        txtTotalPartidas.text = "0"
        txtMejorPuntuacion.text = "0%"
    }

    private fun configurarBotones() {
        // Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        // Botón Volver
        btnVolver.setOnClickListener {
            finish() // Cierra esta actividad y vuelve a la anterior
        }
    }

    private fun configurarMenuInferior() {
        // Botón Añadir Pregunta
        btnAddQuestion.setOnClickListener {
            val intent = Intent(this, PreguntaActivity::class.java)
            startActivity(intent)
        }

        // Botón Fácil
        btnFacil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 1)
            startActivity(intent)
        }

        // Botón Medio
        btnMedio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 2)
            startActivity(intent)
        }

        // Botón Difícil
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

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("SÍ") { dialog, _ ->
                cerrarSesion()
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun cerrarSesion() {
        // Limpiar sesión
        userSession.logout()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        // Ir al LoginActivity y limpiar el stack de actividades
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.quizmaster.utils.UserSession
import com.example.quizmaster.database.PreferenciasDBHelper

class PerfilActivity : AppCompatActivity() {

    // Componentes UI
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtEmailUsuario: TextView
    private lateinit var txtTotalPartidas: TextView
    private lateinit var txtMejorPuntuacion: TextView
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnVolver: Button

    // Switches de configuración
    private lateinit var switchSonidos: Switch
    private lateinit var switchVibracion: Switch
    private lateinit var switchNotificaciones: Switch

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // UserSession y DB
    private lateinit var userSession: UserSession
    private lateinit var preferenciasDRHelper: PreferenciasDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_perfil)

        // Inicializar UserSession y DB
        userSession = UserSession(this)
        preferenciasDRHelper = PreferenciasDBHelper(this)

        // Verificar que el usuario esté logueado
        if (!userSession.isLoggedIn()) {
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

        // Cargar preferencias
        cargarPreferencias()

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

        // Switches de configuración
        switchSonidos = findViewById(R.id.switchSonidos)
        switchVibracion = findViewById(R.id.switchVibracion)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)

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
        val nombreUsuario = userSession.getUserName() ?: "Usuario"
        val email = userSession.getUserEmail() ?: "email@example.com"

        txtNombreUsuario.text = nombreUsuario.uppercase()
        txtEmailUsuario.text = email

        // TODO: Cargar estadísticas del usuario desde la API
        txtTotalPartidas.text = "0"
        txtMejorPuntuacion.text = "0%"
    }

    private fun cargarPreferencias() {
        val userId = userSession.getUserId()
        val preferencias = preferenciasDRHelper.obtenerPreferencias(userId)

        // Establecer estado de los switches SIN triggear listeners
        switchSonidos.setOnCheckedChangeListener(null)
        switchVibracion.setOnCheckedChangeListener(null)
        switchNotificaciones.setOnCheckedChangeListener(null)

        switchSonidos.isChecked = preferencias.sonidosActivados
        switchVibracion.isChecked = preferencias.vibracionActivada
        switchNotificaciones.isChecked = preferencias.notificacionesActivadas

        // Configurar listeners DESPUÉS de establecer estados
        switchSonidos.setOnCheckedChangeListener { _, isChecked ->
            val success = preferenciasDRHelper.actualizarSonidos(userId, isChecked)
            if (success) {
                Toast.makeText(
                    this,
                    if (isChecked) "Sonidos activados" else "Sonidos desactivados",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Error al actualizar preferencia", Toast.LENGTH_SHORT).show()
                // Revertir el switch si falló
                switchSonidos.setOnCheckedChangeListener(null)
                switchSonidos.isChecked = !isChecked
                switchSonidos.setOnCheckedChangeListener { _, checked ->
                    actualizarSonidos(checked)
                }
            }
        }

        switchVibracion.setOnCheckedChangeListener { _, isChecked ->
            val success = preferenciasDRHelper.actualizarVibracion(userId, isChecked)
            if (success) {
                Toast.makeText(
                    this,
                    if (isChecked) "Vibración activada" else "Vibración desactivada",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Error al actualizar preferencia", Toast.LENGTH_SHORT).show()
                switchVibracion.setOnCheckedChangeListener(null)
                switchVibracion.isChecked = !isChecked
                switchVibracion.setOnCheckedChangeListener { _, checked ->
                    actualizarVibracion(checked)
                }
            }
        }

        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            val success = preferenciasDRHelper.actualizarNotificaciones(userId, isChecked)
            if (success) {
                Toast.makeText(
                    this,
                    if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Error al actualizar preferencia", Toast.LENGTH_SHORT).show()
                switchNotificaciones.setOnCheckedChangeListener(null)
                switchNotificaciones.isChecked = !isChecked
                switchNotificaciones.setOnCheckedChangeListener { _, checked ->
                    actualizarNotificaciones(checked)
                }
            }
        }
    }

    private fun actualizarSonidos(isChecked: Boolean) {
        val userId = userSession.getUserId()
        preferenciasDRHelper.actualizarSonidos(userId, isChecked)
    }

    private fun actualizarVibracion(isChecked: Boolean) {
        val userId = userSession.getUserId()
        preferenciasDRHelper.actualizarVibracion(userId, isChecked)
    }

    private fun actualizarNotificaciones(isChecked: Boolean) {
        val userId = userSession.getUserId()
        preferenciasDRHelper.actualizarNotificaciones(userId, isChecked)
    }

    private fun configurarBotones() {
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun configurarMenuInferior() {
        btnAddQuestion.setOnClickListener {
            val intent = Intent(this, PreguntaActivity::class.java)
            startActivity(intent)
        }

        btnFacil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 1)
            startActivity(intent)
        }

        btnMedio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 2)
            startActivity(intent)
        }

        btnDificil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 3)
            startActivity(intent)
        }

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
        userSession.logout()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
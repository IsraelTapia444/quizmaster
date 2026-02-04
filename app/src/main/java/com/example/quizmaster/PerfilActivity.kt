package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.quizmaster.utils.ConfigManager
import com.example.quizmaster.utils.UserSession

class PerfilActivity : AppCompatActivity() {

    // Componentes UI - Información del usuario
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtEmailUsuario: TextView
    private lateinit var txtTotalPartidas: TextView
    private lateinit var txtMejorPuntuacion: TextView

    // Componentes UI - Configuración
    private lateinit var switchSonidos: SwitchCompat
    private lateinit var switchVibraciones: SwitchCompat
    private lateinit var switchNotificaciones: SwitchCompat

    // Componentes UI - Botones
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnVolver: Button

    // UserSession
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_perfil)

        // Inicializar ConfigManager (por si acaso no se hizo en Application)
        ConfigManager.init(this)

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

        // Cargar datos del usuario
        cargarDatosUsuario()

        // Cargar configuración
        cargarConfiguracion()

        // Configurar botones
        configurarBotones()

        // Configurar switches de configuración
        configurarSwitches()
    }

    private fun inicializarComponentes() {
        // Información del usuario
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        txtEmailUsuario = findViewById(R.id.txtEmailUsuario)
        txtTotalPartidas = findViewById(R.id.txtTotalPartidas)
        txtMejorPuntuacion = findViewById(R.id.txtMejorPuntuacion)

        // Switches de configuración
        switchSonidos = findViewById(R.id.switchSonidos)
        switchVibraciones = findViewById(R.id.switchVibraciones)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)

        // Botones
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnVolver = findViewById(R.id.btnVolver)

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

    /**
     * Carga la configuración actual desde la base de datos SQLite
     * y actualiza el estado de los switches
     */
    private fun cargarConfiguracion() {
        try {
            val config = ConfigManager.getConfig()

            // Actualizar los switches con la configuración guardada
            // Usamos setChecked sin trigger del listener
            switchSonidos.isChecked = config.soundEnabled
            switchVibraciones.isChecked = config.vibrationEnabled
            switchNotificaciones.isChecked = config.notificationsEnabled

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error al cargar la configuración",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Configura los listeners de los switches de configuración
     */
    private fun configurarSwitches() {
        // Switch de Sonidos
        switchSonidos.setOnCheckedChangeListener { _, isChecked ->
            val success = ConfigManager.setSoundEnabled(isChecked)
            if (success) {
                val mensaje = if (isChecked) "Sonidos activados" else "Sonidos desactivados"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar configuración", Toast.LENGTH_SHORT).show()
                // Revertir el cambio si falló
                switchSonidos.isChecked = !isChecked
            }
        }

        // Switch de Vibraciones
        switchVibraciones.setOnCheckedChangeListener { _, isChecked ->
            val success = ConfigManager.setVibrationEnabled(isChecked)
            if (success) {
                val mensaje = if (isChecked) "Vibraciones activadas" else "Vibraciones desactivadas"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar configuración", Toast.LENGTH_SHORT).show()
                // Revertir el cambio si falló
                switchVibraciones.isChecked = !isChecked
            }
        }

        // Switch de Notificaciones
        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            val success = ConfigManager.setNotificationsEnabled(isChecked)
            if (success) {
                val mensaje = if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar configuración", Toast.LENGTH_SHORT).show()
                // Revertir el cambio si falló
                switchNotificaciones.isChecked = !isChecked
            }
        }
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

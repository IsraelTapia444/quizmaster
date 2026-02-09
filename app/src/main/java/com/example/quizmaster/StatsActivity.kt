package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.quizmaster.models.Estadisticas
import com.example.quizmaster.network.RetrofitClient
import com.example.quizmaster.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsActivity : AppCompatActivity() {

    // Componentes de estadísticas
    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvTotalPartidas: TextView
    private lateinit var tvPuntuacionMedia: TextView
    private lateinit var tvMejorPuntuacion: TextView
    private lateinit var tvPeorPuntuacion: TextView
    private lateinit var tvUltimaPuntuacion: TextView
    private lateinit var tvPosicionRanking: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtSinEstadisticas: TextView

    // Icono de perfil
    private lateinit var btnIconoPerfil: android.widget.ImageView

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

        setContentView(R.layout.activity_stats)

        // Inicializar UserSession
        userSession = UserSession(this)

        // Verificar que el usuario esté logueado
        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar componentes
        inicializarComponentes()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Configurar listeners
        configurarMenuInferior()

        // Cargar estadísticas
        cargarEstadisticas()
    }

    private fun inicializarComponentes() {
        // TextViews de estadísticas
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvTotalPartidas = findViewById(R.id.tvTotalPartidas)
        tvPuntuacionMedia = findViewById(R.id.tvPuntuacionMedia)
        tvMejorPuntuacion = findViewById(R.id.tvMejorPuntuacion)
        tvPeorPuntuacion = findViewById(R.id.tvPeorPuntuacion)
        tvUltimaPuntuacion = findViewById(R.id.tvUltimaPuntuacion)

        // Si no existe este TextView en tu layout, coméntalo
        // tvPosicionRanking = findViewById(R.id.tvPosicionRanking)

        // ProgressBar y texto alternativo (si no existen, coméntalos)
        // progressBar = findViewById(R.id.progressBar)
        // txtSinEstadisticas = findViewById(R.id.txtSinEstadisticas)

        // Icono de perfil
        btnIconoPerfil = findViewById(R.id.btnIconoPerfil)
        btnIconoPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        // Botones del menú inferior
        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)

        // Mostrar nombre del usuario
        val nombreUsuario = userSession.getUserName() ?: "Usuario"
        tvNombreUsuario.text = "Estadísticas de $nombreUsuario"
    }

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun cargarEstadisticas() {
        // Obtener ID del usuario logueado
        val userId = userSession.getUserId()

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mostrar loading (si tienes ProgressBar)
        // progressBar?.visibility = View.VISIBLE

        // Ocultar estadísticas mientras carga
        ocultarEstadisticas()

        // Llamada a la API
        RetrofitClient.apiService.obtenerEstadisticas(userId).enqueue(object : Callback<Estadisticas> {
            override fun onResponse(call: Call<Estadisticas>, response: Response<Estadisticas>) {
                // Ocultar loading
                // progressBar?.visibility = View.GONE

                if (response.isSuccessful) {
                    val estadisticas = response.body()

                    if (estadisticas != null) {
                        // Mostrar estadísticas
                        mostrarEstadisticas(estadisticas)
                    } else {
                        mostrarError("No se pudieron cargar las estadísticas")
                    }
                } else {
                    mostrarError("Error del servidor: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Estadisticas>, t: Throwable) {
                // Ocultar loading
                // progressBar?.visibility = View.GONE

                mostrarError("Error de conexión: ${t.message}")
                android.util.Log.e("StatsActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun mostrarEstadisticas(stats: Estadisticas) {
        // Mostrar todos los TextViews
        tvTotalPartidas.visibility = View.VISIBLE
        tvPuntuacionMedia.visibility = View.VISIBLE
        tvMejorPuntuacion.visibility = View.VISIBLE
        tvPeorPuntuacion.visibility = View.VISIBLE
        tvUltimaPuntuacion.visibility = View.VISIBLE

        // Ocultar mensaje de error
        // txtSinEstadisticas?.visibility = View.GONE

        // Llenar datos
        tvTotalPartidas.text = stats.total_partidas.toString()
        tvPuntuacionMedia.text = String.format("%.1f%%", stats.puntuacion_media)
        tvMejorPuntuacion.text = "${stats.mejor_puntuacion}%"
        tvPeorPuntuacion.text = "${stats.peor_puntuacion}%"
        tvUltimaPuntuacion.text = "${stats.ultima_puntuacion}%"

        // Si tienes TextView para ranking
        // tvPosicionRanking?.text = "Posición ${stats.posicion_ranking} de ${stats.total_usuarios}"

        // Mostrar mensaje si no hay partidas
        if (stats.total_partidas == 0) {
            Toast.makeText(
                this,
                "Aún no has jugado ninguna partida",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun ocultarEstadisticas() {
        tvTotalPartidas.visibility = View.GONE
        tvPuntuacionMedia.visibility = View.GONE
        tvMejorPuntuacion.visibility = View.GONE
        tvPeorPuntuacion.visibility = View.GONE
        tvUltimaPuntuacion.visibility = View.GONE
    }

    private fun mostrarError(mensaje: String) {
        // txtSinEstadisticas?.visibility = View.VISIBLE
        // txtSinEstadisticas?.text = mensaje

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        // Mostrar valores por defecto
        tvTotalPartidas.text = "0"
        tvPuntuacionMedia.text = "0.0%"
        tvMejorPuntuacion.text = "0%"
        tvPeorPuntuacion.text = "0%"
        tvUltimaPuntuacion.text = "0%"

        // Hacer visibles con valores por defecto
        tvTotalPartidas.visibility = View.VISIBLE
        tvPuntuacionMedia.visibility = View.VISIBLE
        tvMejorPuntuacion.visibility = View.VISIBLE
        tvPeorPuntuacion.visibility = View.VISIBLE
        tvUltimaPuntuacion.visibility = View.VISIBLE
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

        // Botón Stats (ya estamos aquí)
        btnStats.setOnClickListener {
            Toast.makeText(this, "Ya estás en Estadísticas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar estadísticas al volver
        cargarEstadisticas()
    }
}
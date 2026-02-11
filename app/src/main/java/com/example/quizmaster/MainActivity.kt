package com.example.quizmaster

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.quizmaster.models.ApiResponse
import com.example.quizmaster.models.PartidaRequest
import com.example.quizmaster.models.Pregunta
import com.example.quizmaster.network.RetrofitClient
import com.example.quizmaster.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // Componentes UI - Juego
    private lateinit var tvPreguntaNumero: TextView
    private lateinit var tvPuntuacion: TextView
    private lateinit var tvPregunta: TextView
    private lateinit var progressBarJuego: ProgressBar
    private lateinit var radioGroup: RadioGroup
    private lateinit var rbOpcion1: RadioButton
    private lateinit var rbOpcion2: RadioButton
    private lateinit var rbOpcion3: RadioButton
    private lateinit var rbOpcion4: RadioButton
    private lateinit var btnConfirmar: Button

    // Contenedores de vistas
    private lateinit var layoutJuego: LinearLayout
    private lateinit var layoutCargando: LinearLayout
    private lateinit var layoutResultados: ScrollView

    // Componentes UI - Resultados
    private lateinit var tvPuntuacionFinal: TextView
    private lateinit var tvAciertos: TextView
    private lateinit var tvFallos: TextView
    private lateinit var tvComparacion: TextView
    private lateinit var btnVerEstadisticas: Button

    // Icono de perfil
    private lateinit var btnIconoPerfil: ImageView

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // Variables del juego
    private val preguntasJuego = mutableListOf<Pregunta>()
    private var preguntaActualIndex = 0
    private var puntuacion = 0
    private var aciertos = 0
    private var fallos = 0
    private var dificultadSeleccionada = "media" // facil, media, dificil
    private var respuestaSeleccionada = -1

    // UserSession
    private lateinit var userSession: UserSession

    // MediaPlayers para sonidos
    private var sonidoAcierto: MediaPlayer? = null
    private var sonidoFallo: MediaPlayer? = null

    // Handler para delays
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        // Inicializar UserSession
        userSession = UserSession(this)

        // Verificar si está logueado
        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Inicializar componentes
        inicializarComponentes()

        // Inicializar sonidos
        inicializarSonidos()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Configurar menú inferior
        configurarMenuInferior()

        // Obtener dificultad del Intent (si viene de los botones del menú)
        val dificultadIntent = intent.getIntExtra("dificultad", 2)
        dificultadSeleccionada = when (dificultadIntent) {
            1 -> "facil"
            2 -> "media"
            3 -> "dificil"
            else -> "media"
        }

        // Iniciar el juego
        iniciarJuego()
    }

    private fun inicializarComponentes() {
        // Contenedores
        layoutJuego = findViewById(R.id.layoutJuego)
        layoutCargando = findViewById(R.id.layoutCargando)
        layoutResultados = findViewById(R.id.layoutResultados)

        // Componentes del juego
        tvPreguntaNumero = findViewById(R.id.tvPreguntaNumero)
        tvPuntuacion = findViewById(R.id.tvPuntuacion)
        tvPregunta = findViewById(R.id.tvPregunta)
        progressBarJuego = findViewById(R.id.progressBarJuego)
        radioGroup = findViewById(R.id.radioGroup)
        rbOpcion1 = findViewById(R.id.rbOpcion1)
        rbOpcion2 = findViewById(R.id.rbOpcion2)
        rbOpcion3 = findViewById(R.id.rbOpcion3)
        rbOpcion4 = findViewById(R.id.rbOpcion4)
        btnConfirmar = findViewById(R.id.btnConfirmar)

        // Componentes de resultados
        tvPuntuacionFinal = findViewById(R.id.tvPuntuacionFinal)
        tvAciertos = findViewById(R.id.tvAciertos)
        tvFallos = findViewById(R.id.tvFallos)
        tvComparacion = findViewById(R.id.tvComparacion)
        btnVerEstadisticas = findViewById(R.id.btnVerEstadisticas)

        // Icono de perfil
        btnIconoPerfil = findViewById(R.id.btnIconoPerfil)
        btnIconoPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Botones del menú
        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)

        // Listener del botón confirmar
        btnConfirmar.setOnClickListener {
            confirmarRespuesta()
        }

        // Listener del RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            respuestaSeleccionada = when (checkedId) {
                R.id.rbOpcion1 -> 1
                R.id.rbOpcion2 -> 2
                R.id.rbOpcion3 -> 3
                R.id.rbOpcion4 -> 4
                else -> -1
            }
            btnConfirmar.isEnabled = respuestaSeleccionada != -1
        }

        // Listener del botón ver estadísticas
        btnVerEstadisticas.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
            finish()
        }
    }

    private fun inicializarSonidos() {
        try {
            // Sonidos del sistema (puedes reemplazarlos con archivos propios en res/raw/)
            sonidoAcierto = MediaPlayer.create(this, android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION))
            sonidoFallo = MediaPlayer.create(this, android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM))
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error inicializando sonidos: ${e.message}")
        }
    }

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun iniciarJuego() {
        // Resetear variables
        preguntasJuego.clear()
        preguntaActualIndex = 0
        puntuacion = 0
        aciertos = 0
        fallos = 0

        // Mostrar pantalla de carga
        mostrarCargando()

        // Cargar preguntas desde la API
        cargarPreguntas()
    }

    private fun cargarPreguntas() {
        RetrofitClient.apiService.obtenerPreguntasPorDificultad(dificultadSeleccionada)
            .enqueue(object : Callback<List<Pregunta>> {
                override fun onResponse(call: Call<List<Pregunta>>, response: Response<List<Pregunta>>) {
                    if (response.isSuccessful) {
                        val todasPreguntas = response.body()

                        if (todasPreguntas != null && todasPreguntas.size >= 10) {
                            // Seleccionar 10 preguntas aleatorias sin repetir
                            preguntasJuego.addAll(todasPreguntas.shuffled().take(10))

                            // Mostrar la primera pregunta
                            mostrarPregunta()
                        } else {
                            // No hay suficientes preguntas
                            Toast.makeText(
                                this@MainActivity,
                                "No hay suficientes preguntas de esta dificultad (mínimo 10)",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Error al cargar preguntas: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<List<Pregunta>>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })
    }

    private fun mostrarPregunta() {
        if (preguntaActualIndex >= preguntasJuego.size) {
            // Se acabaron las preguntas
            finalizarJuego()
            return
        }

        // Ocultar cargando, mostrar juego
        layoutCargando.visibility = View.GONE
        layoutJuego.visibility = View.VISIBLE
        layoutResultados.visibility = View.GONE

        val pregunta = preguntasJuego[preguntaActualIndex]

        // Actualizar UI
        tvPreguntaNumero.text = "Pregunta ${preguntaActualIndex + 1} de 10"
        tvPuntuacion.text = "Puntuación: $puntuacion"
        tvPregunta.text = pregunta.pregunta

        // Actualizar barra de progreso
        progressBarJuego.max = 10
        progressBarJuego.progress = preguntaActualIndex

        // Configurar opciones
        rbOpcion1.text = pregunta.opcion1
        rbOpcion2.text = pregunta.opcion2
        rbOpcion3.text = pregunta.opcion3
        rbOpcion4.text = pregunta.opcion4

        // Resetear RadioGroup
        radioGroup.clearCheck()
        respuestaSeleccionada = -1
        btnConfirmar.isEnabled = false

        // Habilitar RadioButtons
        habilitarOpciones(true)

        // Resetear colores
        resetearColoresOpciones()
    }

    private fun confirmarRespuesta() {
        if (respuestaSeleccionada == -1) return

        val pregunta = preguntasJuego[preguntaActualIndex]
        val esCorrecta = respuestaSeleccionada == pregunta.correcta

        // Deshabilitar opciones mientras se muestra feedback
        habilitarOpciones(false)
        btnConfirmar.isEnabled = false

        if (esCorrecta) {
            // Respuesta correcta
            puntuacion += 10
            aciertos++

            // Cambiar color a verde
            cambiarColorOpcion(respuestaSeleccionada, true)

            // Reproducir sonido de acierto
            reproducirSonidoAcierto()

            Toast.makeText(this, "¡Correcto! +10 puntos", Toast.LENGTH_SHORT).show()
        } else {
            // Respuesta incorrecta
            fallos++

            // Cambiar color a rojo
            cambiarColorOpcion(respuestaSeleccionada, false)

            // Reproducir sonido de fallo
            reproducirSonidoFallo()

            Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show()
        }

        // Esperar 1.5 segundos antes de la siguiente pregunta
        handler.postDelayed({
            preguntaActualIndex++
            mostrarPregunta()
        }, 1500)
    }

    private fun cambiarColorOpcion(opcion: Int, esCorrecta: Boolean) {
        val color = if (esCorrecta) {
            getColor(R.color.verde_acierto) // #4CAF50 (verde)
        } else {
            getColor(R.color.rojo_error) // #F44336 (rojo)
        }

        when (opcion) {
            1 -> rbOpcion1.setTextColor(color)
            2 -> rbOpcion2.setTextColor(color)
            3 -> rbOpcion3.setTextColor(color)
            4 -> rbOpcion4.setTextColor(color)
        }
    }

    private fun resetearColoresOpciones() {
        val colorNormal = getColor(R.color.texto_normal) // #F5F5F5
        rbOpcion1.setTextColor(colorNormal)
        rbOpcion2.setTextColor(colorNormal)
        rbOpcion3.setTextColor(colorNormal)
        rbOpcion4.setTextColor(colorNormal)
    }

    private fun habilitarOpciones(habilitar: Boolean) {
        rbOpcion1.isEnabled = habilitar
        rbOpcion2.isEnabled = habilitar
        rbOpcion3.isEnabled = habilitar
        rbOpcion4.isEnabled = habilitar
    }

    private fun reproducirSonidoAcierto() {
        try {
            sonidoAcierto?.start()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error reproduciendo sonido: ${e.message}")
        }
    }

    private fun reproducirSonidoFallo() {
        try {
            sonidoFallo?.start()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error reproduciendo sonido: ${e.message}")
        }
    }

    private fun mostrarCargando() {
        layoutCargando.visibility = View.VISIBLE
        layoutJuego.visibility = View.GONE
        layoutResultados.visibility = View.GONE
    }

    private fun finalizarJuego() {
        // Ocultar juego, mostrar resultados
        layoutJuego.visibility = View.GONE
        layoutCargando.visibility = View.GONE
        layoutResultados.visibility = View.VISIBLE

        // Mostrar resultados
        tvPuntuacionFinal.text = "$puntuacion"
        tvAciertos.text = "Aciertos: $aciertos"
        tvFallos.text = "Fallos: $fallos"

        // Guardar partida en la BD
        guardarPartida()
    }

    private fun guardarPartida() {
        val userId = userSession.getUserId()

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto de partida
        val partida = PartidaRequest(
            usuario_id = userId,
            puntuacion = puntuacion
        )

        // Guardar en la API
        RetrofitClient.apiService.guardarPartida(partida).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        // Partida guardada exitosamente

                        // Ahora obtener estadísticas para comparar con mejor puntuación
                        obtenerMejorPuntuacion()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Error al guardar partida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Error de conexión al guardar partida",
                    Toast.LENGTH_SHORT
                ).show()
                android.util.Log.e("MainActivity", "Error guardando partida: ${t.message}", t)
            }
        })
    }

    private fun obtenerMejorPuntuacion() {
        val userId = userSession.getUserId()

        RetrofitClient.apiService.obtenerEstadisticas(userId).enqueue(object : Callback<com.example.quizmaster.models.Estadisticas> {
            override fun onResponse(
                call: Call<com.example.quizmaster.models.Estadisticas>,
                response: Response<com.example.quizmaster.models.Estadisticas>
            ) {
                if (response.isSuccessful) {
                    val stats = response.body()

                    if (stats != null) {
                        val mejorPuntuacion = stats.mejor_puntuacion

                        // Comparar con la puntuación actual
                        if (puntuacion >= mejorPuntuacion && puntuacion > 0) {
                            if (puntuacion == mejorPuntuacion && stats.total_partidas > 1) {
                                tvComparacion.text = "¡Igualaste tu mejor puntuación!"
                            } else {
                                tvComparacion.text = "🎉 ¡NUEVA MEJOR PUNTUACIÓN! 🎉"
                            }
                            tvComparacion.setTextColor(getColor(R.color.verde_acierto))
                        } else {
                            tvComparacion.text = "Tu mejor puntuación es: $mejorPuntuacion"
                            tvComparacion.setTextColor(getColor(R.color.texto_normal))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<com.example.quizmaster.models.Estadisticas>, t: Throwable) {
                tvComparacion.text = ""
            }
        })
    }

    private fun configurarMenuInferior() {
        btnAddQuestion.setOnClickListener {
            startActivity(Intent(this, PreguntaActivity::class.java))
        }

        // Los botones del menú reinician el juego con la dificultad seleccionada
        btnFacil.setOnClickListener {
            dificultadSeleccionada = "facil"
            iniciarJuego()
        }

        btnMedio.setOnClickListener {
            dificultadSeleccionada = "media"
            iniciarJuego()
        }

        btnDificil.setOnClickListener {
            dificultadSeleccionada = "dificil"
            iniciarJuego()
        }

        btnStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos de sonidos
        sonidoAcierto?.release()
        sonidoFallo?.release()
        // Cancelar handlers pendientes
        handler.removeCallbacksAndMessages(null)
    }
}
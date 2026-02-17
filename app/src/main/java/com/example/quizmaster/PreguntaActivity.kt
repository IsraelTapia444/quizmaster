package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.quizmaster.models.ApiResponse
import com.example.quizmaster.models.PreguntaRequest
import com.example.quizmaster.network.RetrofitClient
import com.example.quizmaster.utils.ShakeDetector
import com.example.quizmaster.utils.SonidosHelper
import com.example.quizmaster.database.PreferenciasDBHelper
import com.example.quizmaster.utils.UserSession
import android.os.Vibrator
import android.content.Context
import android.os.VibrationEffect
import android.os.Build
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreguntaActivity : AppCompatActivity() {

    // Componentes del formulario
    private lateinit var etPregunta: EditText
    private lateinit var etOpcion1: EditText
    private lateinit var etOpcion2: EditText
    private lateinit var etOpcion3: EditText
    private lateinit var etOpcion4: EditText
    private lateinit var etRespuestaCorrecta: EditText
    private lateinit var etDificultad: EditText
    private lateinit var txtTitulo: TextView

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

    // Variables para modo edición
    private var modoEdicion = false
    private var preguntaId = -1

    // Helpers
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var sonidosHelper: SonidosHelper
    private lateinit var preferenciasDRHelper: PreferenciasDBHelper
    private lateinit var userSession: UserSession
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_pregunta)

        // Inicializar helpers
        userSession = UserSession(this)
        sonidosHelper = SonidosHelper(this)
        preferenciasDRHelper = PreferenciasDBHelper(this)

        // Obtener servicio de vibración
        vibrator = try {
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } catch (e: Exception) {
            android.util.Log.e("PreguntaActivity", "Error obteniendo Vibrator: ${e.message}")
            null
        }
        android.util.Log.d("PreguntaActivity", "Vibrator inicializado: ${vibrator != null}")

        // Inicializar detector de agitación
        shakeDetector = ShakeDetector(this) {
            onShakeDetected()
        }

        // Inicializar componentes
        inicializarComponentes()

        // Verificar si es modo edición
        verificarModoEdicion()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Configurar listeners
        configurarBotones()
        configurarMenuInferior()
    }

    private fun inicializarComponentes() {
        // Título (si existe en tu layout)
        // txtTitulo = findViewById(R.id.txtTitulo)

        // Componentes del formulario
        etPregunta = findViewById(R.id.etPregunta)
        etOpcion1 = findViewById(R.id.etOpcion1)
        etOpcion2 = findViewById(R.id.etOpcion2)
        etOpcion3 = findViewById(R.id.etOpcion3)
        etOpcion4 = findViewById(R.id.etOpcion4)
        etRespuestaCorrecta = findViewById(R.id.etRespuestaCorrecta)
        etDificultad = findViewById(R.id.etDificultad)

        // Botones principales
        btnEnviar = findViewById(R.id.btnEnviar)
        btnListar = findViewById(R.id.btnListar)

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
    }

    private fun verificarModoEdicion() {
        modoEdicion = intent.getBooleanExtra("modo_edicion", false)

        if (modoEdicion) {
            preguntaId = intent.getIntExtra("pregunta_id", -1)

            // Cambiar título y texto del botón
            // txtTitulo?.text = "EDITAR PREGUNTA"
            btnEnviar.text = "ACTUALIZAR"

            // Pre-llenar campos con los datos recibidos
            etPregunta.setText(intent.getStringExtra("pregunta") ?: "")
            etOpcion1.setText(intent.getStringExtra("opcion1") ?: "")
            etOpcion2.setText(intent.getStringExtra("opcion2") ?: "")
            etOpcion3.setText(intent.getStringExtra("opcion3") ?: "")
            etOpcion4.setText(intent.getStringExtra("opcion4") ?: "")
            etRespuestaCorrecta.setText(intent.getIntExtra("correcta", 1).toString())

            // Convertir dificultad de texto a número
            val dificultadTexto = intent.getStringExtra("dificultad") ?: "media"
            val dificultadNumero = when (dificultadTexto.lowercase()) {
                "facil" -> 1
                "media" -> 2
                "dificil" -> 3
                else -> 2
            }
            etDificultad.setText(dificultadNumero.toString())
        }
    }

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun configurarBotones() {
        // Botón ENVIAR/ACTUALIZAR
        btnEnviar.setOnClickListener {
            if (modoEdicion) {
                actualizarPregunta()
            } else {
                insertarPregunta()
            }
        }

        // Botón LISTAR
        btnListar.setOnClickListener {
            val intent = Intent(this, ListaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun insertarPregunta() {
        // Obtener datos del formulario
        val pregunta = etPregunta.text.toString().trim()
        val opcion1 = etOpcion1.text.toString().trim()
        val opcion2 = etOpcion2.text.toString().trim()
        val opcion3 = etOpcion3.text.toString().trim()
        val opcion4 = etOpcion4.text.toString().trim()
        val respuestaCorrectaStr = etRespuestaCorrecta.text.toString().trim()
        val dificultadStr = etDificultad.text.toString().trim()

        // Validaciones
        if (!validarCampos(pregunta, opcion1, opcion2, opcion3, opcion4, respuestaCorrectaStr, dificultadStr)) {
            return
        }

        // Convertir valores
        val respuestaCorrecta = respuestaCorrectaStr.toInt()
        val dificultadNivel = dificultadStr.toInt()

        // Convertir nivel numérico a texto
        val dificultadTexto = when (dificultadNivel) {
            1 -> "facil"
            2 -> "media"
            3 -> "dificil"
            else -> "media"
        }

        // Deshabilitar botón mientras se procesa
        btnEnviar.isEnabled = false
        btnEnviar.text = "GUARDANDO..."

        // Crear objeto de petición
        val preguntaRequest = PreguntaRequest(
            pregunta = pregunta,
            opcion1 = opcion1,
            opcion2 = opcion2,
            opcion3 = opcion3,
            opcion4 = opcion4,
            correcta = respuestaCorrecta,
            categoria = "General",
            dificultad = dificultadTexto
        )

        // Llamada a la API con Retrofit
        RetrofitClient.apiService.crearPregunta(preguntaRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                // Re-habilitar botón
                btnEnviar.isEnabled = true
                btnEnviar.text = "ENVIAR"

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        Toast.makeText(
                            this@PreguntaActivity,
                            "Pregunta guardada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Reproducir sonido de pregunta guardada
                        val userId = userSession.getUserId()
                        val preferencias = preferenciasDRHelper.obtenerPreferencias(userId)
                        if (preferencias.sonidosActivados) {
                            sonidosHelper.reproducirPreguntaGuardada()
                        }

                        limpiarFormulario()
                    } else {
                        Toast.makeText(
                            this@PreguntaActivity,
                            apiResponse?.message ?: "Error al guardar pregunta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PreguntaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                btnEnviar.isEnabled = true
                btnEnviar.text = "ENVIAR"

                Toast.makeText(
                    this@PreguntaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                android.util.Log.e("PreguntaActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun actualizarPregunta() {
        // Obtener datos del formulario
        val pregunta = etPregunta.text.toString().trim()
        val opcion1 = etOpcion1.text.toString().trim()
        val opcion2 = etOpcion2.text.toString().trim()
        val opcion3 = etOpcion3.text.toString().trim()
        val opcion4 = etOpcion4.text.toString().trim()
        val respuestaCorrectaStr = etRespuestaCorrecta.text.toString().trim()
        val dificultadStr = etDificultad.text.toString().trim()

        // Validaciones
        if (!validarCampos(pregunta, opcion1, opcion2, opcion3, opcion4, respuestaCorrectaStr, dificultadStr)) {
            return
        }

        // Convertir valores
        val respuestaCorrecta = respuestaCorrectaStr.toInt()
        val dificultadNivel = dificultadStr.toInt()

        val dificultadTexto = when (dificultadNivel) {
            1 -> "facil"
            2 -> "media"
            3 -> "dificil"
            else -> "media"
        }

        // Deshabilitar botón mientras se procesa
        btnEnviar.isEnabled = false
        btnEnviar.text = "ACTUALIZANDO..."

        // Crear objeto de petición
        val preguntaRequest = PreguntaRequest(
            pregunta = pregunta,
            opcion1 = opcion1,
            opcion2 = opcion2,
            opcion3 = opcion3,
            opcion4 = opcion4,
            correcta = respuestaCorrecta,
            categoria = "General",
            dificultad = dificultadTexto
        )

        // Llamada a la API para actualizar
        RetrofitClient.apiService.actualizarPregunta(preguntaId, preguntaRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                btnEnviar.isEnabled = true
                btnEnviar.text = "ACTUALIZAR"

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        Toast.makeText(
                            this@PreguntaActivity,
                            "Pregunta actualizada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Reproducir sonido de pregunta guardada
                        val userId = userSession.getUserId()
                        val preferencias = preferenciasDRHelper.obtenerPreferencias(userId)
                        if (preferencias.sonidosActivados) {
                            sonidosHelper.reproducirPreguntaGuardada()
                        }

                        // Volver a ListaActivity
                        finish()
                    } else {
                        Toast.makeText(
                            this@PreguntaActivity,
                            apiResponse?.message ?: "Error al actualizar pregunta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PreguntaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                btnEnviar.isEnabled = true
                btnEnviar.text = "ACTUALIZAR"

                Toast.makeText(
                    this@PreguntaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                android.util.Log.e("PreguntaActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun validarCampos(
        pregunta: String,
        opcion1: String,
        opcion2: String,
        opcion3: String,
        opcion4: String,
        respuestaCorrecta: String,
        dificultad: String
    ): Boolean {
        if (pregunta.isEmpty()) {
            etPregunta.error = "La pregunta es obligatoria"
            etPregunta.requestFocus()
            return false
        }

        if (opcion1.isEmpty()) {
            etOpcion1.error = "La opción 1 es obligatoria"
            etOpcion1.requestFocus()
            return false
        }

        if (opcion2.isEmpty()) {
            etOpcion2.error = "La opción 2 es obligatoria"
            etOpcion2.requestFocus()
            return false
        }

        if (opcion3.isEmpty()) {
            etOpcion3.error = "La opción 3 es obligatoria"
            etOpcion3.requestFocus()
            return false
        }

        if (opcion4.isEmpty()) {
            etOpcion4.error = "La opción 4 es obligatoria"
            etOpcion4.requestFocus()
            return false
        }

        if (respuestaCorrecta.isEmpty()) {
            etRespuestaCorrecta.error = "La respuesta correcta es obligatoria"
            etRespuestaCorrecta.requestFocus()
            return false
        }

        if (dificultad.isEmpty()) {
            etDificultad.error = "La dificultad es obligatoria"
            etDificultad.requestFocus()
            return false
        }

        val respuesta = respuestaCorrecta.toIntOrNull()
        if (respuesta == null || respuesta < 1 || respuesta > 4) {
            etRespuestaCorrecta.error = "Debe ser un número entre 1 y 4"
            etRespuestaCorrecta.requestFocus()
            return false
        }

        val dif = dificultad.toIntOrNull()
        if (dif == null || dif < 1 || dif > 3) {
            etDificultad.error = "Debe ser 1 (Fácil), 2 (Medio) o 3 (Difícil)"
            etDificultad.requestFocus()
            return false
        }

        return true
    }

    private fun limpiarFormulario() {
        etPregunta.text.clear()
        etOpcion1.text.clear()
        etOpcion2.text.clear()
        etOpcion3.text.clear()
        etOpcion4.text.clear()
        etRespuestaCorrecta.text.clear()
        etDificultad.text.clear()

        etPregunta.requestFocus()
    }

    private fun configurarMenuInferior() {
        btnAddQuestion.setOnClickListener {
            if (modoEdicion) {
                // Si estamos editando, volver al modo normal
                val intent = Intent(this, PreguntaActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Ya estás en Añadir Pregunta", Toast.LENGTH_SHORT).show()
            }
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

    /**
     * Método llamado cuando se detecta agitación
     */
    private fun onShakeDetected() {
        val userId = userSession.getUserId()
        val preferencias = preferenciasDRHelper.obtenerPreferencias(userId)

        // Vibrar si está activado
        if (preferencias.vibracionActivada) {
            vibrar()
        }

        // Limpiar formulario
        limpiarFormulario()

        // Mostrar Toast
        Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show()
    }

    /**
     * Vibrar el dispositivo
     */
    private fun vibrar() {
        try {
            if (vibrator == null) {
                vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let { vib ->
                // Intentar vibrar incluso si hasVibrator() devuelve false
                // En algunos dispositivos puede funcionar de todos modos
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(200)
                }
            }
        } catch (e: Exception) {
            // Si falla, no hacer nada (dispositivo sin vibrador)
            android.util.Log.d("PreguntaActivity", "Vibración no disponible: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        // Iniciar detector de agitación
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        // Detener detector de agitación
        shakeDetector.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos
        sonidosHelper.release()
    }
}
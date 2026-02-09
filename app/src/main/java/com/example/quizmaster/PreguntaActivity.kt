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

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_pregunta)

        // Inicializar componentes
        inicializarComponentes()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Configurar listeners
        configurarBotones()
        configurarMenuInferior()
    }

    private fun inicializarComponentes() {
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

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun configurarBotones() {
        // Botón ENVIAR - Insertar pregunta en la BD
        btnEnviar.setOnClickListener {
            insertarPregunta()
        }

        // Botón LISTAR - Ir a ListaActivity
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
            categoria = "General", // Puedes agregar un campo para esto si quieres
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
                        // Pregunta insertada exitosamente
                        Toast.makeText(
                            this@PreguntaActivity,
                            "Pregunta guardada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Limpiar formulario
                        limpiarFormulario()

                    } else {
                        // Error del servidor
                        Toast.makeText(
                            this@PreguntaActivity,
                            apiResponse?.message ?: "Error al guardar pregunta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Error HTTP
                    Toast.makeText(
                        this@PreguntaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Re-habilitar botón
                btnEnviar.isEnabled = true
                btnEnviar.text = "ENVIAR"

                // Error de conexión
                Toast.makeText(
                    this@PreguntaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Log para debug
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
        // Validar que no estén vacíos
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

        // Validar que respuesta correcta sea un número entre 1 y 4
        val respuesta = respuestaCorrecta.toIntOrNull()
        if (respuesta == null || respuesta < 1 || respuesta > 4) {
            etRespuestaCorrecta.error = "Debe ser un número entre 1 y 4"
            etRespuestaCorrecta.requestFocus()
            return false
        }

        // Validar que dificultad sea un número entre 1 y 3
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

        // Enfocar en el primer campo
        etPregunta.requestFocus()
    }

    private fun configurarMenuInferior() {
        // Botón Añadir Pregunta (ya estamos aquí)
        btnAddQuestion.setOnClickListener {
            // Ya estamos en PreguntaActivity
            Toast.makeText(this, "Ya estás en Añadir Pregunta", Toast.LENGTH_SHORT).show()
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
}
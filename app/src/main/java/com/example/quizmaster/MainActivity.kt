package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    // Componentes UI
    private lateinit var txtTituloDificultad: TextView
    private lateinit var recyclerViewPreguntas: RecyclerView
    private lateinit var btnEnviar: Button
    private lateinit var btnIconoPerfil: android.widget.ImageView

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // Adapter
    private lateinit var preguntasAdapter: PreguntasAdapter

    // Lista de preguntas
    private var listaPreguntas = mutableListOf<Pregunta>()
    private var dificultadActual = 3 // 1=Fácil, 2=Medio, 3=Difícil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SOLUCIÓN 1: Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        // Inicializar componentes
        inicializarComponentes()

        // SOLUCIÓN 1: Aplicar WindowInsets al menú inferior
        aplicarWindowInsets()

        // Configurar RecyclerView
        configurarRecyclerView()

        // Configurar listeners
        configurarBotones()
        configurarMenuInferior()

        // Cargar preguntas inicial
        cargarPreguntas(dificultadActual)
    }

    private fun inicializarComponentes() {
        txtTituloDificultad = findViewById(R.id.txtTituloDificultad)
        recyclerViewPreguntas = findViewById(R.id.recyclerViewPreguntas)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnIconoPerfil = findViewById(R.id.btnIconoPerfil)

        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)
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

    private fun configurarRecyclerView() {
        recyclerViewPreguntas.layoutManager = LinearLayoutManager(this)
        preguntasAdapter = PreguntasAdapter(listaPreguntas)
        recyclerViewPreguntas.adapter = preguntasAdapter
    }

    private fun configurarBotones() {
        btnEnviar.setOnClickListener {
            enviarRespuestas()
        }

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
    }

    private fun configurarMenuInferior() {
        // Botón Añadir Pregunta
        btnAddQuestion.setOnClickListener {
            val intent = Intent(this, PreguntaActivity::class.java)
            startActivity(intent)
        }

        // Botón Fácil
        btnFacil.setOnClickListener {
            cambiarDificultad(1, "DIFICULTAD\nFÁCIL")
        }

        // Botón Medio
        btnMedio.setOnClickListener {
            cambiarDificultad(2, "DIFICULTAD\nMEDIO")
        }

        // Botón Difícil
        btnDificil.setOnClickListener {
            cambiarDificultad(3, "DIFICULTAD\nDIFÍCIL")
        }

        // Botón Stats
        btnStats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cambiarDificultad(dificultad: Int, titulo: String) {
        dificultadActual = dificultad
        txtTituloDificultad.text = titulo
        cargarPreguntas(dificultad)
    }

    private fun cargarPreguntas(dificultad: Int) {
        // TODO: Implementar lógica para cargar preguntas desde BD según dificultad
        // Ejemplo temporal con datos de prueba:
        listaPreguntas.clear()

        // Aquí deberías hacer la consulta a tu base de datos
        // listaPreguntas.addAll(dbHelper.obtenerPreguntasPorDificultad(dificultad))

        // Datos de ejemplo para pruebas:
        for (i in 1..4) {
            listaPreguntas.add(
                Pregunta(
                    id = i,
                    textoPregunta = "PREGUNTA $i",
                    opcion1 = "Opción 1",
                    opcion2 = "Opción 2",
                    opcion3 = "Opción 3",
                    opcion4 = "Opción 4",
                    respuestaCorrecta = 1,
                    dificultad = dificultad
                )
            )
        }

        preguntasAdapter.notifyDataSetChanged()
    }

    private fun enviarRespuestas() {
        // Verificar que todas las preguntas estén respondidas
        if (!preguntasAdapter.todasLasPreguntasRespondidas()) {
            Toast.makeText(this, "Por favor responde todas las preguntas", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener respuestas
        val respuestas = preguntasAdapter.getRespuestasSeleccionadas()

        // TODO: Implementar lógica para calcular puntuación
        var aciertos = 0
        respuestas.forEach { (posicion, respuestaSeleccionada) ->
            if (listaPreguntas[posicion].respuestaCorrecta == respuestaSeleccionada) {
                aciertos++
            }
        }

        val puntuacion = (aciertos * 100) / listaPreguntas.size

        Toast.makeText(
            this,
            "Respuestas enviadas. Puntuación: $puntuacion%",
            Toast.LENGTH_LONG
        ).show()

        // TODO: Guardar estadísticas en BD o SharedPreferences
    }
}
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaActivity : AppCompatActivity() {

    // Componentes UI
    private lateinit var recyclerViewLista: RecyclerView
    private lateinit var txtSinPreguntas: TextView
    private lateinit var btnIconoPerfil: android.widget.ImageView

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // Adapter y lista de preguntas
    private lateinit var listaAdapter: ListaAdapter
    private var listaPreguntas = mutableListOf<Pregunta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_lista)

        // Inicializar componentes
        inicializarComponentes()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Configurar RecyclerView
        configurarRecyclerView()

        // Configurar menú inferior
        configurarMenuInferior()

        // Cargar preguntas
        cargarPreguntas()
    }

    private fun inicializarComponentes() {
        recyclerViewLista = findViewById(R.id.recyclerViewLista)
        txtSinPreguntas = findViewById(R.id.txtSinPreguntas)
        btnIconoPerfil = findViewById(R.id.btnIconoPerfil)

        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)

        // Configurar listener del icono de perfil
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

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun configurarRecyclerView() {
        recyclerViewLista.layoutManager = LinearLayoutManager(this)

        // Inicializar adapter con listeners para editar y eliminar
        listaAdapter = ListaAdapter(
            preguntas = listaPreguntas,
            onEditarClick = { pregunta ->
                editarPregunta(pregunta)
            },
            onEliminarClick = { pregunta, position ->
                mostrarDialogoEliminar(pregunta, position)
            }
        )

        recyclerViewLista.adapter = listaAdapter
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

    private fun cargarPreguntas() {
        // TODO: Aquí harás la llamada con Retrofit a tu API PHP para obtener todas las preguntas
        // Por ahora, usaremos datos de ejemplo para que veas cómo funciona el frontend

        listaPreguntas.clear()

        // Datos de ejemplo - ELIMINAR cuando implementes Retrofit
        listaPreguntas.addAll(
            listOf(
                Pregunta(
                    id = 1,
                    textoPregunta = "¿Cuál es la capital de España?",
                    opcion1 = "Madrid",
                    opcion2 = "Barcelona",
                    opcion3 = "Valencia",
                    opcion4 = "Sevilla",
                    respuestaCorrecta = 1,
                    dificultad = 1
                ),
                Pregunta(
                    id = 2,
                    textoPregunta = "¿En qué año se descubrió América?",
                    opcion1 = "1490",
                    opcion2 = "1492",
                    opcion3 = "1500",
                    opcion4 = "1485",
                    respuestaCorrecta = 2,
                    dificultad = 2
                ),
                Pregunta(
                    id = 3,
                    textoPregunta = "¿Cuál es el planeta más grande del sistema solar?",
                    opcion1 = "Tierra",
                    opcion2 = "Marte",
                    opcion3 = "Júpiter",
                    opcion4 = "Saturno",
                    respuestaCorrecta = 3,
                    dificultad = 1
                ),
                Pregunta(
                    id = 4,
                    textoPregunta = "¿Quién escribió 'El Quijote'?",
                    opcion1 = "Lope de Vega",
                    opcion2 = "Miguel de Cervantes",
                    opcion3 = "Federico García Lorca",
                    opcion4 = "Antonio Machado",
                    respuestaCorrecta = 2,
                    dificultad = 1
                ),
                Pregunta(
                    id = 5,
                    textoPregunta = "¿Cuál es el resultado de 15 x 8?",
                    opcion1 = "110",
                    opcion2 = "115",
                    opcion3 = "120",
                    opcion4 = "125",
                    respuestaCorrecta = 3,
                    dificultad = 2
                ),
                Pregunta(
                    id = 6,
                    textoPregunta = "¿Cuántos continentes hay en el mundo?",
                    opcion1 = "5",
                    opcion2 = "6",
                    opcion3 = "7",
                    opcion4 = "8",
                    respuestaCorrecta = 3,
                    dificultad = 1
                ),
                Pregunta(
                    id = 7,
                    textoPregunta = "¿Cuál es el océano más grande?",
                    opcion1 = "Atlántico",
                    opcion2 = "Índico",
                    opcion3 = "Ártico",
                    opcion4 = "Pacífico",
                    respuestaCorrecta = 4,
                    dificultad = 2
                ),
                Pregunta(
                    id = 8,
                    textoPregunta = "¿En qué año terminó la Segunda Guerra Mundial?",
                    opcion1 = "1943",
                    opcion2 = "1944",
                    opcion3 = "1945",
                    opcion4 = "1946",
                    respuestaCorrecta = 3,
                    dificultad = 3
                )
            )
        )

        // Actualizar UI
        if (listaPreguntas.isEmpty()) {
            txtSinPreguntas.visibility = android.view.View.VISIBLE
            recyclerViewLista.visibility = android.view.View.GONE
        } else {
            txtSinPreguntas.visibility = android.view.View.GONE
            recyclerViewLista.visibility = android.view.View.VISIBLE
            listaAdapter.notifyDataSetChanged()
        }
    }

    private fun editarPregunta(pregunta: Pregunta) {
        // TODO: Aquí navegarás a PreguntaActivity pasando los datos de la pregunta para editarla
        // O puedes crear un EditarPreguntaActivity específico

        // Opción 1: Ir a PreguntaActivity con datos de la pregunta
        val intent = Intent(this, PreguntaActivity::class.java)
        intent.putExtra("pregunta_id", pregunta.id)
        intent.putExtra("pregunta_texto", pregunta.textoPregunta)
        intent.putExtra("pregunta_opcion1", pregunta.opcion1)
        intent.putExtra("pregunta_opcion2", pregunta.opcion2)
        intent.putExtra("pregunta_opcion3", pregunta.opcion3)
        intent.putExtra("pregunta_opcion4", pregunta.opcion4)
        intent.putExtra("pregunta_respuesta", pregunta.respuestaCorrecta)
        intent.putExtra("pregunta_dificultad", pregunta.dificultad)
        intent.putExtra("modo_edicion", true) // Flag para indicar que es edición
        startActivity(intent)

        // Mostrar mensaje temporal
        Toast.makeText(
            this,
            "Editar pregunta ID: ${pregunta.id}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun mostrarDialogoEliminar(pregunta: Pregunta, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Pregunta")
            .setMessage("¿Estás seguro de que quieres eliminar esta pregunta?\n\n\"${pregunta.textoPregunta}\"")
            .setPositiveButton("ELIMINAR") { dialog, _ ->
                eliminarPregunta(pregunta, position)
                dialog.dismiss()
            }
            .setNegativeButton("CANCELAR") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun eliminarPregunta(pregunta: Pregunta, position: Int) {
        // TODO: Aquí harás la llamada con Retrofit a tu API PHP para eliminar la pregunta
        // Por ahora solo la eliminamos de la lista local

        listaAdapter.eliminarPregunta(position)

        Toast.makeText(
            this,
            "Pregunta eliminada correctamente",
            Toast.LENGTH_SHORT
        ).show()

        // Verificar si la lista quedó vacía
        if (listaPreguntas.isEmpty()) {
            txtSinPreguntas.visibility = android.view.View.VISIBLE
            recyclerViewLista.visibility = android.view.View.GONE
        }

        // TODO: Implementar llamada Retrofit aquí
        /*
        RetrofitClient.apiService.eliminarPregunta(pregunta.id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listaAdapter.eliminarPregunta(position)
                    Toast.makeText(this@ListaActivity, "Pregunta eliminada", Toast.LENGTH_SHORT).show()

                    if (listaPreguntas.isEmpty()) {
                        txtSinPreguntas.visibility = android.view.View.VISIBLE
                        recyclerViewLista.visibility = android.view.View.GONE
                    }
                } else {
                    Toast.makeText(this@ListaActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ListaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
        */
    }

    // Método para recargar las preguntas (útil cuando vuelves de editar)
    override fun onResume() {
        super.onResume()
        // Opcional: recargar las preguntas al volver a esta actividad
        // cargarPreguntas()
    }
}
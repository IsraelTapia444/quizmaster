package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.models.ApiResponse
import com.example.quizmaster.models.Pregunta
import com.example.quizmaster.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaActivity : AppCompatActivity() {

    // Componentes UI
    private lateinit var recyclerViewLista: RecyclerView
    private lateinit var txtSinPreguntas: TextView
    private lateinit var btnIconoPerfil: android.widget.ImageView

    // Botones de filtro
    private lateinit var btnFiltroTodas: Button
    private lateinit var btnFiltroFacil: Button
    private lateinit var btnFiltroMedio: Button
    private lateinit var btnFiltroDificil: Button

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // Adapter y lista de preguntas
    private lateinit var adapter: ListaAdapter
    private val listaPreguntas = mutableListOf<Pregunta>()

    // Filtro actual
    private var filtroActual = "todas" // "todas", "facil", "media", "dificil"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_lista)

        // Inicializar componentes
        inicializarComponentes()

        // Configurar RecyclerView
        configurarRecyclerView()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Configurar filtros
        configurarFiltros()

        // Configurar menú inferior
        configurarMenuInferior()

        // Cargar preguntas
        cargarPreguntas()
    }

    private fun inicializarComponentes() {
        recyclerViewLista = findViewById(R.id.recyclerViewLista)
        txtSinPreguntas = findViewById(R.id.txtSinPreguntas)
        btnIconoPerfil = findViewById(R.id.btnIconoPerfil)


        btnFiltroTodas = findViewById(R.id.btnFiltroTodas)
        btnFiltroFacil = findViewById(R.id.btnFiltroFacil)
        btnFiltroMedio = findViewById(R.id.btnFiltroMedio)
        btnFiltroDificil = findViewById(R.id.btnFiltroDificil)

        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)

        // Configurar listener del icono de perfil
        btnIconoPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarRecyclerView() {
        adapter = ListaAdapter(
            preguntas = listaPreguntas,
            onEditarClick = { pregunta ->
                editarPregunta(pregunta)
            },
            onEliminarClick = { pregunta, position ->
                confirmarEliminarPregunta(pregunta, position)
            }
        )

        recyclerViewLista.layoutManager = LinearLayoutManager(this)
        recyclerViewLista.adapter = adapter
    }

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun configurarFiltros() {

        // Agregar opción de filtro con un menú contextual
        recyclerViewLista.setOnLongClickListener {
            mostrarMenuFiltros()
            true
        }

        btnFiltroTodas.setOnClickListener {
            filtroActual = "todas"
            actualizarEstiloFiltros()
            cargarPreguntas()
        }

        btnFiltroFacil.setOnClickListener {
            filtroActual = "facil"
            actualizarEstiloFiltros()
            cargarPreguntasPorDificultad("facil")
        }

        btnFiltroMedio.setOnClickListener {
            filtroActual = "media"
            actualizarEstiloFiltros()
            cargarPreguntasPorDificultad("media")
        }

        btnFiltroDificil.setOnClickListener {
            filtroActual = "dificil"
            actualizarEstiloFiltros()
            cargarPreguntasPorDificultad("dificil")
        }
    }

    private fun mostrarMenuFiltros() {
        val opciones = arrayOf("Todas", "Fácil", "Medio", "Difícil")

        AlertDialog.Builder(this)
            .setTitle("Filtrar por Dificultad")
            .setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> {
                        filtroActual = "todas"
                        cargarPreguntas()
                    }
                    1 -> {
                        filtroActual = "facil"
                        cargarPreguntasPorDificultad("facil")
                    }
                    2 -> {
                        filtroActual = "media"
                        cargarPreguntasPorDificultad("media")
                    }
                    3 -> {
                        filtroActual = "dificil"
                        cargarPreguntasPorDificultad("dificil")
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun actualizarEstiloFiltros() {
        // Resetear estilos de todos los botones
        // Código de ejemplo si tienes botones de filtro:
        /*
        btnFiltroTodas.setBackgroundColor(getColor(R.color.filtro_inactivo))
        btnFiltroFacil.setBackgroundColor(getColor(R.color.filtro_inactivo))
        btnFiltroMedio.setBackgroundColor(getColor(R.color.filtro_inactivo))
        btnFiltroDificil.setBackgroundColor(getColor(R.color.filtro_inactivo))

        // Resaltar el filtro activo
        when (filtroActual) {
            "todas" -> btnFiltroTodas.setBackgroundColor(getColor(R.color.filtro_activo))
            "facil" -> btnFiltroFacil.setBackgroundColor(getColor(R.color.filtro_activo))
            "media" -> btnFiltroMedio.setBackgroundColor(getColor(R.color.filtro_activo))
            "dificil" -> btnFiltroDificil.setBackgroundColor(getColor(R.color.filtro_activo))
        }
        */
    }

    private fun cargarPreguntas() {
        txtSinPreguntas.visibility = View.GONE
        recyclerViewLista.visibility = View.GONE

        RetrofitClient.apiService.obtenerTodasLasPreguntas().enqueue(object : Callback<List<Pregunta>> {
            override fun onResponse(call: Call<List<Pregunta>>, response: Response<List<Pregunta>>) {
                if (response.isSuccessful) {
                    val preguntas = response.body()

                    if (preguntas != null && preguntas.isNotEmpty()) {
                        listaPreguntas.clear()
                        listaPreguntas.addAll(preguntas)
                        adapter.notifyDataSetChanged()

                        recyclerViewLista.visibility = View.VISIBLE
                        txtSinPreguntas.visibility = View.GONE

                        // Actualizar título con cantidad
                        supportActionBar?.title = "Preguntas (${preguntas.size})"
                    } else {
                        recyclerViewLista.visibility = View.GONE
                        txtSinPreguntas.visibility = View.VISIBLE
                        txtSinPreguntas.text = "No hay preguntas registradas"
                    }
                } else {
                    recyclerViewLista.visibility = View.GONE
                    txtSinPreguntas.visibility = View.VISIBLE
                    txtSinPreguntas.text = "Error al cargar preguntas"

                    Toast.makeText(
                        this@ListaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Pregunta>>, t: Throwable) {
                recyclerViewLista.visibility = View.GONE
                txtSinPreguntas.visibility = View.VISIBLE
                txtSinPreguntas.text = "Error de conexión\nMantén presionado para filtrar"

                Toast.makeText(
                    this@ListaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                android.util.Log.e("ListaActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun cargarPreguntasPorDificultad(dificultad: String) {
        txtSinPreguntas.visibility = View.GONE
        recyclerViewLista.visibility = View.GONE

        RetrofitClient.apiService.obtenerPreguntasPorDificultad(dificultad).enqueue(object : Callback<List<Pregunta>> {
            override fun onResponse(call: Call<List<Pregunta>>, response: Response<List<Pregunta>>) {
                if (response.isSuccessful) {
                    val preguntas = response.body()

                    if (preguntas != null && preguntas.isNotEmpty()) {
                        listaPreguntas.clear()
                        listaPreguntas.addAll(preguntas)
                        adapter.notifyDataSetChanged()

                        recyclerViewLista.visibility = View.VISIBLE
                        txtSinPreguntas.visibility = View.GONE

                        // Capitalizar primera letra
                        val dificultadTexto = dificultad.replaceFirstChar { it.uppercase() }
                        supportActionBar?.title = "Preguntas $dificultadTexto (${preguntas.size})"

                        Toast.makeText(
                            this@ListaActivity,
                            "Mostrando ${preguntas.size} preguntas de dificultad $dificultadTexto",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        recyclerViewLista.visibility = View.GONE
                        txtSinPreguntas.visibility = View.VISIBLE
                        val dificultadTexto = dificultad.replaceFirstChar { it.uppercase() }
                        txtSinPreguntas.text = "No hay preguntas de dificultad $dificultadTexto"
                    }
                } else {
                    recyclerViewLista.visibility = View.GONE
                    txtSinPreguntas.visibility = View.VISIBLE
                    txtSinPreguntas.text = "Error al cargar preguntas"

                    Toast.makeText(
                        this@ListaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Pregunta>>, t: Throwable) {
                recyclerViewLista.visibility = View.GONE
                txtSinPreguntas.visibility = View.VISIBLE
                txtSinPreguntas.text = "Error de conexión"

                Toast.makeText(
                    this@ListaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                android.util.Log.e("ListaActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun editarPregunta(pregunta: Pregunta) {
        val intent = Intent(this, PreguntaActivity::class.java)
        intent.putExtra("modo_edicion", true)
        intent.putExtra("pregunta_id", pregunta.id)
        intent.putExtra("pregunta", pregunta.pregunta)
        intent.putExtra("opcion1", pregunta.opcion1)
        intent.putExtra("opcion2", pregunta.opcion2)
        intent.putExtra("opcion3", pregunta.opcion3)
        intent.putExtra("opcion4", pregunta.opcion4)
        intent.putExtra("correcta", pregunta.correcta)
        intent.putExtra("dificultad", pregunta.dificultad)
        startActivity(intent)
    }

    private fun confirmarEliminarPregunta(pregunta: Pregunta, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Pregunta")
            .setMessage("¿Estás seguro de que quieres eliminar esta pregunta?\n\n\"${pregunta.pregunta}\"")
            .setPositiveButton("SÍ") { dialog, _ ->
                eliminarPregunta(pregunta.id, position)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun eliminarPregunta(preguntaId: Int, position: Int) {
        RetrofitClient.apiService.eliminarPregunta(preguntaId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        Toast.makeText(
                            this@ListaActivity,
                            "Pregunta eliminada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        listaPreguntas.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        if (listaPreguntas.isEmpty()) {
                            recyclerViewLista.visibility = View.GONE
                            txtSinPreguntas.visibility = View.VISIBLE
                            txtSinPreguntas.text = when (filtroActual) {
                                "facil" -> "No hay preguntas fáciles"
                                "media" -> "No hay preguntas medias"
                                "dificil" -> "No hay preguntas difíciles"
                                else -> "No hay preguntas registradas"
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@ListaActivity,
                            apiResponse?.message ?: "Error al eliminar pregunta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ListaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(
                    this@ListaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                android.util.Log.e("ListaActivity", "Error eliminando: ${t.message}", t)
            }
        })
    }

    private fun configurarMenuInferior() {
        btnAddQuestion.setOnClickListener {
            val intent = Intent(this, PreguntaActivity::class.java)
            startActivity(intent)
        }

        // Los botones del menú inferior también sirven como filtros rápidos
        btnFacil.setOnClickListener {
            filtroActual = "facil"
            cargarPreguntasPorDificultad("facil")
        }

        btnMedio.setOnClickListener {
            filtroActual = "media"
            cargarPreguntasPorDificultad("media")
        }

        btnDificil.setOnClickListener {
            filtroActual = "dificil"
            cargarPreguntasPorDificultad("dificil")
        }

        btnStats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar preguntas según el filtro actual
        when (filtroActual) {
            "todas" -> cargarPreguntas()
            "facil" -> cargarPreguntasPorDificultad("facil")
            "media" -> cargarPreguntasPorDificultad("media")
            "dificil" -> cargarPreguntasPorDificultad("dificil")
        }
    }
}
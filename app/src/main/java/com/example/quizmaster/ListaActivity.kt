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
    private lateinit var progressBar: ProgressBar

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // Adapter y lista de preguntas
    private lateinit var adapter: ListaAdapter
    private val listaPreguntas = mutableListOf<Pregunta>()

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

        // Configurar menú inferior
        configurarMenuInferior()

        // Cargar preguntas desde la API
        cargarPreguntas()
    }

    private fun inicializarComponentes() {
        recyclerViewLista = findViewById(R.id.recyclerViewLista)
        txtSinPreguntas = findViewById(R.id.txtSinPreguntas)
        btnIconoPerfil = findViewById(R.id.btnIconoPerfil)

        // ProgressBar (si no existe en tu layout, puedes comentar)
        // progressBar = findViewById(R.id.progressBar)

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
        // Configurar adapter con callbacks
        adapter = ListaAdapter(
            preguntas = listaPreguntas,
            onEditarClick = { pregunta ->
                editarPregunta(pregunta)
            },
            onEliminarClick = { pregunta, position ->
                confirmarEliminarPregunta(pregunta, position)
            }
        )

        // Configurar RecyclerView
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

    private fun cargarPreguntas() {
        // Mostrar loading (si tienes ProgressBar)
        // progressBar?.visibility = View.VISIBLE
        txtSinPreguntas.visibility = View.GONE
        recyclerViewLista.visibility = View.GONE

        // Llamada a la API para obtener todas las preguntas
        RetrofitClient.apiService.obtenerTodasLasPreguntas().enqueue(object : Callback<List<Pregunta>> {
            override fun onResponse(call: Call<List<Pregunta>>, response: Response<List<Pregunta>>) {
                // Ocultar loading
                // progressBar?.visibility = View.GONE

                if (response.isSuccessful) {
                    val preguntas = response.body()

                    if (preguntas != null && preguntas.isNotEmpty()) {
                        // Mostrar preguntas
                        listaPreguntas.clear()
                        listaPreguntas.addAll(preguntas)
                        adapter.notifyDataSetChanged()

                        recyclerViewLista.visibility = View.VISIBLE
                        txtSinPreguntas.visibility = View.GONE
                    } else {
                        // No hay preguntas
                        recyclerViewLista.visibility = View.GONE
                        txtSinPreguntas.visibility = View.VISIBLE
                        txtSinPreguntas.text = "No hay preguntas registradas"
                    }
                } else {
                    // Error HTTP
                    recyclerViewLista.visibility = View.GONE
                    txtSinPreguntas.visibility = View.VISIBLE
                    txtSinPreguntas.text = "Error al cargar preguntas: ${response.code()}"

                    Toast.makeText(
                        this@ListaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Pregunta>>, t: Throwable) {
                // Ocultar loading
                // progressBar?.visibility = View.GONE

                // Error de conexión
                recyclerViewLista.visibility = View.GONE
                txtSinPreguntas.visibility = View.VISIBLE
                txtSinPreguntas.text = "Error de conexión\nVerifica tu internet"

                Toast.makeText(
                    this@ListaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Log para debug
                android.util.Log.e("ListaActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun editarPregunta(pregunta: Pregunta) {
        // TODO: Implementar edición de pregunta
        // Por ahora, ir a PreguntaActivity con los datos
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
        // Llamada a la API para eliminar
        RetrofitClient.apiService.eliminarPregunta(preguntaId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        // Pregunta eliminada exitosamente
                        Toast.makeText(
                            this@ListaActivity,
                            "Pregunta eliminada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Eliminar de la lista local
                        listaPreguntas.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        // Si no quedan preguntas, mostrar mensaje
                        if (listaPreguntas.isEmpty()) {
                            recyclerViewLista.visibility = View.GONE
                            txtSinPreguntas.visibility = View.VISIBLE
                            txtSinPreguntas.text = "No hay preguntas registradas"
                        }
                    } else {
                        // Error del servidor
                        Toast.makeText(
                            this@ListaActivity,
                            apiResponse?.message ?: "Error al eliminar pregunta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Error HTTP
                    Toast.makeText(
                        this@ListaActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Error de conexión
                Toast.makeText(
                    this@ListaActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Log para debug
                android.util.Log.e("ListaActivity", "Error eliminando: ${t.message}", t)
            }
        })
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

    override fun onResume() {
        super.onResume()
        // Recargar preguntas al volver a la actividad
        cargarPreguntas()
    }
}
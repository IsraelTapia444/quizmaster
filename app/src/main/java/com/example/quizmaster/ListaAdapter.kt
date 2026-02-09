package com.example.quizmaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.models.Pregunta

class ListaAdapter(
    private val preguntas: MutableList<Pregunta>,
    private val onEditarClick: (Pregunta) -> Unit,
    private val onEliminarClick: (Pregunta, Int) -> Unit
) : RecyclerView.Adapter<ListaAdapter.PreguntaViewHolder>() {

    inner class PreguntaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtId: TextView = itemView.findViewById(R.id.txtIdPregunta)
        val txtPregunta: TextView = itemView.findViewById(R.id.txtPreguntaLista)
        val txtOpcion1: TextView = itemView.findViewById(R.id.txtOpcion1Lista)
        val txtOpcion2: TextView = itemView.findViewById(R.id.txtOpcion2Lista)
        val txtOpcion3: TextView = itemView.findViewById(R.id.txtOpcion3Lista)
        val txtOpcion4: TextView = itemView.findViewById(R.id.txtOpcion4Lista)
        val txtCorrecta: TextView = itemView.findViewById(R.id.txtRespuestaCorrectaLista)
        val txtDificultad: TextView = itemView.findViewById(R.id.txtDificultadLista)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditarPregunta)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarPregunta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pregunta_lista, parent, false)
        return PreguntaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val pregunta = preguntas[position]

        holder.txtId.text = "ID: ${pregunta.id}"
        holder.txtPregunta.text = pregunta.pregunta
        holder.txtOpcion1.text = "1. ${pregunta.opcion1}"
        holder.txtOpcion2.text = "2. ${pregunta.opcion2}"
        holder.txtOpcion3.text = "3. ${pregunta.opcion3}"
        holder.txtOpcion4.text = "4. ${pregunta.opcion4}"
        holder.txtCorrecta.text = "Correcta: ${pregunta.correcta}"

        // Convertir dificultad de texto a legible
        val dificultadTexto = when (pregunta.dificultad.lowercase()) {
            "facil" -> "Fácil"
            "media" -> "Media"
            "dificil" -> "Difícil"
            else -> pregunta.dificultad
        }
        holder.txtDificultad.text = "Dificultad: $dificultadTexto"

        // Listeners de botones
        holder.btnEditar.setOnClickListener {
            onEditarClick(pregunta)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminarClick(pregunta, position)
        }
    }

    override fun getItemCount(): Int = preguntas.size

    // Método para actualizar la lista completa
    fun actualizarLista(nuevasPreguntas: List<Pregunta>) {
        preguntas.clear()
        preguntas.addAll(nuevasPreguntas)
        notifyDataSetChanged()
    }

    // Método para eliminar una pregunta
    fun eliminarPregunta(position: Int) {
        if (position in 0 until preguntas.size) {
            preguntas.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
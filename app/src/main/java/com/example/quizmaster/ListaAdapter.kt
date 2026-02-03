package com.example.quizmaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListaAdapter(
    private val preguntas: MutableList<Pregunta>,
    private val onEditarClick: (Pregunta) -> Unit,
    private val onEliminarClick: (Pregunta, Int) -> Unit
) : RecyclerView.Adapter<ListaAdapter.PreguntaViewHolder>() {

    inner class PreguntaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtIdPregunta: TextView = itemView.findViewById(R.id.txtIdPregunta)
        val txtPregunta: TextView = itemView.findViewById(R.id.txtPreguntaLista)
        val txtOpcion1: TextView = itemView.findViewById(R.id.txtOpcion1Lista)
        val txtOpcion2: TextView = itemView.findViewById(R.id.txtOpcion2Lista)
        val txtOpcion3: TextView = itemView.findViewById(R.id.txtOpcion3Lista)
        val txtOpcion4: TextView = itemView.findViewById(R.id.txtOpcion4Lista)
        val txtRespuestaCorrecta: TextView = itemView.findViewById(R.id.txtRespuestaCorrectaLista)
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

        // Establecer datos de la pregunta
        holder.txtIdPregunta.text = "ID: ${pregunta.id}"
        holder.txtPregunta.text = pregunta.textoPregunta
        holder.txtOpcion1.text = "1. ${pregunta.opcion1}"
        holder.txtOpcion2.text = "2. ${pregunta.opcion2}"
        holder.txtOpcion3.text = "3. ${pregunta.opcion3}"
        holder.txtOpcion4.text = "4. ${pregunta.opcion4}"
        holder.txtRespuestaCorrecta.text = "Correcta: ${pregunta.respuestaCorrecta}"

        // Mostrar dificultad en texto
        val dificultadTexto = when (pregunta.dificultad) {
            1 -> "Fácil"
            2 -> "Medio"
            3 -> "Difícil"
            else -> "Desconocida"
        }
        holder.txtDificultad.text = "Dificultad: $dificultadTexto"

        // Configurar botón editar
        holder.btnEditar.setOnClickListener {
            onEditarClick(pregunta)
        }

        // Configurar botón eliminar
        holder.btnEliminar.setOnClickListener {
            onEliminarClick(pregunta, position)
        }
    }

    override fun getItemCount(): Int = preguntas.size

    // Método para eliminar una pregunta de la lista
    fun eliminarPregunta(position: Int) {
        preguntas.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, preguntas.size)
    }

    // Método para actualizar la lista completa
    fun actualizarLista(nuevasPreguntas: List<Pregunta>) {
        preguntas.clear()
        preguntas.addAll(nuevasPreguntas)
        notifyDataSetChanged()
    }
}
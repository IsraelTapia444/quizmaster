package com.example.quizmaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PreguntasAdapter(private val preguntas: List<Pregunta>) :
    RecyclerView.Adapter<PreguntasAdapter.PreguntaViewHolder>() {

    // Mapa para guardar las respuestas seleccionadas
    private val respuestasSeleccionadas = mutableMapOf<Int, Int>()

    inner class PreguntaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPregunta: TextView = itemView.findViewById(R.id.txtPregunta)
        val opcionesGroup: RadioGroup = itemView.findViewById(R.id.opcionesGroup)
        val opcion1: RadioButton = itemView.findViewById(R.id.opcion1)
        val opcion2: RadioButton = itemView.findViewById(R.id.opcion2)
        val opcion3: RadioButton = itemView.findViewById(R.id.opcion3)
        val opcion4: RadioButton = itemView.findViewById(R.id.opcion4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pregunta, parent, false)
        return PreguntaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val pregunta = preguntas[position]

        // Establecer el texto de la pregunta
        holder.txtPregunta.text = pregunta.textoPregunta

        // Establecer las opciones
        holder.opcion1.text = pregunta.opcion1
        holder.opcion2.text = pregunta.opcion2
        holder.opcion3.text = pregunta.opcion3
        holder.opcion4.text = pregunta.opcion4

        // Limpiar selección previa
        holder.opcionesGroup.clearCheck()

        // Restaurar selección si existe
        respuestasSeleccionadas[position]?.let { opcionId ->
            when (opcionId) {
                1 -> holder.opcion1.isChecked = true
                2 -> holder.opcion2.isChecked = true
                3 -> holder.opcion3.isChecked = true
                4 -> holder.opcion4.isChecked = true
            }
        }

        // Listener para guardar la respuesta seleccionada
        holder.opcionesGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.opcion1 -> respuestasSeleccionadas[position] = 1
                R.id.opcion2 -> respuestasSeleccionadas[position] = 2
                R.id.opcion3 -> respuestasSeleccionadas[position] = 3
                R.id.opcion4 -> respuestasSeleccionadas[position] = 4
            }
        }
    }

    override fun getItemCount(): Int = preguntas.size

    // Método para obtener las respuestas seleccionadas
    fun getRespuestasSeleccionadas(): Map<Int, Int> {
        return respuestasSeleccionadas.toMap()
    }

    // Método para verificar si todas las preguntas tienen respuesta
    fun todasLasPreguntasRespondidas(): Boolean {
        return respuestasSeleccionadas.size == preguntas.size
    }
}
package com.example.quizmaster

data class Pregunta(
    val id: Int,
    val textoPregunta: String,
    val opcion1: String,
    val opcion2: String,
    val opcion3: String,
    val opcion4: String,
    val respuestaCorrecta: Int,
    val dificultad: Int
)
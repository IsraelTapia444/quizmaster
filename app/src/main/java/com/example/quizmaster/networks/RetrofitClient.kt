package com.example.quizmaster.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ⚠️ IMPORTANTE: Cambia esta URL por la de tu servidor
    // Ejemplos:
    // - Servidor local (PC): "http://192.168.1.100/quizmaster/api/"
    // - XAMPP local: "http://10.0.2.2/quizmaster/api/" (para emulador Android)
    // - Servidor remoto: "https://tudominio.com/api/"
    private const val BASE_URL = "http://10.0.2.2/quizmaster/api/"

    // Configuración de logging para debug
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con configuración de timeouts
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instancia del servicio API
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
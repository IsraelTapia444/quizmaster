package com.example.quizmaster

import android.app.Application

/**
 * Clase Application personalizada para QuizMaster
 * 
 * Esta clase se ejecuta cuando se inicia la aplicación, antes que cualquier Activity.
 * Es el lugar ideal para inicializar componentes que deben estar disponibles
 * durante todo el ciclo de vida de la app.
 * 
 * IMPORTANTE: Debes registrar esta clase en el AndroidManifest.xml:
 * <application
 *     android:name=".QuizMasterApplication"
 *     ...>
 */
class QuizMasterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}

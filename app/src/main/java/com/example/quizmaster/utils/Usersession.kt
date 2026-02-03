package com.example.quizmaster.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.quizmaster.models.Usuario

/**
 * Clase para manejar la sesión del usuario usando SharedPreferences
 */
class UserSession(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "QuizMasterSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_REMEMBER_ME = "rememberMe"
    }

    /**
     * Guardar sesión del usuario al hacer login
     */
    fun saveUserSession(
        userId: Int,
        userName: String,
        userEmail: String,
        rememberMe: Boolean = false
    ) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_EMAIL, userEmail)
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe)
        editor.apply()
    }

    /**
     * Verificar si el usuario está logueado
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Obtener ID del usuario
     */
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    /**
     * Obtener nombre del usuario
     */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    /**
     * Obtener email del usuario
     */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Verificar si tiene activado "Recordar sesión"
     */
    fun hasRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Cerrar sesión del usuario
     */
    fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    /**
     * Obtener todos los datos del usuario
     */
    fun getUserData(): Usuario? {
        if (!isLoggedIn()) return null

        val userId = getUserId()
        val userName = getUserName() ?: return null
        val userEmail = getUserEmail() ?: return null

        return Usuario(
            id = userId,
            nombre = userName,
            email = userEmail,
            fecha_registro = ""
        )
    }
}
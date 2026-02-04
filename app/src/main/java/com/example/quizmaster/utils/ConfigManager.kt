package com.example.quizmaster.utils

import android.content.Context
import com.example.quizmaster.database.ConfigDatabaseHelper
import com.example.quizmaster.models.AppConfig

/**
 * Manager singleton para gestionar la configuración de la aplicación
 * 
 * Esta clase proporciona una interfaz simple y global para acceder y modificar
 * la configuración de la app desde cualquier parte del código.
 * 
 * Uso:
 * ```
 * // Inicializar (en Application o primera Activity)
 * ConfigManager.init(context)
 * 
 * // Obtener configuración
 * val config = ConfigManager.getConfig()
 * 
 * // Actualizar sonidos
 * ConfigManager.setSoundEnabled(true)
 * 
 * // Verificar si los sonidos están activados
 * if (ConfigManager.isSoundEnabled()) {
 *     // Reproducir sonido
 * }
 * ```
 */
object ConfigManager {
    
    private var dbHelper: ConfigDatabaseHelper? = null
    private var cachedConfig: AppConfig? = null

    /**
     * Inicializa el ConfigManager con el contexto de la aplicación
     * Debe llamarse antes de usar cualquier otro método, idealmente en Application.onCreate()
     * 
     * @param context Contexto de la aplicación
     */
    fun init(context: Context) {
        if (dbHelper == null) {
            dbHelper = ConfigDatabaseHelper(context.applicationContext)
            // Cargar configuración en caché
            cachedConfig = dbHelper?.getConfig()
        }
    }

    /**
     * Obtiene la configuración actual de la aplicación
     * 
     * @return AppConfig con la configuración actual
     * @throws IllegalStateException si no se ha inicializado el ConfigManager
     */
    fun getConfig(): AppConfig {
        checkInitialized()
        // Si hay caché, devolverla; si no, cargar de DB
        return cachedConfig ?: loadConfig()
    }

    /**
     * Recarga la configuración desde la base de datos
     * Útil si se modifica la configuración desde otro proceso
     * 
     * @return AppConfig con la configuración actualizada
     */
    fun loadConfig(): AppConfig {
        checkInitialized()
        cachedConfig = dbHelper?.getConfig()
        return cachedConfig ?: AppConfig()
    }

    /**
     * Actualiza la configuración completa
     * 
     * @param config Nueva configuración
     * @return true si se actualizó correctamente
     */
    fun updateConfig(config: AppConfig): Boolean {
        checkInitialized()
        val success = dbHelper?.updateConfig(config) ?: false
        if (success) {
            cachedConfig = config
        }
        return success
    }

    // ========== SONIDOS ==========

    /**
     * Verifica si los sonidos están activados
     */
    fun isSoundEnabled(): Boolean {
        return getConfig().soundEnabled
    }

    /**
     * Activa o desactiva los sonidos
     * 
     * @param enabled true para activar, false para desactivar
     * @return true si se actualizó correctamente
     */
    fun setSoundEnabled(enabled: Boolean): Boolean {
        checkInitialized()
        val success = dbHelper?.updateSoundEnabled(enabled) ?: false
        if (success) {
            cachedConfig = cachedConfig?.copy(soundEnabled = enabled)
        }
        return success
    }

    // ========== VIBRACIONES ==========

    /**
     * Verifica si las vibraciones están activadas
     */
    fun isVibrationEnabled(): Boolean {
        return getConfig().vibrationEnabled
    }

    /**
     * Activa o desactiva las vibraciones
     * 
     * @param enabled true para activar, false para desactivar
     * @return true si se actualizó correctamente
     */
    fun setVibrationEnabled(enabled: Boolean): Boolean {
        checkInitialized()
        val success = dbHelper?.updateVibrationEnabled(enabled) ?: false
        if (success) {
            cachedConfig = cachedConfig?.copy(vibrationEnabled = enabled)
        }
        return success
    }

    // ========== NOTIFICACIONES ==========

    /**
     * Verifica si las notificaciones están activadas
     */
    fun isNotificationsEnabled(): Boolean {
        return getConfig().notificationsEnabled
    }

    /**
     * Activa o desactiva las notificaciones
     * 
     * @param enabled true para activar, false para desactivar
     * @return true si se actualizó correctamente
     */
    fun setNotificationsEnabled(enabled: Boolean): Boolean {
        checkInitialized()
        val success = dbHelper?.updateNotificationsEnabled(enabled) ?: false
        if (success) {
            cachedConfig = cachedConfig?.copy(notificationsEnabled = enabled)
        }
        return success
    }

    // ========== UTILIDADES ==========

    /**
     * Resetea la configuración a los valores por defecto
     * 
     * @return true si se reseteó correctamente
     */
    fun resetConfig(): Boolean {
        checkInitialized()
        val success = dbHelper?.resetConfig() ?: false
        if (success) {
            cachedConfig = AppConfig()
        }
        return success
    }

    /**
     * Verifica que el ConfigManager haya sido inicializado
     * 
     * @throws IllegalStateException si no se ha inicializado
     */
    private fun checkInitialized() {
        if (dbHelper == null) {
            throw IllegalStateException(
                "ConfigManager no ha sido inicializado. " +
                "Llama a ConfigManager.init(context) primero."
            )
        }
    }

    /**
     * Limpia los recursos (útil para testing)
     */
    fun cleanup() {
        dbHelper?.close()
        dbHelper = null
        cachedConfig = null
    }
}

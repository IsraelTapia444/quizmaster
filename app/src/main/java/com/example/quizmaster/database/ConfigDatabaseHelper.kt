package com.example.quizmaster.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.quizmaster.models.AppConfig

/**
 * Helper de SQLite para gestionar la base de datos local de configuración
 * 
 * Esta clase se encarga de:
 * - Crear la base de datos y las tablas necesarias
 * - Gestionar las versiones de la base de datos
 * - Proporcionar métodos CRUD para la configuración de la app
 */
class ConfigDatabaseHelper(context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Nombre y versión de la base de datos
        private const val DATABASE_NAME = "quizmaster_config.db"
        private const val DATABASE_VERSION = 1

        // Nombre de la tabla
        private const val TABLE_CONFIG = "app_config"

        // Nombres de las columnas
        private const val COLUMN_ID = "id"
        private const val COLUMN_SOUND = "sound_enabled"
        private const val COLUMN_VIBRATION = "vibration_enabled"
        private const val COLUMN_NOTIFICATIONS = "notifications_enabled"
    }

    /**
     * Se ejecuta cuando se crea la base de datos por primera vez
     */
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_CONFIG (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_SOUND INTEGER NOT NULL DEFAULT 1,
                $COLUMN_VIBRATION INTEGER NOT NULL DEFAULT 1,
                $COLUMN_NOTIFICATIONS INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()
        
        db.execSQL(createTableQuery)
        
        // Insertar configuración por defecto
        val defaultConfig = ContentValues().apply {
            put(COLUMN_ID, 1)
            put(COLUMN_SOUND, 1)
            put(COLUMN_VIBRATION, 1)
            put(COLUMN_NOTIFICATIONS, 1)
        }
        db.insert(TABLE_CONFIG, null, defaultConfig)
    }

    /**
     * Se ejecuta cuando se actualiza la versión de la base de datos
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Por ahora, simplemente eliminamos y recreamos la tabla
        // En producción, aquí harías migraciones más sofisticadas
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONFIG")
        onCreate(db)
    }

    /**
     * Obtiene la configuración actual de la aplicación
     * 
     * @return AppConfig con la configuración actual, o configuración por defecto si no existe
     */
    fun getConfig(): AppConfig {
        val db = readableDatabase
        var config = AppConfig() // Configuración por defecto
        
        val cursor = db.query(
            TABLE_CONFIG,
            arrayOf(COLUMN_ID, COLUMN_SOUND, COLUMN_VIBRATION, COLUMN_NOTIFICATIONS),
            "$COLUMN_ID = ?",
            arrayOf("1"),
            null,
            null,
            null
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                config = AppConfig(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    soundEnabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_SOUND)) == 1,
                    vibrationEnabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_VIBRATION)) == 1,
                    notificationsEnabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS)) == 1
                )
            }
        }
        
        return config
    }

    /**
     * Actualiza la configuración completa
     * 
     * @param config Nueva configuración a guardar
     * @return true si se actualizó correctamente, false en caso contrario
     */
    fun updateConfig(config: AppConfig): Boolean {
        val db = writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_SOUND, if (config.soundEnabled) 1 else 0)
            put(COLUMN_VIBRATION, if (config.vibrationEnabled) 1 else 0)
            put(COLUMN_NOTIFICATIONS, if (config.notificationsEnabled) 1 else 0)
        }
        
        val rowsAffected = db.update(
            TABLE_CONFIG,
            values,
            "$COLUMN_ID = ?",
            arrayOf("1")
        )
        
        return rowsAffected > 0
    }

    /**
     * Actualiza únicamente el estado de los sonidos
     * 
     * @param enabled true para activar, false para desactivar
     * @return true si se actualizó correctamente
     */
    fun updateSoundEnabled(enabled: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SOUND, if (enabled) 1 else 0)
        }
        
        val rowsAffected = db.update(
            TABLE_CONFIG,
            values,
            "$COLUMN_ID = ?",
            arrayOf("1")
        )
        
        return rowsAffected > 0
    }

    /**
     * Actualiza únicamente el estado de las vibraciones
     * 
     * @param enabled true para activar, false para desactivar
     * @return true si se actualizó correctamente
     */
    fun updateVibrationEnabled(enabled: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_VIBRATION, if (enabled) 1 else 0)
        }
        
        val rowsAffected = db.update(
            TABLE_CONFIG,
            values,
            "$COLUMN_ID = ?",
            arrayOf("1")
        )
        
        return rowsAffected > 0
    }

    /**
     * Actualiza únicamente el estado de las notificaciones
     * 
     * @param enabled true para activar, false para desactivar
     * @return true si se actualizó correctamente
     */
    fun updateNotificationsEnabled(enabled: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTIFICATIONS, if (enabled) 1 else 0)
        }
        
        val rowsAffected = db.update(
            TABLE_CONFIG,
            values,
            "$COLUMN_ID = ?",
            arrayOf("1")
        )
        
        return rowsAffected > 0
    }

    /**
     * Resetea la configuración a los valores por defecto
     * 
     * @return true si se reseteó correctamente
     */
    fun resetConfig(): Boolean {
        val defaultConfig = AppConfig()
        return updateConfig(defaultConfig)
    }
}

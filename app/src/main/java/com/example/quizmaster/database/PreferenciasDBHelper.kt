package com.example.quizmaster.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PreferenciasDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "QuizMasterPreferencias.db"
        private const val DATABASE_VERSION = 1

        // Tabla de preferencias
        private const val TABLE_PREFERENCIAS = "preferencias"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USUARIO_ID = "usuario_id"
        private const val COLUMN_SONIDOS = "sonidos_activados"
        private const val COLUMN_VIBRACION = "vibracion_activada"
        private const val COLUMN_NOTIFICACIONES = "notificaciones_activadas"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PREFERENCIAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USUARIO_ID INTEGER UNIQUE NOT NULL,
                $COLUMN_SONIDOS INTEGER DEFAULT 1,
                $COLUMN_VIBRACION INTEGER DEFAULT 1,
                $COLUMN_NOTIFICACIONES INTEGER DEFAULT 1
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PREFERENCIAS")
        onCreate(db)
    }

    /**
     * Guardar preferencias del usuario
     */
    fun guardarPreferencias(
        usuarioId: Int,
        sonidos: Boolean,
        vibracion: Boolean,
        notificaciones: Boolean
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USUARIO_ID, usuarioId)
            put(COLUMN_SONIDOS, if (sonidos) 1 else 0)
            put(COLUMN_VIBRACION, if (vibracion) 1 else 0)
            put(COLUMN_NOTIFICACIONES, if (notificaciones) 1 else 0)
        }

        return try {
            // Intentar insertar primero
            val result = db.insertWithOnConflict(
                TABLE_PREFERENCIAS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            result != -1L
        } catch (e: Exception) {
            android.util.Log.e("PreferenciasDB", "Error guardando preferencias: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    /**
     * Obtener preferencias del usuario
     * Si no existen, crea preferencias por defecto (todo activado)
     */
    fun obtenerPreferencias(usuarioId: Int): Preferencias {
        val db = readableDatabase
        var preferencias = Preferencias()

        try {
            val cursor = db.query(
                TABLE_PREFERENCIAS,
                null,
                "$COLUMN_USUARIO_ID = ?",
                arrayOf(usuarioId.toString()),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                val sonidosIndex = cursor.getColumnIndex(COLUMN_SONIDOS)
                val vibracionIndex = cursor.getColumnIndex(COLUMN_VIBRACION)
                val notificacionesIndex = cursor.getColumnIndex(COLUMN_NOTIFICACIONES)

                preferencias = Preferencias(
                    sonidosActivados = cursor.getInt(sonidosIndex) == 1,
                    vibracionActivada = cursor.getInt(vibracionIndex) == 1,
                    notificacionesActivadas = cursor.getInt(notificacionesIndex) == 1
                )
            } else {
                // No existen preferencias, crear por defecto (todo activado)
                guardarPreferencias(usuarioId, true, true, true)
            }

            cursor.close()
        } catch (e: Exception) {
            android.util.Log.e("PreferenciasDB", "Error obteniendo preferencias: ${e.message}")
        } finally {
            db.close()
        }

        return preferencias
    }

    /**
     * Actualizar solo sonidos
     */
    fun actualizarSonidos(usuarioId: Int, activado: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SONIDOS, if (activado) 1 else 0)
        }

        return try {
            val rows = db.update(
                TABLE_PREFERENCIAS,
                values,
                "$COLUMN_USUARIO_ID = ?",
                arrayOf(usuarioId.toString())
            )
            rows > 0
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    /**
     * Actualizar solo vibraciĂłn
     */
    fun actualizarVibracion(usuarioId: Int, activado: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_VIBRACION, if (activado) 1 else 0)
        }

        return try {
            val rows = db.update(
                TABLE_PREFERENCIAS,
                values,
                "$COLUMN_USUARIO_ID = ?",
                arrayOf(usuarioId.toString())
            )
            rows > 0
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    /**
     * Actualizar solo notificaciones
     */
    fun actualizarNotificaciones(usuarioId: Int, activado: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTIFICACIONES, if (activado) 1 else 0)
        }

        return try {
            val rows = db.update(
                TABLE_PREFERENCIAS,
                values,
                "$COLUMN_USUARIO_ID = ?",
                arrayOf(usuarioId.toString())
            )
            rows > 0
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }
}

/**
 * Data class para las preferencias
 */
data class Preferencias(
    val sonidosActivados: Boolean = true,
    val vibracionActivada: Boolean = true,
    val notificacionesActivadas: Boolean = true
)
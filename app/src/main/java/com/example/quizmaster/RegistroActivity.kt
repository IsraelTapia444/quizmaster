package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmaster.models.ApiResponse
import com.example.quizmaster.models.RegistroRequest
import com.example.quizmaster.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var goToLogin: TextView
    // private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar componentes
        nameInput = findViewById(R.id.etNombre)
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        registerButton = findViewById(R.id.btnRegistro)
        goToLogin = findViewById(R.id.goToLogin)

        // ProgressBar para mostrar cuando se está procesando
        // Si no existe en tu layout, puedes comentar esta línea
        // progressBar = findViewById(R.id.progressBar)

        registerButton.setOnClickListener {
            registrarUsuario()
        }

        goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registrarUsuario() {
        val nombre = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Validaciones
        if (!validarCampos(nombre, email, password)) {
            return
        }

        // Mostrar loading (si tienes ProgressBar)
        // progressBar?.visibility = View.VISIBLE
        registerButton.isEnabled = false

        // Crear objeto de petición
        val request = RegistroRequest(nombre, email, password)

        // Llamada a la API
        RetrofitClient.apiService.registrarUsuario(request).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                // Ocultar loading
                // progressBar?.visibility = View.GONE
                registerButton.isEnabled = true

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        // Registro exitoso
                        Toast.makeText(
                            this@RegistroActivity,
                            apiResponse.message,
                            Toast.LENGTH_LONG
                        ).show()

                        // Ir al Login
                        val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                        intent.putExtra("email", email) // Pre-llenar el email en login
                        startActivity(intent)
                        finish()
                    } else {
                        // Error del servidor (ej: email ya existe)
                        Toast.makeText(
                            this@RegistroActivity,
                            apiResponse?.message ?: "Error al registrar usuario",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Error HTTP
                    Toast.makeText(
                        this@RegistroActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Ocultar loading
                // progressBar?.visibility = View.GONE
                registerButton.isEnabled = true

                // Error de conexión
                Toast.makeText(
                    this@RegistroActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Log para debug
                android.util.Log.e("RegistroActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun validarCampos(nombre: String, email: String, password: String): Boolean {
        // Validar nombre
        if (nombre.isEmpty()) {
            nameInput.error = "El nombre es obligatorio"
            nameInput.requestFocus()
            return false
        }

        if (nombre.length < 3) {
            nameInput.error = "El nombre debe tener al menos 3 caracteres"
            nameInput.requestFocus()
            return false
        }

        // Validar email
        if (email.isEmpty()) {
            emailInput.error = "El email es obligatorio"
            emailInput.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Email inválido"
            emailInput.requestFocus()
            return false
        }

        // Validar password
        if (password.isEmpty()) {
            passwordInput.error = "La contraseña es obligatoria"
            passwordInput.requestFocus()
            return false
        }

        if (password.length < 6) {
            passwordInput.error = "La contraseña debe tener al menos 6 caracteres"
            passwordInput.requestFocus()
            return false
        }

        return true
    }
}
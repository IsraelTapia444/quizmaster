package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmaster.models.ApiResponse
import com.example.quizmaster.models.LoginRequest
import com.example.quizmaster.network.RetrofitClient
import com.example.quizmaster.utils.UserSession
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var rememberCheck: CheckBox
    private lateinit var loginButton: Button
    private lateinit var goToRegister: TextView

    // UserSession para manejar la sesión
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar UserSession
        userSession = UserSession(this)

        // Verificar si el usuario ya está logueado
        if (userSession.isLoggedIn() && userSession.hasRememberMe()) {
            // Si tiene sesión activa y "Recordar sesión" marcado, ir directo a MainActivity
            goToMainActivity()
            return
        }

        // Inicializar componentes
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        rememberCheck = findViewById(R.id.checkDatos)
        loginButton = findViewById(R.id.btnLogin)
        goToRegister = findViewById(R.id.goToRegister)

        // Pre-llenar email si viene del registro
        val emailFromRegister = intent.getStringExtra("email")
        if (!emailFromRegister.isNullOrEmpty()) {
            emailInput.setText(emailFromRegister)
        }

        // Configurar listeners
        loginButton.setOnClickListener {
            realizarLogin()
        }

        goToRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun realizarLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Validaciones
        if (!validarCampos(email, password)) {
            return
        }

        // Deshabilitar botón mientras se procesa
        loginButton.isEnabled = false
        loginButton.text = "INICIANDO..."

        // Crear objeto de petición
        val loginRequest = LoginRequest(email, password)

        // Llamada a la API
        RetrofitClient.apiService.loginUsuario(loginRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                // Re-habilitar botón
                loginButton.isEnabled = true
                loginButton.text = "INICIAR SESIÓN"

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        // Login exitoso
                        handleLoginSuccess(apiResponse)
                    } else {
                        // Credenciales incorrectas
                        Toast.makeText(
                            this@LoginActivity,
                            apiResponse?.message ?: "Email o contraseña incorrectos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Error HTTP
                    Toast.makeText(
                        this@LoginActivity,
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Re-habilitar botón
                loginButton.isEnabled = true
                loginButton.text = "INICIAR SESIÓN"

                // Error de conexión
                Toast.makeText(
                    this@LoginActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Log para debug
                android.util.Log.e("LoginActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun handleLoginSuccess(apiResponse: ApiResponse) {
        try {
            // Extraer datos del usuario de la respuesta
            val gson = Gson()
            val dataJson = gson.toJsonTree(apiResponse.data).asJsonObject

            val userId = dataJson.get("id")?.asInt ?: dataJson.get("usuario_id")?.asInt ?: -1
            val userName = dataJson.get("nombre")?.asString ?: ""
            val userEmail = dataJson.get("email")?.asString ?: ""

            // Guardar sesión
            val rememberMe = rememberCheck.isChecked
            userSession.saveUserSession(userId, userName, userEmail, rememberMe)

            // Mostrar mensaje de éxito
            Toast.makeText(
                this,
                "¡Bienvenido $userName!",
                Toast.LENGTH_SHORT
            ).show()

            // Ir a MainActivity
            goToMainActivity()

        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Error parseando datos: ${e.message}", e)
            Toast.makeText(
                this,
                "Error procesando datos del usuario",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun validarCampos(email: String, password: String): Boolean {
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

    override fun onBackPressed() {
        // Si presiona atrás en Login, cerrar la app
        finishAffinity()
    }
}
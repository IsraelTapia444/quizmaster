package com.example.quizmaster

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.quizmaster.utils.UserSession
import com.example.quizmaster.database.PreferenciasDBHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PerfilActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 200
    }

    // Componentes UI
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtEmailUsuario: TextView
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnVolver: Button

    // Switches de configuración
    private lateinit var switchSonidos: Switch
    private lateinit var switchVibracion: Switch
    private lateinit var switchNotificaciones: Switch

    // Botones del menú inferior
    private lateinit var btnAddQuestion: Button
    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button
    private lateinit var btnStats: Button

    // UserSession y DB
    private lateinit var userSession: UserSession
    private lateinit var preferenciasDRHelper: PreferenciasDBHelper

    // Google Maps
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_perfil)

        // Inicializar UserSession y DB
        userSession = UserSession(this)
        preferenciasDRHelper = PreferenciasDBHelper(this)

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Verificar que el usuario esté logueado
        if (!userSession.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Inicializar componentes
        inicializarComponentes()

        // Aplicar WindowInsets
        aplicarWindowInsets()

        // Cargar datos del usuario
        cargarDatosUsuario()

        // Cargar preferencias
        cargarPreferencias()

        // Configurar botones
        configurarBotones()

        // Configurar menú inferior
        configurarMenuInferior()

        // Inicializar mapa
        inicializarMapa()
    }

    private fun inicializarComponentes() {
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        txtEmailUsuario = findViewById(R.id.txtEmailUsuario)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnVolver = findViewById(R.id.btnVolver)

        // Switches de configuración
        switchSonidos = findViewById(R.id.switchSonidos)
        switchVibracion = findViewById(R.id.switchVibracion)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)

        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)
        btnStats = findViewById(R.id.btnStats)
    }

    private fun aplicarWindowInsets() {
        val bottomNav = findViewById<android.view.View>(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
    }

    private fun cargarDatosUsuario() {
        val nombreUsuario = userSession.getUserName() ?: "Usuario"
        val email = userSession.getUserEmail() ?: "email@example.com"

        txtNombreUsuario.text = nombreUsuario.uppercase()
        txtEmailUsuario.text = email
    }

    private fun cargarPreferencias() {
        val userId = userSession.getUserId()
        val preferencias = preferenciasDRHelper.obtenerPreferencias(userId)

        // Establecer estado de los switches SIN triggear listeners
        switchSonidos.setOnCheckedChangeListener(null)
        switchVibracion.setOnCheckedChangeListener(null)
        switchNotificaciones.setOnCheckedChangeListener(null)

        switchSonidos.isChecked = preferencias.sonidosActivados
        switchVibracion.isChecked = preferencias.vibracionActivada
        switchNotificaciones.isChecked = preferencias.notificacionesActivadas

        // Configurar listeners DESPUÉS de establecer estados
        switchSonidos.setOnCheckedChangeListener { _, isChecked ->
            val success = preferenciasDRHelper.actualizarSonidos(userId, isChecked)
            if (success) {
                Toast.makeText(
                    this,
                    if (isChecked) "Sonidos activados" else "Sonidos desactivados",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Error al actualizar preferencia", Toast.LENGTH_SHORT).show()
                switchSonidos.setOnCheckedChangeListener(null)
                switchSonidos.isChecked = !isChecked
                switchSonidos.setOnCheckedChangeListener { _, checked ->
                    actualizarSonidos(checked)
                }
            }
        }

        switchVibracion.setOnCheckedChangeListener { _, isChecked ->
            val success = preferenciasDRHelper.actualizarVibracion(userId, isChecked)
            if (success) {
                Toast.makeText(
                    this,
                    if (isChecked) "Vibración activada" else "Vibración desactivada",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Error al actualizar preferencia", Toast.LENGTH_SHORT).show()
                switchVibracion.setOnCheckedChangeListener(null)
                switchVibracion.isChecked = !isChecked
                switchVibracion.setOnCheckedChangeListener { _, checked ->
                    actualizarVibracion(checked)
                }
            }
        }

        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            val success = preferenciasDRHelper.actualizarNotificaciones(userId, isChecked)
            if (success) {
                Toast.makeText(
                    this,
                    if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Error al actualizar preferencia", Toast.LENGTH_SHORT).show()
                switchNotificaciones.setOnCheckedChangeListener(null)
                switchNotificaciones.isChecked = !isChecked
                switchNotificaciones.setOnCheckedChangeListener { _, checked ->
                    actualizarNotificaciones(checked)
                }
            }
        }
    }

    private fun actualizarSonidos(isChecked: Boolean) {
        val userId = userSession.getUserId()
        preferenciasDRHelper.actualizarSonidos(userId, isChecked)
    }

    private fun actualizarVibracion(isChecked: Boolean) {
        val userId = userSession.getUserId()
        preferenciasDRHelper.actualizarVibracion(userId, isChecked)
    }

    private fun actualizarNotificaciones(isChecked: Boolean) {
        val userId = userSession.getUserId()
        preferenciasDRHelper.actualizarNotificaciones(userId, isChecked)
    }

    private fun configurarBotones() {
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun configurarMenuInferior() {
        btnAddQuestion.setOnClickListener {
            val intent = Intent(this, PreguntaActivity::class.java)
            startActivity(intent)
        }

        btnFacil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 1)
            startActivity(intent)
        }

        btnMedio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 2)
            startActivity(intent)
        }

        btnDificil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("dificultad", 3)
            startActivity(intent)
        }

        btnStats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("SÍ") { dialog, _ ->
                cerrarSesion()
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun cerrarSesion() {
        userSession.logout()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Inicializar Google Maps
     */
    private fun inicializarMapa() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    /**
     * Callback cuando el mapa está listo
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configurar UI del mapa
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
        }

        // Solicitar permisos y obtener ubicación
        verificarPermisosUbicacion()
    }

    /**
     * Verificar y solicitar permisos de ubicación
     */
    private fun verificarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso concedido, obtener ubicación
            obtenerUbicacionActual()
        } else {
            // Solicitar permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Obtener ubicación actual del usuario
     */
    private fun obtenerUbicacionActual() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Habilitar "Mi ubicación" en el mapa
                googleMap?.isMyLocationEnabled = true

                // Obtener última ubicación conocida
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        currentLocation = location
                        mostrarUbicacionEnMapa(location)
                    } else {
                        Toast.makeText(
                            this,
                            "No se pudo obtener la ubicación actual",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Mostrar ubicación por defecto (ejemplo: Madrid)
                        mostrarUbicacionPorDefecto()
                    }
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error de permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Mostrar ubicación en el mapa con marcador
     */
    private fun mostrarUbicacionEnMapa(location: Location) {
        val ubicacion = LatLng(location.latitude, location.longitude)

        googleMap?.apply {
            // Limpiar marcadores anteriores
            clear()

            // Agregar marcador en la ubicación actual
            addMarker(
                MarkerOptions()
                    .position(ubicacion)
                    .title("Mi Ubicación")
            )

            // Mover cámara a la ubicación con zoom
            animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f))
        }
    }

    /**
     * Mostrar ubicación por defecto si no se puede obtener la real
     */
    private fun mostrarUbicacionPorDefecto() {
        // Ubicación por defecto: Salamanca, España
        val salamanca = LatLng(40.9701, -5.6635)

        googleMap?.apply {
            clear()
            addMarker(
                MarkerOptions()
                    .position(salamanca)
                    .title("Ubicación por defecto")
            )
            animateCamera(CameraUpdateFactory.newLatLngZoom(salamanca, 12f))
        }
    }

    /**
     * Manejar resultado de solicitud de permisos
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                    obtenerUbicacionActual()
                } else {
                    // Permiso denegado
                    Toast.makeText(
                        this,
                        "Permiso de ubicación denegado. Mostrando ubicación por defecto.",
                        Toast.LENGTH_LONG
                    ).show()
                    mostrarUbicacionPorDefecto()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar ubicación cada vez que se abre el perfil
        if (googleMap != null) {
            verificarPermisosUbicacion()
        }
    }
}
package com.example.asgard_gym

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    // Referencias a vistas
    private lateinit var etDni: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnEntrar: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var preferencias: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Obtener vistas
        etDni = findViewById(R.id.etDni)
        etCorreo = findViewById(R.id.etCorreo)
        btnEntrar = findViewById(R.id.btnAcceder)

        // inicializar el helper de BBDD
        dbHelper = DatabaseHelper(this)

        // acceder a las preferencias compartidas para guardar sesión
        preferencias = getSharedPreferences("UserSession", MODE_PRIVATE)

        // cargar datos de sesión guardados previamente (si los hay)
        cargarSesion()

        // acción al pulsar el botón "entrar"
        btnEntrar.setOnClickListener {
            val dni = etDni.text.toString().trim()
            val correo = etCorreo.text.toString().trim()

            // verificar que los campos no estén vacíos
            if (dni.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // buscar el usuario por correo
            val usuario = dbHelper.obtenerUsuarioPorCorreo(correo)

            // si el usuario existe y el dni coincide
            if (usuario != null && usuario.DNI == dni) {
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

                // guardar la sesión en preferencias compartidas
                guardarSesion(usuario.DNI, usuario.correo, usuario.idUsuario)

                // redirigir según si es admin o usuario normal
                val destino = if (usuario.esAdmin) {
                    Intent(this, AdminActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }

                startActivity(destino)
                finish()
            } else {
                Toast.makeText(this, "Usuario no encontrado. Verifica tus datos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // guarda la sesión del usuario en preferencias compartidas
    private fun guardarSesion(dni: String, correo: String, idUsuario: Int) {
        val editor = preferencias.edit()
        editor.putString("dni", dni)
        editor.putString("correo", correo)
        editor.putInt("usuario_id", idUsuario)
        editor.apply()
    }

    // carga los datos de sesión si ya estaban guardados previamente
    private fun cargarSesion() {
        val dniGuardado = preferencias.getString("dni", "")
        val correoGuardado = preferencias.getString("correo", "")

        if (!dniGuardado.isNullOrEmpty() && !correoGuardado.isNullOrEmpty()) {
            etDni.setText(dniGuardado)
            etCorreo.setText(correoGuardado)
        }
    }
}

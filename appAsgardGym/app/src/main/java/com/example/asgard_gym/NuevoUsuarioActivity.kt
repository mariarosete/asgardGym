package com.example.asgard_gym

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class NuevoUsuarioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_usuario)

        // Toolbar
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Botón de navegación hacia AdminActivity (inicio)
        findViewById<ImageView>(R.id.logoInicio).setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
        }

        // Botón de salida de la aplicación
        val btnSalir = findViewById<ImageView>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            finishAffinity()
        }

        // Inicialización del helper de BBDD
        dbHelper = DatabaseHelper(this)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellidos = findViewById<EditText>(R.id.etApellidos)
        val etDNI = findViewById<EditText>(R.id.etDNI)
        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etCodigoPostal = findViewById<EditText>(R.id.etCodigoPostal)
        val etEnfermedades = findViewById<EditText>(R.id.etEnfermedades)
        val cbAceptaDatos = findViewById<CheckBox>(R.id.cbAceptaDatos)
        val cbEsAdmin = findViewById<CheckBox>(R.id.cbEsAdmin)
        val layoutEnfermedades = findViewById<LinearLayout>(R.id.layoutEnfermedades)

        // Determinar si el usuario actual tiene rol de administrador
        val esAdminLogueado = intent.getBooleanExtra("esAdminLogueado", false)

        // Mostrar campo de enfermedades solo si es admin
        layoutEnfermedades.visibility = if (esAdminLogueado) View.VISIBLE else View.GONE

        // Mostrar selector de fecha con DatePicker al hacer clic en el campo de fecha
        etFechaNacimiento.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d -> etFechaNacimiento.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Si es edición (se ha pasado un usuario como parámetro), precargar los datos
        val usuario = intent.getSerializableExtra("usuario") as? DTOUsuario
        if (usuario != null) {
            etNombre.setText(usuario.nombre)
            etApellidos.setText(usuario.apellidos)
            etDNI.setText(usuario.DNI)
            etFechaNacimiento.setText(usuario.fechaNacimiento)
            etCorreo.setText(usuario.correo)
            etCodigoPostal.setText(usuario.codigoPostal)
            cbAceptaDatos.isChecked = usuario.aceptacionDatos
            cbEsAdmin.isChecked = usuario.esAdmin

            // Campo adicional visible solo para admins
            if (esAdminLogueado) {
                etEnfermedades.setText(usuario.enfermedades ?: "")
            }
        }

        // Al pulsar el botón guardar
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {

            // Crear objeto DTOUsuario con los datos del formulario
            val nuevoUsuario = DTOUsuario(
                idUsuario = usuario?.idUsuario ?: 0,
                nombre = etNombre.text.toString(),
                apellidos = etApellidos.text.toString(),
                DNI = etDNI.text.toString(),
                fechaNacimiento = etFechaNacimiento.text.toString(),
                correo = etCorreo.text.toString(),
                codigoPostal = etCodigoPostal.text.toString(),
                enfermedades = if (esAdminLogueado) etEnfermedades.text.toString() else "",
                aceptacionDatos = cbAceptaDatos.isChecked,
                esAdmin = cbEsAdmin.isChecked
            )

            // Insertar nuevo usuario o actualizar existente
            val resultado = if (usuario != null) {
                dbHelper.actualizarUsuarioComoAdmin(nuevoUsuario)
            } else {
                dbHelper.insertarUsuario(nuevoUsuario)
            }

            //Mostrar mensaje según resultado
            if (resultado != -1L && resultado != 0) {
                Toast.makeText(this, "Usuario guardado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


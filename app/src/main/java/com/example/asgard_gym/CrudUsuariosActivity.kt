package com.example.asgard_gym

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CrudUsuariosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaView: ListView
    private lateinit var usuarios: MutableList<DTOUsuario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_usuarios)

        // Configurar Toolbar
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Logo: redirige a la pantalla para añadir nuevo usuario como admin
        findViewById<ImageView>(R.id.logoInicio).setOnClickListener {
            val intent = Intent(this, NuevoUsuarioActivity::class.java)
            intent.putExtra("esAdminLogueado", true)
            startActivityForResult(intent, 1)
        }

        // Botón de salida (cerrar app completamente)
        val btnSalir = findViewById<ImageView>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            finishAffinity()
        }

        // Inicialización de la BBDD y el ListView
        dbHelper = DatabaseHelper(this)
        listaView = findViewById(R.id.listaUsuarios)

        // Botón para crear un nuevo usuario
        findViewById<Button>(R.id.btnNuevoUsuario).setOnClickListener {
            val intent = Intent(this, NuevoUsuarioActivity::class.java)
            intent.putExtra("esAdminLogueado", true)
            startActivityForResult(intent, 1)
        }

        // Cargar y mostrar la lista de usuarios actuales
        cargarUsuarios()
    }

    // Función para cargar todos los usuarios desde BBDD
    private fun cargarUsuarios() {
        usuarios = dbHelper.obtenerTodosLosUsuarios()

        // Adaptador personalizado con funciones de editar y eliminar usuario
        val adapter = UsuarioAdminAdapter(
            context = this,
            usuarios = usuarios,
            onEditar = { usuario ->
                val intent = Intent(this, NuevoUsuarioActivity::class.java)
                intent.putExtra("usuario", usuario)
                intent.putExtra("esAdminLogueado", true)
                startActivityForResult(intent, 1)
            },
            onBorrar = { usuario ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar usuario")
                    .setMessage("¿Seguro que deseas eliminar a ${usuario.nombre}?")
                    .setPositiveButton("Sí") { _, _ ->
                        dbHelper.eliminarUsuarioPorId(usuario.idUsuario)
                        cargarUsuarios()  // Refrescar lista tras eliminar
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        // Asignar el adaptador al ListView
        listaView.adapter = adapter
    }

    // Recargar lista de usuarios si se vuelve de una acción de edición o creación
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarUsuarios()
        }
    }
}



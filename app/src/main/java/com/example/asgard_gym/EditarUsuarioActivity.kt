package com.example.asgard_gym

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class EditarUsuarioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var usuarios: MutableList<DTOUsuario>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        // Configurar Toolbar
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // al pulsar el logo, vuelve a adminactivity
        findViewById<ImageView>(R.id.logoInicio).setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
        }

        // salir de la app al pulsar el botón salir
        val btnSalir = findViewById<ImageView>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            finishAffinity()
        }

        // inicializar BBDD y listview
        dbHelper = DatabaseHelper(this)
        listaView = findViewById(R.id.lvUsuarios)

        // cargar lista de usuarios desde la BBDD
        cargarUsuarios()

        // al hacer clic sobre un usuario, abrir pantalla para editarlo
        listaView.setOnItemClickListener { _, _, position, _ ->
            val usuarioSeleccionado = usuarios[position]
            val intent = Intent(this, NuevoUsuarioActivity::class.java)
            intent.putExtra("usuario", usuarioSeleccionado)
            intent.putExtra("esAdminLogueado", true)
            startActivity(intent)
        }

        // al mantener pulsado un usuario, mostrar diálogo para eliminarlo
        listaView.setOnItemLongClickListener { _, _, position, _ ->
            val usuario = usuarios[position]
            AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Seguro que deseas eliminar '${usuario.nombre} ${usuario.apellidos}'?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.eliminarUsuarioPorId(usuario.idUsuario)
                    cargarUsuarios()
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }

    // función para cargar y mostrar todos los usuarios
    private fun cargarUsuarios() {
        usuarios = dbHelper.obtenerTodosLosUsuarios()
        val nombres = usuarios.map { "${it.nombre} ${it.apellidos} - ${it.correo}" }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listaView.adapter = adapter
    }
}

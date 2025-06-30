package com.example.asgard_gym

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class EditarActividadActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var actividades: MutableList<DTOActividad>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_actividad)

        // Configurar Toolbar
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // acción al pulsar el logo: volver a adminactivity
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)
        logoInicio.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        // inicializar BBDD y listview
        dbHelper = DatabaseHelper(this)
        listaView = findViewById(R.id.lvActividades)

        // cargar actividades desde la BBDD y mostrarlas en la lista
        cargarActividades()

        // acción al hacer clic sobre un ítem de la lista: abrir pantalla de edición
        listaView.setOnItemClickListener { _, _, position, _ ->
            val actividadSeleccionada = actividades[position]
            val intent = Intent(this, NuevaActividadActivity::class.java)
            intent.putExtra("actividad_id", actividadSeleccionada.idActividad)
            startActivity(intent)
        }

        // acción al hacer clic largo sobre un ítem: mostrar confirmación para eliminar
        listaView.setOnItemLongClickListener { _, _, position, _ ->
            val actividad = actividades[position]
            AlertDialog.Builder(this)
                .setTitle("Eliminar actividad")
                .setMessage("¿Seguro que deseas eliminar '${actividad.nombre}'?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.eliminarActividadPorId(actividad.idActividad)
                    cargarActividades()
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }

    // función para cargar y mostrar todas las actividades
    private fun cargarActividades() {
        actividades = dbHelper.obtenerTodasLasActividades()
        val nombres = actividades.map { "${it.diaSemana} - ${it.horario} - ${it.nombre}" }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listaView.adapter = adapter
    }
}


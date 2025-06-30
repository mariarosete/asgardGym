package com.example.asgard_gym

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CrudActividadesActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_actividades)

        // Configurar Toolbar
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Logo: vuelve a la pantalla de administración principal
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)
        logoInicio.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        // Botón salir: cierra completamente la app
        val btnSalir = findViewById<ImageView>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            finishAffinity()
        }

        // Inicializar componentes
        listView = findViewById(R.id.listaActividades)
        dbHelper = DatabaseHelper(this)

        // Botón para añadir nueva actividad
        findViewById<Button>(R.id.btnNuevaActividad).setOnClickListener {
            val intent = Intent(this, NuevaActividadActivity::class.java)
            startActivityForResult(intent, 1)  // Código 1: alta o edición
        }

        // Mostrar la lista inicial de actividades
        mostrarActividades()
    }

    // Recargar lista si se vuelve desde la pantalla de edición/creación
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mostrarActividades()
        }
    }

    // Carga y muestra las actividades ordenadas por día y hora
    private fun mostrarActividades() {
        val lista = dbHelper.obtenerTodasLasActividades()
            .sortedWith(compareBy(
                { diaAInt(it.diaSemana) },
                { horaAInt(it.horario) }
            ))

        // Adaptador personalizado con acciones de editar y borrar
        val adapter = ActividadAdminAdapter(this, lista,
            onEditar = { actividad ->
                val intent = Intent(this, NuevaActividadActivity::class.java)
                intent.putExtra("actividad", actividad)
                startActivityForResult(intent, 1)
            },
            onBorrar = { actividad ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar actividad")
                    .setMessage("¿Seguro que quieres eliminar \"${actividad.nombre}\"?")
                    .setPositiveButton("Sí") { _, _ ->
                        dbHelper.eliminarActividadPorId(actividad.idActividad)
                        mostrarActividades()  // Refrescar lista
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        listView.adapter = adapter
    }

    // Conversión del nombre del día a número para ordenación
    private fun diaAInt(dia: String): Int {
        return when (dia.lowercase()) {
            "lunes" -> 1
            "martes" -> 2
            "miércoles" -> 3
            "jueves" -> 4
            "viernes" -> 5
            else -> 6  // Por defecto
        }
    }

    // Conversión de hora en formato "HH:mm" a minutos totales
    private fun horaAInt(hora: String): Int {
        val partes = hora.split(":")
        val horaNum = partes.getOrNull(0)?.toIntOrNull() ?: 0
        val minuto = partes.getOrNull(1)?.toIntOrNull() ?: 0
        return horaNum * 60 + minuto
    }
}


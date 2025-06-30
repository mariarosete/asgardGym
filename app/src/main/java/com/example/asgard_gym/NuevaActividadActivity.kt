package com.example.asgard_gym

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class NuevaActividadActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etNombre: EditText
    private lateinit var etTipo: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etHorario: EditText
    private lateinit var etDuracion: EditText
    private lateinit var etPlazas: EditText
    private lateinit var spDiaSemana: Spinner
    private lateinit var spMonitor: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var gridImagenes: GridView

    private var imagenSeleccionada: Int = R.drawable.ic_launcher_foreground // Imagen por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_actividad)

        // Toolbar
        setSupportActionBar(findViewById(R.id.tbMenu))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        findViewById<ImageView>(R.id.logoInicio).setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
        }
        findViewById<ImageView>(R.id.btnSalir).setOnClickListener { finishAffinity() }

        dbHelper = DatabaseHelper(this)

        // Referencias UI
        etNombre = findViewById(R.id.etNombre)
        etTipo = findViewById(R.id.etTipo)
        etDescripcion = findViewById(R.id.etDescripcion)
        etHorario = findViewById(R.id.etHorario)
        etDuracion = findViewById(R.id.etDuracion)
        etPlazas = findViewById(R.id.etPlazas)
        spDiaSemana = findViewById(R.id.spDiaSemana)
        spMonitor = findViewById(R.id.spMonitor)
        btnGuardar = findViewById(R.id.btnGuardar)
        gridImagenes = findViewById(R.id.gridImagenes)

        val dias = listOf("Día de la semana", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        spDiaSemana.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dias)

        val monitores = listOf("Monitor", "Vanesa Rosete", "Julio Aportela", "Fernando")
        spMonitor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, monitores)

        // IMÁGENES disponibles
        val imagenes = listOf(
            R.drawable.zumba,
            R.drawable.ciclo,
            R.drawable.body_active,
            R.drawable.judo,
            R.drawable.pilates,
            R.drawable.tramp
        )
        gridImagenes.adapter = object : BaseAdapter() {
            override fun getCount() = imagenes.size
            override fun getItem(position: Int) = imagenes[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val imageView = ImageView(this@NuevaActividadActivity)
                imageView.setImageResource(imagenes[position])
                imageView.layoutParams = AbsListView.LayoutParams(200, 200)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                return imageView
            }
        }

        gridImagenes.setOnItemClickListener { _, _, position, _ ->
            imagenSeleccionada = imagenes[position]
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }

        val actividadEditada = intent.getSerializableExtra("actividad") as? DTOActividad
        actividadEditada?.let {
            etNombre.setText(it.nombre)
            etTipo.setText(it.tipo)
            etDescripcion.setText(it.descripcion)
            etHorario.setText(it.horario)
            etDuracion.setText(it.duracion)
            etPlazas.setText(it.plazasDisponibles.toString())

            dias.indexOf(it.diaSemana).takeIf { i -> i != -1 }?.let { spDiaSemana.setSelection(it) }
            monitores.indexOf(it.instructor).takeIf { i -> i != -1 }?.let { spMonitor.setSelection(it) }

            imagenSeleccionada = it.imagen
        }

        btnGuardar.setOnClickListener {
            val nuevaActividad = DTOActividad(
                idActividad = actividadEditada?.idActividad ?: 0,
                nombre = etNombre.text.toString(),
                tipo = etTipo.text.toString(),
                descripcion = etDescripcion.text.toString(),
                horario = etHorario.text.toString(),
                instructor = spMonitor.selectedItem.toString(),
                duracion = etDuracion.text.toString(),
                imagen = imagenSeleccionada,
                diaSemana = spDiaSemana.selectedItem.toString(),
                plazasDisponibles = etPlazas.text.toString().toIntOrNull() ?: 15
            )

            val resultado = if (actividadEditada != null) {
                dbHelper.actualizarActividad(nuevaActividad)
            } else {
                dbHelper.insertarActividad(nuevaActividad)
            }

            if (resultado != -1L) {
                Toast.makeText(this, "Actividad guardada", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

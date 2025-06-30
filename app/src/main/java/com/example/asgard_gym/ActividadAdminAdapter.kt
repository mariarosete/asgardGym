package com.example.asgard_gym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

// Adaptador personalizado para mostrar una lista de actividades
class ActividadAdminAdapter(
    private val context: Context,
    private val actividades: List<DTOActividad>,
    private val onEditar: (DTOActividad) -> Unit,
    private val onBorrar: (DTOActividad) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = actividades.size
    override fun getItem(position: Int): Any = actividades[position]
    override fun getItemId(position: Int): Long = actividades[position].idActividad.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_actividad_admin, parent, false)
        val actividad = actividades[position]

        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvHorario = view.findViewById<TextView>(R.id.tvHorario)
        val imgActividad = view.findViewById<ImageView>(R.id.imgActividad)
        imgActividad.setImageResource(actividad.imagen)

        val btnEditar = view.findViewById<ImageButton>(R.id.btnEditar)
        val btnBorrar = view.findViewById<ImageButton>(R.id.btnBorrar)

        // Asignar datos
        tvNombre.text = actividad.nombre
        tvHorario.text = "${actividad.diaSemana} ${actividad.horario}"
        imgActividad.setImageResource(actividad.imagen)

        // Mostrar detalles en diálogo
        val listenerDetalles = View.OnClickListener {
            val mensaje = """
                📅 Día: ${actividad.diaSemana}
                ⏰ Horario: ${actividad.horario}
                🧑‍ Monitor: ${actividad.instructor}
                ⌛ Duración: ${actividad.duracion}
                🏷️ Tipo: ${actividad.tipo}
                
                📝 Descripción:
                ${actividad.descripcion}
            """.trimIndent()

            AlertDialog.Builder(context)
                .setTitle(actividad.nombre)
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }

        // Listeners
        tvNombre.setOnClickListener(listenerDetalles)
        tvHorario.setOnClickListener(listenerDetalles)
        imgActividad.setOnClickListener(listenerDetalles)

        btnEditar.setOnClickListener { onEditar(actividad) }
        btnBorrar.setOnClickListener { onBorrar(actividad) }

        return view
    }
}


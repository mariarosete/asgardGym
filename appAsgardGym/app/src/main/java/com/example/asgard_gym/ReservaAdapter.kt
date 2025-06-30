package com.example.asgard_gym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

// Adaptador personalizado para mostrar reservas en un ListView
class ReservaAdapter(
    private val context: Context,
    private var reservas: MutableList<DTOReserva>
) : BaseAdapter() {

    private val dbHelper = DatabaseHelper(context)

    override fun getCount(): Int = reservas.size
    override fun getItem(position: Int): Any = reservas[position]
    override fun getItemId(position: Int): Long = reservas[position].idReserva.toLong()
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false)

        val reserva = reservas[position]

        val tvNombre = view.findViewById<TextView>(R.id.tvNombreActividad)
        val tvDiaHorario = view.findViewById<TextView>(R.id.tvDiaHorario)
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaReserva)
        val imgActividad = view.findViewById<ImageView>(R.id.imgActividad)
        val btnEliminar = view.findViewById<ImageButton>(R.id.btnEliminarReserva)

        tvNombre.text = reserva.nombreActividad
        tvDiaHorario.text = "${reserva.diaSemana} ${reserva.fechaClase} - ${reserva.horarioActividad}"
        tvFecha.text = "Reservado el: ${reserva.fecha} ${reserva.hora}"

        // Asignar imagen según tipo de actividad
        val nombre = reserva.nombreActividad.lowercase()
        val imagenRes = when {

            "zumba" in nombre -> R.drawable.zumba
            "pilates" in nombre -> R.drawable.pilates
            "judo" in nombre -> R.drawable.judo
            "ciclo" in nombre -> R.drawable.ciclo
            "body" in nombre -> R.drawable.body_active
            "minitramp" in nombre -> R.drawable.tramp
            else -> R.drawable.ic_launcher_foreground
        }
        imgActividad.setImageResource(imagenRes)

        // Eliminar reserva con confirmación
        btnEliminar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Anular reserva")
                .setMessage("¿Seguro que quieres anular la reserva de ${reserva.nombreActividad}?")
                .setPositiveButton("Sí") { _, _ ->

                    // Eliminar la reserva en la BBDD
                    val resultadoEliminacion = dbHelper.eliminarReserva(reserva.idReserva)
                    if (resultadoEliminacion > 0) {
                        // Eliminar de la lista de reservas
                        reservas.removeAt(position)
                        notifyDataSetChanged()

                        // Obtener la actividad y usuario para registrar el histórico
                        val actividad = dbHelper.obtenerActividadPorId(reserva.idActividad)
                        val usuario = dbHelper.obtenerUsuarioPorId(reserva.idUsuario)
                        val nombreActividad = actividad?.nombre ?: "Actividad desconocida"
                        val nombreUsuario = usuario?.nombre ?: "Usuario desconocido"
                        val dniUsuario = usuario?.DNI ?: "DNI desconocido"

                        // Obtener la fecha de la clase y actualizar plazas
                        val fechaClase = reserva.fechaClase
                        val plazasDisponibles = actividad?.plazasDisponibles ?: 0

                        // Registrar el cambio en la tabla de histórico
                        dbHelper.registrarHistorico(
                            idReserva = reserva.idReserva.toLong(),
                            accion = "delete",
                            nombreUsuario = nombreUsuario,
                            dniUsuario = dniUsuario,
                            nombreActividad = nombreActividad,
                            fechaClase = fechaClase,
                            plazasDisponiblesActualizadas = plazasDisponibles
                        )

                        // Mostrar mensaje de confirmación
                        actividad?.let {
                            Toast.makeText(
                                context,
                                "Reserva anulada. Plazas disponibles ahora: ${it.plazasDisponibles}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } ?: run {
                            Toast.makeText(context, "Reserva anulada", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Error al eliminar la reserva.", Toast.LENGTH_SHORT).show()
                    }

                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        return view
    }
}

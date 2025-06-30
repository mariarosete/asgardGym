package com.example.asgard_gym

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adaptador personalizado para mostrar el historial de reservas en un RecyclerView.
class HistorialReservasAdapter(private val historial: List<DTOHistorialCompleto>) :
    RecyclerView.Adapter<HistorialReservasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
        val txtAccion: TextView = view.findViewById(R.id.txtAccion)
        val txtActividad: TextView = view.findViewById(R.id.txtActividad)
        val txtUsuario: TextView = view.findViewById(R.id.txtUsuario)
        val txtDni: TextView = view.findViewById(R.id.txtDni)
        val txtValor: TextView = view.findViewById(R.id.txtValor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_reserva, parent, false)
        return ViewHolder(vista)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historial[position]

        holder.txtFecha.text = "Fecha: ${item.fechaModificacion}"
        holder.txtAccion.text = "Acción: ${item.accion}"
        holder.txtActividad.text = "Actividad: ${item.nombreActividad ?: "-"}"
        holder.txtUsuario.text = "Usuario: ${item.nombreUsuario ?: "-"}"
        holder.txtDni.text = "DNI: ${item.dniUsuario ?: "-"}"
        holder.txtValor.text = "Plazas tras operación: ${item.valorNuevo ?: "-"}"

        // Cambiar color según la acción
        val colorAccion = when (item.accion.lowercase()) {
            "insert" -> Color.parseColor("#388E3C") // Verde
            "delete" -> Color.parseColor("#D32F2F") // Rojo
            else -> Color.DKGRAY
        }
        holder.txtAccion.setTextColor(colorAccion)
    }


    override fun getItemCount() = historial.size
}




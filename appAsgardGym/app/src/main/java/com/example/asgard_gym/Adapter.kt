package com.example.asgard_gym

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adaptador personalizado para mostrar una lista de actividades en un RecyclerView.
class Adapter(private val actividades: List<DTOActividad>) :
    RecyclerView.Adapter<Adapter.ActividadViewHolder>() {

    inner class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgActividad)
        val txt: TextView = itemView.findViewById(R.id.txtNombreActividad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]
        holder.img.setImageResource(actividad.imagen)
        holder.txt.text = actividad.nombre
    }

    override fun getItemCount(): Int = actividades.size
}


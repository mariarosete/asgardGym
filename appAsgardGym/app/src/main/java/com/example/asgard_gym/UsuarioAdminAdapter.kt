package com.example.asgard_gym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

// Adaptador personalizado para mostrar usuarios
class UsuarioAdminAdapter(
    private val context: Context,
    private val usuarios: List<DTOUsuario>,
    private val onEditar: (DTOUsuario) -> Unit,
    private val onBorrar: (DTOUsuario) -> Unit
) : BaseAdapter() {

    override fun getCount() = usuarios.size
    override fun getItem(position: Int) = usuarios[position]
    override fun getItemId(position: Int) = usuarios[position].idUsuario.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_usuario_admin, parent, false)
        val usuario = usuarios[position]

        val tvInfo = view.findViewById<TextView>(R.id.tvInfo)
        val btnEditar = view.findViewById<ImageButton>(R.id.btnEditar)
        val btnBorrar = view.findViewById<ImageButton>(R.id.btnBorrar)

        tvInfo.text = "${usuario.nombre} ${usuario.apellidos}\n${usuario.correo}"

        // Mostrar diálogo al pulsar el texto
        tvInfo.setOnClickListener {
            val mensaje = """
               
                Apellidos: ${usuario.apellidos}
                DNI: ${usuario.DNI}
                Fecha de nacimiento: ${usuario.fechaNacimiento}
                Correo electrónico: ${usuario.correo}
                Código Postal: ${usuario.codigoPostal ?: "No especificado"}
                Enfermedades o lesiones: ${usuario.enfermedades?.takeIf { it.isNotBlank() } ?: "Ninguna"}
            """.trimIndent()

            AlertDialog.Builder(context)
                .setTitle("📋 ${usuario.nombre}")
                .setMessage(mensaje)
                .setPositiveButton("CERRAR", null)
                .show()

        }

        btnEditar.setOnClickListener { onEditar(usuario) }
        btnBorrar.setOnClickListener { onBorrar(usuario) }

        return view
    }
}

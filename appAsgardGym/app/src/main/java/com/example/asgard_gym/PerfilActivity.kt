package com.example.asgard_gym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat

class PerfilActivity : AppCompatActivity() {

    // Referencias a vistas
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etDni: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etCodigoPostal: EditText
    private lateinit var etNivel: EditText
    private lateinit var tvResumen: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnEditar: ImageButton
    private lateinit var cbAceptacionDatos: CheckBox
    private var edicion = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Inicializar y configurar la barra de herramientas
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)

        // Al pulsar el logo vuelve a MainActivity
        logoInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        // Botón de acceso directo al perfil
        findViewById<ImageButton>(R.id.btnUsuario).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Mostrar cuadro de ayuda
        findViewById<ImageButton>(R.id.btnAyuda).setOnClickListener {
            cuadroDialogoAyuda()
        }

        // Obtener vistas
        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etDni = findViewById(R.id.etDni)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etCorreo = findViewById(R.id.etCorreo)
        etCodigoPostal = findViewById(R.id.etCodigoPostal)
        cbAceptacionDatos = findViewById(R.id.cbAceptacionDatos)
        tvResumen = findViewById(R.id.tvResumen)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEditar = findViewById(R.id.btnEditar)

        // Menú contextual del botón ☰
        btnMenu.setOnClickListener {
            val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
            val popupMenu = PopupMenu(wrapper, findViewById(R.id.btnMenu))

            // Inflar solo las opciones del menú contextual
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)

            // Eliminar los ítems que eran exclusivos de la Toolbar
            popupMenu.menu.removeItem(R.id.usuario)
            popupMenu.menu.removeItem(R.id.ayuda)

            // Forzar iconos en el menú contextual
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            } else {
                try {
                    val fields = popupMenu.javaClass.declaredFields
                    for (field in fields) {
                        if (field.name == "mPopup") {
                            field.isAccessible = true
                            val menuHelper = field.get(popupMenu)
                            val clazz = Class.forName(menuHelper.javaClass.name)
                            val setForceShowIcon = clazz.getMethod("setForceShowIcon", Boolean::class.java)
                            setForceShowIcon.invoke(menuHelper, true)
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Opciones del menú
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.agenda -> {
                        startActivity(Intent(this, AgendaActivity::class.java))
                        true
                    }
                    R.id.menu_ver_reservas -> {
                        startActivity(Intent(this, ReservasActivity::class.java))
                        true
                    }

                    R.id.salir -> {
                        finishAffinity()
                        true
                    }
                    else -> false

                }
            }

            popupMenu.show()
        }

        // Conexión a la BBDD
        dbHelper = DatabaseHelper(this)

        // Obtener el ID del usuario desde las preferencias guardadas
        val userId = getSharedPreferences("UserSession", MODE_PRIVATE).getInt("usuario_id", -1)

        // Si hay usuario válido, cargar sus datos y resumen de actividad
        if (userId != -1) {
            val usuario = dbHelper.obtenerUsuarioPorId(userId)
            usuario?.let {
                etNombre.setText(it.nombre)
                etApellidos.setText(it.apellidos)
                etDni.setText(it.DNI)
                etFechaNacimiento.setText(it.fechaNacimiento)
                etCorreo.setText(it.correo)
                etCodigoPostal.setText(it.codigoPostal ?: "")
                cbAceptacionDatos.isChecked = it.aceptacionDatos
            }

            // Resumen de reservas
            val reservas = dbHelper.obtenerReservasPorUsuario(userId)
            val numReservas = reservas.size
            val ultimaReserva = reservas.firstOrNull()?.let {
                "${it.fecha} ${it.hora}"
            } ?: "Sin registros"

            tvResumen.text = """
                • Reservas realizadas: $numReservas
                • Última reserva: $ultimaReserva
            """.trimIndent()
        }

        // Bloquear campos al inicio (modo lectura)
        habilitarCampos(false)

        // Activar/desactivar edición al pulsar el botón
        btnEditar.setOnClickListener {
            edicion = !edicion
            habilitarCampos(edicion)
            Toast.makeText(
                this,
                if (edicion) "Modo edición activado" else "Modo lectura activado",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Guardar los cambios introducidos por el usuario
        btnGuardar.setOnClickListener {
            val nuevoCodigoPostal = etCodigoPostal.text.toString().trim()


            val usuarioActualizado = DTOUsuario(
                idUsuario = userId,
                nombre = etNombre.text.toString(),
                apellidos = etApellidos.text.toString(),
                DNI = etDni.text.toString(),
                fechaNacimiento = etFechaNacimiento.text.toString(),
                correo = etCorreo.text.toString(),
                codigoPostal = nuevoCodigoPostal,

            )

            // Actualizar en la BBDD
            dbHelper.actualizarUsuarioBasico(usuarioActualizado)

            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            habilitarCampos(false)
            edicion = false
        }
    }

    // Función para habilitar o bloquear los campos del perfil
    private fun habilitarCampos(habilitar: Boolean) {
        etCodigoPostal.isEnabled = habilitar
        btnGuardar.isEnabled = habilitar

        val fondo = if (habilitar) android.R.drawable.edit_text else android.R.color.transparent
        etCodigoPostal.setBackgroundResource(fondo)

    }

    private fun cuadroDialogoAyuda() {
        val view = layoutInflater.inflate(R.layout.dialog_ayuda, null)

        val contenido = """
            <b>1. Accede a la Agenda:</b><br>
            Desde el menú principal (☰), pulsa sobre la opción "Agenda" para ver el calendario semanal.<br><br>
        
            <b>2. Navega por la semana:</b><br>
            Usa los botones "Semana anterior / siguiente" para cambiar de semana. Verás una tabla con las actividades organizadas por día y hora.<br><br>
        
            <b>3. Selecciona una actividad:</b><br>
            Toca sobre ella para ver todos los detalles.<br><br>
        
            <b>4. Reserva tu plaza:</b><br>
            Si hay plazas libres, verás el botón "Reservar Plaza".<br>
            🔒 Máximo 15 plazas por actividad.<br>
            ⏰ No se permiten reservas en actividades ya pasadas.<br><br>
        
            <b>5. Consulta o cancela tus reservas:</b><br>
            Entra en "Mis Reservas" desde el menú para ver tus inscripciones activas. Pulsa el icono 🗑️ para cancelar.
        """.trimIndent()

        val tvContenido = view.findViewById<TextView>(R.id.tvContenidoAyuda)
        tvContenido.text = HtmlCompat.fromHtml(contenido, HtmlCompat.FROM_HTML_MODE_LEGACY)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Entendido", null)
            .show()
    }
}

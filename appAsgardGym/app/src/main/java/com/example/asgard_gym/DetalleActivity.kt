package com.example.asgard_gym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DetalleActivity : AppCompatActivity() {

    private lateinit var btnMenu: ImageButton
    private lateinit var fechaClase: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        // Inicializar y configurar la barra de herramientas
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)

        //Inicializar acceso a la BBDD
        val dbHelper = DatabaseHelper(this)

        //Al pulsar el logo del menu vuelve a MainActivity
        logoInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        // Abrir perfil desde el icono de usuario
        findViewById<ImageButton>(R.id.btnUsuario).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Mostrar ayuda desde el icono de ayuda
        findViewById<ImageButton>(R.id.btnAyuda).setOnClickListener {
            cuadroDialogoAyuda()
        }

        //Variables
        val imgDetalle = findViewById<ImageView>(R.id.imgDetalle)
        val txtNombre = findViewById<TextView>(R.id.txtNombreDetalle)
        val txtTipo = findViewById<TextView>(R.id.txtTipoDetalle)
        val txtDescripcion = findViewById<TextView>(R.id.txtDescripcionDetalle)
        val txtHorario = findViewById<TextView>(R.id.txtHorarioDetalle)
        val txtInstructor = findViewById<TextView>(R.id.txtInstructorDetalle)
        val txtDuracion = findViewById<TextView>(R.id.txtDuracionDetalle)
        val btnReservar = findViewById<Button>(R.id.btnReservar)

        //Datos de la actividad recibidos por Intent
        val idActividad = intent.getIntExtra("id", -1)
        val nombre = intent.getStringExtra("nombre")
        val tipo = intent.getStringExtra("tipo")
        val descripcion = intent.getStringExtra("descripcion")
        val horario = intent.getStringExtra("horario")
        val instructor = intent.getStringExtra("instructor")
        val duracion = intent.getStringExtra("duracion")
        val imagen = intent.getIntExtra("imagen", R.drawable.ic_launcher_foreground)

        fechaClase = intent.getStringExtra("fecha_clase") ?: ""
        val actividadActualizada = dbHelper.obtenerActividadPorId(idActividad)
        val plazasTotales = actividadActualizada?.plazasDisponibles ?: 0
        val plazasOcupadas = dbHelper.contarReservasPorFecha(idActividad, fechaClase)
        var plazasDisponibles = plazasTotales - plazasOcupadas

        // Mostrar la imagen de la actividad
        imgDetalle.setImageResource(imagen)

        //Asignar texto a los campos de detalle con los datos recibidos
        txtNombre.text = nombre
        txtTipo.text = "Tipo: $tipo"
        txtDescripcion.text = descripcion
        txtHorario.text = horario
        txtInstructor.text = instructor
        txtDuracion.text = duracion

        val txtPlazas = findViewById<TextView>(R.id.txtPlazasDetalle)
        txtPlazas.text = plazasDisponibles.toString()

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

            //Opciones del Menú Contextual
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

        // Si no hay plazas disponibles, desactivar el botón de reserva
        if (plazasDisponibles == 0) {

            btnReservar.isEnabled = false
            btnReservar.text = "COMPLETA"
            btnReservar.setBackgroundColor(getColor(R.color.red))
            btnReservar.alpha = 0.5f
        }

        //Botón Reservar Plaza
        btnReservar.setOnClickListener {
            //val fechaClase = intent.getStringExtra("fecha_clase") ?: ""
            val horarioActividad = intent.getStringExtra("horario") ?: ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val hoy = LocalDate.now()
                val fechaActividad = LocalDate.parse(fechaClase)

                // Validar clase en días pasados
                if (fechaActividad.isBefore(hoy)) {
                    btnReservar.isEnabled = false
                    btnReservar.text = "CLASE PASADA"
                    btnReservar.setBackgroundColor(getColor(R.color.red))
                    btnReservar.alpha = 0.5f
                    Toast.makeText(this,
                        "No se pueden reservar clases en fechas pasadas.",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Validar hora pasada del mismo día
                if (fechaActividad.isEqual(hoy)) {
                    val formatter = DateTimeFormatter.ofPattern("H:mm")
                    val horaActual = LocalTime.now()
                    val horaActividad = LocalTime.parse(horarioActividad, formatter)

                    if (horaActividad.isBefore(horaActual)) {
                        btnReservar.isEnabled = false
                        btnReservar.text = "HORA PASADA"
                        btnReservar.setBackgroundColor(getColor(R.color.red))
                        btnReservar.alpha = 0.5f

                        Toast.makeText(this,
                            "No se pueden reservar clases en horarios ya pasados.",
                            Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            // Continuar si pasa las validaciones anteriores
            if (plazasDisponibles > 0) {
                val preferencias = getSharedPreferences("UserSession", MODE_PRIVATE)
                val idUsuario = preferencias.getInt("usuario_id", -1)

                if (idUsuario != -1) {
                    if (dbHelper.yaEstaReservado(idUsuario, idActividad, fechaClase)) {
                        Toast.makeText(this,
                            "Ya tienes una reserva para esta clase en esa fecha",
                            Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val resultadoReserva = dbHelper.insertarReserva(idActividad, idUsuario, fechaClase)

                    if (resultadoReserva != -1L) {

                        // cuántas reservas existen para esa actividad en esa fecha específica
                        val plazasOcupadasActuales = dbHelper.contarReservasPorFecha(idActividad, fechaClase)

                        //Obtiener las plazas totales definidas para la actividad
                        val plazasTotalesActuales = dbHelper.obtenerActividadPorId(idActividad)?.plazasDisponibles ?: 0

                        //cuántas plazas quedan restando las ya ocupadas a las totales
                        plazasDisponibles = plazasTotalesActuales - plazasOcupadasActuales
                        txtPlazas.text = plazasDisponibles.toString()

                        Toast.makeText(
                            this,
                            "Has reservado plaza en $nombre. Quedan $plazasDisponibles plazas.",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (plazasDisponibles == 0) {
                            btnReservar.isEnabled = false
                            btnReservar.text = "CLASE COMPLETA"
                            btnReservar.setBackgroundColor(getColor(R.color.red))
                            btnReservar.alpha = 0.5f
                        }

                        // Obtener datos de la actividad
                        val actividad = dbHelper.obtenerActividadPorId(idActividad)
                        val nombreActividad = actividad?.nombre ?: "Actividad desconocida"

                        //Obtener nombre de usuario desde BBDD
                        val usuario = dbHelper.obtenerUsuarioPorId(idUsuario)
                        val nombreUsuario = usuario?.nombre ?: "Usuario desconocido"
                        val dniUsuario = usuario?.DNI ?: "DNI desconocido"

                        // Registrar el histórico de la reserva
                        dbHelper.registrarHistorico(
                            idReserva = resultadoReserva,
                            accion = "insert",
                            nombreUsuario = nombreUsuario,
                            dniUsuario = dniUsuario,
                            nombreActividad = nombreActividad,
                            fechaClase = fechaClase,
                            plazasDisponiblesActualizadas = plazasDisponibles
                        )

                    } else {
                        Toast.makeText(this, "Error al registrar la reserva.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Error de sesión.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "No quedan plazas disponibles.", Toast.LENGTH_SHORT).show()
                btnReservar.isEnabled = false
                btnReservar.text = "CLASE COMPLETA"
                btnReservar.setBackgroundColor(getColor(R.color.red))
                btnReservar.alpha = 0.5f
            }
        }

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



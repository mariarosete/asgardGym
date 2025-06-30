package com.example.asgard_gym

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek

class AgendaActivity : AppCompatActivity() {

    private lateinit var tabla: TableLayout
    private lateinit var leyendaColor: LinearLayout
    private lateinit var leyendaDispo: LinearLayout
    private lateinit var spNombre: Spinner
    private lateinit var spMonitor: Spinner
    private lateinit var spTipo: Spinner
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var btnMenu: ImageButton
    private lateinit var btnAnterior: LinearLayout
    private lateinit var btnSiguiente: LinearLayout
    private val horas = listOf("9:15", "10:15", "17:15", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00")
    private var fechaInicioSemana = obtenerLunesDeEstaSemana()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        // Inicializar y configurar la barra de herramientas
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)

        val logoInicio = findViewById<ImageView>(R.id.logoInicio)

        //Al pulsar el logo del menu vuelve a MainActivity
        logoInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        btnMenu = findViewById(R.id.btnMenu)
        tabla = findViewById(R.id.tablaAgenda)
        leyendaColor = findViewById(R.id.leyendaColores)
        leyendaDispo = findViewById(R.id.leyendaDisponibilidad)
        spNombre = findViewById(R.id.spinnerNombre)
        spMonitor = findViewById(R.id.spinnerMonitor)
        spTipo = findViewById(R.id.spinnerTipo)
        btnAnterior = findViewById(R.id.btnSemanaAnterior)
        btnSiguiente = findViewById(R.id.btnSemanaSiguiente)
        dbHelper = DatabaseHelper(this)

        btnMenu.setOnClickListener { mostrarMenu() }
        findViewById<ImageButton>(R.id.btnUsuario).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnAyuda).setOnClickListener {
            cuadroDialogoAyuda()
        }

        // Navegación entre semanas
        btnAnterior.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fechaInicioSemana = fechaInicioSemana.minusDays(7)
            }
            actualizarAgenda()
        }

        btnSiguiente.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fechaInicioSemana = fechaInicioSemana.plusDays(7)
            }
            actualizarAgenda()
        }

        // Carga inicial
        cargarSpinners()
        agregarLeyenda()
    }

    // Carga de los spinners de filtro con sus opciones
    private fun cargarSpinners() {
        cargarSpinnerNombre()
        cargarSpinnerMonitor()
        cargarSpinnerTipo()

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                actualizarAgenda()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spNombre.onItemSelectedListener = listener
        spMonitor.onItemSelectedListener = listener
        spTipo.onItemSelectedListener = listener
    }

    // Cargar spinner de nombres de actividades
    private fun cargarSpinnerNombre() {
        val nombres = dbHelper.obtenerNombresActividades()
        spNombre.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    // Cargar spinner de nombres de monitores
    private fun cargarSpinnerMonitor() {
        val monitores = dbHelper.obtenerNombresMonitores()
        spMonitor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monitores).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    // Cargar spinner de tipos de actividades
    private fun cargarSpinnerTipo() {
        val tipos = dbHelper.obtenerTiposActividades()
        spTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    // Actualiza la tabla de agenda según los filtros seleccionados
    private fun actualizarAgenda() {
        tabla.removeAllViews()
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Cabecera con los días y fechas
        val encabezado = TableRow(this)
        encabezado.addView(crearCeldas("Hora", bold = true))

        // Cabecera con día + fecha
        for (i in 0 until 5) {
            val fecha = fechaInicioSemana.plusDays(i.toLong())
            val fechaFormateada = formatter.format(fecha)
            encabezado.addView(crearEncabezadoDia(diasSemana[i], fechaFormateada))
        }

        tabla.addView(encabezado)

        // Filtros actuales
        val fNombre = spNombre.selectedItem.toString().trim()
        val fMonitor = spMonitor.selectedItem.toString().trim()
        val fTipo = spTipo.selectedItem.toString().trim()
        val actividades = dbHelper.filtrarActividades(fNombre, fMonitor, fTipo)

        // Crear fila por cada hora
        for (hora in horas) {
            val fila = TableRow(this)
            fila.addView(crearCeldas(hora, bold = true, isHora = true))

            for (i in 0 until 5) {
                val diaTexto = diasSemana[i]
                val contenedor = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(4, 4, 4, 4)
                    layoutParams = TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val actividadesEnCelda = actividades.filter {
                    it.diaSemana.equals(diaTexto, ignoreCase = true) && it.horario == hora
                }

                if (actividadesEnCelda.isNotEmpty()) {
                    val fechaClase = formatter.format(fechaInicioSemana.plusDays(i.toLong()))

                    actividadesEnCelda.forEach { actividad ->
                        val ocupadas = dbHelper.contarReservasPorFecha(actividad.idActividad, fechaClase)
                        val plazasDisponibles = actividad.plazasDisponibles - ocupadas

                        val celda = TextView(this).apply {
                            text = actividad.nombre
                            setBackgroundColor(obtenerColorActividad(actividad.nombre))
                            setTextColor(resources.getColor(android.R.color.white))
                            gravity = Gravity.CENTER
                            setPadding(8, 12, 8, 12)
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 0, 0, 4)
                            }

                            setOnClickListener {
                                val intent = Intent(this@AgendaActivity, DetalleActivity::class.java).apply {
                                    putExtra("id", actividad.idActividad)
                                    putExtra("nombre", actividad.nombre)
                                    putExtra("tipo", actividad.tipo)
                                    putExtra("descripcion", actividad.descripcion)
                                    putExtra("horario", actividad.horario)
                                    putExtra("instructor", actividad.instructor)
                                    putExtra("duracion", actividad.duracion)
                                    putExtra("imagen", actividad.imagen)
                                    putExtra("plazas", plazasDisponibles)
                                    putExtra("fecha_clase", fechaClase)
                                }
                                startActivity(intent)
                            }
                        }

                        val icono = ImageView(this).apply {
                            val iconoRes = if (plazasDisponibles > 0) R.drawable.libre else R.drawable.completo
                            setImageResource(iconoRes)
                            layoutParams = LinearLayout.LayoutParams(40, 40).apply {
                                gravity = Gravity.END
                                setMargins(0, 0, 0, 4)
                            }
                        }

                        val filaActividad = FrameLayout(this).apply {
                            addView(celda)
                            addView(icono)
                        }

                        contenedor.addView(filaActividad)
                    }
                } else {
                    val celdaVacia = TextView(this).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        gravity = Gravity.CENTER
                        setPadding(8, 16, 8, 16)
                    }
                    contenedor.addView(celdaVacia)
                }

                fila.addView(contenedor)
            }
            tabla.addView(fila)
        }

    }

    // Crea una celda para el encabezado con nombre de día y fecha
    private fun crearEncabezadoDia(dia: String, fecha: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.parseColor("#F9EAC3"))

            val tvDia = TextView(this@AgendaActivity).apply {
                text = dia
                setTypeface(null, Typeface.BOLD)
                textSize = 16f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
            }

            val tvFecha = TextView(this@AgendaActivity).apply {
                text = fecha
                textSize = 13f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.CENTER
            }

            addView(tvDia)
            addView(tvFecha)
        }
    }

    // Crea una celda de texto para la agenda
    private fun crearCeldas(text: String, bold: Boolean = false, isHora: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            gravity = Gravity.CENTER
            setPadding(8, if (text == "Hora") 24 else 8, 8, 8)
            if (bold) setTypeface(null, Typeface.BOLD)

            if (isHora) {
                setBackgroundColor(
                    when (text) {
                        "9:15", "10:15" -> 0xFFDDD1BC.toInt()
                        else -> 0xFFF5ECD6.toInt()
                    }
                )
            }
        }
    }

    // Agrega la leyenda de colores y disponibilidad a la vista
    private fun agregarLeyenda() {
        leyendaColor.removeAllViews()
        leyendaDispo.removeAllViews()

        val actividades = listOf("Pilates", "Body Active", "Zumba", "Judo", "Ciclo Indoor", "Minitramp")
        actividades.forEach { nombre ->
            leyendaColor.addView(crearItemColor(obtenerColorActividad(nombre), nombre))
        }
        leyendaDispo.addView(crearItemLeyenda(R.drawable.libre, "Plazas disponibles"))
        leyendaDispo.addView(crearItemLeyenda(R.drawable.completo, "Completa"))
    }

    // Crea un ítem de leyenda con color de fondo
    private fun crearItemColor(color: Int, texto: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 8)

            val colorBox = View(this@AgendaActivity).apply {
                layoutParams = LinearLayout.LayoutParams(40, 40).apply { setMargins(0, 0, 24, 0) }
                setBackgroundColor(color)
            }
            val nombreText = TextView(this@AgendaActivity).apply {
                text = texto
                textSize = 16f
            }
            addView(colorBox)
            addView(nombreText)
        }
    }

    // Crea un ítem de leyenda con icono de disponibilidad
    private fun crearItemLeyenda(iconResId: Int, texto: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 8)

            val icono = ImageView(this@AgendaActivity).apply {
                setImageResource(iconResId)
                layoutParams = LinearLayout.LayoutParams(40, 40).apply { setMargins(0, 0, 24, 0) }
            }
            val textoLeyenda = TextView(this@AgendaActivity).apply {
                text = texto
                textSize = 16f
            }
            addView(icono)
            addView(textoLeyenda)
        }
    }

    // Devuelve el color asignado a una actividad por su nombre
    private fun obtenerColorActividad(nombre: String): Int {
        return when (nombre.lowercase()) {
            "ciclo indoor" -> 0xFF9E9E9E.toInt()
            "pilates" -> 0xFFE57373.toInt()
            "body active" -> 0xFFCE93D8.toInt()
            "judo" -> 0xFFAED581.toInt()
            "zumba" -> 0xFF4DD0E1.toInt()
            "minitramp" -> 0xFFFFB74D.toInt()
            else -> 0xFF009688.toInt()
        }
    }

    // Devuelve la fecha del lunes de esta semana
    private fun obtenerLunesDeEstaSemana(): LocalDate {
        val hoy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hoy.with(DayOfWeek.MONDAY)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    // Muestra el menú contextual al pulsar el botón ☰
    private fun mostrarMenu() {
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

    // Al reanudar la actividad, se actualiza la agenda
    override fun onResume() {
        super.onResume()
        actualizarAgenda()
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

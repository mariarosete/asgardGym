package com.example.asgard_gym

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat

class ReservasActivity : AppCompatActivity() {

    // Referencias a vistas
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ReservaAdapter
    private lateinit var btnMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        // Inicializar y configurar la barra de herramientas
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)

        //Al pulsar el logo del menu vuelve a MainActivity
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)
        logoInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        // Botón de usuario que abre el perfil
        findViewById<ImageButton>(R.id.btnUsuario).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Mostrar ayuda desde el icono de ayuda
        findViewById<ImageButton>(R.id.btnAyuda).setOnClickListener {
            cuadroDialogoAyuda()
        }

        // Obtener vistas
        btnMenu = findViewById(R.id.btnMenu)
        listView = findViewById(R.id.listViewReservas)

        //Inicializar acceso a la BBDD
        dbHelper = DatabaseHelper(this)

        // Obtener ID del usuario desde SharedPreferences
        val usuarioId = getSharedPreferences("UserSession", MODE_PRIVATE)
            .getInt("usuario_id", -1)

        // Si hay un usuario logueado, cargar sus reservas
        if (usuarioId != -1) {

            // Obtener lista de reservas del usuario
            val reservas = dbHelper.obtenerReservasPorUsuario(usuarioId)

            // Adaptador personalizado para mostrar cada reserva
            adapter = ReservaAdapter(this, reservas.toMutableList())
            listView.adapter = adapter
        }

        // Configuración del botón del menú contextual
        btnMenu.setOnClickListener {

            val wrapper = androidx.appcompat.view.ContextThemeWrapper(this, R.style.CustomPopupMenu)
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

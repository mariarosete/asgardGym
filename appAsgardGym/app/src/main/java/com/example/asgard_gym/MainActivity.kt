package com.example.asgard_gym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.*
import android.text.Html
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.*
import com.google.android.material.button.MaterialButton
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: Adapter
    private lateinit var actividadesOriginales: List<DTOActividad>
    private val handler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)


        // Al pulsar el logo se recarga la misma actividad
        logoInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        // Navegación al perfil
        findViewById<ImageButton>(R.id.btnUsuario).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Diálogo de ayuda
        findViewById<ImageButton>(R.id.btnAyuda).setOnClickListener {
            cuadroDialogoAyuda()
        }

        // Menú contextual personalizado
        btnMenu.setOnClickListener {

            val wrapper = androidx.appcompat.view.ContextThemeWrapper(this, R.style.CustomPopupMenu)
            val popupMenu = android.widget.PopupMenu(wrapper, findViewById(R.id.btnMenu))

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

        // Configuración de carrusel (ViewPager)
        viewPager = findViewById(R.id.viewPagerActividades)
        findViewById<MaterialButton>(R.id.btnVerAgenda).setOnClickListener {
            startActivity(Intent(this, AgendaActivity::class.java))
        }

        // Obtener lista de actividades y mezclarlas
        actividadesOriginales = DatabaseHelper(this).obtenerActividadesCarrusel()
        val mezcladas = actividadesOriginales.shuffled()

        // Establecer adaptador del ViewPager
        adapter = Adapter(mezcladas)
        viewPager.adapter = adapter
        viewPager.currentItem = 0

        // Configurar apariencia del ViewPager
        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        // Efecto visual de transformación al hacer scroll
        val transformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(28))

            addTransformer { page, position ->
                val r = 1 - abs(position)

                val scale = 0.85f + r * 0.15f
                page.scaleY = scale
                page.scaleX = scale

                page.alpha = 0.5f + r * 0.5f

                page.translationX = -position * 50
            }
        }
        viewPager.setPageTransformer(transformer)

        // Reinicia el scroll automático al cambiar de página
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                reiniciarScrollConDelay()
            }
        })

        iniciarScrollAutomatico()

        // Animación en imagen de filosofía
        val imgFilosofia = findViewById<ImageView>(R.id.imgFilosofia)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        imgFilosofia.startAnimation(fadeIn)

        // Al hacer clic, muestra el diálogo de filosofía
        imgFilosofia.setOnClickListener {
            val clickAnim = AnimationUtils.loadAnimation(this, R.anim.click_scale)
            imgFilosofia.startAnimation(clickAnim)

            mostrarDialogoFilosofia()
        }

    }

    // Iniciar scroll automático del carrusel cada 4 segundos
    private fun iniciarScrollAutomatico() {
        scrollRunnable = object : Runnable {
            override fun run() {
                val siguiente = viewPager.currentItem + 1
                if (siguiente < adapter.itemCount) {
                    viewPager.setCurrentItem(siguiente, true)
                } else {
                    val nuevaLista = actividadesOriginales.shuffled()
                    adapter = Adapter(nuevaLista)
                    viewPager.adapter = adapter
                    viewPager.setCurrentItem(0, false)
                }
                handler.postDelayed(this, 4000)
            }
        }
        handler.postDelayed(scrollRunnable!!, 4000)
    }

    // Reiniciar scroll automático con retraso
    private fun reiniciarScrollConDelay() {
        scrollRunnable?.let { handler.removeCallbacks(it) }
        handler.postDelayed(scrollRunnable!!, 4000)
    }

    // Cancelar scroll automático al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        scrollRunnable?.let { handler.removeCallbacks(it) }
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun mostrarDialogoContacto() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_contacto, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton("Cerrar", null)
            .create()

        dialog.show()

        val webView = dialogView.findViewById<WebView>(R.id.webViewMapa)
        val mapaHtml = """
        <html>
            <body style="margin:0;padding:0;">
                <iframe 
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2892.372311344322!2d-5.655859263251102!3d43.53628044934015!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd367c877c4b8b91%3A0xf8cc7acc4e7b18e4!2sAsgard%20Gym!5e0!3m2!1ses!2ses!4v1745057654185!5m2!1ses!2ses"
                    width="100%" height="100%" frameborder="0" style="border:0;" allowfullscreen=""
                    loading="lazy" referrerpolicy="no-referrer-when-downgrade">
                </iframe>
            </body>
        </html>
    """.trimIndent()

        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, mapaHtml, "text/html", "UTF-8", null)

        dialogView.findViewById<TextView>(R.id.txtTelefono).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:985093393")
            startActivity(intent)
        }
    }

    // Mostrar cuadro de diálogo con la filosofía del gimnasio
    private fun mostrarDialogoFilosofia() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filosofia, null)

        // Cargar el texto justificado con párrafos desde strings.xml
        val textoFilosofia = dialogView.findViewById<TextView>(R.id.textoFilosofia)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textoFilosofia.text = Html.fromHtml(getString(R.string.texto_tarjeta), Html.FROM_HTML_MODE_LEGACY)
        } else {
            textoFilosofia.text = Html.fromHtml(getString(R.string.texto_tarjeta))
        }

        // Crear y mostrar el diálogo
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton("Cerrar", null)
            .create()

        // Acción del botón de contacto dentro del diálogo
        dialogView.findViewById<Button>(R.id.btnContactoFilosofia).setOnClickListener {
            dialog.dismiss()
            mostrarDialogoContacto()
        }

        dialog.show()
    }

}

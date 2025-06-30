package com.example.asgard_gym

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialReservasActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_reservas)

        // Inicializar y configurar la barra de herramientas
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)

        //al pulsar el logo vuelve a adminactivity
        logoInicio.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        // al pulsar el botón salir se cierra la app
        val btnSalir = findViewById<ImageView>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            finishAffinity()
        }

        // configurar el recyclerview con diseño vertical
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // obtener el historial desde la BBDD
        val dbHelper = DatabaseHelper(this)
        val historial = dbHelper.obtenerHistorialCompleto()

        // asignar adaptador con los datos del historial
        recyclerView.adapter = HistorialReservasAdapter(historial)
    }
}

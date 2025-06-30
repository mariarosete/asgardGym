package com.example.asgard_gym

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Configurar Toolbar
        val tbMenu = findViewById<Toolbar>(R.id.tbMenu)
        setSupportActionBar(tbMenu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Al hacer clic en el logo, recarga la propia actividad
        val logoInicio = findViewById<ImageView>(R.id.logoInicio)
        logoInicio.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        // Botón para salir de la aplicación completamente
        val btnSalir = findViewById<ImageView>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            finishAffinity()
        }

        // Botón para gestionar actividades: abre pantalla CRUD de actividades
        findViewById<Button>(R.id.btnGestionActividades).setOnClickListener {
            startActivity(Intent(this, CrudActividadesActivity::class.java))
        }

        // Botón para gestionar usuarios: abre pantalla CRUD de usuarios
        findViewById<Button>(R.id.btnGestionUsuarios).setOnClickListener {
            startActivity(Intent(this, CrudUsuariosActivity::class.java))
        }

        // Botón para visualizar el historial de cambios en reservas
        findViewById<Button>(R.id.btnVerHistorico).setOnClickListener {
            startActivity(Intent(this, HistorialReservasActivity::class.java))
        }
    }
}



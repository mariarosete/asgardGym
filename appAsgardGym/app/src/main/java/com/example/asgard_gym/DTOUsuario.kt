package com.example.asgard_gym

import java.io.Serializable

// DTO para Usuario
data class DTOUsuario(
    val idUsuario: Int = 0,
    var nombre: String,
    var apellidos: String,
    var DNI: String,
    var fechaNacimiento: String,
    var correo: String,
    var codigoPostal: String? = null,
    var aceptacionDatos: Boolean = false,
    var esAdmin: Boolean = false,
    var enfermedades: String? = ""
) : Serializable



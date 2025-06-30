package com.example.asgard_gym

import java.io.Serializable

data class DTOActividad(
    val idActividad: Int = 0,
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val horario: String,
    val instructor: String,
    val duracion: String,
    val imagen: Int,
    val diaSemana: String,
    val plazasDisponibles: Int = 15
) : Serializable



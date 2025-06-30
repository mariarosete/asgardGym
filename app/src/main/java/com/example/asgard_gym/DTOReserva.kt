package com.example.asgard_gym

data class DTOReserva(

    val idReserva: Int = 0,
    val idActividad: Int,
    val idUsuario: Int,

    val nombreActividad: String,
    val diaSemana: String,
    val horarioActividad: String,
    val fechaClase: String,
    val fecha: String,
    val hora: String
)


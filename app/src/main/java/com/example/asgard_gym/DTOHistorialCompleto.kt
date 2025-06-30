package com.example.asgard_gym

data class DTOHistorialCompleto(
    val idHistorico: Int,
    val fechaModificacion: String,
    val accion: String,
    val valorNuevo: Int?,
    val nombreActividad: String?,
    val fechaClase: String?,
    val nombreUsuario: String?,
    val dniUsuario: String?
)

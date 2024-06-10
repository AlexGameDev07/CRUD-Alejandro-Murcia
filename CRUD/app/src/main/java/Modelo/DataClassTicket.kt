package Modelo

import java.util.Date

data class DataClassTicket(
    val numTicket: Int,
    var titulo: String,
    val descripción: String,
    val autor: String,
    val correo: String,
    val fechaCreación: Date,
    val estado: String,
    val fechaFinalización: String
)

package com.example.reto_final.data.socket

data class SocketMessageReq(
    val room: Int,
    val message: String,
    val sent: Long
)
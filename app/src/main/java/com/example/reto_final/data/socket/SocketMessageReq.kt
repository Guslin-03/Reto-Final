package com.example.reto_final.data.socket

data class SocketMessageReq(
    val idChat: Int,
    val localId: Int,
    val message: String,
    val sent: Long
)
package com.example.reto_final.data.socket

import java.util.Date

data class SocketMessageReq(
    val roomId: Int,
    val message: String,
    val sent: Date
)
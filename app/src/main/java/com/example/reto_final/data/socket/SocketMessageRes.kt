package com.example.reto_final.data.socket

import java.util.Date

data class SocketMessageRes (
    val messageType: MessageType,
    val roomId: Int,
    val messageId: Int,
    val message: String,
    val authorName: String,
    val authorId: Int,
    val sent: Long,
    val saved: Long,
)
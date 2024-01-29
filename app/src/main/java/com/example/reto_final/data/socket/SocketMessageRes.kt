package com.example.reto_final.data.socket

data class SocketMessageRes (
    val messageType: MessageType,
    val room: Int,
    val messageId: Int,
    val message: String,
    val authorName: String,
    val authorId: Int,
    val sent: Long,
    val saved: Long,
)
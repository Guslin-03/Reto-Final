package com.example.reto_final.data.socket

import java.util.Date

data class SocketMessageRes (
    val messageType: MessageType,
    val room: String,
    val message: String,
    val authorName: String,
    val dateTime: Date,
    val authorId: Int
)
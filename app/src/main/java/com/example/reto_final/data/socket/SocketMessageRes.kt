package com.example.reto_final.data.socket

import com.example.reto_final.data.repository.local.message.MessageEnumClass

data class SocketMessageRes (
    val messageType: MessageType,
    val room: Int,
    val messageServerId: Int,
    val localId: Int,
    val message: String,
    val authorName: String,
    val authorId: Int,
    val sent: Long,
    val saved: Long,
    val type: String
)
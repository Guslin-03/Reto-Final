package com.example.reto_final.data.socket

enum class SocketEvents(val value: String) {
    ON_MESSAGE_RECEIVED("receive message"),
    ON_SEND_MESSAGE("send message"),
    ON_CONNECT("connect"),
    ON_DISCONNECT("disconnect"),

    ON_ROMM_LEFT("room left"),
    ON_ROMM_JOIN("room join")
}
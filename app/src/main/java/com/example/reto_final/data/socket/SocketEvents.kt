package com.example.reto_final.data.socket

enum class SocketEvents(val value: String) {
    ON_MESSAGE_RECEIVED("receive message"),
    ON_SEND_MESSAGE("send message"),
    ON_CONNECT("connect"),
    ON_DISCONNECT("disconnect"),

    ON_ROOM_LEFT("room left"),
    ON_ROOM_JOIN("room join")

}
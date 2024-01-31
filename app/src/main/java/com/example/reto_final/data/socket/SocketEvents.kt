package com.example.reto_final.data.socket

enum class SocketEvents(val value: String) {
    ON_MESSAGE_RECEIVED("receive message"),
    ON_SEND_MESSAGE("send message"),
    ON_CONNECT("connect"),
    ON_DISCONNECT("disconnect"),

    ON_CHAT_JOIN("chat join"),
    ON_CHAT_ADDED("added to chat"),
    ON_CHAT_LEFT("chat left"),
    ON_CHAT_THROW_OUT("throw out from chat"),

}
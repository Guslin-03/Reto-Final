package com.example.reto_final.data.socket

enum class SocketEvents(val value: String) {

    ON_MESSAGE_RECEIVED("receive message"),
    ON_SEND_MESSAGE("send message"),
    ON_MESSAGE_NOT_SENT("not sent message"),

    ON_CONNECT("connect"),
    ON_DISCONNECT("disconnect"),

    ON_CHAT_RECEIVED("receive chat"),
    ON_SEND_CHAT("send chat"),
    ON_CHAT_NOT_SENT("not sent chat"),


    ON_CHAT_JOIN_RECEIVED("receive hat join"),
    ON_CHAT_JOIN("chat join"),
    ON_CHAT_NOT_JOIN("not chat join"),

    ON_CHAT_LEAVE_RECEIVED("receive chat leave"),
    ON_CHAT_LEAVE("chat leave"),
    ON_CHAT_NOT_LEAVE("not chat leave"),

    ON_CHAT_ADD_RECEIVED("receive add to chat"),
    ON_CHAT_ADD("add to chat"),
    ON_CHAT_NOT_ADD("not add to chat"),

    ON_CHAT_THROW_OUT_RECEIVED("receive throw out from chat"),
    ON_CHAT_THROW_OUT("throw out from chat"),
    ON_CHAT_NOT_THROW_OUT("not throw out from chat"),

    ON_FILE_RECEIVED ("send file"),
    ON_SEND_FILE("receive file"),
    ON_FILE__NOT_SENT("not send file");

}
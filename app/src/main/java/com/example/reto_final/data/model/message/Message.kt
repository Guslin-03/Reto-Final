package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    var id: Int?,
    val idServer: Int?,
    var text: String,
    val sent: Long,
    val saved: Long?,
    var chatId: Int,
    val userId: Int,
    val type: String,
    var userName: String?

) : Parcelable {
    constructor(
        text: String,
        sent: Long,
        type:String,
        chatId: Int,
        userId: Int
    ) : this(null, null, text, sent, null, chatId, userId, type, null)

    constructor(
        idServer: Int,
        text: String,
        sent: Long,
        saved: Long,
        type: String,
        chatId: Int,
        userId: Int
    ) : this(null, idServer, text, sent, saved, chatId, userId, type, null)

    constructor(
        id: Int,
        text: String,
        sent: Long,
        type: String,
        chatId: Int,
        userId: Int
    ) : this(id, null, text, sent, null, chatId, userId, type, null)

    constructor(
        id: Int,
        text: String,
        sent: Long,
        type: String,
        chatId: Int,
        userId: Int,
        userName: String
    ) : this(id, null, text, sent, null, chatId, userId, type, userName)

}

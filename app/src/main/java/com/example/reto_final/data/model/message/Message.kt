package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    var id: Int?,
    val idServer: Int?,
    val text: String,
    val sent: Long,
    val saved: Long?,
    var chatId: Int,
    val userId: Int
) : Parcelable {
    constructor(
        text: String,
        sent: Long,
        chatId: Int,
        userId: Int
    ) : this(null, null, text, sent, null, chatId, userId)

    constructor(
        idServer: Int,
        text: String,
        sent: Long,
        saved: Long,
        chatId: Int,
        userId: Int
    ) : this(null, idServer, text, sent, saved, chatId, userId)

    constructor(
        id: Int,
        text: String,
        sent: Long,
        chatId: Int,
        userId: Int
    ) : this(id, null, text, sent, null, chatId, userId)

}

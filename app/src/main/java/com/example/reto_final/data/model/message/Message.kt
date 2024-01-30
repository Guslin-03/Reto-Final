package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    var id: Int?,
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
    ) : this(null, text, sent, null, chatId, userId)
}

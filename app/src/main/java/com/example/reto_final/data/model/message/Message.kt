package com.example.reto_final.data.model.message

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.example.reto_final.data.repository.local.message.MessageEnumClass
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    var id: Int?,
    val idServer: Int?,
    val text: String,
    val sent: Long,
    val saved: Long?,
    var chatId: Int,
    val userId: Int,
    val type: String
) : Parcelable {
    constructor(
        text: String,
        sent: Long,
        type:String,
        chatId: Int,
        userId: Int
    ) : this(null, null, text, sent, null, chatId, userId,type)

    constructor(
        idServer: Int,
        text: String,
        sent: Long,
        saved: Long,
        type: String,
        chatId: Int,
        userId: Int
    ) : this(null, idServer, text, sent, saved, chatId, userId,type)

    constructor(
        id: Int,
        text: String,
        sent: Long,
        type: String,
        chatId: Int,
        userId: Int
    ) : this(id, null, text, sent, null, chatId, userId, type)

}

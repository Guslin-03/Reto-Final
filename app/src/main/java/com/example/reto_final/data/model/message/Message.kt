package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    var id: Int?,
    val text: String,
    val sent: Long,
    val saved: Long,
    var groupId: Int,
    val authorId: Int
) : Parcelable {
    constructor(
        text: String,
        sent: Long,
        saved: Long,
        groupId: Int,
        authorId: Int
    ) : this(null, text, sent, saved, groupId, authorId)
}

package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageGetResponse(
    var id: Int,
    val text: String,
    val sent: Long,
    val saved: Long,
    val chatId: Int,
    val userId: Int,
    val type: String,
) : Parcelable
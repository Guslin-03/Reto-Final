package com.example.reto_final.data.model.message

import android.os.Parcelable
import com.example.reto_final.data.repository.local.message.MessageEnumClass
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageResponse(
    var id: Int,
    val text: String,
    val sent: Long,
    val saved: Long,
    val chatId: Int,
    val userId: Int,
    val type: String,
) : Parcelable
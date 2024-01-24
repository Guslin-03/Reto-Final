package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class MessageRequest(
    var id: Int?,
    val text: String,
    val sent: SimpleDateFormat,
    val saved: SimpleDateFormat,
    val groupId: Int,
    val authorId: Int
) : Parcelable
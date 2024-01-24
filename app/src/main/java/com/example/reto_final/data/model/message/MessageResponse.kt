package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MessageResponse(
    var id: Int?,
    val text: String,
    val sent: Date,
    val saved: Date,
    val groupId: Int,
    val authorId: Int
) : Parcelable
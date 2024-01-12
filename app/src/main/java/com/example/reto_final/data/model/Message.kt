package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message (
    var id: Int?,
    val text: String,
    val groupId: Int,
    val userId: Int
) : Parcelable
package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Message (
    var id: Int?,
    val text: String,
    val date: Date?,
    val groupId: Int,
    val userId: Int
) : Parcelable
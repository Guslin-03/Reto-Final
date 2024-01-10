package com.example.reto_final.data

import android.os.Parcelable
import com.example.reto_final.data.repository.local.message.DbMessage
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message (

    val id: Int?,
    val text: String

) : Parcelable
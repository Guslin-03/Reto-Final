package com.example.reto_final.data.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PendingMessages(
    val room: Int,
    val userId: Int,
    val localId: Int,
    val message: String,
    val sent: Long,
    val type: String
) : Parcelable



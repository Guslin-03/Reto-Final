package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRequest (
    var id: Int?,
    val name: String,
    val surname: String,
    val email: String,
    val phoneNumber: Int,
    val roleId: Int,
    val chatId: List<Int>
): Parcelable
package com.example.reto_final.data.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    var id: Int?,
    val name: String,
    val surname: String,
    val email: String,
    val phoneNumber: Int,
    val roleId: Int
):Parcelable
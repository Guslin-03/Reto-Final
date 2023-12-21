package com.example.reto_final.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
):Parcelable

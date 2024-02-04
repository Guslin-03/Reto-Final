package com.example.reto_final.data.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthRequest(
    val email: String,
    val password: String,
    val device_name: String
) : Parcelable




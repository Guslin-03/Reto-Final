package com.example.reto_final.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthRequest(
    val email: String,
    val password: String
) : Parcelable




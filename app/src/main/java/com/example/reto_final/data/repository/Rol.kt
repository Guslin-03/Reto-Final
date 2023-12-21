package com.example.reto_final.data.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rol (

    val id: Int,
    val name: String

) : Parcelable
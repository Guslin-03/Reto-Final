package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Degree (

    val id: Int,
    val name: String,
    val modules: Array<Module>

) : Parcelable
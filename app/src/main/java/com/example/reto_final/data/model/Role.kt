package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role (
    var id: Int?,
    val type: String
) : Parcelable
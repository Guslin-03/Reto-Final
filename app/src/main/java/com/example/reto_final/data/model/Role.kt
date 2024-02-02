package com.example.reto_final.data.model

import android.os.Parcelable
import com.example.reto_final.data.repository.local.user.UserRoleType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role (
    var id: Int?,
    val name: String
) : Parcelable
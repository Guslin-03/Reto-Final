package com.example.reto_final.data.model.userGroup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserGroup (
    val roomId: Int,
    val userId: Int,
    val name: String,
    val adminId: Int?,
    val adminName: String?
):Parcelable

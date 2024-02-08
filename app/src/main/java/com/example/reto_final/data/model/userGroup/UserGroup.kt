package com.example.reto_final.data.model.userGroup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserGroup (
    val roomId: Int,
    val userId: Int,
    val adminId: Int?,
    val userName: String,
    val adminName: String?,
    val joined: Long,
    val deleted: Long?
):Parcelable

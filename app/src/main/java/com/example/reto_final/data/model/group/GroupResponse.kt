package com.example.reto_final.data.model.group

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupResponse (
    var id: Int,
    val name: String,
    val type: String,
    val created: Long,
    val deleted: Long,
    val localDeleted: Long?,
    val adminId: Int
): Parcelable
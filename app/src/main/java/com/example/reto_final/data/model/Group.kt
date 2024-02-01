package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group (
    var id: Int?,
    val name: String,
    val type: String,
    val created: Long?,
    val deleted: Long?,
    val adminId: Int
) : Parcelable {

    constructor() : this(
        id = 0,
        name = "",
        type = "",
        created = null,
        deleted = null,
        adminId = 0
    )

}

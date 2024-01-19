package com.example.reto_final.data.model

import android.os.Parcelable
import com.example.reto_final.data.repository.local.group.ChatEnumType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group (
    var id: Int?,
    val name: String,
    val type: String,
    val adminId: Int
) : Parcelable {

    constructor() : this(
        id = 0,
        name = "",
        type = "",
        adminId = 0
    )

}

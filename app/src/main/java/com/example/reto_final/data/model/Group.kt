package com.example.reto_final.data.model

import android.os.Parcelable
import com.example.reto_final.data.repository.local.group.ChatEnumType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group (
    var id: Int?,
    val name: String,
    val type: String,
    val adminId: Int,
    var joinedUsers : List<User>
) : Parcelable {

    constructor(

        id: Int?,
        name: String,
        type: String,
        adminId: Int,

        ): this (

        id = id,
        name = name,
        type = type,
        adminId = adminId,
        joinedUsers = emptyList()

    )

}

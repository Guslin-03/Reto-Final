package com.example.reto_final.data.model

import android.os.Parcelable
import com.example.reto_final.data.repository.local.group.GroupType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group (
    var id: Int?,
    val name: String,
    val groupType: GroupType,
    val adminId: Int,
    var joinedUsers : List<User>
) : Parcelable {

    constructor(

        id: Int?,
        name: String,
        groupType: GroupType,
        adminId: Int,

    ): this (

        id = id,
        name = name,
        groupType = groupType,
        adminId = adminId,
        joinedUsers = emptyList()

    )

}

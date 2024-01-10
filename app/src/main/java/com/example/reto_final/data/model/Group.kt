package com.example.reto_final.data.model

import android.os.Parcelable
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.data.repository.local.group.GroupType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group (
    var id: Int?,
    val name: String,
    val groupType: GroupType

) : Parcelable
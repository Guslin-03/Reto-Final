package com.example.reto_final.data.repository.local.user

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.reto_final.data.repository.local.group.DbGroup

@Entity(tableName = "group_user",
    primaryKeys = ["groupId", "userId",],
    foreignKeys = [
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"]
        ),
        ForeignKey(
            entity = DbUser::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ])
data class DbUserGroup (
    val groupId: Int,
    val userId: Int
)
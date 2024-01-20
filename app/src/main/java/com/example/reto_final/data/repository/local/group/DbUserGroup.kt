package com.example.reto_final.data.repository.local.group

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.reto_final.data.repository.local.user.DbUser

@Entity(tableName = "group_user",
    primaryKeys = ["groupId", "userId",],
    foreignKeys = [
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
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
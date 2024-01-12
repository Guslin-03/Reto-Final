package com.example.reto_final.data.repository.local.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.reto_final.data.repository.local.user.DbUser

@Entity(tableName = "groups", foreignKeys = [
    ForeignKey(
        entity = DbUser::class,
        parentColumns = ["id"],
        childColumns = ["adminId"],
        onDelete = ForeignKey.NO_ACTION
    )
])
data class DbGroup (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val groupType: GroupType,
    @ColumnInfo(name = "adminId") val adminId: Int
)
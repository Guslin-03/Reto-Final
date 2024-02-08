package com.example.reto_final.data.repository.local.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reto_final.data.repository.local.user.DbUser
import java.util.Date

@Entity(tableName = "groups",
    foreignKeys = [
    ForeignKey(
        entity = DbUser::class,
        parentColumns = ["id"],
        childColumns = ["adminId"],
        onDelete = ForeignKey.NO_ACTION)
                  ],
    indices = [
    Index(value = ["name"],
        unique = true)
])
data class DbGroup (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val chatEnumType: String,
    @ColumnInfo(name = "created") val created: Date?,
    @ColumnInfo(name = "deleted") val deleted: Date?,
    @ColumnInfo(name = "adminId") val adminId: Int,
    )
package com.example.reto_final.data.repository.local.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.data.repository.local.user.DbUser
import java.util.Date

@Entity(tableName = "messages", foreignKeys = [
    ForeignKey(
        entity = DbGroup::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = DbUser::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.NO_ACTION
    )
])
data class DbMessage(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "idServer") val idServer: Int?,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "sent") val sentDate: Date,
    @ColumnInfo(name = "saved") val saveDate: Date?,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "groupId") val groupId: Int,
    @ColumnInfo(name = "userId") val userId: Int
)
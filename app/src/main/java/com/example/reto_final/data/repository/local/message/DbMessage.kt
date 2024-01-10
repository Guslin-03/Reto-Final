package com.example.reto_final.data.repository.local.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.reto_final.data.Message
import com.example.reto_final.data.repository.local.group.DbGroup

@Entity(tableName = "messages")
data class DbMessage (

    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "groupId") val groupId: Int // Clave externa que hace referencia al id de DbGroup
) {

    fun toMessage() = Message(id, text)
}
package com.example.reto_final.data.repository.local.role

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "role")
data class DbRole (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "type") val type: String
)

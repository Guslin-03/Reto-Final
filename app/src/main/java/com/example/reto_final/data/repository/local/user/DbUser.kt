package com.example.reto_final.data.repository.local.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class DbUser (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "surname") val surname: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phoneNumber") val phoneNumber: Int
)
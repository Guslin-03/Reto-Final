package com.example.demoemployees.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="employees")
data class DbEmployee (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val name: String,
    val position: String,
    val salary: Int,
)
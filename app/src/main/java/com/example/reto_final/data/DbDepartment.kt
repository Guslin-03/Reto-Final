package com.example.demoemployees.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="departments")
data class DbDepartment (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val name: String,
    val city: String
)
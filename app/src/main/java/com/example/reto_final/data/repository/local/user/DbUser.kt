package com.example.reto_final.data.repository.local.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.reto_final.data.repository.local.role.DbRole

@Entity(tableName = "users",
    foreignKeys = [
    ForeignKey(
        entity = DbRole::class,
        parentColumns = ["id"],
        childColumns = ["roleId"],
        onDelete = ForeignKey.NO_ACTION
    )],
    )
data class DbUser (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "surname") val surname: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phoneNumber") val phone_number1: Int,
    @ColumnInfo(name = "roleId") val roleId: Int
)
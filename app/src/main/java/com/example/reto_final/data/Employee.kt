package com.example.demoemployees.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Employee (
    val id: Int?,
    val name: String,
    val position: String,
    val salary: Int,
): Parcelable
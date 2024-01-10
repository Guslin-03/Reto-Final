package com.example.demoemployees.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Department (
    val id: Int?,
    val name: String,
    val city: String,
): Parcelable
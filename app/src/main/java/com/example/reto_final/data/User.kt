package com.example.reto_final.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    val id: Int,
    val DNI: String,
    val name: String,
    val surname: String,
    val phoneNumber1: Int,
    val phoneNumber2: Int,
    val address: String,
    val photo: String,
    val FCTDUAL: Int,
    val email: String,
    val degrees: Array<Degree>,
    val department_id: Int,
    val token: String

): Parcelable {
    constructor(
        DNI: String,
        name: String,
        surname: String,
        phoneNumber1: Int,
        phoneNumber2: Int,
        address: String,
        photo: String,
        FCTDUAL: Int,
        email: String,
        department_id: Int
    ) : this(
        id = 0,
        DNI = DNI,
        name = name,
        surname = surname,
        phoneNumber1 = phoneNumber1,
        phoneNumber2 = phoneNumber2,
        address = address,
        photo = photo,
        FCTDUAL = FCTDUAL,
        email = email,
        degrees = emptyArray(),
        department_id = department_id,
        token = ""
    )

}
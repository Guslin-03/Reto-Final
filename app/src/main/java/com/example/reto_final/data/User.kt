package com.example.reto_final.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    val id: Int,
    val dni: String,
    val name: String,
    val surname: String,
    val phoneNumber1: Int,
    val phoneNumber2: String,
    val address: String,
    val photo: Int,
    val FCTDUAL: Boolean,
    val email: String,
    val department_id: Int,
    val accessToken: String

): Parcelable {
    constructor(
        dni: String,
        name: String,
        surname: String,
        phoneNumber1: Int,
        phoneNumber2: String,
        address: String,
        photo: Int,
        FCTDUAL: Boolean,
        email: String,
        department_id: Int
    ) : this(
        id = 0,
        dni = dni,
        name = name,
        surname = surname,
        phoneNumber1 = phoneNumber1,
        phoneNumber2 = phoneNumber2,
        address = address,
        photo = photo,
        FCTDUAL = FCTDUAL,
        email = email,
        department_id = department_id,
        accessToken = ""
    )

}
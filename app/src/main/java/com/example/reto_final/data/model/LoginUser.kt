package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginUser(

    val id: Int,
    var DNI: String,
    var name: String,
    var surname: String,
    var phoneNumber1: Int,
    var phoneNumber2: Int,
    var address: String,
    val photo: String,
    val FCTDUAL: Int,
    val email: String,
    val degrees: Array<Degree>,
    val roles: Array<Rol>,
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
        id = 1,
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
        roles = emptyArray(),
        department_id = department_id,
        token = ""
    )

}
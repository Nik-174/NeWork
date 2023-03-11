package ru.netology.nework.dto

data class UserRequest(
    val id: Int = 0,
    val login: String = "",
    val name: String = "",
    val avatar: String = "",
    var checked: Boolean = false
)
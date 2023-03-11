package ru.netology.nework.dto

data class PostRequest(
    val id: Int,
    val content: String,
    val coords: Coordinates?,
    val link: String?,
    val attachment: Attachment?,
    val mentionIds: List<Int>,
)
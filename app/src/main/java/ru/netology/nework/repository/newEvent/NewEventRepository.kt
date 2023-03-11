package ru.netology.nework.repository.newEvent

import okhttp3.MultipartBody
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.EventRequest
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.enumeration.AttachmentType

interface NewEventRepository {
    suspend fun loadUsers(): List<UserRequest>
    suspend fun addPictureToTheEvent(
        attachmentType: AttachmentType,
        image: MultipartBody.Part
    ): Attachment

    suspend fun addEvent(event: EventRequest)
    suspend fun getEvent(id: Int): EventRequest
    suspend fun getUser(id: Int): UserRequest
}
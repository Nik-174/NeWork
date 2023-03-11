package ru.netology.nework.repository.newPost

import okhttp3.MultipartBody
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.PostRequest
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.enumeration.AttachmentType

interface NewPostRepository {
    suspend fun loadUsers(): List<UserRequest>
    suspend fun addPictureToThePost(
        attachmentType: AttachmentType,
        image: MultipartBody.Part
    ): Attachment

    suspend fun addPost(post: PostRequest)
    suspend fun getPost(id: Int): PostRequest
    suspend fun getUser(id: Int): UserRequest
}
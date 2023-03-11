package ru.netology.nework.repository.newPost

import okhttp3.MultipartBody
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.PostDao
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.PostRequest
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.repository.newPost.NewPostRepository
import java.io.IOException
import javax.inject.Inject


class NewPostRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: PostDao
) : NewPostRepository {

    override suspend fun loadUsers(): List<UserRequest> {
        val usersList: List<UserRequest>
        try {
            val response = apiService.getUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            usersList = response.body() ?: throw ApiError(response.code(), response.message())
            return usersList
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun addPictureToThePost(
        attachmentType: AttachmentType,
        image: MultipartBody.Part
    ): Attachment {

        try {
            val response = apiService.addMultimedia(image)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val mediaResponse =
                response.body() ?: throw ApiError(response.code(), response.message())
            return Attachment(mediaResponse.url, attachmentType)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun addPost(post: PostRequest) {
        try {
            val response = apiService.addPost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                dao.insert(PostEntity.fromDto(body))
            }
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getPost(id: Int): PostRequest {
        try {
            val response = apiService.getPost(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                val body =
                    response.body() ?: throw ApiError(response.code(), response.message())
                return PostRequest(
                    id = body.id,
                    content = body.content,
                    coords = body.coords,
                    link = body.link,
                    attachment = body.attachment,
                    mentionIds = body.mentionIds
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        }

    }

    override suspend fun getUser(id: Int): UserRequest {
        try {
            val response = apiService.getUser(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                return response.body() ?: throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        }
    }

}
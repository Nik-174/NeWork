package ru.netology.nework.repository.user

import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.UserRegistration
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import ru.netology.nework.repository.user.UserRepository

import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth
) : UserRepository {

    override suspend fun onSignIn(userResponse: UserResponse) {
        try {
            val response = apiService.onSignIn(userResponse.login, userResponse.password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authState = response.body() ?: throw ApiError(response.code(), response.message())
            val token = authState.token ?: throw UnknownError
            val idUser = authState.id.toInt()
            val responseUser = apiService.getUser(idUser)
            if (!responseUser.isSuccessful) {
                throw ApiError(responseUser.code(), responseUser.message())
            }
            val avatarUser = responseUser.body()?.avatar
            val name = responseUser.body()?.name
            appAuth.setAuth(authState.id, token, avatarUser, name)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun onSignUp(user: UserRegistration) {
        val login = user.login.toRequestBody()
        val password = user.password.toRequestBody()
        val nameUser = user.name.toRequestBody()
        try {
            val response = if (user.file != null) {
                apiService.onSignUpHasAva(login, password, nameUser, user.file)
            } else {
                apiService.onSignUpNoAva(user.login, user.password, user.name, null)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authState = response.body() ?: throw ApiError(response.code(), response.message())

            val token = authState.token ?: throw UnknownError
            val idUser = authState.id.toInt()
            val responseUser = apiService.getUser(idUser)
            if (!responseUser.isSuccessful) {
                throw ApiError(responseUser.code(), responseUser.message())
            }
            val avatarUser = responseUser.body()?.avatar
            val name = responseUser.body()?.name
            appAuth.setAuth(authState.id, token, avatarUser, name)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
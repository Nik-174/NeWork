package ru.netology.nework.repository.user

import ru.netology.nework.dto.UserRegistration
import ru.netology.nework.dto.UserResponse

interface UserRepository {
    suspend fun onSignIn(userResponse: UserResponse)
    suspend fun onSignUp(user: UserRegistration)
}
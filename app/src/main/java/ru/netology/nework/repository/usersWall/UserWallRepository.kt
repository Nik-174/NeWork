package ru.netology.nework.repository.usersWall


import androidx.lifecycle.MutableLiveData
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.UserRequest

interface UserWallRepository {
    val data: MutableLiveData<List<Job>>
    val userData: MutableLiveData<UserRequest>
    suspend fun getJobUser(id: String)
    suspend fun getUser(id: Int)
}
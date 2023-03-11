package ru.netology.nework.repository.userWall.job

import androidx.lifecycle.MutableLiveData
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.Job
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import java.io.IOException
import javax.inject.Inject

val emptyList = mutableListOf<Job>()

class MyWallJobRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MyWallJobRepository {
    override val dateJob: MutableLiveData<MutableList<Job>> = MutableLiveData(emptyList)

    override suspend fun getMyJob() {
        val usersList: List<Job>
        try {
            val response = apiService.getMyJob()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            usersList = response.body() ?: throw ApiError(response.code(), response.message())
            dateJob.postValue(usersList)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            val response = apiService.deleteJob(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            getMyJob()
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun save(job: Job) {
        try {
            val response = apiService.addJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                response.body() ?: throw ApiError(response.code(), response.message())
                dateJob.value
                getMyJob()
            }
        } catch (e: IOException) {
            throw NetworkError
        }
    }
}
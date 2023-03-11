package ru.netology.nework.repository.userWall.job

import androidx.lifecycle.MutableLiveData
import ru.netology.nework.dto.Job

interface MyWallJobRepository {
    val dateJob: MutableLiveData<MutableList<Job>>
    suspend fun getMyJob()
    suspend fun removeById(id: Int)
    suspend fun save(job: Job)
}
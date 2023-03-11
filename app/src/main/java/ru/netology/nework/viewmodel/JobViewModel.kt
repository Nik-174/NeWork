package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.model.FeedModelState
import ru.netology.nework.repository.userWall.job.MyWallJobRepository
import ru.netology.nework.util.SingleLiveEvent
import javax.inject.Inject

val jobZero = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
    link = null
)

@ExperimentalCoroutinesApi
@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: MyWallJobRepository
) : ViewModel() {
    val data: MutableLiveData<MutableList<Job>> = repository.dateJob

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _editedJob: MutableLiveData<Job> = MutableLiveData(jobZero)
    val editedJob: LiveData<Job>
        get() = _editedJob

    fun getMyJob() {
        viewModelScope.launch {
            try {
                repository.getMyJob()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun editJob(job: Job) {
        _editedJob.value = job
    }

    fun addJob() {
        viewModelScope.launch {
            try {
                val job = _editedJob.value
                job?.let { repository.save(it) }

                _dataState.value = FeedModelState(error = false)
                _editedJob.postValue(jobZero)
                _postCreated.value = Unit
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun addDateStart(date: String) {
        _editedJob.value = _editedJob.value?.copy(start = date)
    }

    fun addDateFinish(date: String) {
        _editedJob.value = _editedJob.value?.copy(finish = date)
    }

    fun deleteEditJob() {
        _editedJob.postValue(jobZero)
    }
}
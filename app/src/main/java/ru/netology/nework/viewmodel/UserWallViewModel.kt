package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.model.FeedModelState
import ru.netology.nework.repository.usersWall.UserWallRepository
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val repository: UserWallRepository
) : ViewModel() {
    val data: MutableLiveData<List<Job>> = repository.data
    val userData: MutableLiveData<UserRequest> = repository.userData
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun getJobUser(id: String) {
        viewModelScope.launch {
            try {
                repository.getJobUser(id)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun getUser(idUser: String) {
        val id = idUser.toInt()
        viewModelScope.launch {
            try {
                repository.getUser(id)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}
package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.EventResponse
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.model.FeedModelState
import ru.netology.nework.repository.event.EventRepository
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repositoryEvent: EventRepository,
    appAuth: AppAuth
) : ViewModel() {

    private var lastAction: ActionType? = null
    private var lastId = 0
    private var errorCounter = 0
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _eventResponse = MutableLiveData<EventResponse>()
    val eventResponse: LiveData<EventResponse>
        get() = _eventResponse

    val dataUserSpeakers: LiveData<List<UserRequest>> = repositoryEvent.dataUsersSpeakers

    val data: Flow<PagingData<EventResponse>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            val cached = repositoryEvent.data.cachedIn(viewModelScope)
            cached.map { pagingData ->
                pagingData.map {
                    it.copy(ownerByMe = it.authorId.toLong() == myId)
                }
            }
        }

    fun loadUsersSpeakers(mentionIds: List<Int>) {
        viewModelScope.launch {
            try {
                repositoryEvent.loadUsersSpeakers(mentionIds)
                _dataState.value = FeedModelState(loading = false)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(loading = true)
            }
        }
    }

    fun removeById(id: Int) {
        lastAction = ActionType.REMOVE
        viewModelScope.launch {
            try {
                repositoryEvent.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun likeById(id: Int) {
        lastAction = ActionType.LIKE
        lastId = id
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repositoryEvent.likeById(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun disLikeById(id: Int) {
        lastAction = ActionType.DISLIKE
        lastId = id
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repositoryEvent.disLikeById(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun participateInEvent(id: Int) {
        lastAction = ActionType.PARTICIPATE
        lastId = id
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repositoryEvent.participateInEvent(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun doNotParticipateInEvent(id: Int) {
        lastAction = ActionType.REFUSE
        lastId = id
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repositoryEvent.doNotParticipateInEvent(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun goToUserParticipateInEvent(users: List<Int>) {
        viewModelScope.launch {
            try {
                repositoryEvent.loadUsersSpeakers(users)
                _dataState.value = FeedModelState(loading = false)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(loading = true)
            }
        }
    }

    fun getEvent(id: Int) {
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repositoryEvent.getEvent(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun retry() {
        if (errorCounter == 3 || errorCounter > 3) {
            _dataState.value = FeedModelState(loading = true)
        } else {
            when (lastAction) {
                ActionType.LIKE -> retryLikeById()
                ActionType.DISLIKE -> retryDisLikeById()
                ActionType.REMOVE -> retryRemove()
                ActionType.PARTICIPATE -> retryParticipateInEvent()
                ActionType.REFUSE -> retryDoNotParticipateInEvent()
                null -> {}
            }
        }
    }

    private fun retryLikeById() {
        likeById(lastId)
        errorCounter++
    }

    private fun retryDisLikeById() {
        disLikeById(lastId)
        errorCounter++
    }

    private fun retryRemove() {
        removeById(lastId)
        errorCounter++
    }

    private fun retryParticipateInEvent() {
        participateInEvent(lastId)
        errorCounter++
    }

    private fun retryDoNotParticipateInEvent() {
        doNotParticipateInEvent(lastId)
        errorCounter++
    }
}

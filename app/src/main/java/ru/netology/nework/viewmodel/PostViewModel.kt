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
import ru.netology.nework.dto.PostResponse
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.model.FeedModelState
import ru.netology.nework.repository.post.PostRepository
import ru.netology.nework.viewmodel.ActionType.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repositoryPost: PostRepository,
    appAuth: AppAuth
) : ViewModel() {

    private var lastAction: ActionType? = null
    private var lastId = 0
    private var errorCounter = 0
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _postResponse = MutableLiveData<PostResponse>()
    val postResponse: LiveData<PostResponse>
        get() = _postResponse

    val dataUserMentions: LiveData<List<UserRequest>> = repositoryPost.dataUsersMentions
    val data: Flow<PagingData<PostResponse>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            val cached = repositoryPost.data.cachedIn(viewModelScope)
            cached.map { pagingData ->
                pagingData.map {
                    it.copy(ownerByMe = it.authorId.toLong() == myId)
                }
            }
        }

    fun loadUsersMentions(mentionIds: List<Int>) {
        viewModelScope.launch {
            try {
                repositoryPost.loadUsersMentions(mentionIds)
                _dataState.value = FeedModelState(loading = false)
                errorCounter = 0
            } catch (e: Exception) {
                _dataState.value = FeedModelState(loading = true)
            }
        }
    }

    fun removeById(id: Int) {
        lastAction = REMOVE
        viewModelScope.launch {
            try {
                repositoryPost.removeById(id)
                _dataState.value = FeedModelState()
                errorCounter = 0
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun likeById(id: Int) {
        lastAction = LIKE
        lastId = id
        viewModelScope.launch {
            try {
                _postResponse.postValue(repositoryPost.likeById(id))
                _dataState.value = FeedModelState()
                errorCounter = 0
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun disLikeById(id: Int) {
        lastAction = DISLIKE
        lastId = id
        viewModelScope.launch {
            try {
                _postResponse.postValue(repositoryPost.disLikeById(id))
                _dataState.value = FeedModelState()
                errorCounter = 0
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
                LIKE -> retryLikeById()
                DISLIKE -> retryDisLikeById()
                REMOVE -> retryRemove()
                PARTICIPATE -> {}
                REFUSE -> {}
                null -> {}
            }
        }
    }

    fun getPost(id: Int) {
        viewModelScope.launch {
            try {
                _postResponse.postValue(repositoryPost.getPost(id))
                _dataState.value = FeedModelState(refreshing = false)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(loading = true)
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
}


enum class ActionType {
    LIKE,
    DISLIKE,
    REMOVE,
    PARTICIPATE,
    REFUSE
}

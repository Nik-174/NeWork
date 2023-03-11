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
import ru.netology.nework.repository.userWall.post.MyWallPostRepository
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MyWallPostViewModel @Inject constructor(
    private val repositoryPost: MyWallPostRepository,
    appAuth: AppAuth
) : ViewModel() {
    var user = UserRequest()
    private var lastAction: ActionType? = null
    private var lastId = 0
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

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
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Int) {
        lastAction = ActionType.REMOVE
        viewModelScope.launch {
            try {
                repositoryPost.removeById(id)
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
                repositoryPost.likeById(id)
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
                repositoryPost.disLikeById(id)


            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeAll() {
        viewModelScope.launch {
            try {
                repositoryPost.removeAll()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}




package ru.netology.nework.repository.post

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.PostResponse
import ru.netology.nework.dto.UserRequest

interface PostRepository {
    val data: Flow<PagingData<PostResponse>>
    val dataUsersMentions: MutableLiveData<List<UserRequest>>
    suspend fun loadUsersMentions(list: List<Int>)
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int): PostResponse
    suspend fun disLikeById(id: Int): PostResponse
    suspend fun getPost(id: Int): PostResponse
}
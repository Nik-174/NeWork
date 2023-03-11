package ru.netology.nework.repository.userWall.post


import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.MyWallPostDao
import ru.netology.nework.dto.PostResponse
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import java.io.IOException
import javax.inject.Inject

val emptyList = listOf<UserRequest>()

@OptIn(ExperimentalPagingApi::class)
class MyWallPostRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    mediator: MyWallPostRemoteMediator,
    private val dao: MyWallPostDao,
    private val daoPost: PostDao
) : MyWallPostRepository {

    override val data: Flow<PagingData<PostResponse>> =
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { dao.getAll() },
            remoteMediator = mediator
        ).flow.map {
            it.map(PostEntity::toDto)
        }

    override val dataUsersMentions: MutableLiveData<List<UserRequest>> =
        MutableLiveData(emptyList)


    override suspend fun loadUsersMentions(list: List<Int>) {
        val usersList = mutableListOf<UserRequest>()
        try {
            list.forEach {
                val response = apiService.getUser(it)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                usersList.add(
                    response.body() ?: throw ApiError(
                        response.code(),
                        response.message()
                    )
                )
            }
            dataUsersMentions.postValue(usersList)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
            daoPost.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun likeById(id: Int) {
        try {
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
            daoPost.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun disLikeById(id: Int) {
        try {
            val response = apiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
            daoPost.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeAll() {
        dao.removeAll()
    }
}
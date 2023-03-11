package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import ru.netology.nework.dto.Coordinates
import ru.netology.nework.dto.EventRequest
import ru.netology.nework.dto.UserRequest
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.enumeration.EventType
import ru.netology.nework.model.FeedModelState
import ru.netology.nework.repository.newEvent.NewEventRepository
import ru.netology.nework.util.SingleLiveEvent
import javax.inject.Inject
import kotlin.math.roundToInt

val editedEvent = EventRequest(
    id = 0,
    content = "",
    datetime = null,
    coords = null,
    type = EventType.OFFLINE,
    attachment = null,
    link = null,
    speakerIds = listOf()
)
val speakers = mutableListOf<UserRequest>()

@ExperimentalCoroutinesApi
@HiltViewModel
class NewEventViewModel @Inject constructor(
    private val repository: NewEventRepository
) : ViewModel() {

    var inJob = false

    val newEvent: MutableLiveData<EventRequest> = MutableLiveData(editedEvent)

    val data: MutableLiveData<List<UserRequest>> = MutableLiveData()

    val speakersLive: MutableLiveData<MutableList<UserRequest>> = MutableLiveData()

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun getEvent(id: Int) {
        speakersLive.postValue(speakers)
        viewModelScope.launch {
            try {
                newEvent.value = repository.getEvent(id)
                newEvent.value?.speakerIds?.forEach {
                    speakersLive.value!!.add(repository.getUser(it))
                }
                _dataState.value = FeedModelState(error = false)
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun getUsers() {
        speakersLive.postValue(speakers)
        viewModelScope.launch {
            try {
                data.value = repository.loadUsers()
                data.value?.forEach { user ->
                    newEvent.value?.speakerIds?.forEach {
                        if (user.id == it) {
                            user.checked = true
                        }
                    }
                }
                _dataState.value = FeedModelState(error = false)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun addSpeakersIds() {
        speakersLive.postValue(speakers)
        val listChecked = mutableListOf<Int>()
        val speakersUserList = mutableListOf<UserRequest>()
        data.value?.forEach { user ->
            if (user.checked) {
                listChecked.add(user.id)
                speakersUserList.add(user)
            }
        }
        speakersLive.postValue(speakersUserList)
        newEvent.value = newEvent.value?.copy(speakerIds = listChecked)
    }

    fun isChecked(id: Int) {
        data.value?.forEach {
            if (it.id == id) {
                it.checked = true
            }
        }
    }

    fun unChecked(id: Int) {
        data.value?.forEach {
            if (it.id == id) {
                it.checked = false
            }
        }
    }

    fun addPost(content: String) {
        newEvent.value = newEvent.value?.copy(content = content)
        val event = newEvent.value!!
        println(event)
        viewModelScope.launch {
            try {
                repository.addEvent(event)
                _dataState.value = FeedModelState(error = false)
                _postCreated.value = Unit
                deleteEditPost()
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun addCoords(point: Point) {
        val coordinates = Coordinates(
            ((point.latitude * 1000000.0).roundToInt() / 1000000.0).toString(),
            ((point.longitude * 1000000.0).roundToInt() / 1000000.0).toString()
        )
        newEvent.value = newEvent.value?.copy(coords = coordinates)
        inJob = false
    }

    fun addLink(link: String) {
        if (link != "") {
            newEvent.value = newEvent.value?.copy(link = link)
        } else {
            newEvent.value = newEvent.value?.copy(link = null)
        }
    }

    fun addPictureToThePost(image: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val attachment = repository.addPictureToTheEvent(AttachmentType.IMAGE, image)
                newEvent.value = newEvent.value?.copy(attachment = attachment)
                _dataState.value = FeedModelState(error = false)
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun addDateTime(dateTime: String) {
        newEvent.value = newEvent.value?.copy(datetime = dateTime)
    }

    fun addTypeEvent() {
        val type = when (newEvent.value?.type) {
            EventType.OFFLINE -> EventType.ONLINE
            else -> EventType.OFFLINE
        }
        newEvent.value = newEvent.value?.copy(type = type)
    }

    fun deletePicture() {
        newEvent.value = newEvent.value?.copy(attachment = null)
    }


    fun deleteEditPost() {
        newEvent.postValue(editedEvent)
        speakers.clear()
        speakersLive.postValue(speakers)
    }
}
package ru.netology.nework.auth

import android.content.SharedPreferences
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppAuth @Inject constructor(
    private val prefs: SharedPreferences
) {
    companion object {
        const val idKey = "id"
        const val tokenKey = "token"
        const val avatar = "avatarUser"
        const val name = "nameUser"
    }


    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = prefs.getLong(idKey, 0)
        val token = prefs.getString(tokenKey, null)
        val avatar = prefs.getString(avatar, null)
        val name = prefs.getString(name, null)

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token, avatar, name))
        }
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String, avatarUser: String?, nameUser: String?) {
        _authStateFlow.value = AuthState(id, token, avatarUser, nameUser)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            putString(avatar, avatarUser)
            putString(name, nameUser)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }
}

data class AuthState(
    val id: Long = 0, val token: String? = null,
    val avatarUser: String? = null, val nameUser: String? = null
)

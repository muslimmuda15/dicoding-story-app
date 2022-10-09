package com.rachmad.training.dicodingstoryapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.LoginData
import com.rachmad.training.dicodingstoryapp.repository.StoryRepository
import com.rachmad.training.dicodingstoryapp.sql.access.LoginAccess
import kotlinx.coroutines.flow.first

class MainViewModel: ViewModel() {
    val loginAccess = LoginAccess()
    fun getUserLiveData(): LiveData<LoginData?> = loginAccess.getAccountData()
    val getToken: String? = loginAccess.getToken()
    fun logout() = loginAccess.deleteLogin()

    private val storyRepository = StoryRepository()

    fun stories(token: String, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit) = storyRepository.stories(token, success, error, failure)
}
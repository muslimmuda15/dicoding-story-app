package com.rachmad.training.dicodingstoryapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.LoginData
import com.rachmad.training.dicodingstoryapp.repository.StoryRepository
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import kotlinx.coroutines.flow.first

class MainViewModel(private val pref: UserPreference): ViewModel() {
    fun getUserLiveData(): LiveData<LoginData> = pref.getUser().asLiveData()
    suspend fun getUser(): LoginData = pref.getUser().first()
    suspend fun logout() = pref.logout()

    private val storyRepository = StoryRepository()

    fun stories(token: String, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit) = storyRepository.stories(token, success, error, failure)
}
package com.rachmad.training.dicodingstoryapp.ui.story.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.CreateStoryRequestData
import com.rachmad.training.dicodingstoryapp.model.LoginData
import com.rachmad.training.dicodingstoryapp.repository.StoryRepository
import com.rachmad.training.dicodingstoryapp.sql.access.LoginAccess

class NewStoryViewModel: ViewModel() {
    private val loginAccess = LoginAccess()
    private val storyRepository = StoryRepository()

    fun getUserLiveData(): LiveData<LoginData?> = loginAccess.getAccountData()
    fun addStory(
        token: String?,
        requestData: CreateStoryRequestData,
        success: (BaseResponseData?) -> Unit,
        error: (BaseResponseData?) -> Unit,
        failure: (Throwable?) -> Unit
    ) = storyRepository.addStory(
        token,
        requestData,
        success,
        error,
        failure
    )
}
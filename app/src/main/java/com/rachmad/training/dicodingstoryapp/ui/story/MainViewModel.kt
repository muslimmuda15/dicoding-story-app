package com.rachmad.training.dicodingstoryapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.LoginData
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.repository.StoryRepository
import com.rachmad.training.dicodingstoryapp.sql.access.LoginAccess
import kotlinx.coroutines.flow.first

class MainViewModel: ViewModel() {
    val loginAccess = LoginAccess()
    fun getUserLiveData(): LiveData<LoginData?> = loginAccess.getAccountData()
    val getToken: String? = loginAccess.getToken()
    fun logout() = loginAccess.deleteLogin()

    private val storyRepository = StoryRepository()

    fun stories(): LiveData<PagingData<StoryData>> = storyRepository.stories().cachedIn(viewModelScope)
}
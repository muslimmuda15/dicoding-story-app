package com.rachmad.training.dicodingstoryapp.ui.story.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rachmad.training.dicodingstoryapp.model.LoginData
import com.rachmad.training.dicodingstoryapp.repository.UserPreference

class NewStoryViewModel(private val pref: UserPreference): ViewModel() {
    fun getUserLiveData(): LiveData<LoginData> = pref.getUser().asLiveData()
}
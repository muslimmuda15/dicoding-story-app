package com.rachmad.training.dicodingstoryapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import com.rachmad.training.dicodingstoryapp.ui.login.LoginViewModel
import com.rachmad.training.dicodingstoryapp.ui.register.RegisterViewModel
import com.rachmad.training.dicodingstoryapp.ui.story.MainViewModel

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
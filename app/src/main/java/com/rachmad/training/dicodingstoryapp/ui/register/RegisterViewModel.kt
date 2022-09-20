package com.rachmad.training.dicodingstoryapp.ui.register

import androidx.lifecycle.ViewModel
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.LoginRequestData
import com.rachmad.training.dicodingstoryapp.repository.AuthRepository
import com.rachmad.training.dicodingstoryapp.repository.UserPreference

class RegisterViewModel(private val pref: UserPreference): ViewModel() {
    private val loginRepository = AuthRepository()
    fun register(user: LoginRequestData, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit) = loginRepository.register(user, success, error, failure)
}
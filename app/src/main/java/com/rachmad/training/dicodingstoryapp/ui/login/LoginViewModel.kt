package com.rachmad.training.dicodingstoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rachmad.training.dicodingstoryapp.model.*
import com.rachmad.training.dicodingstoryapp.repository.AuthRepository
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference): ViewModel() {
    private val loginRepository = AuthRepository()
    fun getUser(): LiveData<LoginData> = pref.getUser().asLiveData()
    fun saveUser(user: LoginData){
        viewModelScope.launch {
            pref.login(user)
        }
    }

    fun login(user: LoginRequestData, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit) = loginRepository.login(user, success, error, failure)
}
package com.rachmad.training.dicodingstoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rachmad.training.dicodingstoryapp.model.*
import com.rachmad.training.dicodingstoryapp.repository.AuthRepository
import com.rachmad.training.dicodingstoryapp.sql.access.LoginAccess
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val loginRepository = AuthRepository()
    private val loginAccess = LoginAccess()
    fun getUser(): LiveData<LoginData?> = loginAccess.getAccountData()
    fun saveUser(user: LoginData) = loginAccess.insertLogin(user)

    fun login(user: LoginRequestData, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit) = loginRepository.login(user, success, error, failure)
}
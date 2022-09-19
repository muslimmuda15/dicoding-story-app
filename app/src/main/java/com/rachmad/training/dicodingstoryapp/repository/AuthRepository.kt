package com.rachmad.training.dicodingstoryapp.repository

import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.LoginRequestData
import com.rachmad.training.dicodingstoryapp.util.errorResponseData
import com.rachmad.training.dicodingstoryapp.webservice.EndPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthRepository {
    @Inject lateinit var client: EndPoint
    init {
        App.appComponent.inject(this)
    }

    fun login(user: LoginRequestData, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit){
        val call = client.login(user)
        call.enqueue(object: Callback<BaseResponseData> {
            override fun onResponse(
                call: Call<BaseResponseData>,
                response: Response<BaseResponseData>
            ) {
                if(response.errorBody() == null)
                    success(response.body())
                else
                    error(errorResponseData(response.errorBody()))
            }

            override fun onFailure(call: Call<BaseResponseData>, t: Throwable) {
                failure(t)
            }
        })
    }

    fun register(user: LoginRequestData, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit){
        val call = client.register(user)
        call.enqueue(object: Callback<BaseResponseData> {
            override fun onResponse(
                call: Call<BaseResponseData>,
                response: Response<BaseResponseData>
            ) {
                if(response.errorBody() == null)
                    success(response.body())
                else
                    error(errorResponseData(response.errorBody()))
            }

            override fun onFailure(call: Call<BaseResponseData>, t: Throwable) {
                failure(t)
            }
        })
    }
}
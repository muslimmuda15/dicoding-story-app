package com.rachmad.training.dicodingstoryapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.CreateStoryRequestData
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.sql.ApiDatabaseMediator
import com.rachmad.training.dicodingstoryapp.sql.StoryDatabase
import com.rachmad.training.dicodingstoryapp.util.errorResponseData
import com.rachmad.training.dicodingstoryapp.webservice.EndPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class StoryRepository {
    @Inject lateinit var database: StoryDatabase
    @Inject lateinit var client: EndPoint
    init {
        App.appComponent.inject(this)
    }

    fun stories(): LiveData<PagingData<StoryData>>? {
        database.loginQuery().getAccountId()?.let { id ->
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(pageSize = 20),
                remoteMediator = ApiDatabaseMediator(),
                pagingSourceFactory = {
                    database.storyQuery().getListData(id)
                }
            ).liveData
        } ?: run {
            return null
        }
    }

    fun addStory(token: String?, requestData: CreateStoryRequestData, success: (BaseResponseData?) -> Unit, error: (BaseResponseData?) -> Unit, failure: (Throwable?) -> Unit) {
        val image = requestData.photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val description = requestData.description
        val latitude = requestData.lat
        val longitude = requestData.lon

        val call = if (token == null)
            client.addGuestStories(image, description, latitude, longitude)
        else
            client.addStories(token = "Bearer $token",image, description, latitude, longitude)

        call.enqueue(object : Callback<BaseResponseData> {
            override fun onResponse(
                call: Call<BaseResponseData>,
                response: Response<BaseResponseData>
            ) {
                if (response.errorBody() == null)
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
package com.rachmad.training.dicodingstoryapp.sql

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.util.errorResponseData
import com.rachmad.training.dicodingstoryapp.webservice.EndPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//@OptIn(ExperimentalPagingApi::class)
//class ApiDatabaseMediator(
//    private val database: StoryDatabase,
//    private val api: EndPoint
//): RemoteMediator<Int, ApiDatabaseMediator>() {
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, ApiDatabaseMediator>
//    ): MediatorResult {
//        val token = database.loginQuery().getToken()
//        val page = INIT_PAGE
//        val responseData = api.getAllStories(
//            token = token,
//            location = 1,
//            page = page,
//            size = state.config.pageSize
//        )
//        responseData.enqueue(object: Callback<BaseResponseData> {
//            var endPaging = false
//            override fun onResponse(
//                call: Call<BaseResponseData>,
//                response: Response<BaseResponseData>
//            ) {
//                if(response.errorBody() == null)
//                     endPaging = (response.body()?.listStory?.size ?: 0) < state.config.pageSize
//                else
//                    error(errorResponseData(response.errorBody()))
//            }
//
//            override fun onFailure(call: Call<BaseResponseData>, t: Throwable) {
//                failure(t)
//            }
//        })
//    }
//
//    override suspend fun initialize(): InitializeAction {
//        return InitializeAction.LAUNCH_INITIAL_REFRESH
//    }
//
//    companion object {
//        const val INIT_PAGE = 1
//    }
//}
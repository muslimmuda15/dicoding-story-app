package com.rachmad.training.dicodingstoryapp.sql

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.model.RemoteKeys
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.webservice.EndPoint
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ApiDatabaseMediator: RemoteMediator<Int, StoryData>() {
    @Inject lateinit var database: StoryDatabase
    @Inject lateinit var api: EndPoint

    init {
        App.appComponent.inject(this)
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryData>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INIT_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try{
            var endPagination = true

            database.loginQuery().getAccount()?.let { account ->
                val responseData = api.getAllStories(
                    token = "Bearer ${account.token}",
                    location = 1,
                    page = page,
                    size = state.config.pageSize
                )

                endPagination = responseData?.listStory?.isEmpty() ?: true
                database.withTransaction {
                    responseData?.listStory?.let {
                        if (loadType == LoadType.REFRESH) {
                            database.remoteKeyQuery().deleteRemoteKeys()
                            database.storyQuery().deleteAll()
                        }
                        val prevKey = if (page == 1) null else page - 1
                        val nextKey = if (endPagination) null else page + 1
                        val keys = it.map {
                            it.accountId = account.userId
                            RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                        }
                        database.remoteKeyQuery().insertAll(keys)
                        database.storyQuery().insertAllStory(it)
                    }
                }
            } ?: run {
//                endPagination = true
                return MediatorResult.Error(Throwable("No token"))
            }

            return MediatorResult.Success(endOfPaginationReached = endPagination)
        } catch (e: Exception){
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryData>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeyQuery().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryData>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeyQuery().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryData>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeyQuery().getRemoteKeysId(id)
            }
        }
    }

    companion object {
        const val INIT_PAGE = 1
    }
}
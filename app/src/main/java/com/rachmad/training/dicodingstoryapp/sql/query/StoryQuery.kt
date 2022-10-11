package com.rachmad.training.dicodingstoryapp.sql.query

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.rachmad.training.dicodingstoryapp.model.StoryData

@Dao
interface StoryQuery {
    @Query("select * from story")
    fun getListData(): PagingSource<Int, StoryData>

    @Insert(onConflict = REPLACE)
    fun insertAllStory(stories: List<StoryData>)

    @Query("delete from story")
    fun deleteAll()
}
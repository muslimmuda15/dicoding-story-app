package com.rachmad.training.dicodingstoryapp.sql.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.rachmad.training.dicodingstoryapp.model.StoryData

@Dao
interface StoryQuery {
    @Query("select * from story limit :size offset :page")
    fun getListData(size: Int, page: Int): LiveData<List<StoryData>>

    @Insert(onConflict = REPLACE)
    fun insertAllStory(stories: List<StoryData>)

    @Query("delete from story")
    fun deleteAll()
}
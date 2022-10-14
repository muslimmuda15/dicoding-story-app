package com.rachmad.training.dicodingstoryapp.sql.query

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rachmad.training.dicodingstoryapp.model.RemoteKeys

@Dao
interface RemoteKeyQuery {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("select * from remote_keys where id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeys?

    @Query("delete from remote_keys")
    suspend fun deleteRemoteKeys()
}
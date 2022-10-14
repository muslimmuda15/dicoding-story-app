package com.rachmad.training.dicodingstoryapp.sql.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.rachmad.training.dicodingstoryapp.model.LoginData

@Dao
interface LoginQuery {
    @Insert(onConflict = REPLACE)
    fun insertData(loginData: LoginData)

    @Query("select * from account limit 1")
    fun getAccountData(): LiveData<LoginData?>

    @Query("select * from account limit 1")
    fun getAccount(): LoginData?

    @Query("select token from account limit 1")
    fun getToken(): String?

    @Query("select user_id from account limit 1")
    fun getAccountId(): String?

    @Query("delete from account")
    fun deleteData()
}
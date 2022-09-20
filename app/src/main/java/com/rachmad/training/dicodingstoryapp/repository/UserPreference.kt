package com.rachmad.training.dicodingstoryapp.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rachmad.training.dicodingstoryapp.model.LoginData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    fun getUser(): Flow<LoginData> = dataStore.data.map { preferences ->
        LoginData(
            preferences[ID_KEY],
            preferences[NAME_KEY],
            preferences[TOKEN_KEY],
        )
    }

    suspend fun login(user: LoginData) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.userId ?: ""
            preferences[NAME_KEY] = user.name ?: ""
            preferences[TOKEN_KEY] = user.token ?: ""
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = ""
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        private val ID_KEY = stringPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        private var instance: UserPreference? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreference = instance ?: synchronized(this) {
            val _instance = UserPreference(dataStore)
            instance = _instance
            _instance
        }
    }
}
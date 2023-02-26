package com.android_a865.appblocker.common

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PreferencesManager2 @Inject constructor(
    @ApplicationContext private val context: Context
) {


    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "AppBlockerPref"
    )

    val preferencesFlow = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("PreferencesManager", "Error reading the preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }

        }
        .map { preferences ->

        }

    suspend fun updateDateFormat(dateFormat: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DATE_FORMAT] = dateFormat
        }
    }

    suspend fun getSomething(dateFormat: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DATE_FORMAT] = dateFormat
        }
    }


    private object PreferencesKeys {
        val COMPANY_INFO = stringPreferencesKey("company_info")
        val DATE_FORMAT = stringPreferencesKey("date_format")
        val CURRENCY = stringPreferencesKey("currency")
        val IS_FIRST = booleanPreferencesKey("is_first")
        val IS_SUBSCRIBED = booleanPreferencesKey("is_subscribed")
    }
}




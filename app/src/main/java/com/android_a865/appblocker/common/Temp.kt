package com.android_a865.appblocker.common

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/*
@Singleton
class PreferencesManager2 @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")

    val preferencesFlow = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("PreferencesManager", "Error reading the preferences", exception)
                emit(emptyPreferences())
            }else {
                throw exception
            }

        }
        .map { preferences ->
            AppSettings(
                company = preferences[PreferencesKeys.COMPANY_INFO]?.toObject() ?: Company(),
                dateFormat = preferences[PreferencesKeys.DATE_FORMAT] ?: DATE_FORMATS[0],
                currency = preferences[PreferencesKeys.CURRENCY] ?: "",
                isFirst = preferences[PreferencesKeys.IS_FIRST] ?: true,
                isSubscribed = preferences[PreferencesKeys.IS_SUBSCRIBED] ?: false
            )
        }


    suspend fun updateCompanyInfo(company: Company) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COMPANY_INFO] = company.toJson()
        }
    }

    suspend fun updateDateFormat(dateFormat: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DATE_FORMAT] = dateFormat
        }
    }

    suspend fun updateCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY] = currency
        }
    }

    suspend fun updateIsFirst(isFirst: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST] = isFirst
        }
    }

    suspend fun updateIsSubscribed(isSubscribed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_SUBSCRIBED] = isSubscribed
        }
    }


    private object PreferencesKeys {
        val COMPANY_INFO = stringPreferencesKey("company_info")
        val DATE_FORMAT = stringPreferencesKey("date_format")
        val CURRENCY = stringPreferencesKey("currency")
        val IS_FIRST = booleanPreferencesKey("is_first")
        val IS_SUBSCRIBED = booleanPreferencesKey("is_subscribed")
    }
}*/

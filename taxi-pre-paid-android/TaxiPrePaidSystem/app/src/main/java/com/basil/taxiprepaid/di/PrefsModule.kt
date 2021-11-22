package com.basil.taxiprepaid.di

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.basil.taxiprepaid.data.local.PreferenceStorage
import com.basil.taxiprepaid.data.local.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PrefsModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPreferenceStorage.PREFS_NAME, Activity.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePrefsStore(preferences: SharedPreferences): PreferenceStorage {
        return SharedPreferenceStorage(preferences!!)
    }
}
package com.basil.taxiprepaid.di


import com.basil.taxiprepaid.data.local.PreferenceStorage
import com.basil.taxiprepaid.data.remote.ApiService
import com.basil.taxiprepaid.data.remote.MapService
import com.basil.taxiprepaid.data.repository.booth.BoothRepository
import com.basil.taxiprepaid.data.repository.booth.BoothRepositoryImp
import com.basil.taxiprepaid.data.repository.driver.DriverRepository
import com.basil.taxiprepaid.data.repository.driver.DriverRepositoryImp
import com.basil.taxiprepaid.data.repository.login.LoginRepository
import com.basil.taxiprepaid.data.repository.login.LoginRepositoryImp
import com.google.gson.Gson
import com.roshnee.data.repository.PreferenceRepository
import com.roshnee.data.repository.PreferenceRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun providePrefsRepository(preferenceStorage: PreferenceStorage): PreferenceRepository {
        return PreferenceRepositoryImp(preferenceStorage)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(apiService: ApiService): LoginRepository {
        return LoginRepositoryImp(apiService)
    }

    @Provides
    @Singleton
    fun provideBoothRepository(apiService: ApiService, mapService: MapService): BoothRepository {
        return BoothRepositoryImp(apiService, mapService)
    }

    @Provides
    @Singleton
    fun provideDriverRepository(apiService: ApiService): DriverRepository {
        return DriverRepositoryImp(apiService)
    }
}
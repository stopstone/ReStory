package com.cyber.restory.di

import com.cyber.restory.data.api.ApiClient
import com.cyber.restory.data.repository.CityRepositoryImpl
import com.cyber.restory.data.repository.FilterRepositoryImpl
import com.cyber.restory.data.repository.PostRepositoryImpl
import com.cyber.restory.domain.repository.CityRepository
import com.cyber.restory.domain.repository.FilterRepository
import com.cyber.restory.domain.repository.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providePostRepository(postApi: ApiClient): PostRepository {
        return PostRepositoryImpl(postApi)
    }

    @Provides
    @Singleton
    fun provideFilterRepository(filterApi: ApiClient): FilterRepository {
        return FilterRepositoryImpl(filterApi)
    }

    @Provides
    @Singleton
    fun provideCityRepository(apiService: ApiClient): CityRepository {
        return CityRepositoryImpl(apiService)
    }
}
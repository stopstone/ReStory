package com.cyber.restory.di

import com.cyber.restory.data.api.ApiClient
import com.cyber.restory.data.api.TourApiService
import com.cyber.restory.data.db.RecentSearchDao
import com.cyber.restory.data.repository.CityRepositoryImpl
import com.cyber.restory.data.repository.FilterRepositoryImpl
import com.cyber.restory.data.repository.PostRepositoryImpl
import com.cyber.restory.data.repository.SearchRepositoryImpl
import com.cyber.restory.data.repository.TourRepositoryImpl
import com.cyber.restory.domain.repository.CityRepository
import com.cyber.restory.domain.repository.FilterRepository
import com.cyber.restory.domain.repository.PostRepository
import com.cyber.restory.domain.repository.SearchRepository
import com.cyber.restory.domain.repository.TourRepository
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

    @Provides
    @Singleton
    fun provideTourRepository(apiService: TourApiService): TourRepository {
        return TourRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(dao: RecentSearchDao): SearchRepository {
        return SearchRepositoryImpl(dao)
    }
}
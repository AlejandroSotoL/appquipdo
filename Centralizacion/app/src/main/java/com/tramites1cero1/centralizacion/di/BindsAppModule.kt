package com.tramites1cero1.centralizacion.di

import com.tramites1cero1.centralizacion.data.network.NetworkProvider
import com.tramites1cero1.centralizacion.data.network.NewsApiService
import com.tramites1cero1.centralizacion.data.network.RetrofitClient
import com.tramites1cero1.centralizacion.data.repository.NewsRepositoryImpl
import com.tramites1cero1.centralizacion.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsAppModule {

    //BINDS
    @Binds
    @Singleton
    abstract fun bindNewsRepo(NewsRepositoryImpl: NewsRepositoryImpl): NewsRepository

    companion object {
        @Provides
        @Singleton
        fun provideNewsApiService(): NewsApiService {
            return RetrofitClient.createService(
                "https://default.url.com/",//esta url se reemplaza en NewsApiService por la URL base del municipio
                NewsApiService::class.java
            )
        }
    }

}
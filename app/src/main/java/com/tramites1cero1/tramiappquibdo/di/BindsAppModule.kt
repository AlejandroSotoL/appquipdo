package com.tramites1cero1.tramiappquibdo.di

import com.tramites1cero1.tramiappquibdo.data.network.NewsApiService
import com.tramites1cero1.tramiappquibdo.data.network.RetrofitClient
import com.tramites1cero1.tramiappquibdo.data.repository.NewsRepositoryImpl
import com.tramites1cero1.tramiappquibdo.domain.repository.NewsRepository
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
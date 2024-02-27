package org.supla.launcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.supla.launcher.BuildConfig
import org.supla.launcher.data.source.network.DownloadUpdateApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideDownloadUpdateApi(retrofit: Retrofit): DownloadUpdateApi =
        retrofit.create(DownloadUpdateApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.UPDATE_SERVER)
            .build()
}
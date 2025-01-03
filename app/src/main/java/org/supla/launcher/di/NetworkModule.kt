package org.supla.launcher.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.supla.launcher.data.source.network.DownloadUpdateApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

private const val GITHUB_API = "https://api.github.com"
private const val RETROFIT_GITHUB = "github"
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideDownloadUpdateApi(@Named(RETROFIT_GITHUB) retrofit: Retrofit): DownloadUpdateApi =
        retrofit.create(DownloadUpdateApi::class.java)

    @Provides
    @Singleton
    @Named(RETROFIT_GITHUB)
    fun provideGithubRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(GITHUB_API)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
}
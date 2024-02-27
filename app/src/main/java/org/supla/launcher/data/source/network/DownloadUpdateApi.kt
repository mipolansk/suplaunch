package org.supla.launcher.data.source.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface DownloadUpdateApi {

    @Streaming
    @GET("supla/update/{version}/Suplaunch.apk")
    suspend fun downloadUpdate(@Path("version") version: String): ResponseBody
}
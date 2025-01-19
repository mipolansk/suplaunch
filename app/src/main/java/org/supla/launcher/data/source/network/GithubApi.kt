package org.supla.launcher.data.source.network

import okhttp3.ResponseBody
import org.supla.launcher.data.source.network.dto.ReleaseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GithubApi {

    @GET("repos/{project}/{repository}/releases/latest")
    suspend fun latestRelease(@Path("project") project: String, @Path("repository") repository: String): ReleaseDto

    @Streaming
    @GET
    suspend fun apkFile(@Url fileUrl: String): Response<ResponseBody>
}
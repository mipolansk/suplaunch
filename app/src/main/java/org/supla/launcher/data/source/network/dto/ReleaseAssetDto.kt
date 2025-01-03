package org.supla.launcher.data.source.network.dto

import com.google.gson.annotations.SerializedName

data class ReleaseAssetDto(
  val id: Long,
  val name: String,
  @SerializedName("browser_download_url") val browserDownloadUrl: String
)
package org.supla.launcher.data.source.network.dto

data class ReleaseDto(
  val tagName: String,
  val name: String,
  val assets: List<ReleaseAssetDto>
)

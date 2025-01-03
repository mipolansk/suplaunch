package org.supla.launcher.data.model

data class Version(
  val major: Int,
  val minor: Int,
  val patch: Int
) {

  operator fun compareTo(other: Version): Int {
    if (major > other.major) {
      return 1
    } else if (major < other.major) {
      return -1
    }

    if (minor > other.minor) {
      return 1
    } else if (minor < other.minor) {
      return -1
    }

    if (patch > other.patch) {
      return 1
    } else if (patch < other.patch) {
      return -1
    }

    return 0
  }

  override fun toString(): String {
    return "v$major.$minor.$patch"
  }
  companion object {
    fun parse(version: String): Version? {
      if (version.isEmpty()) {
        return null
      }

      val parts = version.split('.')

      val major = parts.getOrNull(0)?.let { major(it) } ?: 0
      val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
      val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

      return Version(major, minor, patch)
    }

    private fun major(major: String): Int {
      val numberString = if (major.startsWith("v")) {
        major.substring(1)
      } else {
        major
      }

      return numberString.toIntOrNull() ?: 0
    }
  }
}
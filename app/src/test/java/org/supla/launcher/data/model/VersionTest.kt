package org.supla.launcher.data.model

import org.assertj.core.api.Assertions
import org.junit.Test


class VersionTest {

  @Test
  fun `should compare versions with major part different`() {
    // given
    val version1 = Version(1, 0 ,0)
    val version2 = Version(2, 0, 0)

    // when
    val result = version2 > version1

    // then
    Assertions.assertThat(result).isTrue()
  }

  @Test
  fun `should compare versions with minor part different`() {
    // given
    val version1 = Version(1, 1 ,0)
    val version2 = Version(1, 2, 0)

    // when
    val result = version2 > version1

    // then
    Assertions.assertThat(result).isTrue()
  }

  @Test
  fun `should compare versions with patch part different`() {
    // given
    val version1 = Version(1, 1 ,1)
    val version2 = Version(1, 1, 2)

    // when
    val result = version2 > version1

    // then
    Assertions.assertThat(result).isTrue()
  }

  @Test
  fun `should assert same version`() {
    // given
    val version1 = Version(1, 1 ,1)
    val version2 = Version(1, 1, 1)

    // when
    val higher = version2 > version1
    val lower = version2 < version1
    val equal = version1 == version2

    // then
    Assertions.assertThat(higher).isFalse()
    Assertions.assertThat(lower).isFalse()
    Assertions.assertThat(equal).isTrue()
  }

  @Test
  fun `should parse with v`() {
    // when
    val version = Version.parse("v24.12.1")

    // then
    Assertions.assertThat(version).isNotNull
    Assertions.assertThat(version?.major).isEqualTo(24)
    Assertions.assertThat(version?.minor).isEqualTo(12)
    Assertions.assertThat(version?.patch).isEqualTo(1)
  }

  @Test
  fun `should parse without v`() {
    // when
    val version = Version.parse("v25.01.01")

    // then
    Assertions.assertThat(version).isNotNull
    Assertions.assertThat(version?.major).isEqualTo(25)
    Assertions.assertThat(version?.minor).isEqualTo(1)
    Assertions.assertThat(version?.patch).isEqualTo(1)
  }
}
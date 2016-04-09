package siosio.sql

import org.hamcrest.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import org.junit.Assert.*

class UtilsTest {

  @Test
  fun toColumnName() {
    assertThat(toColumnName("name"), `is`("name"))
    assertThat(toColumnName("userName"), `is`("user_name"))
    assertThat(toColumnName("hogeFugaPiyo"), `is`("hoge_fuga_piyo"))
  }
}

package siosio.sql.converter

import org.hamcrest.CoreMatchers.*
import org.junit.*
import org.junit.Assert.*
import siosio.sql.*

class StringConverterTest {

  data class Str(val str: String?)

  @Test
  fun notNull() {
    val database = createDatabase()
    val actual = database.findFirstRow(Str::class, "select '1' str")
    assertThat(actual, `is`(Str("1")))
  }

  @Test
  fun `null`() {
    val database = createDatabase()
    val actual = database.findFirstRow(Str::class, "select null str")

    assertThat(actual, `is`(Str(null)))
  }
}

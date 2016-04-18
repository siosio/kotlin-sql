package siosio.sql.converter

import org.hamcrest.CoreMatchers.*
import org.junit.*
import org.junit.Assert.*
import siosio.sql.*

class LongConverterTest {

  data class Num(val i: Long?)

  @Test
  fun notNull() {
    val database = createDatabase()
    val actual = database.findFirstRow(Num::class, "select 12345 i")
    assertThat(actual, `is`(Num(12345L)))
  }

  @Test
  fun `null`() {
    val database = createDatabase()
    val actual = database.findFirstRow(Num::class, "select null i")

    assertThat(actual, `is`(Num(null)))
  }
}

package siosio.sql.converter

import siosio.sql.*

import org.junit.*
import org.junit.Assert.*
import org.hamcrest.CoreMatchers.*

class IntConverterTest2 {

  data class Num(val i: Int?)

  @Test
  fun notNull() {
    val database = createDatabase()
    val actual = database.findFirstRow(Num::class, "select 12345 i")
    assertThat(actual, `is`(Num(12345)))
  }

  @Test
  fun `null`() {
    val database = createDatabase()
    val actual = database.findFirstRow(Num::class, "select null i")

    assertThat(actual, `is`(Num(null)))
  }
}

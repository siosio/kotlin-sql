package siosio.sql.converter

import org.hamcrest.CoreMatchers.*
import org.junit.*
import org.junit.Assert.*
import siosio.sql.*

class BinaryConverterTest {

  data class Binary(val binary: ByteArray?)

  @Test
  fun notNull() {
    val database = createDatabase()
    val actual = database.findFirstRow(Binary::class, "select X'3132' binary")

    assertThat(actual.binary!![0], `is`(0x31.toByte()))
    assertThat(actual.binary!![1], `is`(0x32.toByte()))

  }

  @Test
  fun `null`() {
    val database = createDatabase()
    val actual = database.findFirstRow(Binary::class, "select null binary")

    assertThat(actual, `is`(Binary(null)))
  }
}

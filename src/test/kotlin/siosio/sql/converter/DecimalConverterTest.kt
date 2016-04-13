package siosio.sql.converter

import org.hamcrest.*
import org.junit.*
import siosio.sql.*
import java.math.*

class DecimalConverterTest {

  data class Decimal(val d: BigDecimal?)

  @Test
  fun notNull() {
    val database = createDatabase()
    val actual = database.findFirstRow(Decimal::class, "select 108.23 d")
    Assert.assertThat(actual, CoreMatchers.`is`(Decimal(BigDecimal("108.23"))))
  }

  @Test
  fun `null`() {
    val database = createDatabase()
    val actual = database.findFirstRow(Decimal::class, "select null d")
    Assert.assertThat(actual, CoreMatchers.`is`(Decimal(null)))
  }
}

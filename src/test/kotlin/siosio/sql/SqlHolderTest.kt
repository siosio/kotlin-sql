package siosio.sql

import org.hamcrest.*
import org.junit.*
import org.junit.Assert.*

class SqlHolderTest {

  @Test
  fun withoutParameter() {
    val actual = SqlHolder.valueOf("select * from hoge")
    assertThat(actual, CoreMatchers.`is`(SqlHolder("select * from hoge", emptyList())))
  }

  @Test
  fun withJdbcParameter() {
    val actual = SqlHolder.valueOf("select * from hoge where col = ?")
    assertThat(actual, CoreMatchers.`is`(SqlHolder("select * from hoge where col = ?", emptyList())))
  }

  @Test
  fun withNamedParameter() {
    val actual = SqlHolder.valueOf("select * from hoge where col = :col and fuga = :abcdefg")
    assertThat(actual, CoreMatchers.`is`(SqlHolder(
        "select * from hoge where col = ? and fuga = ?",
        listOf(SqlHolder.Parameters(1, "col"), SqlHolder.Parameters(2, "abcdefg")))))
  }
}
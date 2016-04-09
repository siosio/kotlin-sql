package siosio.sql

import org.hamcrest.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.core.*
import org.junit.*
import org.junit.Assert.*
import org.junit.experimental.runners.*
import org.junit.rules.*
import org.junit.runner.*
import siosio.sql.converter.*
import java.sql.*
import java.util.*
import kotlin.reflect.*

data class Ret(val str: String)
data class MultiCol(val str: String, val i: Int)

@RunWith(Enclosed::class)
class SqlTest {

  class ForEach {
    @Test
    fun `find all rows(single column)`() {
      val sut = createSql()

      val actual = ArrayList<Ret>()
      sut.eachRow(Ret::class, "select 'a_0' union all select 'a_1' order by 1") {
        actual.add(it)
      }
      assertThat(actual, hasItems(
          `is`(Ret("a_0")),
          `is`(Ret("a_1"))
      ))
    }

    @Test
    fun `find all rows(multiple column)`() {
      val sut = createSql()

      val actual = ArrayList<MultiCol>()
      sut.eachRow(MultiCol::class, "select 'abc', 1234") {
        actual.add(it)
      }
      assertThat(actual, IsCollectionContaining.hasItems(
          `is`(MultiCol("abc", 1234))
      ))
    }

    @Test
    fun `custom converter`() {
      val sut = createSql(object : ValueConverterFactory() {
        override fun create(columnName: String, type: KType): Converter<*> {
          return object : Converter<Any?> {
            override fun convert(row: ResultSet, columnIndex: Int): Any? {
              val value = row.getInt(columnIndex)
              return (value * 2).toString()
            }
          }
        }
      })

      val actual = ArrayList<Ret>()
      sut.eachRow(Ret::class, "select 0  union all select 1 order by 1") {
        actual.add(it)
      }
      assertThat(actual, hasItems(
          `is`(Ret("0")),
          `is`(Ret("2"))
      ))
    }
  }

  class FirstRow {
    var expectedException = ExpectedException.none()
      @Rule get

    @Test
    fun findFirstRow() {
      val sut = createSql()
      val result = sut.findFirstRow(Ret::class, "select 'abcdefg'")
      assertThat(result, `is`(Ret("abcdefg")))
    }

    @Test
    fun findFirstRow_noDataFound() {
      val sut = createSql()

      expectedException.expect(NoDataFoundException::class.java)
      sut.findFirstRow(Ret::class, "select 'abcdefg' where 1 = 2")
    }
  }

  class Execute {
    data class TestEntity(val id: Int)
    @Test
    fun executeDDL() {
      val sut = createSql()
      sut.execute("create table test(id int, primary key(id))")

      sut.execute("insert into test values (1)")
      sut.execute("insert into test values (2)")

      val actual = ArrayList<TestEntity>()
      sut.eachRow(TestEntity::class, "select * from test order by id") {
        actual.add(it)
      }

      assertThat(actual, hasItems(
          `is`(TestEntity(1)),
          `is`(TestEntity(2))
      ))

    }
  }

  class WithTransaction {
    data class TestEntity(val id: Int)

    @Test
    fun commitTransaction() {
      val sut = createSql()

      sut.withTransaction {
        execute("create table test(id int, primary key(id))")
        execute("insert into test values (1)")
        execute("insert into test values (2)")
      }

      val actual = ArrayList<TestEntity>()
      sut.eachRow(TestEntity::class, "select id from test order by id desc") {
        actual.add(it)
      }

      assertThat(actual, hasItems(
          `is`(TestEntity(2)),
          `is`(TestEntity(1))
      ))
    }

    @Test
    fun rollbackTransaction() {
      val sut = createSql()

      try {
        sut.withTransaction {
          execute("create table test(id int, primary key(id))")
          execute("insert into test values (1)")

          throw Exception()
        }
      } catch(e: Exception) {
        println("exception: ${e}")
      }

      sut.eachRow(TestEntity::class, "select id from test") {
        assert(false)
      }
    }
  }
}

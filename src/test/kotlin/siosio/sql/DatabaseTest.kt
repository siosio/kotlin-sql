package siosio.sql

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.core.*
import org.junit.*
import org.junit.Assert.assertThat
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
class DatabaseTest {

  class ForEach {
    @Test
    fun `find all rows(single column)`() {
      val sut = createDatabase()

      val actual = ArrayList<Ret>()
      sut.eachRow(Ret::class, "select 'a_0' str union all select 'a_1' order by 1") {
        actual.add(it)
      }
      assertThat(actual, hasItems(
          `is`(Ret("a_0")),
          `is`(Ret("a_1"))
      ))
    }

    @Test
    fun `find all rows(multiple column)`() {
      val sut = createDatabase()

      val actual = ArrayList<MultiCol>()
      sut.eachRow(MultiCol::class, "select 'abc' str, 1234 i") {
        actual.add(it)
      }
      assertThat(actual, IsCollectionContaining.hasItems(
          `is`(MultiCol("abc", 1234))
      ))
    }

    @Test
    fun `custom converter`() {
      val sut = createDatabase(object : ValueConverterFactory() {
        override fun create(columnName: String, type: KType): Converter<*> {
          return object : Converter<Any?> {
            override fun convert(row: ResultSet, columnName: String): Any? {
              val value = row.getInt(columnName)
              return (value * 2).toString()
            }
          }
        }
      })

      val actual = ArrayList<Ret>()
      sut.eachRow(Ret::class, "select 0 str union all select 1 order by 1") {
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
      val sut = createDatabase()
      val result = sut.findFirstRow(Ret::class, "select 'abcdefg' str")
      assertThat(result, `is`(Ret("abcdefg")))
    }

    @Test
    fun withConfig() {
      val sut = createDatabase()
      sut.config {
        fetchSize = 10
      }
      val result = sut.findFirstRow(Ret::class, "select 'hoge' str")
      assertThat(result, `is`(Ret("hoge")))
    }

    @Test
    fun findFirstRow_noDataFound() {
      val sut = createDatabase()

      expectedException.expect(NoDataFoundException::class.java)
      sut.findFirstRow(Ret::class, "select 'abcdefg' where 1 = 2")
    }
  }

  class Execute {
    data class TestEntity(val id: Int)
    data class WithParam(val name:String)
    @Test
    fun executeDDL() {
      val sut = createDatabase()
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

    @Test
    fun withData() {
      val sut = createDatabase()

      sut.withTransaction {
        sut.execute("create table with_param(id int auto_increment not null, name varchar(100), primary key(id))")


        1.until(10).forEachIndexed { index, num ->
          sut.execute("insert into with_param (name) values (:name)", WithParam("name_$num"))
        }
      }
    }
  }

  class WithTransaction {
    data class TestEntity(val id: Int)

    @Test
    fun commitTransaction() {
      val sut = createDatabase()

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
      val sut = createDatabase()

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

  class Find {

    data class Ret(val str: String)

    @Test
    fun withoutInParameter() {
      val sut = createDatabase()

      val actual = sut.find(Ret::class, "select '12345' str")
      assertThat(actual, hasItems(
          Ret("12345")
      ))
    }

    @Test
    fun withInParameter() {
      val sut = createDatabase()

      data class Condition(val name : String)

      val condition = Condition("hoge")

      val actual = sut.find(Ret::class, "select '12345' str where 'hoge' = :name", condition)
      assertThat(actual, hasItems(
          Ret("12345")
      ))
    }
  }
}

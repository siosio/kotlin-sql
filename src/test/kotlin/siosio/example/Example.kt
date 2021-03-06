package siosio.example

import org.h2.jdbcx.*
import siosio.sql.*

object Example {

  data class User(val id: Long? = null, val name: String)

  @JvmStatic
  fun main(args: Array<String>) {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG")

    val dataSource = JdbcDataSource()
    dataSource.setURL("jdbc:h2:mem:kotlin-sql-example;DB_CLOSE_DELAY=-1")
    dataSource.user = "sa"

    val database = Database(dataSource)
    database.withTransaction {
      execute("create table users (id bigint auto_increment, name varchar2(100), primary key(id))")

      val users = (1..10).map {
        User(name = "name_$it")
      }.toList()
      executeBatch("insert into users (name) values (:name)", users)
    }

    database.eachRow(User::class, "select id, name from users order by id") {
      println("it = ${it}")
    }

    println("--------------------------------------------------")

    data class Param(val id: Long)
    database.eachRow(User::class, "select * from users where id = :id", Param(5)) {
      println("it = ${it}")
    }
  }
}

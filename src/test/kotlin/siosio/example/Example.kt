package siosio.example

import org.h2.jdbcx.*
import siosio.sql.*

object Example {

  data class User(val id: Long, val name: String)

  @JvmStatic
  fun main(args: Array<String>) {
    val dataSource = JdbcDataSource()
    dataSource.setURL("jdbc:h2:mem:kotlin-sql-example;DB_CLOSE_DELAY=-1")
    dataSource.user = "sa"

    val database = Database(dataSource)
    database.withTransaction {
      execute("create table users (id bigint auto_increment, name varchar2(100), primary key(id))")

      (1..10).forEach {
        execute("insert into users (name) values ('name_$it')")
      }
    }

    database.eachRow(User::class, "select id, name from users order by id") {
      println("it = ${it}")
    }

    println("--------------------------------------------------")

    database.eachRow(User::class, "select * from users where id = ?", 5) {
      println("it = ${it}")
    }
  }
}

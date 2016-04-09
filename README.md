#Kotlin-SQL
SQL execution library of Kotlin

##Example
```kotlin
data class User(val id: Long, val name: String)

val dataSource = JdbcDataSource()
dataSource.setURL("jdbc:h2:mem:kotlin-sql-example;DB_CLOSE_DELAY=-1")
dataSource.user = "sa"

val sql = Sql(dataSource)
sql.withTransaction {
  execute("create table users (id bigint auto_increment, name varchar2(100), primary key(id))")

  (1..10).forEach {
    execute("insert into users (name) values ('name_$it')")
  }
}

sql.eachRow(User::class, "select id, name from users order by id") {
  println("it = ${it}")
}
```

##license
This software is released under the MIT License, see LICENSE.txt.
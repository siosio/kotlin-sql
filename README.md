#Kotlin-SQL
SQL execution library of Kotlin

##Example
```kotlin
data class User(val id: Long? = null, val name: String)

val dataSource = JdbcDataSource()
dataSource.setURL("jdbc:h2:mem:kotlin-sql-example;DB_CLOSE_DELAY=-1")
dataSource.user = "sa"

val database = Database(dataSource)
database.withTransaction {
  execute("create table users (id bigint auto_increment, name varchar2(100), primary key(id))")

  (1..10).forEach {
    execute("insert into users (name) values (:name)", User(name = "name_$it"))
  }
}

data class Param(val id: Long)
database.eachRow(User::class, "select * from users where id = :id", Param(5)) {
  println("it = ${it}")
}
```

##license
This software is released under the MIT License, see LICENSE.txt.

package siosio.sql

import org.h2.jdbcx.JdbcDataSource

fun createSql(): Database {
  return createSql(null)
}

fun createSql(factory: ValueConverterFactory?): Database {
  val dataSource = JdbcDataSource()
  dataSource.setURL("jdbc:h2:mem:kotlin-sql;DB_CLOSE_DELAY=-1")
  dataSource.user = "sa"
  dataSource.connection.use {
    it.createStatement().use {
      it.execute("drop table if exists test")
    }
  }
  return Database(dataSource, factory)
}

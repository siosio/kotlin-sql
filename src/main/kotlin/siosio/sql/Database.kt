package siosio.sql

import javax.sql.*
import kotlin.reflect.*


class Database(private val dataSource: DataSource, converterFactory: ValueConverterFactory?) {

  private val converterFactory: ValueConverterFactory

  init {
    if (converterFactory == null) {
      this.converterFactory = ValueConverterFactory()
    } else {
      this.converterFactory = converterFactory
    }
  }

  constructor(dataSource: DataSource) : this(dataSource, null)

  fun <T : Any> eachRow(type: KClass<T>, query: String, vararg condition: Any = emptyArray(), block: (row: T) -> Unit) {
    Query(getConnection(), converterFactory).use {
      forEach(type, query, condition) {
        block(it)
      }
    }
  }

  fun <T : Any> findFirstRow(type: KClass<T>, query: String): T {
    return Query(getConnection(), converterFactory).use {
      findFirstRow(type, query)
    }
  }

  fun <T : Any> find(type: KClass<T>, query: String, vararg condition: Any = emptyArray()): List<T> {
    return Query(getConnection(), converterFactory).use {
      this.find(type, query, condition)
    }
  }

  /**
   *
   */
  fun withTransaction(transaction: Query.() -> Unit): Unit {
    Query(getConnection(), converterFactory).use {
      connection.autoCommit = false
      try {
        transaction()
        connection.commit()
      } finally {
        connection.rollback()
      }
    }
  }

  /**
   * execute sql.
   *
   * the transaction is automatically committed.
   *
   * @param sql sql
   * @see Query#execute
   */
  fun execute(sql: String) {
    Query(getConnection(), converterFactory).use {
      execute(sql)
    }
  }

  /**
   * create databae connection.
   *
   * @return database connection
   */
  private fun getConnection() = dataSource.connection
}

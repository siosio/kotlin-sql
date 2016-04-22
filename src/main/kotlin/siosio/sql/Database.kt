package siosio.sql

import java.sql.*
import javax.sql.*
import kotlin.reflect.*

class Database(private val dataSource: DataSource, converterFactory: ValueConverterFactory?) {

  private val converterFactory: ValueConverterFactory

  private var config: Statement.() -> Unit = {}

  init {
    if (converterFactory == null) {
      this.converterFactory = ValueConverterFactory()
    } else {
      this.converterFactory = converterFactory
    }
  }

  constructor(dataSource: DataSource) : this(dataSource, null)

  fun config(config: Statement.() -> Unit) {
    this.config = config
  }

  fun <T : Any> eachRow(type: KClass<T>, query: String, block: (row: T) -> Unit) {
    eachRow(type, query, null, block)
  }

  fun <T : Any, C : Any> eachRow(type: KClass<T>, query: String, condition: C?, block: (row: T) -> Unit) {
    Query(getConnection(), converterFactory, config).use {
      forEach(type, query, condition) {
        block(it)
      }
    }
  }

  fun <T : Any> findFirstRow(type: KClass<T>, query: String): T {
    val firstRowConfig: Statement.() -> Unit = {
      config()
      maxRows = 1
    }
    return Query(getConnection(), converterFactory, firstRowConfig).use {
      findFirstRow(type, query)
    }
  }

  fun <T : Any> find(type: KClass<T>, query: String): List<T> {
    return find(type, query, null)
  }

  fun <T : Any, C : Any> find(type: KClass<T>, query: String, condition: C? = null): List<T> {
    return Query(getConnection(), converterFactory, config).use {
      this.find(type, query, condition)
    }
  }

  /**
   *
   */
  fun withTransaction(transaction: Query.() -> Unit): Unit {
    Query(getConnection(), converterFactory, config).use {
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
    Query(getConnection(), converterFactory, config).use {
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

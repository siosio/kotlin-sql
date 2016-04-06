package siosio.sql

import java.sql.*
import javax.sql.*
import kotlin.reflect.*


class Sql(private val dataSource: DataSource, converterFactory: ValueConverterFactory?) {

  var converterFactory: ValueConverterFactory

  init {
    if (converterFactory == null) {
      this.converterFactory = ValueConverterFactory()
    } else {
      this.converterFactory = converterFactory
    }
  }

  constructor(dataSource: DataSource) : this(dataSource, null)

  fun <T : Any> eachRow(type: KClass<T>, query: String, block: (row: T) -> Unit) {
    Query(getConnection(), converterFactory).use {
      forEach(type, query) {
        block(it)
      }
    }
  }

  fun <T : Any> findFirstRow(type: KClass<T>, query: String): T {
    return Query(getConnection(), converterFactory).use {
      findFirstRow(type, query)
    }
  }

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

  fun execute(sql: String) {
    Query(getConnection(), converterFactory).use {
      execute(sql)
    }
  }

  private fun getConnection() = dataSource.connection

  private fun <T : Any> createResultObject(classMeta: ClassMeta<T>, rs: ResultSet): T {
    val args = classMeta.parameters.mapIndexed { i, parameter ->
      val converter = converterFactory.create(parameter.name, parameter.type)
      converter.convert(rs, i + 1)
    }.toTypedArray()

    return classMeta.createInstance(args)
  }
}
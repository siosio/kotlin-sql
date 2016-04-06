package siosio.sql

import org.slf4j.*
import java.io.*
import java.sql.*
import kotlin.reflect.*

class Query(internal val connection: Connection, private val converterFactory: ValueConverterFactory) : Closeable {

  companion object {
    private val logger = LoggerFactory.getLogger(Query::class.java)
  }

  override fun close() {
    try {
      connection.close()
    } catch(sqle: SQLException) {
      logger.warn("failed to connection close.", sqle)
    }
  }

  fun <T> use(block: Query.() -> T): T {
    return try {
      block();
    } finally {
      close()
    }
  }

  fun <T : Any> forEach(type: KClass<T>, query: String, block: (row: T) -> Unit): Unit {
    val classMeta = ClassMeta(type)
    connection.createStatement().use { st ->
      st.executeQuery(query).use { rs ->
        while (rs.next()) {
          val result = createResultObject(classMeta, rs)
          block(result)
        }
      }
    }
  }

  fun <T : Any> findFirstRow(type: KClass<T>, query: String): T {
    val classMeta = ClassMeta(type)
    connection.createStatement().use { st ->
      st.executeQuery(query).use { rs ->
        if (rs.next()) {
          return createResultObject(classMeta, rs)
        } else {
          throw NoDataFoundException(query)
        }
      }
    }
    throw IllegalStateException("not arrowed hear.")
  }

  fun execute(sql: String): Unit {
    connection.createStatement().use { st ->
      logger.trace("execute sql. sql: ${sql}")
      st.execute(sql)
    }
  }

  private fun <T : Any> createResultObject(classMeta: ClassMeta<T>, rs: ResultSet): T {
    val args = classMeta.parameters.mapIndexed { i, parameter ->
      val converter = converterFactory.create(parameter.name, parameter.type)
      converter.convert(rs, i + 1)
    }.toTypedArray()

    return classMeta.createInstance(args)
  }
}

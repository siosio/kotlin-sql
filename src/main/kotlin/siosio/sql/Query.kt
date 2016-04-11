package siosio.sql

import org.slf4j.*
import java.io.*
import java.sql.*
import java.util.*
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
      block()
    } finally {
      close()
    }
  }

  fun <T : Any> forEach(type: KClass<T>, query: String, condition: Array<out Any>, block: (row: T) -> Unit): Unit {
    val classMeta = ClassMeta(type)
    executeQuery(query, condition) {
      while (it.next()) {
        val result = createResultObject(classMeta, it)
        block(result)
      }
    }
  }

  /**
   * find first row.
   *
   * @param T return data type
   * @param type return type
   * @param query sql
   * @return first row.
   */
  fun <T : Any> findFirstRow(type: KClass<T>, query: String): T {
    val classMeta = ClassMeta(type)
    executeQuery(query, emptyArray()) {
      if (it.next()) {
        return createResultObject(classMeta, it)
      } else {
        throw NoDataFoundException(query)
      }
    }
    throw IllegalStateException("not arrowed hear.")
  }

  fun <T : Any> find(type: KClass<T>, query: String, condition: Array<out Any>): List<T> {
    val classMeta = ClassMeta(type)

    val result = ArrayList<T>()
    executeQuery(query, condition) {
      while (it.next()) {
        result.add(createResultObject(classMeta, it))
      }
    }
    return result
  }

  fun execute(sql: String): Unit {
    connection.createStatement().use { st ->
      st.execute(sql)
    }
  }

  private inline fun <T> executeQuery(query: String, condition: Array<out Any>, block: (ResultSet) -> T): Unit {
    connection.prepareStatement(query).use { st ->
      condition.forEachIndexed { index, param ->
        st.setObject(index + 1, param)
      }
      st.executeQuery().use { rs ->
        block(rs)
      }
    }
  }

  private fun <T : Any> createResultObject(classMeta: ClassMeta<T>, rs: ResultSet): T {
    val args = classMeta.parameters.mapIndexed { i, parameter ->
      val converter = converterFactory.create(parameter.name, parameter.type)
      converter.convert(rs, toColumnName(parameter.name))
    }.toTypedArray()

    return classMeta.createInstance(args)
  }
}

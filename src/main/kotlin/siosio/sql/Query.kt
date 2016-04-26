package siosio.sql

import org.slf4j.*
import java.io.*
import java.sql.*
import java.util.*
import kotlin.reflect.*

class Query(
    internal val connection: Connection,
    private val converterFactory: ValueConverterFactory,
    private val config: Statement.() -> Unit) : Closeable {

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

  fun <T : Any, PARAM : Any> forEach(type: KClass<T>, query: String, condition: PARAM?, block: (row: T) -> Unit): Unit {
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
    executeQuery(query, null) {
      if (it.next()) {
        return createResultObject(classMeta, it)
      } else {
        throw NoDataFoundException(query)
      }
    }
    throw IllegalStateException("not arrowed hear.")
  }

  fun <T : Any, PARAM : Any> find(type: KClass<T>, query: String, condition: PARAM?): List<T> {
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
    execute(sql, null)
  }

  fun <PARAM : Any> execute(sql: String, param: PARAM?): Unit {
    val (jdbcSql, parameters) = SqlHolder.valueOf(sql)

    connection.prepareStatement(jdbcSql).use { st ->
      st.config()

      if (logger.isDebugEnabled) {
        logger.debug("execute sql=[{}], statement config: fetchSize[{}], maxRows[{}]", jdbcSql, st.fetchSize, st.maxRows)
      }

      param?.let {
        setInParameter(st, parameters, it)
      }

      st.execute()
    }
  }

  private inline fun <T, PARAM : Any> executeQuery(query: String, condition: PARAM?, block: (ResultSet) -> T): Unit {
    val (jdbcSql, parameters) = SqlHolder.valueOf(query)

    connection.prepareStatement(jdbcSql).use { st ->
      st.config()

      if (logger.isDebugEnabled) {
        logger.debug("execute sql=[{}], statement config: fetchSize[{}], maxRows[{}]", jdbcSql, st.fetchSize, st.maxRows)
      }

      condition?.let {
        setInParameter(st, parameters, it)
      }

      st.executeQuery().use { rs ->
        block(rs)
      }
    }
  }

  private fun <PARAM : Any> setInParameter(st: PreparedStatement, parameters: List<SqlHolder.Parameters>, param: PARAM) {
    val classMeta = ClassMeta(param.javaClass.kotlin)

    parameters.forEach { parameter ->
      val p = classMeta.parameters.first {
        it.name == parameter.name
      }

      val inParameter = p.member.call(param)
      if (logger.isDebugEnabled) {
        logger.debug("in parameter[{}] value=[{}]", parameter.index, inParameter)
      }
      st.setObject(parameter.index, inParameter)
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

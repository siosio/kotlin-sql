package siosio.sql

import java.util.*

data class SqlHolder(val sql: String, val parameters: List<Parameters>) {

  companion object {
    val paramPattern = Regex(":([a-zA-Z0-9]+)")

    fun valueOf(sql: String): SqlHolder {
      val parameters = ArrayList<Parameters>()
      var index = 0
      val jdbcSql = paramPattern.replace(sql) {
        index++
        parameters.add(Parameters(index, it.groupValues[1]))
        "?"
      }
      return SqlHolder(jdbcSql, parameters)
    }
  }

  data class Parameters(val index: Int, val name: String)
}
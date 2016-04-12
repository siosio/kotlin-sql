package siosio.sql.converter

import java.sql.*

class ObjectConverter : Converter<Any?> {
  override fun convert(row: ResultSet, columnName: String): Any? {
    return row.getObject(columnName)
  }

  override fun isConvertible(type: String): Boolean = true
}

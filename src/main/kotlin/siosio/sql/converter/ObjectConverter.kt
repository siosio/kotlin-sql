package siosio.sql.converter

import java.sql.ResultSet

class ObjectConverter : Converter<Any> {
  override fun convert(row: ResultSet, columnIndex: Int): Any {
    return row.getObject(columnIndex)
  }
}

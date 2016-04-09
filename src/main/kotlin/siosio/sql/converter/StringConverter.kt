package siosio.sql.converter

import java.sql.ResultSet

class StringConverter : Converter<String> {
  override fun convert(row: ResultSet, columnName: String): String {
    return row.getString(columnName)
  }
}

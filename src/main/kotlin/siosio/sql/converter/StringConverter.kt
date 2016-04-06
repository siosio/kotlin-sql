package siosio.sql.converter

import java.sql.ResultSet

class StringConverter : Converter<String> {
  override fun convert(row: ResultSet, columnIndex: Int): String {
    return row.getString(columnIndex)
  }
}

package siosio.sql.converter

import java.sql.ResultSet

interface Converter<T> {
  fun convert(row: ResultSet, columnName: String): T
}

package siosio.sql.converter

import java.sql.ResultSet

interface Converter<T> {
  fun convert(row: ResultSet, columnIndex: Int): T
}

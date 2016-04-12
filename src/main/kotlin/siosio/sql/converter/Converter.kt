package siosio.sql.converter

import java.sql.*

interface Converter<T> {
  fun convert(row: ResultSet, columnName: String): T

  fun isConvertible(type: String): Boolean {
    return false
  }
}

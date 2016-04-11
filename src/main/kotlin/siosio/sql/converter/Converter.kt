package siosio.sql.converter

import java.sql.*

interface Converter<T> {
  fun convert(row: ResultSet, columnName: String): T

  fun getType(): java.lang.Class<*> {
    return Any::class.java
  }
}

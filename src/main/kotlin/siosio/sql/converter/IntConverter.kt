package siosio.sql.converter

import java.sql.*

class IntConverter : Converter<Int?> {
  override fun convert(row: ResultSet, columnName: String): Int? {
    val value = row.getInt(columnName)
    return if (row.wasNull()) {
      null
    } else {
      value
    }
  }

  override fun isConvertible(type: String): Boolean = typePattern.matches(type)

  companion object {
    private  val typePattern = Regex("kotlin.Int\\??")
  }
}

package siosio.sql.converter

import java.sql.*

class LongConverter : Converter<Long?> {
  override fun convert(row: ResultSet, columnName: String): Long? {
    val value = row.getLong(columnName)
    return if (row.wasNull()) {
      null
    } else {
      value
    }
  }

  companion object {
    private val typePattern = Regex("${Long::class.qualifiedName}\\??")
  }
}

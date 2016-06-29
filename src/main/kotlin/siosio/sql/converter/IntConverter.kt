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

  companion object {
    private  val typePattern = Regex("${Int::class.qualifiedName}\\??")
  }
}

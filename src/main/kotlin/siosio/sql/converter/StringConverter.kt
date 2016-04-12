package siosio.sql.converter

import java.sql.*

class StringConverter : Converter<String?> {
  override fun convert(row: ResultSet, columnName: String): String? {
    return row.getString(columnName)
  }

  override fun isConvertible(type: String): Boolean = typePattern.matches(type)

  companion object {
    private  val typePattern = Regex("kotlin.String\\??")
  }

}

package siosio.sql.converter

import java.sql.*

class StringConverter : Converter<String?> {
  override fun convert(row: ResultSet, columnName: String): String? {
    return row.getString(columnName)
  }

  companion object {
    private  val typePattern = Regex("${String::class.qualifiedName}\\??")
  }

}

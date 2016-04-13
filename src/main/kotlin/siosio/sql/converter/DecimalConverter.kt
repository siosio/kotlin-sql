package siosio.sql.converter

import java.math.*
import java.sql.*

class DecimalConverter : Converter<BigDecimal?> {

  override fun convert(row: ResultSet, columnName: String): BigDecimal? {
    return row.getBigDecimal(columnName)
  }

  override fun isConvertible(type: String): Boolean {
    return typePattern.matches(type)
  }

  companion object {
    private val typePattern = Regex("${BigDecimal::class.qualifiedName}\\??")
  }
}

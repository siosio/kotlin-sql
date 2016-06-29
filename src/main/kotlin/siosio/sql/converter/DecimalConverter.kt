package siosio.sql.converter

import java.math.*
import java.sql.*

class DecimalConverter : Converter<BigDecimal?> {

  override fun convert(row: ResultSet, columnName: String): BigDecimal? {
    return row.getBigDecimal(columnName)
  }

  companion object {
    private val typePattern = Regex("${BigDecimal::class.qualifiedName}\\??")
  }
}

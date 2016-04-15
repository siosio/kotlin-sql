package siosio.sql.converter

import java.sql.*

class BinaryConverter : Converter<ByteArray?> {

  override fun convert(row: ResultSet, columnName: String): ByteArray? {
    return row.getBytes(columnName)
  }

  override fun isConvertible(type: String): Boolean = typePattern.matches(type)

  companion object {
    private  val typePattern = Regex("${ByteArray::class.qualifiedName}\\??")
  }
}

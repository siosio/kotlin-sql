package siosio.sql.converter

import java.sql.*

class BinaryConverter : Converter<ByteArray?> {

  override fun convert(row: ResultSet, columnName: String): ByteArray? {
    return row.getBytes(columnName)
  }
}

package siosio.sql

import siosio.sql.converter.*
import kotlin.reflect.*

open class ValueConverterFactory {

  companion object {
    private val converters = listOfNotNull(
        StringConverter(),
        IntConverter(),
        DecimalConverter(),
        BinaryConverter(),
        ObjectConverter()
    )
  }

  open fun create(columnName: String, type: KType): Converter<*> {
    val typeString = type.toString()
    return converters.firstOrNull() {
      it.isConvertible(typeString)
    }!!
  }
}

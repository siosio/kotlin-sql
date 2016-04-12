package siosio.sql

import siosio.sql.converter.*
import kotlin.reflect.*

open class ValueConverterFactory {

  companion object {
    private val converters = listOfNotNull(StringConverter(), IntConverter(), ObjectConverter())

    val convertor = mapOf<KType, Converter<*>>(
        Int::class.defaultType to IntConverter()
    )
  }

  open fun create(columnName: String, type: KType): Converter<*> {
    val typeString = type.toString()
    return converters.firstOrNull() {
      it.isConvertible(typeString)
    }!!
  }
}

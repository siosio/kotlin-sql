package siosio.sql

import siosio.sql.converter.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

open class ValueConverterFactory {

  companion object {
    private val converters = listOfNotNull(StringConverter(), IntConverter(), ObjectConverter())

    val convertor = mapOf<KType, Converter<*>>(
        Int::class.defaultType to IntConverter()
    )
  }

  open fun create(columnName: String, type: KType): Converter<*> {
    val clazz = type.javaType as Class<*>
    return converters.firstOrNull() {
      it.getType().isAssignableFrom(clazz)
    }!!
  }
}

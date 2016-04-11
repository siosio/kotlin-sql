package siosio.sql

import siosio.sql.converter.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

open class ValueConverterFactory {

  companion object {
    private val anyConverter = ObjectConverter()

    private val converters = listOfNotNull(StringConverter(), IntConverter())
  }

  open fun create(columnName: String, type: KType): Converter<*> {
    val typeName = type.javaType.typeName
    return converters.firstOrNull() {
      typeName.contains(it.getTypeName())
    } ?: anyConverter
  }
}

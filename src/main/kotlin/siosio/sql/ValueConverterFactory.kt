package siosio.sql

import siosio.sql.converter.*
import kotlin.reflect.*

open class ValueConverterFactory {

  companion object {
    private val stringConverter = StringConverter()
  }

  open fun create(columnName: String, type: KType): Converter<*> {
    return when (type) {
      String::class -> stringConverter
      else -> ObjectConverter()
    }
  }
}

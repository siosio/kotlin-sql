package siosio.sql

import siosio.sql.converter.Converter
import siosio.sql.converter.ObjectConverter
import siosio.sql.converter.StringConverter
import kotlin.reflect.KType

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

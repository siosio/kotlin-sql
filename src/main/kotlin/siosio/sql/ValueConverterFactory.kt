package siosio.sql

import siosio.sql.converter.*
import java.lang.reflect.*
import java.math.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

open class ValueConverterFactory {

  companion object {
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    private val converters = mapOf<Type, Converter<*>>(
        String::class.java to StringConverter(),
        Int::class.java to IntConverter(),
        java.lang.Integer::class.java to IntConverter(),
        Long::class.java to LongConverter(),
        java.lang.Long::class.java to LongConverter(),
        BigDecimal::class.java to DecimalConverter(),
        ByteArray::class.java to BinaryConverter()
    )
    private val defaultConverter = ObjectConverter()
  }

  open fun create(columnName: String, type: KType): Converter<*> = converters.getOrElse(type.javaType, { defaultConverter })
}

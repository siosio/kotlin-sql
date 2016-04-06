package siosio.sql

import sun.management.snmp.jvminstr.JvmRTInputArgsEntryImpl
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.primaryConstructor

class ClassMeta<T : Any>(type: KClass<T>) {

  val parameters: List<Parameter>
  val primaryConstructor: KFunction<T>

  init {

    val constructor = type.primaryConstructor
        ?: throw IllegalArgumentException("invalid class. should have a primary constructor.")

    parameters = constructor.parameters.map {
      Parameter(it.name!!, it.type)
    }
    primaryConstructor = constructor
  }

  fun createInstance(args: Array<in Any>): T {
    return primaryConstructor.call(*args)
  }

  data class Parameter(val name: String, val type: KType)
}

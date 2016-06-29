package siosio.sql

import kotlin.reflect.*

class ClassMeta<T : Any>(type: KClass<T>) {

  val parameters: List<Parameter>
  val primaryConstructor: KFunction<T>

  init {
    val constructor = type.primaryConstructor
        ?: throw IllegalArgumentException("invalid class. should have a primary constructor.")

    val members = type.members
    parameters = constructor.parameters.map {parameter ->
      val member = members.first { m ->
        m.name == parameter.name
      }
      Parameter(parameter.name!!, parameter.type, member)
    }
    primaryConstructor = constructor

  }

  fun createInstance(args: Array<in Any>): T {
    return primaryConstructor.call(*args)
  }

  data class Parameter(val name: String, val type: KType, val member:KCallable<*>)
}

package siosio.sql

private val UPPER_CHARACTER: Regex = Regex("([A-Z])")

fun toColumnName(propertyName: String): String {
  return UPPER_CHARACTER.replace(propertyName) {
    "_${it.groupValues[0]}"
  }.toLowerCase()
}


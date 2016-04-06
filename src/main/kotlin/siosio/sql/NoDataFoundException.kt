package siosio.sql

class NoDataFoundException(val sql:String) : RuntimeException() {
}

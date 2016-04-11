package siosio.sql

import org.slf4j.*
import java.sql.*
import java.util.*

val logger: Logger = LoggerFactory.getLogger("sql")

inline fun Connection.use(block: (Connection) -> Unit) {
  try {
    block(this)
  } finally {
    try {
      this.close()
    } catch(e: Exception) {
      logger.warn("failed to close database connection.", e)
    }
  }
}

inline fun <ST: Statement> ST.use(block: (ST) -> Unit) {
  try {
    block(this)
  } finally {
    try {
      this.close()
    } catch(e: Exception) {
      logger.warn("failed to close statement.", e)
    }
  }
}

inline fun ResultSet.use(block: (ResultSet) -> Unit) {
  try {
    block(this)
  } finally {
    try {
      this.close()
    } catch(e: Exception) {
      logger.warn("failed to close result set.", e)
    }
  }
}

inline fun <RT> ResultSet.map(block: ResultSet.() -> RT): List<RT> {
  var rows = ArrayList<RT>()
  while (next()) {
    rows.add(block())
  }
  return rows
}

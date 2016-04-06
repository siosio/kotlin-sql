package siosio.sql

import org.slf4j.*
import java.sql.*

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

inline fun Statement.use(block: (Statement) -> Unit) {
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

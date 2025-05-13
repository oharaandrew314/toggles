package dev.andrewohara.toggles.storage

import java.sql.Connection
import javax.sql.DataSource
import kotlin.use

internal fun <T> DataSource.transaction(fn: Connection.() -> T) = connection.use { conn ->
    conn.autoCommit = false
    try {
        fn(conn)
        conn.commit()
    } catch (e: Exception) {
        conn.rollback()
        throw e
    }
}
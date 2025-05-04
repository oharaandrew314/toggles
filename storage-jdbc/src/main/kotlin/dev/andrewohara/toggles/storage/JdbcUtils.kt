package dev.andrewohara.toggles.storage

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import org.http4k.lens.BiDiMapping
import java.sql.Connection
import javax.sql.DataSource
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.use

internal fun <K: Value<*>, V: Value<*>> keyValuePairsMapping(
    kf: ValueFactory<K, *>, vf: ValueFactory<V, *>
) = BiDiMapping<Map<K, V>, String>(
    asIn = { text -> text
        .split(",")
        .filter { it.isNotEmpty() }
        .associate {
            val (key, value) = it.split("=")
            kf.parse(key) to vf.parse(value)
        }
    },
    asOut = { it.entries.joinToString(",") { (key, value) -> "$key=$value" } }
)

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
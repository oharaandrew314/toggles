package dev.andrewohara.toggles.storage

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory

// csv
internal fun Collection<Value<*>>.toCsv() = joinToString(",")
internal fun <V: Value<String>> String.parseCsv(vf: ValueFactory<V, String>) =
    split(",").map(vf::parse)

// key-value pairs

internal fun <K: Value<*>, V: Value<*>> String.parseKeyValuePairs(
    kf: ValueFactory<K, *>, vf: ValueFactory<V, *>
) = split(",")
    .filter { it.isNotEmpty() }
    .associate {
        val (key, value) = it.split("=")
        kf.parse(key) to vf.parse(value)
    }

internal fun <K: Value<*>, V: Value<*>> Map<K, V>.toKeyValuePairs() =
    entries.joinToString(",") { (key, value) -> "$key=$value" }

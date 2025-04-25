package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleSource
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.VariationName
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peekFailure
import dev.forkhandles.result4k.recover
import mu.KotlinLogging
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.roundToInt

class Feature(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    private val toggleSource: ToggleSource,
    private val defaultVariation: VariationName
) {
    private val logger = KotlinLogging.logger { }

    operator fun invoke(subjectId: SubjectId): VariationName {
        return toggleSource(projectName, toggleName)
            .peekFailure { logger.error(it) }
            .map { it.evaluate(subjectId) }
            .recover { defaultVariation }
    }
}

fun ToggleState.evaluate(subjectId: SubjectId): VariationName {
    if (subjectId in overrides) return overrides.getValue(subjectId)

    // distribute hash into 1 of 100 buckets
    val bucket = MessageDigest.getInstance("MD5")
        .digest(subjectId.value.toByteArray())
        .let { abs(ByteBuffer.wrap(it).int) % 100 }

    var remainder = bucket
    for ((variation, weight) in generateBuckets()) {
        remainder -= weight
        if (remainder < 0) return variation
    }

    return defaultVariation
}

// TODO optimize; likely don't need to build the list of buckets
private fun ToggleState.generateBuckets(): List<Pair<VariationName, Int>> {
    val totalWeight = variations.values.sumOf { it.value }

    fun getBucketSize(name: VariationName): Int {
        val weight = variations[name]?.value ?: 0
        return (weight.toDouble() / totalWeight * 100).roundToInt()
    }

    return buildList {
        add(defaultVariation to getBucketSize(defaultVariation))
        for (name in variations.keys.minus(defaultVariation).sorted()) {
            add(name to getBucketSize(name))
        }
    }
}
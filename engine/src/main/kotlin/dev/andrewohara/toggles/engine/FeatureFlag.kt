package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.VariationName
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.peekFailure
import mu.KotlinLogging
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.roundToInt

class FeatureFlag(
    val toggleName: ToggleName,
    private val toggleSource: ToggleSource,
    private val defaultVariation: VariationName
) {
    private val logger = KotlinLogging.logger { }

    operator fun invoke(subjectId: SubjectId): VariationName {
        val state = toggleSource(toggleName)
            .peekFailure { logger.error("$toggleName -> engine_default($defaultVariation): $it") }
            .onFailure { return defaultVariation }

        if (subjectId in state.overrides) return state.overrides.getValue(subjectId)

        // distribute salted hash into 1 of 100 buckets
        val bucket = MessageDigest.getInstance("MD5")
            .digest("${state.uniqueId}:$subjectId".encodeToByteArray())
            .let { abs(ByteBuffer.wrap(it).int) % 100 }

        var remainder = bucket
        for ((variation, weight) in state.generateBuckets()) {
            remainder -= weight
            if (remainder < 0) {
                logger.debug { "$toggleName -> bucket:$bucket($variation)" }
                return variation
            }
        }

        logger.debug { "$toggleName -> bucket $bucket:toggle_default($defaultVariation)" }
        return state.defaultVariation
    }
}

// TODO optimize; likely don't need to build the list of buckets
private fun ToggleState.generateBuckets(): List<Pair<VariationName, Int>> {
    val totalWeight = variations.values.sumOf { it.value }

    fun getBucketSize(name: VariationName): Int {
        val weight = variations[name]?.value ?: 1
        return (weight.toDouble() / totalWeight * 100).roundToInt()
    }

    return buildList {
        add(defaultVariation to getBucketSize(defaultVariation))
        for (name in variations.keys.minus(defaultVariation).sorted()) {
            add(name to getBucketSize(name))
        }
    }
}
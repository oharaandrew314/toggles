package dev.andrewohara.toggles

import dev.forkhandles.values.AbstractValue
import dev.forkhandles.values.Base64StringValueFactory
import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import dev.forkhandles.values.Maskers
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.exactLength
import dev.forkhandles.values.maxLength
import dev.forkhandles.values.minLength
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex
import kotlin.random.Random

private val tokenValidator = "[a-zA-Z0-9-_]+".regex

private val nameValidation = 4.minLength
    .and(32.maxLength)
    .and(tokenValidator)

abstract class ResourceIdValueFactory<V: Value<String>>(
    coerceFn: (String) -> V
): Base64StringValueFactory<V>(coerceFn, validation = 16.exactLength) {
    private val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    fun random(random: Random) = of(List(16) { chars.random(random) }.joinToString(""))
}

abstract class HexValue(value: ByteArray): AbstractValue<ByteArray>(value) {
    override fun equals(other: Any?): Boolean {
        if (other !is HexValue) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode() = value.contentHashCode()
}

@OptIn(ExperimentalStdlibApi::class)
abstract class HexValueFactory<V: Value<ByteArray>>(coerceFn: (ByteArray) -> V, bytes: Int): ValueFactory<V, ByteArray>(
    coerceFn = coerceFn,
    parseFn = String::hexToByteArray,
    showFn = ByteArray::toHexString,
    validation = { it.size == bytes }
)

class TenantId private constructor(value: String): StringValue(value), ComparableValue<TenantId, String> {
    companion object: ResourceIdValueFactory<TenantId>(::TenantId)
}

class TenantName private constructor(value: String): StringValue(value), ComparableValue<TenantName, String> {
    companion object: StringValueFactory<TenantName>(::TenantName, nameValidation)
}

class UserId private constructor(value: ByteArray): HexValue(value), Comparable<UserId> {
    override fun compareTo(other: UserId) = show(this).compareTo(show(other))

    companion object: HexValueFactory<UserId>(::UserId, 16)
}

class EmailAddress private constructor(value: String): StringValue(value), ComparableValue<EmailAddress, String> {
    companion object: StringValueFactory<EmailAddress>(::EmailAddress)
}

class ProjectName private constructor(value: String): StringValue(value), ComparableValue<ProjectName, String> {
    companion object: StringValueFactory<ProjectName>(::ProjectName, nameValidation)
}

class ToggleName private constructor(value: String): StringValue(value), ComparableValue<ToggleName, String> {
    companion object: StringValueFactory<ToggleName>(::ToggleName, nameValidation)
}

class VariationName private constructor(value: String): StringValue(value), ComparableValue<VariationName, String> {
    companion object: StringValueFactory<VariationName>(::VariationName) {
        val default = of("off")
    }
}

class SubjectId private constructor(value: String): StringValue(value), ComparableValue<SubjectId, String> {
    companion object: StringValueFactory<SubjectId>(::SubjectId, 64.maxLength)
}

class Weight private constructor(value: Int): IntValue(value), ComparableValue<Weight, Int> {
    companion object: IntValueFactory<Weight>(::Weight, 0.minValue)
}

class EnvironmentName private constructor(value: String): StringValue(value), ComparableValue<EnvironmentName, String> {
    companion object: StringValueFactory<EnvironmentName>(
        fn = ::EnvironmentName,
        validation = 2.minLength.and(32.maxLength).and(tokenValidator)
    )
}

class ApiKey private constructor(value: String): StringValue(value, Maskers.hidden()) {
    companion object: ResourceIdValueFactory<ApiKey>(::ApiKey)
}

class UniqueId private constructor(value: String): StringValue(value) {
    companion object: Base64StringValueFactory<UniqueId>(::UniqueId, 16.exactLength)
}
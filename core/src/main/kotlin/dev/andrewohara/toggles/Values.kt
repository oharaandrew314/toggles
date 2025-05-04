package dev.andrewohara.toggles

import dev.forkhandles.values.Base64StringValueFactory
import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import dev.forkhandles.values.Maskers
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.exactLength
import dev.forkhandles.values.maxLength
import dev.forkhandles.values.minLength
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex

private val tokenValidator = "[a-zA-Z0-9-_]+".regex

private val nameValidation = 4.minLength
    .and(32.maxLength)
    .and(tokenValidator)

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
        fn =::EnvironmentName,
        validation = 2.minLength.and(32.maxLength).and(tokenValidator)
    )
}

class ApiKey private constructor(value: String): StringValue(value, Maskers.hidden()) {
    companion object: Base64StringValueFactory<ApiKey>(::ApiKey, validation = 16.exactLength)
}

class UniqueId private constructor(value: String): StringValue(value) {
    companion object: Base64StringValueFactory<UniqueId>(::UniqueId, 8.exactLength) {
        const val LENGTH = 8
    }
}
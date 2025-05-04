package dev.andrewohara.togles.storage

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dev.andrewohara.toggles.ApiKey
import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
private object TogglesDynamoStorageJsonAdapterFactory : JsonAdapter.Factory by KotshiTogglesDynamoStorageJsonAdapterFactory

internal val togglesJson = Moshi.Builder()
    .add(TogglesDynamoStorageJsonAdapterFactory)
    .add(ListAdapter)
    .add(MapAdapter)
    .asConfigurable()
    .withStandardMappings()
    .value(ProjectName)
    .value(ToggleName)
    .value(VariationName)
    .value(Weight)
    .value(SubjectId)
    .value(EnvironmentName)
    .value(UniqueId)
    .value(ApiKey)
    .done()
    .let { ConfigurableMoshi(it) }
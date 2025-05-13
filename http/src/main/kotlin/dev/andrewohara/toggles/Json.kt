package dev.andrewohara.toggles

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
private object TogglesHttpJsonAdapterFactory : JsonAdapter.Factory by KotshiTogglesHttpJsonAdapterFactory

internal val togglesJson = Moshi.Builder()
    .add(TogglesHttpJsonAdapterFactory)
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
    .done()
    .let { ConfigurableMoshi(it) }
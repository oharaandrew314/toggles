package dev.andrewohara.toggles

val mostlyOld = ToggleEnvironment(
    weights = mapOf(
        old to Weight.of(2),
        new to Weight.of(1)
    ),
    overrides = mapOf(
        testUser to new
    )
)

val mostlyNew = ToggleEnvironment(
    weights = mapOf(
        old to Weight.of(1),
        new to Weight.of(2)
    ),
    overrides = emptyMap()
)

val mostlyOff = ToggleEnvironment(
    weights = mapOf(
        off to Weight.of(2),
        on to Weight.of(1)
    ),
    overrides = mapOf(
        testUser to on
    )
)

val alwaysOn = ToggleEnvironment(
    weights = mapOf(
        off to Weight.of(0),
        on to Weight.of(1)
    ),
    overrides = emptyMap()
)

val oldNewData = ToggleUpdateData(
    defaultVariation = old,
    variations = listOf(old, new),
    environments = mapOf(
        dev to mostlyNew,
        prod to mostlyOld
    )
)

val onOffData = ToggleUpdateData(
    defaultVariation = off,
    variations = listOf(off, on),
    environments = mapOf(
        dev to alwaysOn,
        prod to mostlyOff
    )
)

fun ToggleUpdateData.toCreate(toggleName: ToggleName) = ToggleCreateData(
    toggleName = toggleName,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments,
)
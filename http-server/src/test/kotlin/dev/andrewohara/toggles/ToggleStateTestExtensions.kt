package dev.andrewohara.toggles

fun ToggleState.toCreate(toggleName: ToggleName) = ToggleCreateData(
    toggleName = toggleName,
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)
package dev.andrewohara.toggles

val projectName1 = ProjectName.of("project1")
val projectName2 = ProjectName.of("project2")
val projectName3 = ProjectName.of("project3")

val toggleName1 = ToggleName.of("toggle1")
val toggleName2 = ToggleName.of("toggle2")
val toggleName3 = ToggleName.of("toggle3")

val toggleData1 = ToggleState(
    variations = mapOf(
        VariationName.of("old") to Weight.of(1),
        VariationName.of("new") to Weight.of(2)
    ),
    defaultVariation = VariationName.of("old"),
    overrides = mapOf(
        SubjectId.of("user1") to VariationName.of("new")
    )
)

val toggleData2 = ToggleState(
    variations = mapOf(
        VariationName.of("off") to Weight.of(2),
        VariationName.of("on") to Weight.of(2)
    ),
    defaultVariation = VariationName.of("off"),
    overrides = mapOf(
        SubjectId.of("user2") to VariationName.of("on")
    )
)

fun ToggleState.toCreate(toggleName: ToggleName) = ToggleCreateData(
    toggleName = toggleName,
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)
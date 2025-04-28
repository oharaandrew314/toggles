package dev.andrewohara.toggles

val projectName1 = ProjectName.of("project1")
val projectName2 = ProjectName.of("project2")
val projectName3 = ProjectName.of("project3")

val toggleName1 = ToggleName.of("toggle1")
val toggleName2 = ToggleName.of("toggle2")
val toggleName3 = ToggleName.of("toggle3")

val old = VariationName.of("old")
val new = VariationName.of("new")

val off = VariationName.of("off")
val on = VariationName.of("on")

val user1 = SubjectId.of("user1")
val user2 = SubjectId.of("user-second")
val testUser = SubjectId.of("testuser")

val mostlyOld = ToggleState(
    variations = mapOf(
        old to Weight.of(1),
        new to Weight.of(2)
    ),
    defaultVariation = old,
    overrides = mapOf(
        testUser to new
    )
)

val alwaysOn = ToggleState(
    variations = mapOf(
        off to Weight.of(0),
        on to Weight.of(1)
    ),
    defaultVariation = off,
    overrides = mapOf(
        user2 to on
    )
)
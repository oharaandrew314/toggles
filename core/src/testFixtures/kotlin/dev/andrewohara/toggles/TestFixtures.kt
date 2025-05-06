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

val dev = EnvironmentName.of("dev")
val staging = EnvironmentName.of("staging")
val prod = EnvironmentName.of("prod")
val devAndProd = listOf(dev, prod)

val uid1 = UniqueId.of("abcdefgh")
val uid2 = UniqueId.of("bcdefghi")


const val IDP1 = "idp1.com"
val idp1Email1 = EmailAddress.of("user1@$IDP1")
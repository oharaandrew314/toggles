plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    api(platform("org.http4k:http4k-bom:_"))

    api(project(":core"))
    api("org.http4k:http4k-api-openapi")
    api("org.http4k:http4k-format-moshi")
    api("se.ansman.kotshi:api:_")

    ksp("se.ansman.kotshi:compiler:_")
}
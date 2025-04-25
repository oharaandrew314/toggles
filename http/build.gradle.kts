dependencies {
    api(platform("org.http4k:http4k-bom:_"))

    api(project(":core"))
    api("org.http4k:http4k-api-openapi")
    api("org.http4k:http4k-format-moshi")
}
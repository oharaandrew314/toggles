dependencies {
    api(platform("org.http4k:http4k-bom:_"))

    implementation(project(":service"))
    implementation(project(":auth-core"))

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:_")
    implementation("org.http4k:http4k-web-htmx")
    implementation("com.iodesystems.kotlin-htmx:htmx:_")

    runtimeOnly("org.slf4j:slf4j-simple:_")
}
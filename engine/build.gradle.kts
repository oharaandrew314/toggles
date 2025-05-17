dependencies {
    api(project(":core"))
    implementation("io.github.microutils:kotlin-logging:_")
    runtimeOnly("org.slf4j:slf4j-simple:_")

    testImplementation(project(":http-server"))
    testImplementation(testFixtures(project(":http-server")))
}
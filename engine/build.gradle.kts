dependencies {
    api(project(":core"))
    implementation("io.github.microutils:kotlin-logging:_")

    testImplementation(project(":http-server"))
    testImplementation(testFixtures(project(":http-server")))
}
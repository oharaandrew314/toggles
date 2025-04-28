dependencies {
    api(project(":core"))
    implementation("io.github.microutils:kotlin-logging:_")
    compileOnly("com.github.ben-manes.caffeine:caffeine:_")

    testImplementation(project(":http-server"))
    testImplementation(project(":http-client"))
    testImplementation(testFixtures(project(":core")))
    testImplementation(testFixtures(project(":http-server")))
}
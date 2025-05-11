dependencies {
    api(project(":http"))
    api("dev.andrewohara:service-utils:_")
    api("io.github.microutils:kotlin-logging:_")
    runtimeOnly("org.slf4j:slf4j-simple:_")
    implementation("com.nimbusds:nimbus-jose-jwt:_")

    testImplementation("dev.forkhandles:result4k-kotest")
    testImplementation(testFixtures(project(":core")))

    testFixturesApi("dev.forkhandles:result4k-kotest")
    testFixturesApi(testFixtures(project(":core")))
    testFixturesApi(project(":http-client"))
    testFixturesImplementation("com.nimbusds:nimbus-jose-jwt:_")
}
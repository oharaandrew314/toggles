dependencies {
    api(project(":http"))
    api("dev.andrewohara:service-utils:_")
    api("dev.forkhandles:result4k")
    api("io.github.microutils:kotlin-logging:_")
    runtimeOnly("org.slf4j:slf4j-simple:_")

    testFixturesImplementation(project(":http-server"))
    testFixturesImplementation(project(":http-client"))
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testFixturesImplementation("org.http4k:http4k-testing-kotest")
    testFixturesImplementation("dev.forkhandles:result4k-kotest")
}
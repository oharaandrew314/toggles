dependencies {
    api(project(":core"))
    api("dev.andrewohara:service-utils:_")
    api("io.github.microutils:kotlin-logging:_")
    runtimeOnly("org.slf4j:slf4j-simple:_")

    testFixturesApi(testFixtures(project(":core")))
    testFixturesApi("dev.forkhandles:result4k-kotest")
}
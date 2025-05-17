dependencies {
    api(project(":core"))
    api("dev.andrewohara:service-utils:_")
    api("io.github.microutils:kotlin-logging:_")

    testFixturesApi(testFixtures(project(":core")))
    testFixturesApi("dev.forkhandles:result4k-kotest")
}
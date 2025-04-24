plugins {
    kotlin("jvm")
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

dependencies {
    api(platform("org.http4k:http4k-bom:_"))
    api(platform("dev.forkhandles:forkhandles-bom:_"))

    api("org.http4k:http4k-core")
    api("dev.forkhandles:result4k")
    api("dev.forkhandles:values4k")
    api("dev.andrewohara:service-utils:_")

    implementation("io.github.microutils:kotlin-logging:_")
    runtimeOnly("org.slf4j:slf4j-simple:_")

    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testFixturesImplementation(project(":"))
    testFixturesImplementation("org.http4k:http4k-testing-kotest")
    testFixturesImplementation("dev.forkhandles:result4k-kotest")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
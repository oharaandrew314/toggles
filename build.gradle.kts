plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish") apply false
    `java-test-fixtures`
}


allprojects {
    repositories {
        mavenCentral()
    }

    tasks.register<Test>("unittest") {
        description = "Runs tests excluding those annotated with @TestContainers"
        group = "verification"

        useJUnitPlatform {
            excludeTags("org.testcontainers.junit.jupiter.Testcontainers")
        }
    }

}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-test-fixtures")

    dependencies {
        testImplementation(platform("org.testcontainers:testcontainers-bom:_"))

        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-assertions-core-jvm:_")
        testImplementation("org.slf4j:slf4j-simple:_")
        testImplementation("org.testcontainers:junit-jupiter")

        testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
        testFixturesImplementation("io.kotest:kotest-assertions-core-jvm:_")
    }

    tasks.test {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks.compileKotlin {
        compilerOptions {
            allWarningsAsErrors = true
        }
    }
}
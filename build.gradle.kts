plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish") apply false
    `java-test-fixtures`
}


allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-test-fixtures")

    dependencies {
        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-assertions-core-jvm:_")
        testImplementation("org.slf4j:slf4j-simple:_")

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
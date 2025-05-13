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
        testImplementation(platform("org.testcontainers:testcontainers-bom:_"))

        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-assertions-core-jvm:_")
        testImplementation("org.slf4j:slf4j-simple:_")
        testImplementation("org.testcontainers:junit-jupiter")

        testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
        testFixturesImplementation("io.kotest:kotest-assertions-core-jvm:_")
    }

    tasks.test {
        useJUnitPlatform {
            val tags = project.findProperty("excludeTags")?.toString()?.split(",").orEmpty()
            excludeTags.addAll(tags)
        }
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
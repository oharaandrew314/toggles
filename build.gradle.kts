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
    }

    tasks.test {
        useJUnitPlatform()
    }
    kotlin {
        jvmToolchain(21)
    }
}
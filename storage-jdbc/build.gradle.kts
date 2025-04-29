
dependencies {
    api(project(":http-server"))

    testImplementation("com.h2database:h2:_")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("mysql:mysql-connector-java:_")
    testRuntimeOnly("org.postgresql:postgresql:_")
    testImplementation("com.zaxxer:HikariCP:_")
    testImplementation(testFixtures(project(":http-server")))
}
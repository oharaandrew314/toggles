
dependencies {
    api(project(":http-server"))
    implementation("org.flywaydb:flyway-core:_")

    testImplementation("com.h2database:h2:_")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("mysql:mysql-connector-java:_")
    testRuntimeOnly("org.postgresql:postgresql:_")
    testImplementation("com.zaxxer:HikariCP:_")
    testImplementation(testFixtures(project(":http-server")))
    implementation("org.flywaydb:flyway-mysql:_")
    implementation("org.flywaydb:flyway-database-postgresql:_")
}
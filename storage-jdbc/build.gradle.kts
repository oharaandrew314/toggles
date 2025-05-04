
dependencies {
    api(project(":http-server"))
    implementation("org.flywaydb:flyway-core:_")

    testImplementation("com.h2database:h2:_")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mariadb")
    testRuntimeOnly("mysql:mysql-connector-java:_")
    testRuntimeOnly("org.postgresql:postgresql:_")
    testRuntimeOnly("org.mariadb.jdbc:mariadb-java-client:3.5.3")
    testImplementation("com.zaxxer:HikariCP:_")
    testImplementation(testFixtures(project(":http-server")))
    implementation("org.flywaydb:flyway-mysql:_")
    implementation("org.flywaydb:flyway-database-postgresql:_")
}
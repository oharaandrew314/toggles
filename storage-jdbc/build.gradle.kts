dependencies {
    api(project(":service"))
    implementation("org.flywaydb:flyway-core:_")

    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mariadb")
    testImplementation("org.testcontainers:mysql")
    testImplementation("com.zaxxer:HikariCP:_")
    testImplementation(testFixtures(project(":http-server")))

    testRuntimeOnly("com.h2database:h2:_")
    testRuntimeOnly("mysql:mysql-connector-java:_")
    testRuntimeOnly("org.postgresql:postgresql:_")
    testRuntimeOnly("org.mariadb.jdbc:mariadb-java-client:3.5.3")
    testRuntimeOnly("org.flywaydb:flyway-mysql:_")
    testRuntimeOnly("org.flywaydb:flyway-database-postgresql:_")
}
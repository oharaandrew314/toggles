plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    api(project(":http-server"))
    api("org.http4k:http4k-connect-amazon-dynamodb")

    ksp("se.ansman.kotshi:compiler:_")

    testImplementation("org.http4k:http4k-connect-amazon-dynamodb-fake")
    testImplementation(testFixtures(project(":http-server")))
}
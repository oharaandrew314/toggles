dependencies {
    api(project(":http"))
    api(project(":service"))
    api("com.nimbusds:nimbus-jose-jwt:_")

    testFixturesApi(project(":http-client"))
    testFixturesApi(testFixtures(project(":service")))
}
dependencies {
    api(project(":http"))
    api(project(":service"))
    api(project(":auth-core"))

    testFixturesApi(project(":http-client"))
    testFixturesApi(testFixtures(project(":service")))
}
plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.3"
}
rootProject.name = "toggles"
include("core")
include("http")
include("http-server")
include("http-client")
include("engine")
include("storage-dynamodb")
include("storage-jdbc")
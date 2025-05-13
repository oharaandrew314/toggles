[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Toggles

Feature Flags Server and Kotlin Library

## Storage
- Memory
- DynamoDB
- JDBC (agnostic, but tested with following)
  - H2
  - MySql
  - MariaDB
  - Postgres

## TODO
- websocket/sse engine
- user login (google, microsoft, github)
  - Users should be able to make tenants, and also be invited to others (ie users can have multiple tenants)
- file storage
- readme should include bucketing spec
- readme should include required flyway drivers
- docker image
- readme to show how to build your own server with kotlin
- web frontend
  - kotlinx.html
  - htmx: https://github.com/IodeSystems/kotlinx-htmx
  - Daisy UI
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Toggles

Feature Flags Server and Kotlin Library

## Storage
- Memory
- DynamoDB
- JDBC (agnostic, but tested with following)
  - H2
  - MySql
  - Postgres

## TODO
- environments
- tenant mode
- client keys (kms, database)
- user login (google, microsoft, github)
  - Users should be able to make their own tenant, and also be invited to others (ie users can have multiple tenants)
- user roles (admin, operator, tester)
- file storage
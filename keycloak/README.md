# Authentication Server

The compose file `compose.e2e.yml` is created with the help of this generation
tool https://skycloak.io/tools/keycloak-docker-compose-generator/  
The `realm-data` directory holds keycloak imported data related to the `storehub` realm.  
The `keycloak-themes` directory holds the customization for the login page.

## Clients

| Client                | Role                                                                                                   |
|-----------------------|--------------------------------------------------------------------------------------------------------|
| angular-public-client | Used by the frontend app                                                                               |
| e2e-test-client       | Used by seed scripts [cypress](../frontend/cypress/e2e/seed)  and [node](../scripts/seed.js) for login |
| order-admin           | Used by order service to create users and assign roles                                                 |
| order-internal        | Used by service for internal communications                                                            |

## Roles
 - `CUSTOMER`: role assigned to every user.
 - `OWNER`: role assigned to store owners.

## Compose setup
Link to needed configuration to use keycloak as an auth server for the application [see](../README.md#auth-configuration-locale2e)
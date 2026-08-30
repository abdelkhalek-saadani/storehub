# StoreHub

A multi tenant e-commerce platform.

## End-to-end testing

E2e testing is done with `cypress`.
`compose.e2e.yml` spins up the infrastructure needed to run e2e tests.
Since no migration strategy is set for `order-service`, manual setup is needed each time the schema changes.
The current approach uses the postgres image's `/docker-entrypoint-initdb.d` to run an init script.
The init script lives at `./backend/order-service/src/main/resources/init-scripts` and is generated with:

```shell
pg_dump \
  -h localhost \
  -p 5432 \
  -U postgres \
  -d order_db \
  --schema-only \
  --no-owner \
  --no-privileges \
  --no-tablespaces \
  --no-comments \
  -f schema_dump.sql
```

The checkout flow e2e test assumes the pre-existence of two stores, two users, and products — that's why the seed
script (`cypress/e2e/seed/seed.cy.ts`) must be run before the checkout flow test.
The checkout flow tests are designed to run sequentially; they are not independent.

### Run checkout flow end-to-end test

This test does the following, in order:

- Visits the `/welcome` page and asserts it shows the two CTAs: Login and Create An Account.
- Tries to create an account with an already existing email and asserts that an error is shown.
- Signs up with a new email, then logs in through Keycloak, asserting the login succeeded.
- Asserts a redirect to `/welcome-pick-store` happens, and picks a store.
- Performs actions on the cart, then clicks the continue checkout button.
- Fills in the checkout form and places the order (`*`).
- Intercepts the response from the order service and replaces the value of `paymentApprovalUrl` into
  the track order URL, so the frontend skips the redirect and goes straight to the track order page.
- Asserts the order status is `CREATED` on the track order page.
- Simulates a PayPal webhook and watches for order status changes, to test SSE and live status updates.

To run the test, the app needs data (stores, users, products, etc.), so we run the seed script first.
To perform the end-to-end test, run:

```shell
docker compose -f compose.e2e.yml -f frontend.override.yml down
docker compose -f compose.e2e.yml -f frontend.override.yml up -d --build
cd frontend/ && npx cypress run --spec "cypress/e2e/seed/seed.cy.ts"
npx cypress run --spec "cypress/e2e/checkout-flow/checkout-flow.cy.ts"
```
or 
```shell
make e2e
```


`*`: PayPal is mocked using WireMock for the order creation endpoint, OAuth token, and webhook verification. The mock
returns a mocked approval URL containing a token; the order service saves that token alongside the order. When the
frontend receives the redirect URL, it replaces it with the track order page URL, passing the payment token as a query
param (`paymentOrderId`).

## Seed

To seed the running `compose.e2e.yml` project with stores, users, and products, run (from the frontend directory):

```shell
cd frontend
npx cypress run --config baseUrl=http://localhost:4200 --spec "cypress/e2e/seed/seed.cy.ts"
```

## Frontend dev using e2e compose file and seed script

To run your dev frontend:

```shell
ng serve --configuration e2e
export FRONTEND_URL=http://localhost:4200
docker compose -f compose.e2e.yml -f frontend.override.yml up -d
docker compose -f compose.e2e.yml -f frontend.override.yml stop frontend
```

Note: make sure to set `FRONTEND_URL`, or the order and catalog services will reject frontend requests due to CORS.

## Auth setup for local/e2e

Keycloak must be reachable at the same hostname:port from both the browser and backend containers, or JWTs will fail
issuer validation (401 "iss claim is not valid").

1. Add this to your hosts file (`/etc/hosts`):

```shell
127.0.0.1  auth-server
# needed so the browser can resolve auth-server, same as Docker's
# internal DNS does for backend containers
```

2. Keycloak's internal listening port must match its published port (see `keycloak/compose.e2e.yml`:
   `KC_HTTP_PORT=8088`, `ports: "8088:8088"`).

3. All backend services and the frontend must use `http://auth-server:8088/realms/storehub` as the Keycloak base URL —
   not `localhost`, not port `8080`.
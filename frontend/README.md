# frontend

The customer-facing storefront and checkout experience.

Tech: Angular, Angular Material, Tailwind CSS, and SignalStore for state management.

## Structure

The app is structured into feature folders. `core` holds the auth guard and app-wide configuration; `shared` holds
components, models, and services used across more than one feature.

## State Management

### Cart

Cart state is stored in a SignalStore. The cart is modeled as a list of items, and every update is an upsert,
upserting an item with quantity 0 removes it. The store exposes three main methods:

- `loadCart`: fetches the cart from the backend when a component is first initialized.
- `upsertItems` : adds, removes, or edits the quantity of one or more cart items; each call hits the backend and returns the
  updated item list.
- `clearCart`: resets the cart.

### Selected Store & Guest Identity

The selected store is managed by a store-context service, backed by `localStorage`. For guests, the guest ID is
also stored in `localStorage`.

## Signup

The signup form collects user information (username, address, email, password) and an account type: customer or
owner. When the owner type is selected, additional store-detail inputs are shown.

**Customer signup**: the user API is called to create the user, then the user is redirected to the login page, and
finally to the store-selection modal.

**Owner signup** involves two operations: creating the user and creating the store. Since store creation requires
an authenticated JWT (the owner's token), it can't happen in the same request as signup, and is instead coordinated
across the login redirect:

1. On submit, store details are saved to `sessionStorage`, and the signup endpoint is called with the user
   information.
2. After the user is created, they're redirected to the login page.
3. After login (now holding a valid JWT) the user is redirected to a post-login page. This page checks whether
   saved store details exist in `sessionStorage`: if so (owner path), it calls the store API to create the store;
   if not (customer path), it proceeds directly to store selection.
4. Once the store is created, its details are cleared from `sessionStorage`.

For implementation details, see the [`auth`](./src/app/auth) feature folder.

## Auth

A user can log in either by clicking the login button, or by being redirected to login when visiting a protected
route. `keycloak-js` handles the login flow.

Two interceptors are used:

- **Bearer token interceptor**: attaches the JWT from `keycloak-js` to requests, if one exists.
- **Guest ID interceptor**: attaches the `X-Guest-Id` header to requests if a guest ID exists in `localStorage`,
  and stores the guest ID returned by the backend (if any) back into `localStorage`.

The backend determines whether a request comes from a connected user or a guest by checking the bearer token first,
then falling back to the guest ID header.

## Responsive Design

Responsiveness is implemented for two breakpoints: 1440px (desktop) and 375px (mobile).

## Running Locally

To run the dev server:

```shell
ng serve
```

Or with Docker Compose:

```shell
docker compose --profile dev watch angular-dev
```

## Testing

Karma is used as the test runner with Jasmine for unit tests, covering the main features, components, and services
with unit and integration tests. `TestBed` is used to mock the Angular runtime environment.

To run tests:

```shell
ng test
```

Or with Docker Compose:

```shell
docker compose --profile test run --rm --build angular-test
```

For end-to-end tests, see the root [Testing](../README.md/#testing) section.

## Development Against the Quickstart Stack

To develop the frontend locally with `ng serve` while backed by the full quickstart stack (order-service, catalog-service,
and payment-service with dummy data):

```shell
cd ..
export FRONTEND_URL=http://localhost:4200               # allowed origins by order service and catalog service
make quickstart                                         # spin up the compose project and execute the seed script
docker compose -f compose.quickstart.yml stop frontend  # remove the frontend service from the stack because it is not needed(optional)
cd frontend 
ng serve --configuration quickstart                     # run angular with hot reload                  
```  

For more details about the quickstart setup see [Quickstart Section](../README.md/#quick-start)

This runs the frontend on port 4200 with hot reload, backed by order-service, catalog-service, payment-service and auth server.
`FRONTEND_URL` is required for CORS on order-service and catalog-service. The `quickstart` configuration points the
frontend at the correct service ports for the quickstart setup.  

Note: make sure to set `FRONTEND_URL`, or the order and catalog services will reject frontend requests due to CORS.


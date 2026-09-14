# order-service

Role: manages orders, carts, users, and stores.

This is a reactive service; its stack is WebFlux and R2DBC for database connectivity.
The code follows a feature-package structure, where top-level packages represent features, and nested directories
represent the technical split (services, entities, etc.).

## Quick Start

To run using Docker:

```shell
docker build -t order-image .
docker run -e SPRING_PROFILES_ACTIVE=e2e --env-file .env --network host --name order-app order-image
```

This assumes RabbitMQ is running on `5672`/`15672`, Postgres is running on `5432`, and Keycloak is reachable at
`http://auth-server:8088`.

## Authentication & Authorization

Auth is JWT-based. Store owner endpoints are protected with the `STORE_OWNER` role, while inter-service endpoints
(e.g. the stores summary endpoint) are protected with the `SERVICE` role.

## Events Consumed

- **Payment Status Updated** — updates the order status based on the received payment status.

## Events Published

- **Store Created** — on store creation.
- **User Created** — on user creation.
- **Items Released** — on items release (e.g. order creation rollback).
- **Slot Released** — on slot release (e.g. order creation rollback).
- **Order Created** — on order creation.

## Cart

Role: creates and mutates the cart.

All cart updates go through `upsertItems`. Calling `upsertItems` with an item at quantity 0 is equivalent to removing
it. On each upsert, the cart is repopulated with current prices fetched from catalog-service.

Both guest and connected-user carts are persisted — a guest cart has its `guestId` field set, a connected-user cart
has `userId` set. The two are processed the same way; at the controller level, an owner-resolver service determines
whether the request belongs to a user or a guest. A connected user's request carries a JWT; a guest's request
carries an `X-Guest-Id` header. If neither is present, a random `guestId` is generated and returned to the caller
via an `X-Guest-Id` header. Orders use the same ownership/identification model as carts.

### Implementation Notes

- A timeout is applied when fetching prices from catalog-service, to avoid the request hanging indefinitely.
- `Mono.defer(() -> createEmptyCart(owner, storeId))` is used so that `createEmptyCart` isn't run eagerly at
  assembly time, but lazily when the Mono is subscribed to. Without deferring, the save-to-database call would
  execute prematurely, causing bugs.

```java
private Mono<CartEntity> createEmptyCart(CartOwner owner, UUID storeId) {
    CartEntity cart = new CartEntity();
    // logic ...
    return cartRepository.save(cart);
}
```

## Order

Role: processes order requests and creates orders.

### Order Processing Pipeline

An order request goes through a pipeline that processes, checks, and saves the order. The request references a
cart rather than carrying a list of items directly:

1. Check order existence by idempotency key: return the existing order if found, otherwise continue.
2. Process the order request:
    1. Check resource availability (items and slot) by calling catalog-service: return an error if unavailable,
       otherwise continue.
    2. Resolve the owner (guest or connected user).
    3. Retain resources (items and slot) by calling catalog-service.
    4. Create the order:
        1. Look up the cart to extract its items.
        2. Build the order object from the cart items and the order request fields.
        3. Calculate order prices by calling catalog-service, and populate the order and its items with the fetched
           prices.
        4. Save the order to the database.
        5. Publish an `Order Created` event via RabbitMQ.
            - On error, release the retained resources by publishing `Items Released` and `Slot Released` events to
              catalog-service.
3. Attach payment by calling payment-service, and save the result.
4. Clear the cart.
5. Return the response (order ID, payment link).

### Idempotency

Each order carries an idempotency key generated client-side, by the checkout form. It's used to prevent duplicate
order processing if the checkout submit button is clicked multiple times.

### Live Status Updates (SSE)

Live order status updates are implemented with Server-Sent Events. A sinks map holds one status sink per order,
keyed by order ID. This map lives in `StatusService`, which encapsulates the logic to emit events into a sink and
to subscribe to one.

The frontend hits the track status endpoint and receives a stream: first the current status looked up from the
database, then subsequent events from that order's sink. All order status updates go through `StatusService`, to
guarantee that every status update is both saved to the database and emitted to the corresponding sink.

### Parallel Resource Retention

`Mono.zip()` is used to run slot retention and item retention in parallel. The system has exactly two consistent
end states after these two operations: either both are retained (and processing continues to the next step), or
both are released. Partial retention (only the slot or only the items) is never permitted, since it would leave
the system holding resources for nothing.

By default, if one operation in a `zip` fails, the other is discarded, the whole chain short-circuits, and the
error propagates, the thrown exception acts as a flow-control signal rather than data. To avoid that, each
operation is wrapped in a container holding its outcome (success or failure) rather than letting it throw. This
way, `zip` always completes for both operations, since neither emits an error anymore, and the downstream step can
inspect both outcomes and perform a partial rollback if either operation failed, bringing the system back to a
state where all resources for that order are free.

The container is the `Result<T>` record:

```java
record Result<T>(T value, Throwable error, boolean isSuccess) {
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error, false);
    }
}
```

`wrap` adapts a `Mono<T>` into a `Mono<Result<T>>`:

```java
private <T> Mono<Result<T>> wrap(Mono<T> operation, String errorMessage) {
    return operation
            .map(Result::success)
            .onErrorResume(e -> {
                log.error("{}: {}", errorMessage, e.getClass().getSimpleName());
                return Mono.just(Result.failure(e));
            });
}
```

## Store

Role: creates stores and assigns owners.

Ownership is modeled with a record in the `store_membership` table, holding `storeId`, `userId`, and `role` set to
`OWNER`. Each store has exactly one owner, and each owner has exactly one store.

Employee support has been partially started, with a record in `store_membership` using `role` set to `EMPLOYEE`;
the feature is planned to be completed in the future.

Store creation includes: creating the store, adding a `store_membership` entry with role `OWNER`, adding the
`STORE_OWNER` role in Keycloak.

## User

Role: creates users.

Calls Keycloak to create the user there with role `CUSTOMER`. A local user record is stored in the database and
linked to its Keycloak user via `keycloakId`.

## Persistence

### Address as JSON

The `Order` entity has an `address` field composed of state, city, and zip code, which needs to be persisted as
JSON. `Converter<Json, Address>` and `Converter<Address, Json>` are implemented and annotated with
`@ReadingConverter`/`@WritingConverter` to serialize `Address` to JSON on write and deserialize it back on read.
Both converters are registered via an `R2dbcCustomConversions` bean. Hibernate's `@JdbcTypeCode(SqlTypes.JSON)`
can't be used here, since it's a Hibernate-specific annotation and this service uses R2DBC, not JPA.

### Entity Relationships

[expand: how spring-r2dbc-relationships (JoseLion) is used to model entity relationships, since R2DBC doesn't
support them natively]

## Schema Setup for E2E

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

## Testing

- `Jwt.withTokenValue`: used to test `OwnerResolver`.
- `WebTestClient`: used for controller integration tests.
- WireMock: used to stub external endpoints.
- `StepVerifier`: used to test reactive streams.
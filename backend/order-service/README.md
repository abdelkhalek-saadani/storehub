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
The current approach uses the Postgres image's `/docker-entrypoint-initdb.d` to run an init script.
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

## Schema setup for e2e

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
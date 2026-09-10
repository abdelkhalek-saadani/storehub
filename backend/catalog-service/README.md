# catalog-service

The code follows a feature-package structure, where top-level packages represent features, and nested directories
represent the technical split (services, entities, etc.).

## Quick Start 
To run using docker:
```shell
docker build -t catalog-image .
docker run -e SPRING_PROFILES_ACTIVE=e2e --env-file .env --network host --name catalog-app catalog-image
```
Assuming rabbitmq broker is running on `5672,15672` and postgres database is running on `5432` and keycloak on `http://auth-server:8088`


## Authentication & Authorization

Auth is JWT-based. Store owner endpoints are protected with the `STORE_OWNER` role, while inter-service endpoints
(e.g. reservation endpoints and the price calculation endpoint) are protected with the `SERVICE` role.

## Events Consumed

- **Store Created** — updates its shadow store table (a mirror of order-service's store table).
- **User Created** — updates its shadow user table.
- **Items Released** — releases retained items.
- **Slot Released** — releases a retained slot.
- **Order Created** — switches slot-retain and items-retain records to the `CONFIRMED` state.

## Inventory

Role: performs reservation/release of order items.

### Stock Modeling

Product quantity is modeled with two objects: `Stock` and `StockMovement`.

- **Stock** — mutable, linked to a product, and holds its quantity state. It owns the logic to update the product's
  on-hand, available, and reserved quantities: `quantityAvailable = quantityOnHand - quantityReserved`.
- **StockMovement** — an append-only ledger that serves as the history of stock changes.

Reservations are held as a mutable `Reservation` object, linked to a product, holding the reserved quantity and a
state: `ACTIVE -> {CONFIRMED | RELEASED | EXPIRED}`.

### Reservation & Release Logic

This logic lives in `StockService`.

`reserveForOrder` calls `reserveForOrderTx` to perform the reservation for an order. `reserveForOrderTx` is
transactional, so if a reservation fails for one item, the reservation for all items in the order is rolled back.

One subtlety: `@Transactional` only takes effect when a method is called through the Spring-managed proxy (i.e. via
dependency injection). Calling `reserveForOrderTx` directly from within its own class would bypass the proxy and
call it as a plain method, silently losing transactional behavior. To avoid this, `StockService` injects itself
(a self-reference), so that calling `reserveForOrderTx` goes through the proxy and transactional behavior takes
effect as expected.

### Concurrency Strategy

Stock updates use optimistic locking via a `version` column, since race conditions are treated as the exception
rather than the norm here. A repository method using pessimistic writes is also kept in place, ready to switch to
for hot SKUs (e.g. during a sale event).

## Pricing

Role: applies discounts to a list of items (a cart or order) and calculates line-item totals and the overall total.

Discount types are implemented using the **Strategy** pattern: a `DiscountStrategy` interface defines an `apply`
method, and each discount type implements it with its own business logic and attributes (e.g. a quantity discount
has a `minimumQuantity` field, while a "Buy X Get Y" discount has `requiredQuantity` and `freeQuantity`). A
**Factory** pattern is used to create the appropriate discount strategy from a unified holder object.
`@JsonTypeInfo`/`@JsonSubTypes` (Jackson polymorphic typing) is used for serialization/deserialization.
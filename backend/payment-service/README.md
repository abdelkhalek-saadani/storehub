# payment-service

Role: the system's gateway to the payment service provider. For each order, it creates a payment linking the order
to the corresponding PayPal order. This service manages the full PayPal order lifecycle: creation, approval,
authorization, void, capture, and refund.

Uses `RestClient` to communicate with PayPal, with JWT-based auth.

## Events Published

- **Payment Status Updated**: notifies consumers of payment status changes.

## Status Transitions

Every status transition follows the same two-step pattern: it moves to `X_PENDING` as soon as a response is
received from the PayPal API, then to `X` only once the corresponding webhook is received. The webhook is what
guarantees PayPal has actually processed the request successfully.

## Authentication & Authorization

All endpoints are protected with the `SERVICE` role, since they currently only serve order-service. Once the admin
panel is added, some endpoints will also be made accessible with the `STORE_OWNER` role.

## Auditing & Idempotency

- **PaymentAudit**: traces payment status transitions.
- **ProcessedWebhookEvent**: prevents duplicate processing of the same webhook.

## Example Flow

1. order-service calls the payment creation endpoint.
2. payment-service builds a request with the order total and sends it to PayPal to create a PayPal order.
3. PayPal creates the order.
4. payment-service saves the payment locally with status `CREATED`, and returns the payment ID and approval link to
   order-service.
5. order-service returns the approval link to the client.
6. The client approves the payment.
7. PayPal processes the approval and sends a webhook to payment-service.
8. payment-service processes the webhook: sets the payment status to `APPROVED`, publishes an event, then sends an
   authorization request to PayPal and sets the status to `AUTHORIZATION_PENDING`.
9. PayPal processes the authorization request and sends another webhook.
10. payment-service processes the webhook, updates the status to `AUTHORIZED`, and publishes an event.
11. order-service consumes the event and updates its own status accordingly.

## Testing

- Testcontainers: spins up a Postgres database for payment specification integration tests and spins up a rabbit broker for integration tests.
- MockMvc: used to test the HTTP layer.
- WireMock: used to stub PayPal endpoints.
- Mockito: used to spy on some service methods.
- Awaitility: used to await assertions that depend on async work (events).
- A testing queue: used to verify that payment-service has emitted events to RabbitMQ.


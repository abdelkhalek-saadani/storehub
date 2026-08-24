# Mock Server
This is a lightweight wiremock server used to stub PayPal endpoints for e2e testing.  
This server stubs two endpoints:
- `POST` `/v2/checkout/orders` for PayPal order creation.
- `POST` `/v1/notifications/verify-webhook-signature` for webhook signature verification.  
To start the server run:
```shell
docker compose up -d wiremock
```
To test:
```shell
curl -X POST http://localhost:8089/v2/checkout/orders
curl -X POST http://localhost:8089/v1/notifications/verify-webhook-signature
```
For using with the e2e testing, set PayPal base url env variable to `http://localhost:8089`. 
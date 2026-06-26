# Interaction with the order microservice 

Here is the list of possible interactions: 
- How to create a payment?
    - The order sends an http request to create a payment, it receives the approval url
- How to cancel?
    - The order send an https request and receives that request is accepted, order changes its status to CANCEL_PENDING and  wait for payment to emit an event to update it status to CANCELLED
When is payment get captured ? 
    - When Order is delivered, then an event Order->Payment , then payment get captured
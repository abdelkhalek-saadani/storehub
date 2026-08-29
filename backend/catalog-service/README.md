# Catalog Service
To run using docker: 
```shell
docker build -t catalog-image .
docker run -e SPRING_PROFILES_ACTIVE=e2e --env-file .env --network host --name catalog-app catalog-image
```
Assuming rabbitmq broker is running on `5672,15672` and postgres database is running on `5432` and keycloak on `http://auth-server:8088`
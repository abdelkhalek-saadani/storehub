# Frontend

This the frontend app, it is built with angular.  
To run development server:  
```shell
ng serve
```
and with compose 
```shell
docker compose --profile dev watch angular-dev
```
To run tests:
```shell
ng test
```
and with compose
```shell
docker compose --profile test run --rm --build angular-test
```


The frontend service `frontend` defined in the `compose.yml` file is used by the `compose.e2e.yml` at the root. The `compose.e2e.yml` is used spin up the infrastructure for e2e testing.

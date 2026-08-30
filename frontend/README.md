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

## Seed
To seed the running `compose.e2e.yml` project with stores, users and products run (from the frontend directory):
```shell
npx cypress run --config baseUrl=http://localhost:4200 --spec "cypress/e2e/seed/seed.cy.ts"
```
To run a single feed spec use : 
```shell
 npx cypress run --config baseUrl=http://localhost:4200 --expose grep='creates slot configs' --spec "cypress/e2e/seed/seed.cy.ts"
```

## Development
A server development using `ng serve` can run with backend defined in `compose.e2e.yml` with dummy data
```shell
cd ..
docker compose -f compose.e2e.yml -f frontend.override.yml down 
export FRONTEND_URL=http://localhost:4200
docker compose -f compose.e2e.yml -f frontend.override.yml up -d
docker compose -f compose.e2e.yml -f frontend.override.yml stop frontend 
ng serve --configuration e2e
```
This way we will have a frontend running on 4200 with hot reload, and a backend of order, catalog and payment server running.
The `FRONTEND_URL` is for order and catalog CORS, the `--configuration e2e` is to configure the frontend for the order and catalog port in e2e setup

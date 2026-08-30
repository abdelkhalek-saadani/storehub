.PHONY: e2e up down seed test

e2e:
	$(MAKE) down
	trap '$(MAKE) down' EXIT; \
	$(MAKE) up && \
	$(MAKE) seed && \
	$(MAKE) test

up:
	docker compose -f compose.e2e.yml -f frontend.override.yml up -d --build

seed:
	cd frontend && npx cypress run --spec "cypress/e2e/seed/seed.cy.ts"

test:
	cd frontend && npx cypress run --spec "cypress/e2e/checkout-flow/checkout-flow.cy.ts"

down:
	docker compose -f compose.e2e.yml -f frontend.override.yml down
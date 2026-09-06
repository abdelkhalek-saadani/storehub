.PHONY: e2e e2e-up e2e-down e2e-seed e2e-test quickstart quickstart-up quickstart-seed quickstart-down

# ---- E2E ----
e2e:
	$(MAKE) e2e-down
	trap '$(MAKE) e2e-down' EXIT; \
	$(MAKE) e2e-up && \
	$(MAKE) e2e-seed && \
	$(MAKE) test

e2e-up:
	docker compose -f compose.e2e.yml -f frontend.override.yml up -d --build

e2e-seed:
	cd frontend && npx cypress run --spec "cypress/e2e/seed/seed.cy.ts"

e2e-test:
	cd frontend && npx cypress run --spec "cypress/e2e/checkout-flow/checkout-flow.cy.ts"

e2e-down:
	docker compose -f compose.e2e.yml -f frontend.override.yml down

# ---- Quickstart ----
quickstart:
	$(MAKE) quickstart-down
	trap '$(MAKE) quickstart-down' EXIT; \
	$(MAKE) quickstart-up && \
	$(MAKE) quickstart-seed && \
	docker compose -f compose.quickstart.yml logs -f

quickstart-up:
	docker compose -f compose.quickstart.yml up -d --build --wait

quickstart-seed:
	node scripts/seed.js

quickstart-down:
	docker compose -f compose.quickstart.yml down
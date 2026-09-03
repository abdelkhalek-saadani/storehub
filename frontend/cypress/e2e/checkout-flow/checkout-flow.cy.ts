import { EXISTENT_USER, STORE_ONE_SLUG } from '../../support/seed-data';

describe('End-to-end checkout flow', { testIsolation: false }, () => {
  const existingUser = {
    email: EXISTENT_USER.email, // seeded user1
    password: 'Passw0rd!',
  };

  const newUser = {
    email: `newuser+${Date.now()}@gmail.com`, // unique per run, avoid seed collisions
    password: 'Passw0rd!',
    firstName: 'New',
    lastName: 'User',
    address: {
      street: 'Street',
      city: 'City',
      zipCode: '9050',
      deliveryInstructions: 'Some instructions',
      apartment: 'Apartment 5',
    },
    phoneNumber: '+21600000002',
  };

  function getAddressString(address: typeof newUser.address) {
    return address.apartment + address.street + address.city;
  }

  let paymentOrderId: string;

  before(() => {
    cy.visit('/welcome');
  });

  it('shows welcome buttons', () => {
    cy.get('[data-cy=login-btn]').should('be.visible');
    cy.get('[data-cy=create-account-btn]').should('be.visible').click();
    cy.location('pathname').should('eq', '/signup');
  });

  it('rejects duplicate email', () => {
    cy.location('pathname').should('eq', '/signup');
    cy.get('[data-cy=email-input]').clear().type(existingUser.email);
    cy.get('[data-cy=firstName-input]').clear().type(newUser.firstName);
    cy.get('[data-cy=lastName-input]').clear().type(newUser.lastName);
    cy.get('[data-cy=address-input]').clear().type(getAddressString(newUser.address));
    cy.get('[data-cy=phone-input]').clear().type(newUser.phoneNumber);
    cy.get('[data-cy=password-input]').clear().type(newUser.password);
    cy.get('[data-cy=signup-submit]').click();

    cy.get('[data-cy=signup-error]').should('contain', 'Email already registered');
    cy.location('pathname').should('eq', '/signup');
  });

  it('signs up with a new email, redirects to Keycloak and logs in', () => {
    cy.get('[data-cy=email-input]').clear().type(newUser.email);
    cy.get('[data-cy=signup-submit]').click();

    cy.location('href', { timeout: 10000 }).should(
      'include',
      '/realms/storehub/protocol/openid-connect/auth',
    );
    cy.location('search').should('include', `login_hint=${encodeURIComponent(newUser.email)}`);

    const keycloakOrigin = Cypress.env('keycloakUrl');

    cy.origin(keycloakOrigin, { args: { newUser } }, ({ newUser }) => {
      cy.get('#username').should('have.value', newUser.email);
      cy.get('#password').type(newUser.password);
      cy.get('#kc-login').click();
    });

    cy.location('pathname', { timeout: 10000 }).should('eq', '/welcome-pick-store');
  });

  it('lists and picks a store', () => {
    cy.get('[data-cy=choose-store-btn]').click();
    cy.get('[data-cy^="store-card"]').should('have.length', 2);
    cy.get(`[data-cy=store-card-${STORE_ONE_SLUG}]`).click();
    cy.location('pathname').should('eq', `/store/${STORE_ONE_SLUG}/products-explorer`);
  });

  it('adds/removes products and checks cart', () => {
    cy.get('[data-cy=product-card]').eq(0).find('[data-cy=add-btn]').click();
    cy.get('[data-cy=product-card]').eq(1).find('[data-cy=add-btn]').click().click(); // qty 2
    cy.get('[data-cy=product-card]').eq(1).find('[data-cy=remove-btn]').click(); // qty 1

    cy.get('[data-cy=cart-btn]').click();
    cy.get('[data-cy=cart-item]').should('have.length', 2);
  });

  it('fills checkout and places order', () => {
    cy.get('[data-cy=continue-checkout-btn]').click();
    cy.location('pathname').should('eq', `/store/${STORE_ONE_SLUG}/checkout`);

    cy.get('[data-cy=delivery-street-input]').type(newUser.address.street);
    cy.get('[data-cy=delivery-city-input]').type(newUser.address.city);
    cy.get('[data-cy=delivery-apartment-input]').type(newUser.address.apartment);
    cy.get('[data-cy=delivery-zipCode-input]').type(newUser.address.zipCode);
    cy.get('[data-cy=delivery-instructions-input]').type(newUser.address.deliveryInstructions);
    cy.get('[data-cy=delivery-slot-select]').click();
    cy.get('[data-cy^=slot-]').should('have.length.greaterThan', 0);
    cy.get('[data-cy=slot-0]').click();

    cy.intercept('POST', '**/api/orders', (req) => {
      req.continue((res) => {
        const approvalUrl = res.body.paymentApprovalUrl;
        expect(
          () => new URL(approvalUrl),
          `Expected "${approvalUrl}" to be a valid URL`,
        ).not.to.throw();
        const url = new URL(approvalUrl);
        const token = url.searchParams.get('token');
        expect(token, 'payment token in approval URL').to.exist;
        paymentOrderId = token!;
        res.body.paymentApprovalUrl = `/store/${STORE_ONE_SLUG}/track-order?token=${paymentOrderId}`;
      });
    }).as('placeOrder');

    cy.get('[data-cy=checkout-btn]').click();
    cy.wait('@placeOrder');
    cy.intercept('POST', '**/api/orders').as('placeOrder'); // Overrides the previous handler so any later POSTs to this route are passed through untouched, instead of re-running our res.body mutation and overwriting paymentOrderId

    cy.location('pathname').should('eq', `/store/${STORE_ONE_SLUG}/track-order`);
    cy.location('search').should((search) => {
      expect(search).to.include(`token=${paymentOrderId}`);
    });
    cy.get('[data-cy=order-status]').contains('created', { matchCase: false });
  });

  it('reflects live status updates via SSE after payment webhooks', () => {
    cy.wrap(null).then(() => {
      expect(paymentOrderId, 'payment order id must exist before sending webhook').to.exist;
      sendPaypalWebhook('CHECKOUT.ORDER.APPROVED', paymentOrderId);
    });
    // no fixed wait: rely on Cypress's built-in retry-ability against the SSE-driven DOM
    cy.get('[data-cy=order-status]', { timeout: 10000 }).contains('processing your payment', {
      matchCase: false,
    });

    cy.wrap(null).then(() => sendPaypalWebhook('PAYMENT.CAPTURE.COMPLETED', paymentOrderId));
    cy.get('[data-cy=order-status]', { timeout: 10000 }).contains('paid', { matchCase: false });
  });
});

function sendPaypalWebhook(eventType: string, resourceId: string) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.env('paymentServiceUrl')}/api/payments/paypal/webhook`,
    headers: {
      'PAYPAL-TRANSMISSION-ID': 'test-transmission-id',
      'PAYPAL-TRANSMISSION-TIME': new Date().toISOString(),
      'PAYPAL-CERT-URL': 'https://api-m.sandbox.paypal.com/v1/notifications/certs/test',
      'PAYPAL-AUTH-ALGO': 'SHA256withRSA',
      'PAYPAL-TRANSMISSION-SIG': 'test-signature',
    },
    body: {
      id: `webhook-${Math.floor(Math.random() * 1000).toString()}`, // random id so backend don't treat this a processed webhook
      event_type: eventType,
      resource_type: 'checkout-order',
      resource: {
        id: resourceId,
        status: eventType.includes('APPROVED') ? 'APPROVED' : 'COMPLETED',
      },
    },
  });
}

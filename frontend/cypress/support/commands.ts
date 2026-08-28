export interface SignupPayload {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
}

export interface StorePayload {
  name: string;
  description: string;
  address: string;
}

export interface ProductPayload {
  name: string;
  unitPrice: number;
  initialQty: number;
}

export interface SlotConfigPayload {
  dayOfWeek: number;
  startTime: string;
  endTime: string;
  slotDurationMin: number;
  maxCapacity: number;
  cutoffMinutes: number;
  active: boolean;
}

Cypress.Commands.add('signup', (user: SignupPayload) => {
  return cy.request({
    method: 'POST',
    url: `${Cypress.env('orderServiceUrl')}/api/auth/signup`,
    body: user,
    failOnStatusCode: false, // To assert for conflict status code
  });
});

Cypress.Commands.add('login', (email: string, password: string) => {
  return cy
    .request({
      method: 'POST',
      url: `${Cypress.env('keycloakUrl')}/realms/storehub/protocol/openid-connect/token`,
      form: true,
      body: {
        grant_type: 'password',
        client_id: 'e2e-test-client',
        username: email,
        password: password,
      },
    })
    .then((res) => res.body.access_token as string);
});

Cypress.Commands.add('createStore', (token: string, store: StorePayload) => {
  return cy.request({
    method: 'POST',
    url: `${Cypress.env('orderServiceUrl')}/api/stores`,
    headers: { Authorization: `Bearer ${token}` },
    body: store,
  });
});

Cypress.Commands.add('createProduct', (token: string, product: ProductPayload) => {
  return cy.request({
    method: 'POST',
    url: `${Cypress.env('catalogServiceUrl')}/api/products`,
    headers: { Authorization: `Bearer ${token}` },
    body: product,
  });
});

Cypress.Commands.add('createSlotConfig', (token: string, config: SlotConfigPayload) => {
  return cy.request({
    method: 'POST',
    url: `${Cypress.env('catalogServiceUrl')}/api/admin/slot-configs`,
    headers: { Authorization: `Bearer ${token}` },
    body: config,
  });
});

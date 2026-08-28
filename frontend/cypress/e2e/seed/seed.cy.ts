import { EXISTENT_USER } from '../../support/seed-data';

describe('Seed test data', () => {
  const user1 = {
    email: EXISTENT_USER.email,
    password: 'Passw0rd!',
    firstName: 'Abdelkhalek',
    lastName: 'Test',
    address: '123 Main St',
    phoneNumber: '+21600000000',
  };

  const user2 = {
    email: 'store2owner@gmail.com',
    password: 'Passw0rd!',
    firstName: 'Store2',
    lastName: 'Owner',
    address: '456 Second St',
    phoneNumber: '+21600000001',
  };

  let token1: string;
  let token2: string;

  it('creates user1', () => {
    cy.signup(user1).then((res) => {
      expect(res.status).to.eq(201);
      expect(res.body.message).to.contain('please login');
    });
    cy.login(user1.email, user1.password).then((t) => {
      token1 = t;
    });
  });

  it('creates user2 for the second store owner', () => {
    cy.signup(user2).then((res) => {
      expect(res.status).to.eq(201);
    });
    cy.login(user2.email, user2.password).then((t) => {
      token2 = t;
    });
  });

  it('creates two stores', () => {
    cy.createStore(token1, {
      name: 'Store One', // Assuming standard slug generation, this string slug must match STORE_ONE_SLUG
      description: 'First test store',
      address: '123 Main St',
    }).then((res) => {
      expect(res.status).to.eq(201);
    });

    cy.createStore(token2, {
      name: 'Store Two',
      description: 'Second test store',
      address: '456 Second St',
    }).then((res) => {
      expect(res.status).to.eq(201);
    });
  });

  it('creates 8 products with qty 9999', () => {
    // We log in again, to get the updated jwt with role STORE_OWNER
    cy.login(user1.email, user1.password).then((t) => {
      token1 = t;
    });
    for (let i = 1; i <= 8; i++) {
      cy.createProduct(token1, {
        name: `Product ${i}`,
        unitPrice: 10 + i,
        initialQty: 9999,
      }).then((res) => {
        expect(res.status).to.eq(201);
      });
    }
  });

  it('creates slot configs', () => {
    // We log in again, to get the updated jwt with role STORE_OWNER
    cy.login(user1.email, user1.password).then((t) => {
      token1 = t;
    });
    [0, 1].forEach((day) => {
      cy.createSlotConfig(token1, {
        dayOfWeek: day,
        startTime: '09:00',
        endTime: '18:00',
        slotDurationMin: 30,
        maxCapacity: 10,
        cutoffMinutes: 60,
        active: true,
      }).then((res) => {
        expect(res.status).to.eq(200);
      });
    });
  });
});

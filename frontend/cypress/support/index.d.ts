/// <reference types="cypress" />

import { ProductPayload, SignupPayload, SlotConfigPayload, StorePayload } from './commands';

declare namespace Cypress {
  interface Chainable {
    signup(user: SignupPayload): Chainable<Cypress.Response<any>>;
    login(email: string, password: string): Chainable<string>;
    createStore(token: string, store: StorePayload): Chainable<Cypress.Response<any>>;
    createProduct(token: string, product: ProductPayload): Chainable<Cypress.Response<any>>;
    createSlotConfig(token: string, config: SlotConfigPayload): Chainable<Cypress.Response<any>>;
  }
}

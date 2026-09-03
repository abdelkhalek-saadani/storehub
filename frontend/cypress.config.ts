import { defineConfig } from 'cypress';
import { plugin as cypressGrepPlugin } from '@cypress/grep/plugin';

export default defineConfig({
  allowCypressEnv: true,

  e2e: {
    specPattern: ['cypress/e2e/**/*.cy.ts', 'cypress/demo/**/*.cy.ts'],
    setupNodeEvents(on, config) {
      cypressGrepPlugin(config);
      return config;
    },

    baseUrl: 'http://localhost:8080',
    env: {
      catalogServiceUrl: 'http://localhost:8100',
      orderServiceUrl: 'http://localhost:8090',
      keycloakUrl: 'http://auth-server:8088',
      paymentServiceUrl: 'http://localhost:8200',
    },
  },
});

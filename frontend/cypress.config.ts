import { defineConfig } from 'cypress';
import { plugin as cypressGrepPlugin } from '@cypress/grep/plugin';

export default defineConfig({
  allowCypressEnv: true,

  e2e: {
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

import { defineConfig } from 'cypress';

export default defineConfig({
  allowCypressEnv: true,

  e2e: {
    baseUrl: 'http://localhost:8080',
    env: {
      catalogServiceUrl: 'http://localhost:8100',
      orderServiceUrl: 'http://localhost:8090',
      keycloakUrl: 'http://auth-server:8088',
      paymentServiceUrl: 'http://localhost:8200',
    },
  },
});

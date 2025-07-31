import { Environment } from './environment.interface';

export const environment: Environment = {
  production: true,
  apiBaseUrl: '/api'  // Will be configured via nginx proxy in production
};

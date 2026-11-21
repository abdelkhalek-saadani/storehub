import Keycloak from 'keycloak-js';

export const keycloak = new Keycloak({
  url: 'http://auth-server:8088',
  realm: 'storehub',
  clientId: 'admin-backoffice',
});

export function initKeycloak() {
  return keycloak.init({
    onLoad: 'check-sso',
    silentCheckSsoRedirectUri: location.origin + '/silent-check-sso.html',
  });
}

export function hasAnyRole(roles: string[]): boolean {
  const userRoles = keycloak.tokenParsed?.realm_access?.roles ?? [];
  return roles.some(r => userRoles.includes(r));
}

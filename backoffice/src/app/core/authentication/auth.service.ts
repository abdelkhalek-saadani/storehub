import {Injectable, inject} from '@angular/core';
import {BehaviorSubject, catchError, from, iif, map, merge, of, share, switchMap, tap} from 'rxjs';
import {User} from './interface';
import {LoginService} from './login.service';
import {initKeycloak, keycloak} from '@core/keycloak';
import {Menu} from '@core/bootstrap';
import {HttpClient} from '@angular/common/http';

@Injectable({providedIn: 'root'})
export class AuthService {
  private readonly loginService = inject(LoginService);
  private user$ = new BehaviorSubject<User>({});
  private http = inject(HttpClient);

  async init() {
    await initKeycloak();
    if (keycloak.authenticated) {
      const p = await keycloak.loadUserProfile();
      this.user$.next({
        id: p.id,
        name: p.username,
        email: p.email,
        avatar: 'images/avatar.jpg',
      });
    }
  }

  change() {
    return this.user$.asObservable();
  }

  check() {
    return keycloak.authenticated;
  }

  user() {
    return this.user$.asObservable();
  }

   menu() {
      return this.http
        .get<{ menu: Menu[] }>('data/menu.json?_t=' + Date.now())
        .pipe(map(res => res.menu));
    }


  logout() {
    return from(keycloak.logout({redirectUri: location.origin})).pipe(map(() => true));
  }

}

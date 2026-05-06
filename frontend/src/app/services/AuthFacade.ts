import {inject, Injectable} from '@angular/core';
import {SignInParams, User} from '../models/User';
import {ProductStore} from '../product-store';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthFacade {
  store = inject(ProductStore);
  router = inject(Router);

  signIn(
    {
      user,
      redirect,
      redirectTo
    }:
    {
      user: SignInParams,
      redirect: boolean,
      redirectTo: string
    }) {
    this.store.signIn(user);
    if (redirect) this.router.navigate([redirectTo]);
  }
}

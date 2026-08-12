import { Routes } from '@angular/router';
import Layout from './layout/layout/layout';
import { authGuard } from '@core/auth/auth-guard';
import { guestOnlyGuard } from '@core/auth/guest-only.guard';
import { storeResolver } from './store/store-resolver';
import { storeRequiredGuard } from './store/guard/store-required.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'products-explorer',
  },
  {
    path: 'dev',
    pathMatch: 'full',
    loadComponent: () => import('./dev/dev'),
  },
  {
    path: 'forbidden',
    loadComponent: () => import('./auth/forbidden/forbidden').then((m) => m.Forbidden),
  },
  {
    path: 'welcome',
    canActivate: [guestOnlyGuard],
    loadComponent: () => import('./auth/welcome/welcome').then((m) => m.Welcome),
  },
  {
    path: 'signup',
    canActivate: [guestOnlyGuard],
    loadComponent: () => import('./auth/signup/signup').then((m) => m.Signup),
  },
  {
    path: 'post-login',
    loadComponent: () => import('./auth/post-login/post-login').then((m) => m.PostLogin),
  },

  {
    path: 'checkout/return',
    loadComponent: () => import('./checkout/return/return'),
  },
  {
    path: 'checkout/cancel',
    loadComponent: () => import('./checkout/cancel/cancel'),
  },
  {
    path: 'checkout/return/success',
    loadComponent: () => import('./checkout/return/success/success'),
  },
  {
    path: 'welcome-pick-store',
    loadComponent: () => import('./store/welcome-pick-store'),
  },
  {
    path: '',
    loadComponent: () => import('./layout/layout/layout'),
    children: [
      {
        path: 'store/:storeSlug',
        resolve: { storeId: storeResolver },
        children: [
          {
            path: 'wishlist',
            loadComponent: () => import('./wishlist/wishlist'),
          },
          {
            path: 'products',
            loadComponent: () => import('./products/products/products'),
          },
          {
            path: 'products-explorer',
            loadComponent: () => import('./products/product-explorer/product-explorer'),
          },
          {
            path: 'checkout',
            loadComponent: () => import('./checkout/checkout'),
          },

          {
            path: 'track-order',
            canActivate: [authGuard],
            loadComponent: () => import('./track-order/track-order'),
          },
          {
            path: 'guest-track-order',
            loadComponent: () => import('./track-order/guest-track-order'),
          },
          {
            path: 'product/:productId',
            loadComponent: () => import('./product-details/product-details'),
          },
        ],
      },
    ],
  },
  { path: '**', canActivate: [storeRequiredGuard], children: [] },
];

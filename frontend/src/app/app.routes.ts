import { Routes } from '@angular/router';
import Layout from './layout/layout/layout';
import { authGuard } from '@core/auth/auth-guard';
import { guestOnlyGuard } from '@core/auth/guest-only.guard';
import { storeResolver } from './products/store-resolver';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dev',
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
    path: 'login',
    pathMatch: 'full',
    loadComponent: () => import('./pages/legacy/login/login'),
  },

  {
    path: 'checkout/return',
    canActivate: [authGuard],
    loadComponent: () => import('./checkout/return/return'),
  },
  {
    path: 'checkout/cancel',
    canActivate: [authGuard],
    loadComponent: () => import('./checkout/cancel/cancel'),
  },
  {
    path: 'checkout/return/success',
    loadComponent: () => import('./checkout/return/success/success'),
  },
  {
    path: '',
    loadComponent: () => import('./layout/layout/layout'),
    children: [
      {
        path: 'wishlist',
        loadComponent: () => import('./pages/wishlist/wishlist'),
      },

      {
        path: 'store/:storeSlug',
        resolve: { storeId: storeResolver },
        children: [
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
        ],
      },
      {
        path: 'product/:productId',
        loadComponent: () => import('./pages/product-details/product-details'),
      },
    ],
  },

  {
    path: 'legacy',
    loadComponent: () => import('./layout/layout/layout'),
    children: [
      {
        path: 'products',
        pathMatch: 'full',
        redirectTo: 'products/all',
      },
      {
        path: 'wishlist',
        loadComponent: () => import('./pages/legacy/my-wishlist/my-wishlist'),
      },
      {
        path: 'cart',
        loadComponent: () => import('./pages/cart/cart'),
      },
      {
        path: 'checkout',
        loadComponent: () => import('./pages/legacy/checkout/checkout'),
      },
      {
        path: 'order-success',
        loadComponent: () => import('./pages/order-success/order-success'),
      },
      {
        path: 'product/:productId',
        loadComponent: () => import('./pages/legacy/product-details/product-details'),
      },
    ],
  },
];

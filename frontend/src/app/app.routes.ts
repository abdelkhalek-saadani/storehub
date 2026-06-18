import { Routes } from '@angular/router';
import Layout from './layout/layout/layout';

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
    path: 'login',
    pathMatch: 'full',
    loadComponent: () => import('./pages/login/login'),
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
        path: 'checkout',
        loadComponent: () => import('./pages/checkout/checkout'),
      },
      {
        path: 'track-order',
        loadComponent: () => import('./pages/track-order/track-order'),
      },
      {
        path: 'payment-success',
        loadComponent: () => import('./pages/payment-success/payment-success'),
      },
      {
        path: 'payment-failure',
        loadComponent: () => import('./pages/payment-failed/payment-failed'),
      },
      {
        path: 'products',
        loadComponent: () => import('./pages/products/products'),
      },
      {
        path: 'products-explorer',
        loadComponent: () => import('./pages/product-explorer/product-explorer'),
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
        path: 'products/:category',
        loadComponent: () => import('./pages/products-grid/products-grid'),
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

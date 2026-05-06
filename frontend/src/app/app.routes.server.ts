import {RenderMode, ServerRoute} from '@angular/ssr';
import {inject} from '@angular/core';
import {ProductStore} from './product-store';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'wishlist',
    renderMode: RenderMode.Client
  },
  {
    path: 'cart',
    renderMode: RenderMode.Client
  },
  {
    path: 'checkout',
    renderMode: RenderMode.Client
  },
  {
    path: 'products/:category',
    renderMode: RenderMode.Prerender,
    getPrerenderParams: async () => {
      const store = inject(ProductStore);
      const categories = store.categories();
      return categories.map((cat) => ({category: cat.name}));
    }
  },
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];

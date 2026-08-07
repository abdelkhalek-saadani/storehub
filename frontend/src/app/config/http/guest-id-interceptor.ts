import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { environment } from '@environments/environment';
import { tap } from 'rxjs';
import { escapeRegex } from './bearer-interceptor-config';

export const GUEST_ID_KEY = 'guestId';

export const guestIdInterceptor: HttpInterceptorFn = (req, next) => {
  const orderApiPattern = new RegExp('^' + escapeRegex(environment.orderApiUrl) + '(/.*)?$', 'i');

  if (!orderApiPattern.test(req.url)) {
    return next(req);
  }

  const guestId = localStorage.getItem(GUEST_ID_KEY);
  const request = guestId ? req.clone({ setHeaders: { 'X-Guest-Id': guestId } }) : req;

  return next(request).pipe(
    tap((event) => {
      if (event instanceof HttpResponse) {
        const returnedGuestId = event.headers.get('X-Guest-Id');
        if (returnedGuestId) {
          localStorage.setItem(GUEST_ID_KEY, returnedGuestId);
        }
      }
    }),
  );
};

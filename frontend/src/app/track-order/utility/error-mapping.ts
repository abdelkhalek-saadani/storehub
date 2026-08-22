import { HttpErrorResponse } from '@angular/common/http';

export function mapHttpError(err: unknown): string {
  if (err === null) return 'Unknown error';
  const actual = (err as Error).cause ?? err;
  if (actual instanceof HttpErrorResponse) {
    switch (actual.status) {
      case 404:
        return 'Not found';
      case 401:
        return 'Please log in again';
      case 403:
        return "You don't have access to this";
      case 0:
        return 'Network error';
      default:
        return 'Unknown error, please try again';
    }
  }
  return 'Unknown error';
}

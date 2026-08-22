import { HttpErrorResponse } from '@angular/common/http';
import { mapHttpError } from './error-mapping';

describe('mapHttpError', () => {
  it('returns "Not found" for a 404', () => {
    expect(mapHttpError(new HttpErrorResponse({ status: 404 }))).toBe('Not found');
  });

  it('returns "Please log in again" for a 401', () => {
    expect(mapHttpError(new HttpErrorResponse({ status: 401 }))).toBe('Please log in again');
  });

  it('returns "You don\'t have access to this" for a 403', () => {
    expect(mapHttpError(new HttpErrorResponse({ status: 403 }))).toBe(
      "You don't have access to this",
    );
  });

  it('returns "Network error" for a status of 0', () => {
    expect(mapHttpError(new HttpErrorResponse({ status: 0 }))).toBe('Network error');
  });

  it('returns a generic message for any other status', () => {
    expect(mapHttpError(new HttpErrorResponse({ status: 500 }))).toBe(
      'Unknown error, please try again',
    );
  });

  it('returns "Unknown error" when the error is not an HttpErrorResponse', () => {
    expect(mapHttpError(new Error('boom'))).toBe('Unknown error');
    expect(mapHttpError('some string')).toBe('Unknown error');
    expect(mapHttpError(null)).toBe('Unknown error');
  });

  it('unwraps err.cause and maps based on that instead', () => {
    const inner = new HttpErrorResponse({ status: 404 });
    const wrapper = new Error('wrapped', { cause: inner });

    expect(mapHttpError(wrapper)).toBe('Not found');
  });
});

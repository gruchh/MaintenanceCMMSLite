import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError, filter, take } from 'rxjs';
import { TokenStorageService } from '../services/token-storage.service';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

const addTokenHeader = (req: HttpRequest<unknown>, token: string) => {
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
};

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const tokenService = inject(TokenStorageService);

  if (req.url.startsWith(environment.apiUrl) && tokenService.getAccessToken()) {
    req = addTokenHeader(req, tokenService.getAccessToken()!);
  }

  return next(req).pipe(
    catchError((error) => {
      if (
        error instanceof HttpErrorResponse &&
        error.status === 401 &&
        !req.url.includes('/auth/refresh-token')
      ) {
        return handle401Error(req, next, authService, tokenService);
      }
      return throwError(() => error);
    })
  );
};

const handle401Error = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  tokenService: TokenStorageService
) => {
  if (!authService.isRefreshingToken) {
    authService.isRefreshingToken = true;
    authService.tokenRefreshed$.next(false);

    return authService.refreshToken().pipe(
      switchMap((tokens) => {
        authService.isRefreshingToken = false;
        authService.tokenRefreshed$.next(true);
        return next(addTokenHeader(req, tokens.accessToken!));
      }),
      catchError((error) => {
        authService.isRefreshingToken = false;
        authService.logout();
        return throwError(() => error);
      })
    );
  } else {
    return authService.tokenRefreshed$.pipe(
      filter(refreshed => refreshed),
      take(1),
      switchMap(() => {
        const newAccessToken = tokenService.getAccessToken();
        return next(addTokenHeader(req, newAccessToken!));
      })
    );
  }
};

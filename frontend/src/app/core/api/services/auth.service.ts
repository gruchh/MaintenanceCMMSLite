import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of, tap, switchMap, throwError, BehaviorSubject } from 'rxjs';
import { TokenStorageService } from './token-storage.service';
import { environment } from '../../../../environments/environment';
import { JwtAuthRequest, JwtAuthResponse, RegisterRequest, UserProfileDto, UserProfileResponse } from '../generated';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private tokenStorage = inject(TokenStorageService);

  currentUser = signal<UserProfileDto | null>(null);
  isLoggedIn = computed(() => !!this.currentUser());

  isRefreshingToken = false;
  tokenRefreshed$ = new BehaviorSubject<boolean>(false);

  private fetchAndStoreUser(): Observable<UserProfileDto | null> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/auth/getCurrentUser`).pipe(
      map(response => {
        if (response?.status === 'success' && response.data) {
          this.currentUser.set(response.data);
          return response.data;
        }
        this.logout();
        return null;
      }),
      catchError(() => {
        this.logout();
        return of(null);
      })
    );
  }

  public initializeAuthState(): Observable<UserProfileDto | null> {
    const token = this.tokenStorage.getAccessToken();
    if (token) {
      return this.fetchAndStoreUser();
    }
    this.currentUser.set(null);
    return of(null);
  }

  login(credentials: JwtAuthRequest): Observable<UserProfileDto | null> {
    return this.http
      .post<JwtAuthResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        switchMap((loginResponse) => {
          if (loginResponse?.accessToken && loginResponse.refreshToken) {
            this.tokenStorage.saveTokens(loginResponse.accessToken, loginResponse.refreshToken);
            return this.fetchAndStoreUser();
          }
          this.logout();
          return of(null);
        }),
        catchError((error) => {
          this.logout();
          return throwError(() => error);
        })
      );
  }

   register(userData: RegisterRequest): Observable<UserProfileDto | null> {
    return this.http
      .post<JwtAuthResponse>(`${this.apiUrl}/auth/register`, userData)
      .pipe(
        switchMap((registerResponse) => {
          if (registerResponse?.accessToken && registerResponse.refreshToken) {
            this.tokenStorage.saveTokens(registerResponse.accessToken, registerResponse.refreshToken);
            return this.fetchAndStoreUser();
          }
          this.logout();
          return of(null);
        }),
        catchError((error) => {
          this.logout();
          return throwError(() => error);
        })
      );
  }

  refreshToken(): Observable<JwtAuthResponse> {
    const refreshToken = this.tokenStorage.getRefreshToken();
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('Brak tokena odświeżającego.'));
    }

    return this.http.post<JwtAuthResponse>(`${this.apiUrl}/auth/refresh-token`, { refreshToken }).pipe(
      tap((tokens) => {
        if (tokens.accessToken && tokens.refreshToken) {
          this.tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken);
        } else {
          this.logout();
        }
      }),
      catchError((error) => {
        this.logout();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    this.tokenStorage.clearTokens();
    this.currentUser.set(null);
  }
}

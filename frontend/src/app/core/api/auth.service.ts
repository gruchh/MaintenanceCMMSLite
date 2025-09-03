import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of, tap, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JwtAuthRequest, JwtAuthResponse, RegisterRequest, UserProfileDto, UserProfileResponse } from './generated';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly authTokenKey = 'authToken';

  currentUser = signal<UserProfileDto | null>(null);
  isLoggedIn = computed(() => !!this.currentUser());

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
    const token = this.getToken();
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
          if (loginResponse?.accessToken) {
            this.saveToken(loginResponse.accessToken);
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
          if (registerResponse?.accessToken) {
            this.saveToken(registerResponse.accessToken);
            return this.fetchAndStoreUser();
          }
          this.logout();
          return of(null);
        }),
        catchError((error) => {
          this.logout(); // W przypadku błędu rejestracji również wylogowujemy (jeśli jakiś token jakimś cudem był)
          return throwError(() => error);
        })
      );
  }

  logout(): void {
    this.removeToken();
    this.currentUser.set(null);
  }

  saveToken(token: string): void { localStorage.setItem(this.authTokenKey, token); }
  getToken(): string | null { return localStorage.getItem(this.authTokenKey); }
  removeToken(): void { localStorage.removeItem(this.authTokenKey); }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, map, catchError, throwError, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JwtAuthRequest, JwtAuthResponse, UserProfileDto, UserProfileResponse } from './generated';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly authTokenKey = 'authToken';

  private currentUserSource = new BehaviorSubject<UserProfileDto | null>(null);
  public currentUser$ = this.currentUserSource.asObservable();
  public isLoggedIn$ = this.currentUser$.pipe(map(user => !!user));

  constructor() {
    this.loadInitialUser();
  }

  private fetchAndStoreUser(): Observable<UserProfileDto | null> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/auth/getCurrentUser`).pipe(
      map(response => {
        if (response && response.status === 'success' && response.data) {
          this.currentUserSource.next(response.data);
          return response.data;
        }
        this.logout();
        return null;
      }),
      catchError((error) => {
        this.logout();
        return throwError(() => error);
      })
    );
  }

  login(credentials: JwtAuthRequest): Observable<JwtAuthResponse> {
    return this.http
      .post<JwtAuthResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap((loginResponse) => {
          if (loginResponse && loginResponse.accessToken) {
            this.saveToken(loginResponse.accessToken);
            this.fetchAndStoreUser().subscribe();
          }
        })
      );
  }

  logout(): void {
    this.removeToken();
    this.currentUserSource.next(null);
  }

  private loadInitialUser(): void {
    const token = this.getToken();
    if (token) {
      this.fetchAndStoreUser().subscribe();
    }
  }

  saveToken(token: string): void {
    localStorage.setItem(this.authTokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.authTokenKey);
  }

  removeToken(): void {
    localStorage.removeItem(this.authTokenKey);
  }

  isLoggedIn(): boolean {
    return this.currentUserSource.value !== null;
  }
}

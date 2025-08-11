import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JwtAuthRequest } from './generated/model/jwtAuthRequest';
import { JwtAuthResponse } from './generated/model/jwtAuthResponse';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly authTokenKey = 'authToken';

  login(credentials: JwtAuthRequest): Observable<JwtAuthResponse> {
    return this.http
      .post<JwtAuthResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap((response) => {
          if (response && response.accessToken) {
            this.saveToken(response.accessToken);
          }
        })
      );
  }

  saveToken(token: string): void {
    localStorage.setItem(this.authTokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.authTokenKey);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  logout(): void {
    localStorage.removeItem(this.authTokenKey);
  }
}

import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { TokenStorageService } from '../api/token-storage.service';
import { AuthService } from '../api/auth.service';
import { jwtInterceptor } from './jwt.interceptor';
import { environment } from '../../../environments/environment';

describe('jwtInterceptor', () => {
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let tokenStorageService: TokenStorageService;
  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        TokenStorageService,
        provideHttpClient(withInterceptors([jwtInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
    tokenStorageService = TestBed.inject(TokenStorageService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('powinien odświeżyć token po błędzie 401 i ponowić zapytanie', () => {
    const protectedUrl = `${apiUrl}/data`;
    const refreshTokenUrl = `${apiUrl}/auth/refresh-token`;

    tokenStorageService.saveTokens('stary-wygasly-token', 'dzialajacy-refresh-token');

    httpClient.get(protectedUrl).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const failedRequest = httpMock.expectOne(protectedUrl);
    expect(failedRequest.request.headers.get('Authorization')).toBe('Bearer stary-wygasly-token');
    failedRequest.flush({}, { status: 401, statusText: 'Unauthorized' });

    const refreshRequest = httpMock.expectOne(refreshTokenUrl);
    refreshRequest.flush({
      accessToken: 'nowy-swiezy-token',
      refreshToken: 'nowy-swiezy-refresh-token'
    });

    const retriedRequest = httpMock.expectOne(protectedUrl);
    expect(retriedRequest.request.headers.get('Authorization')).toBe('Bearer nowy-swiezy-token');
    retriedRequest.flush({ result: 'sukces' });
  });
});

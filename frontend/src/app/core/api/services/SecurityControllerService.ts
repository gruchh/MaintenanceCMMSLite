/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { JwtAuthRequest } from '../models/JwtAuthRequest';
import type { JwtAuthResponse } from '../models/JwtAuthResponse';
import type { RegisterRequest } from '../models/RegisterRequest';
import type { UserProfileResponse } from '../models/UserProfileResponse';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
@Injectable({
    providedIn: 'root',
})
export class SecurityControllerService {
    constructor(public readonly http: HttpClient) {}
    /**
     * @param requestBody
     * @returns JwtAuthResponse OK
     * @throws ApiError
     */
    public register(
        requestBody: RegisterRequest,
    ): Observable<JwtAuthResponse> {
        return __request(OpenAPI, this.http, {
            method: 'POST',
            url: '/auth/register',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns JwtAuthResponse OK
     * @throws ApiError
     */
    public login(
        requestBody: JwtAuthRequest,
    ): Observable<JwtAuthResponse> {
        return __request(OpenAPI, this.http, {
            method: 'POST',
            url: '/auth/login',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns UserProfileResponse OK
     * @throws ApiError
     */
    public getCurrentUser(): Observable<UserProfileResponse> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/auth/getCurrentUser',
        });
    }
}

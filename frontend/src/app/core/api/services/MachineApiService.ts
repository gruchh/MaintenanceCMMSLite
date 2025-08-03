/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { CreateRequest } from '../models/CreateRequest';
import type { Pageable } from '../models/Pageable';
import type { PageResponse } from '../models/PageResponse';
import type { Response } from '../models/Response';
import type { UpdateRequest } from '../models/UpdateRequest';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
@Injectable({
    providedIn: 'root',
})
export class MachineApiService {
    constructor(public readonly http: HttpClient) {}
    /**
     * Find a machine by ID
     * Returns a single machine by its ID. Requires authentication.
     * @param id
     * @returns Response OK
     * @throws ApiError
     */
    public findById1(
        id: number,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/api/machines/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Update an existing machine
     * Updates an existing machine by its ID. Requires ADMIN role.
     * @param id
     * @param requestBody
     * @returns Response OK
     * @throws ApiError
     */
    public update1(
        id: number,
        requestBody: UpdateRequest,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'PUT',
            url: '/api/machines/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * Delete a machine
     * Deletes a machine by its ID. Requires ADMIN role.
     * @param id
     * @returns any OK
     * @throws ApiError
     */
    public delete1(
        id: number,
    ): Observable<any> {
        return __request(OpenAPI, this.http, {
            method: 'DELETE',
            url: '/api/machines/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Find all machines
     * Returns a paginated list of all machines. Requires authentication.
     * @param pageable
     * @returns PageResponse OK
     * @throws ApiError
     */
    public findAll1(
        pageable: Pageable,
    ): Observable<PageResponse> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/api/machines',
            query: {
                'pageable': pageable,
            },
        });
    }
    /**
     * Create a new machine
     * Creates a new machine entry. Requires ADMIN role.
     * @param requestBody
     * @returns Response OK
     * @throws ApiError
     */
    public create1(
        requestBody: CreateRequest,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'POST',
            url: '/api/machines',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}

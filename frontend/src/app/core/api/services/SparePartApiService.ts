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
export class SparePartApiService {
    constructor(public readonly http: HttpClient) {}
    /**
     * Find a spare part by ID
     * Returns a single spare part by its ID. Requires authentication.
     * @param id
     * @returns Response OK
     * @throws ApiError
     */
    public findById(
        id: number,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/api/spare-parts/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Update an existing spare part
     * Updates an existing spare part by its ID. Requires ADMIN role.
     * @param id
     * @param requestBody
     * @returns Response OK
     * @throws ApiError
     */
    public update(
        id: number,
        requestBody: UpdateRequest,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'PUT',
            url: '/api/spare-parts/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * Delete a spare part
     * Deletes a spare part from the inventory by its ID. Requires ADMIN role.
     * @param id
     * @returns any OK
     * @throws ApiError
     */
    public delete(
        id: number,
    ): Observable<any> {
        return __request(OpenAPI, this.http, {
            method: 'DELETE',
            url: '/api/spare-parts/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Find all spare parts
     * Returns a paginated list of all available spare parts. Requires authentication.
     * @param pageable
     * @returns PageResponse OK
     * @throws ApiError
     */
    public findAll(
        pageable: Pageable,
    ): Observable<PageResponse> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/api/spare-parts',
            query: {
                'pageable': pageable,
            },
        });
    }
    /**
     * Create a new spare part
     * Adds a new spare part to the inventory. Requires ADMIN role.
     * @param requestBody
     * @returns Response OK
     * @throws ApiError
     */
    public create(
        requestBody: CreateRequest,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'POST',
            url: '/api/spare-parts',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}

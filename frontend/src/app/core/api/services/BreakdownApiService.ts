/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { AddPartRequest } from '../models/AddPartRequest';
import type { BreakdownStatsDTO } from '../models/BreakdownStatsDTO';
import type { CloseRequest } from '../models/CloseRequest';
import type { CreateRequest } from '../models/CreateRequest';
import type { Pageable } from '../models/Pageable';
import type { PageResponse } from '../models/PageResponse';
import type { Response } from '../models/Response';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
@Injectable({
    providedIn: 'root',
})
export class BreakdownApiService {
    constructor(public readonly http: HttpClient) {}
    /**
     * Find all breakdowns
     * Returns a paginated list of all breakdowns. Requires authentication.
     * @param pageable
     * @returns PageResponse OK
     * @throws ApiError
     */
    public findAll2(
        pageable: Pageable,
    ): Observable<PageResponse> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/api/breakdowns',
            query: {
                'pageable': pageable,
            },
        });
    }
    /**
     * Create a new breakdown
     * Creates a new breakdown entry. Requires SUBCONTRACTOR role or higher.
     * @param requestBody
     * @returns Response OK
     * @throws ApiError
     */
    public createBreakdown(
        requestBody: CreateRequest,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'POST',
            url: '/api/breakdowns',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * Add a part to a breakdown
     * Adds a used part to a specific breakdown. Requires SUBCONTRACTOR role or higher.
     * @param breakdownId
     * @param requestBody
     * @returns Response OK
     * @throws ApiError
     */
    public addPartToBreakdown(
        breakdownId: number,
        requestBody: AddPartRequest,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'POST',
            url: '/api/breakdowns/{breakdownId}/parts',
            path: {
                'breakdownId': breakdownId,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * Close a breakdown
     * Marks a breakdown as closed. Requires TECHNICAN role or higher.
     * @param breakdownId
     * @param requestBody
     * @returns Response OK
     * @throws ApiError
     */
    public closeBreakdown(
        breakdownId: number,
        requestBody: CloseRequest,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'PATCH',
            url: '/api/breakdowns/{breakdownId}/close',
            path: {
                'breakdownId': breakdownId,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * Find a breakdown by ID
     * Returns a single breakdown by its ID. Requires authentication.
     * @param id
     * @returns Response OK
     * @throws ApiError
     */
    public findById2(
        id: number,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/api/breakdowns/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Get breakdown statistics
     * Returns public statistics about breakdowns. No authentication required.
     * @returns BreakdownStatsDTO Successfully retrieved statistics
     * @throws ApiError
     */
    public getStats(): Observable<BreakdownStatsDTO> {
        return __request(OpenAPI, this.http, {
            method: 'GET',
            url: '/api/breakdowns/stats',
        });
    }
    /**
     * Remove a part from a breakdown
     * Removes a used part from a specific breakdown. Requires SUBCONTRACTOR role or higher.
     * @param breakdownId
     * @param usedPartId
     * @returns Response OK
     * @throws ApiError
     */
    public removePartFromBreakdown(
        breakdownId: number,
        usedPartId: number,
    ): Observable<Response> {
        return __request(OpenAPI, this.http, {
            method: 'DELETE',
            url: '/api/breakdowns/{breakdownId}/parts/{usedPartId}',
            path: {
                'breakdownId': breakdownId,
                'usedPartId': usedPartId,
            },
        });
    }
}

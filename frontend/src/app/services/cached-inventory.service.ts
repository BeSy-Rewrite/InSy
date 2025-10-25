import { Injectable } from '@angular/core';
import { AsyncSubject, BehaviorSubject, debounceTime, of, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Inventories } from '../models/inventories';
import { InventoriesService } from './inventories.service';
import { Filter } from './server-table-data-source.service';

type CacheEntry = {
  response: Inventories;
  createdAt: Date;
};

type RequestParams = {
  page: number;
  size: number;
  sortBy: string;
  sortDirection: string;
  filters: Filter;
  searchTerm: string;
};

@Injectable({
  providedIn: 'root'
})
/**
 * Service to fetch and cache inventories with filtering support.
 * The cache is invalidated when filters change or after a set duration.
 * This improves performance by reducing redundant API calls.
 * The cache duration is configurable (default: 5 minutes).
 */
export class CachedInventoryService {

  private totalElements: number = 0;
  private readonly cacheDurationMs: number = environment.cacheDurationMs ?? 5 * 60 * 1000; // 5 minutes default

  private readonly cache: Map<string, CacheEntry> = new Map();

  private readonly fetchDebouncer = new BehaviorSubject<RequestParams>({
    page: 0,
    size: 20,
    sortBy: 'id',
    sortDirection: 'asc',
    filters: {},
    searchTerm: ''
  });
  private fetchedData = new AsyncSubject<Inventories>();

  constructor(private readonly inventoriesService: InventoriesService) {
    this.fetchDebouncer.pipe(
      debounceTime(environment.searchAndFilterDebounceMs ?? 100),
    ).subscribe(params => {
      this.fetchOrders(
        params.page,
        params.size,
        params.sortBy,
        params.sortDirection,
        params.filters,
        params.searchTerm
      ).subscribe(data => {
        this.fetchedData.next(data);
        this.fetchedData.complete();
        this.fetchedData = new AsyncSubject<Inventories>();
      });
    });
  }

  /**
   * Fetch inventories from the API and cache the result.
   * If the requested data is in the cache and still valid, it returns the cached response.
   * @param page Number of the page to fetch (0-indexed)
   * @param size Number of items per page
   * @param sortBy Sorting criteria id.
   * @param sortDirection Sorting direction (asc|desc). Default is ascending.
   * @param filters Filter parameters to apply
   * @param searchTerm Search term to filter results
   * @returns An observable of the paged inventory response
   */
  getInventories(
    page: number = 0,
    size: number = 20,
    sortBy: string = 'id',
    sortDirection: string = 'asc',
    filters: Filter = {},
    searchTerm: string = ''
  ) {
    this.cleanCache();

    const cacheKey = this.getCacheKey(page, size, sortBy, sortDirection, filters, searchTerm);
    if (this.cache.has(cacheKey)) {
      return of(this.cache.get(cacheKey)!.response);
    }

    this.fetchDebouncer.next({ page, size, sortBy, sortDirection, filters, searchTerm });
    return this.fetchedData.asObservable();
  }

  /**
   * Clear the cache and reset total elements count.
   */
  clearCache() {
    this.cache.clear();
    this.totalElements = 0;
  }

  /**
   * Fetch inventories from the API.
   * This method handles the API call and caches the response.
   * @param page Number of the page to fetch (0-indexed)
   * @param size Number of items per page
   * @param sortBy Sorting criteria id.
   * @param sortDirection Sorting direction (asc|desc). Default is ascending.
   * @param filters Filter parameters to apply
   * @param searchTerm Search term to filter results
   * @returns An observable of the paged inventory response
   */
  private fetchOrders(
    page: number,
    size: number,
    sortBy: string,
    sortDirection: string,
    filters: Filter,
    searchTerm: string = ''
  ) {
    return this.inventoriesService.getInventories(
      page,
      size,
      sortBy,
      sortDirection,
      filters,
      searchTerm
    ).pipe(
      tap((pageResponse: Inventories) => {

        if (pageResponse.totalElements !== undefined &&
          this.totalElements !== pageResponse.totalElements) {
          this.cache.clear();
          this.totalElements = pageResponse.totalElements;
        }

        this.cache.set(this.getCacheKey(page, size, sortBy, sortDirection, filters, searchTerm),
          { response: pageResponse, createdAt: new Date() }
        );
      })
    );
  }

  /**
   * Check if the cache is still valid based on its age.
   * This method removes stale cache entries based on the defined cache duration.
   */
  private cleanCache(): void {
    this.cache.forEach((entry, key) => {
      const cacheAge = new Date().getTime() - entry.createdAt.getTime();
      if (cacheAge > this.cacheDurationMs) {
        this.cache.delete(key);
      }
    });
  }

  /**
   * Generate a cache key based on page, size, sort parameters, filters, and search term.
   * @param page Page number
   * @param size Page size
   * @param sortBy Sort parameter
   * @param sortDirection Sort direction (asc|desc)
   * @param filters Filter parameters
   * @param searchTerm Search term to filter results
   * @returns A unique cache key
   */
  private getCacheKey(page: number, size: number, sortBy: string, sortDirection: string, filters: Filter, searchTerm: string): string {
    const filtersKey = JSON.stringify(filters);
    return `${page}-${size}-${sortBy}-${sortDirection}-${filtersKey}-${searchTerm}`;
  }

}

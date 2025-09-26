import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, startWith } from 'rxjs/operators';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import {
  Pageable,
  SparePartResponseDTO,
  SparePartService,
} from '../../../core/api/generated';
import { BreakpointService } from '../../../core/services/breakout.service';
import { PageableRequest } from '../../../core/models/pageableRequest ';

@Component({
  selector: 'app-spare-parts',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './spare-parts.component.html',
})
export class SparePartsComponent implements OnInit, OnDestroy {
  private sparePartService = inject(SparePartService);
  private breakpointService = inject(BreakpointService);

  spareParts = signal<SparePartResponseDTO[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  currentPage = signal(0);
  pageSize = signal(10);
  totalElements = signal(0);
  totalPages = signal(0);
  pageSizes = [5, 10, 25, 50, 100];

  isAdmin = true;

  searchControl = new FormControl('');
  private searchSubscription!: Subscription;

  isMobile = this.breakpointService.isMobile;

  ngOnInit(): void {
    this.searchSubscription = this.searchControl.valueChanges.pipe(
      startWith(''),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(searchValue => {
      this.currentPage.set(0);
      this.fetchSpareParts(searchValue || '');
    });
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  fetchSpareParts(search: string): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const pageable: PageableRequest = {
      page: this.currentPage(),
      size: this.pageSize(),
      sort: 'id,asc',
    };

    this.sparePartService
      .getAllSpareParts(pageable as unknown as Pageable, search)
      .subscribe({
        next: (response) => {
          this.spareParts.set(response.content || []);
          this.totalElements.set(response.totalElements || 0);
          this.totalPages.set(response.totalPages || 0);
          this.isLoading.set(false);
        },
        error: (err) => {
          this.errorMessage.set(
            'Nie udało się załadować danych o częściach zamiennych. Spróbuj ponownie później.'
          );
          this.isLoading.set(false);
          console.error(err);
        },
      });
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize.set(+target.value);
    this.currentPage.set(0);
    this.fetchSpareParts(this.searchControl.value || '');
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((page) => page - 1);
      this.fetchSpareParts(this.searchControl.value || '');
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((page) => page + 1);
      this.fetchSpareParts(this.searchControl.value || '');
    }
  }
}

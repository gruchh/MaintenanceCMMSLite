import { Component, inject, model, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Pageable,
  SparePartResponseDTO,
  SparePartService,
} from '../../../core/api/generated';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-spare-parts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './spare-parts.component.html',
})
export class SparePartsComponent implements OnInit {
  private sparePartService = inject(SparePartService);

  spareParts = signal<SparePartResponseDTO[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  currentPage = signal(0);
  pageSize = signal(10);
  totalElements = signal(0);
  totalPages = signal(0);
  pageSizes = [5, 10, 25, 50, 100];

  isAdmin = true;

  searchTerm = model('');
  private searchSubject = new Subject<string>();

  ngOnInit(): void {
    this.fetchSpareParts();

    this.searchSubject
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((searchValue) => {
        this.currentPage.set(0);
        this.fetchSpareParts(searchValue);
      });
  }

  fetchSpareParts(search: string = ''): void {
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

  onSearchChange(): void {
    this.searchSubject.next(this.searchTerm());
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize.set(+target.value);
    this.currentPage.set(0);
    this.fetchSpareParts(this.searchTerm());
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((page) => page - 1);
      this.fetchSpareParts(this.searchTerm());
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((page) => page + 1);
      this.fetchSpareParts(this.searchTerm());
    }
  }
}

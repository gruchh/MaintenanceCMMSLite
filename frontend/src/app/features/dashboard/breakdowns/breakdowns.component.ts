import { Component, inject, model, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import {
  BreakdownResponseDTO,
  BreakdownService,
  Pageable,
} from '../../../core/api/generated';
import { BreakdownCloseModalComponent } from './components/breakdown-close-modal/breakdown-close-modal.component';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-breakdowns',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    BreakdownCloseModalComponent,
    FormsModule,
  ],
  templateUrl: './breakdowns.component.html',
})
export class BreakdownsComponent implements OnInit {
  private breakdownService = inject(BreakdownService);

  breakdowns = signal<BreakdownResponseDTO[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  currentPage = signal(0);
  pageSize = signal(10);
  totalElements = signal(0);
  totalPages = signal(0);
  pageSizes = [5, 10, 25, 50, 100];

  isModalOpen = signal(false);
  selectedBreakdownId = signal<number | null>(null);

  isAdmin = true;

  searchTerm = model('');
  private searchSubject = new Subject<string>();

  ngOnInit(): void {
    this.fetchBreakdowns();

    this.searchSubject
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((searchValue) => {
        this.currentPage.set(0);
        this.fetchBreakdowns(searchValue);
      });
  }

  fetchBreakdowns(search: string = ''): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const pageable: PageableRequest = {
      page: this.currentPage(),
      size: this.pageSize(),
      sort: 'reportedAt,desc',
    };

    this.breakdownService
      .getAllBreakdowns(pageable as unknown as Pageable, search)
      .subscribe({
        next: (response) => {
          this.breakdowns.set(response.content || []);
          this.totalElements.set(response.totalElements || 0);
          this.totalPages.set(response.totalPages || 0);
          this.isLoading.set(false);
        },
        error: (err) => {
          this.errorMessage.set(
            'Nie udało się załadować danych o awariach. Spróbuj ponownie później.'
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
    this.fetchBreakdowns(this.searchTerm());
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((page) => page - 1);
      this.fetchBreakdowns(this.searchTerm());
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((page) => page + 1);
      this.fetchBreakdowns(this.searchTerm());
    }
  }

  openCloseModal(breakdown: BreakdownResponseDTO): void {
    this.selectedBreakdownId.set(breakdown.id ?? null);
    this.isModalOpen.set(true);
  }

  closeCloseModal(): void {
    this.isModalOpen.set(false);
    this.selectedBreakdownId.set(null);
  }

  handleBreakdownClosed(): void {
    this.closeCloseModal();
    this.fetchBreakdowns(this.searchTerm());
  }
}

import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, startWith } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import {
  BreakdownResponseDTO,
  BreakdownService,
  Pageable,
} from '../../../core/api/generated';
import { BreakdownCloseModalComponent } from './components/breakdown-close-modal/breakdown-close-modal.component';
import { BreakpointService } from '../../../core/services/breakout.service';
import { PageableRequest } from '../../../core/models/pageableRequest ';

@Component({
  selector: 'app-breakdowns',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    BreakdownCloseModalComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './breakdowns.component.html',
})
export class BreakdownsComponent implements OnInit, OnDestroy {
  private breakdownService = inject(BreakdownService);
  private toastr = inject(ToastrService);
  private breakpointService = inject(BreakpointService);

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

  searchControl = new FormControl('');
  private searchSubscription!: Subscription;

  isMobile = this.breakpointService.isMobile;

  isAdmin = true;

  ngOnInit(): void {
    this.searchSubscription = this.searchControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(searchValue => {
      this.currentPage.set(0);
      this.fetchBreakdowns(searchValue || '');
    });
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  fetchBreakdowns(search: string): void {
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
          this.errorMessage.set('Nie udało się załadować danych o awariach.');
          this.toastr.error('Nie udało się załadować danych o awariach.', 'Błąd');
          this.isLoading.set(false);
        },
      });
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize.set(+target.value);
    this.currentPage.set(0);
    this.fetchBreakdowns(this.searchControl.value || '');
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((page) => page - 1);
      this.fetchBreakdowns(this.searchControl.value || '');
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((page) => page + 1);
      this.fetchBreakdowns(this.searchControl.value || '');
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
    this.fetchBreakdowns(this.searchControl.value || '');
  }
}

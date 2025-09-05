import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import {
  BreakdownResponse,
  BreakdownService,
  Pageable,
} from '../../../core/api/generated';
import { BreakdownCloseModalComponent } from '../../../shared/components/breakdown-close-modal/breakdown-close-modal.component';

@Component({
  selector: 'app-breakdowns',
  standalone: true,
  imports: [CommonModule, DatePipe, BreakdownCloseModalComponent],
  templateUrl: './breakdowns.component.html',
})
export class BreakdownsComponent implements OnInit {
  breakdowns: BreakdownResponse[] = [];
  isLoading = true;
  errorMessage: string | null = null;

  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  pageSizes = [5, 10, 25, 50, 100];

  isModalOpen = false;
  selectedBreakdownId: number | null = null;

  isAdmin = true;

  constructor(private breakdownService: BreakdownService) {}

  ngOnInit(): void {
    this.fetchBreakdowns();
  }

  fetchBreakdowns(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const pageable: any = {
      page: this.currentPage,
      size: this.pageSize,
      sort: 'reportedAt,desc',
    };

    this.breakdownService.getAllBreakdowns(pageable).subscribe({
      next: (response) => {
        this.breakdowns = response.content || [];
        this.totalElements = response.totalElements || 0;
        this.totalPages = response.totalPages || 0;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage =
          'Nie udało się załadować danych o awariach. Spróbuj ponownie później.';
        this.isLoading = false;
        console.error(err);
      },
    });
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = +target.value;
    this.currentPage = 0;
    this.fetchBreakdowns();
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.fetchBreakdowns();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.fetchBreakdowns();
    }
  }

  openCloseModal(breakdown: BreakdownResponse): void {
    this.selectedBreakdownId = breakdown.id ?? null;
    this.isModalOpen = true;
  }

  closeCloseModal(): void {
    this.isModalOpen = false;
    this.selectedBreakdownId = null;
  }

  handleBreakdownClosed(): void {
    this.closeCloseModal();
    this.fetchBreakdowns();
  }
}

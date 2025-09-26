import { Component, OnDestroy, OnInit, inject, signal, model } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';

import {
  EmployeeResponseDTO,
  EmployeesService,
  Pageable,
  PageEmployeeResponseDTO
} from '../../../core/api/generated';

import { EmployeeEditModalComponent } from '../../../shared/components/employee-edit-modal/employee-edit-modal.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, EmployeeEditModalComponent, FormsModule],
  templateUrl: './employee.component.html',
})
export class EmployeesComponent implements OnInit, OnDestroy {
  private employeeService = inject(EmployeesService);
  private toastr = inject(ToastrService);

  employees = signal<EmployeeResponseDTO[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  currentPage = signal(0);
  pageSize = signal(10);
  totalElements = signal(0);
  totalPages = signal(0);
  pageSizes: number[] = [5, 10, 25, 50, 100];

  isModalOpen = signal(false);
  selectedEmployeeId = signal<number | null>(null);

  searchTerm = model('');
  private searchSubject = new Subject<string>();
  private searchSubscription!: Subscription;

  isAdmin: boolean = true;

  ngOnInit(): void {
    this.loadEmployees();

    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.currentPage.set(0);
      this.loadEmployees(term);
    });
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  // 8. Metody publiczne i obsługa zdarzeń
  loadEmployees(search: string = this.searchTerm()): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const pageable: Pageable = {
      page: this.currentPage(),
      size: this.pageSize(),
    };

    this.employeeService.getAllEmployees(pageable, search).subscribe({
      next: (page: PageEmployeeResponseDTO) => {
        this.employees.set(page.content ?? []);
        this.totalElements.set(page.totalElements ?? 0);
        this.totalPages.set(page.totalPages ?? 0);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Błąd podczas ładowania pracowników', err);
        this.errorMessage.set('Nie udało się załadować danych o pracownikach.');
        this.toastr.error('Nie udało się załadować pracowników.', 'Błąd');
        this.employees.set([]);
        this.totalElements.set(0);
        this.totalPages.set(0);
        this.isLoading.set(false);
      }
    });
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchTerm());
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(page => page + 1);
      this.loadEmployees();
    }
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(page => page - 1);
      this.loadEmployees();
    }
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize.set(+target.value);
    this.currentPage.set(0);
    this.loadEmployees();
  }

  openEditModal(employee: EmployeeResponseDTO): void {
    this.selectedEmployeeId.set(employee.id ?? null);
    if (this.selectedEmployeeId()) {
      this.isModalOpen.set(true);
    }
  }

  closeEditModal(): void {
    this.isModalOpen.set(false);
    this.selectedEmployeeId.set(null);
  }

  handleEmployeeUpdate(): void {
    this.closeEditModal();
    this.loadEmployees();
  }
}

import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, startWith } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import {
  EmployeeResponseDTO,
  EmployeesService,
  Pageable,
  PageEmployeeResponseDTO
} from '../../../core/api/generated';
import { EmployeeEditModalComponent } from '../../../shared/components/employee-edit-modal/employee-edit-modal.component';
import { BreakpointService } from '../../../core/services/breakout.service';
import { PageableRequest } from '../../../core/models/pageableRequest ';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, EmployeeEditModalComponent, ReactiveFormsModule],
  templateUrl: './employee.component.html',
})
export class EmployeesComponent implements OnInit, OnDestroy {
  private employeeService = inject(EmployeesService);
  private toastr = inject(ToastrService);
  private breakpointService = inject(BreakpointService);

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

  searchControl = new FormControl('');
  private searchSubscription!: Subscription;

  isMobile = this.breakpointService.isMobile;
  isAdmin: boolean = true;

  ngOnInit(): void {
    this.searchSubscription = this.searchControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.currentPage.set(0);
      this.fetchEmployees(term || '');
    });
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  fetchEmployees(search: string): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const pageable: PageableRequest = {
      page: this.currentPage(),
      size: this.pageSize(),
      sort: 'lastName,asc',
    };

    this.employeeService.getAllEmployees(pageable as unknown as Pageable, search).subscribe({
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
        this.isLoading.set(false);
      }
    });
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(page => page + 1);
      this.fetchEmployees(this.searchControl.value || '');
    }
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(page => page - 1);
      this.fetchEmployees(this.searchControl.value || '');
    }
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize.set(+target.value);
    this.currentPage.set(0);
    this.fetchEmployees(this.searchControl.value || '');
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
    this.fetchEmployees(this.searchControl.value || '');
  }
}

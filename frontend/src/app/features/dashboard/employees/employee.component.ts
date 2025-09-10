import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import {
  EmployeeResponseDTO,
  EmployeesService,
  Pageable,
  PageEmployeeResponseDTO
} from '../../../core/api/generated';

import { EmployeeEditModalComponent } from '../../../shared/components/employee-edit-modal/employee-edit-modal.component';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, EmployeeEditModalComponent],
  templateUrl: './employee.component.html',
})
export class EmployeesComponent implements OnInit, OnDestroy {
  private employeeService = inject(EmployeesService);
  private searchSubject = new Subject<string>();
  private searchSubscription!: Subscription;

  public isAdmin: boolean = true;
  public employees: EmployeeResponseDTO[] = [];
  public searchTerm: string = '';

  public currentPage: number = 0;
  public pageSize: number = 10;
  public totalElements: number = 0;
  public totalPages: number = 0;

  public pageSizes: number[] = [5, 10, 25, 50, 100];

  public isModalOpen = false;
  public selectedEmployeeId: number | null = null;

  ngOnInit(): void {
    this.loadEmployees();

    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTerm = term;
      this.currentPage = 0;
      this.loadEmployees();
    });
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  loadEmployees(): void {
    const pageable: Pageable = {
      page: this.currentPage,
      size: this.pageSize,
    };

    this.employeeService.getAllEmployees(pageable, this.searchTerm).subscribe({
      next: (page: PageEmployeeResponseDTO) => {
        this.employees = page.content ?? [];
        this.totalElements = page.totalElements ?? 0;
        this.totalPages = page.totalPages ?? 0;
      },
      error: (err) => {
        console.error('Błąd podczas ładowania pracowników', err);
        this.employees = [];
        this.totalElements = 0;
        this.totalPages = 0;
      }
    });
  }

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchSubject.next(value);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadEmployees();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadEmployees();
    }
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = +target.value;
    this.currentPage = 0;
    this.loadEmployees();
  }

  openEditModal(employee: EmployeeResponseDTO): void {
    this.selectedEmployeeId = employee.id ?? null;
    if (this.selectedEmployeeId) {
      this.isModalOpen = true;
    }
  }

  closeEditModal(): void {
    this.isModalOpen = false;
    this.selectedEmployeeId = null;
  }

  handleEmployeeUpdate(): void {
    this.closeEditModal();
    this.loadEmployees();
  }
}

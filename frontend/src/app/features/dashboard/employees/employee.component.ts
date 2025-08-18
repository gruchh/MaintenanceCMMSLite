import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  EmployeesService,
  EmployeeSummaryResponse,
  Pageable,
  PageEmployeeSummaryResponse
} from '../../../core/api/generated';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employee.component.html',
})
export class EmpolyeesComponent implements OnInit {
  private employeeService = inject(EmployeesService);

  public isAdmin: boolean = true;
  public employees: EmployeeSummaryResponse[] = [];

  public currentPage: number = 0;
  public pageSize: number = 10;
  public totalElements: number = 0;
  public totalPages: number = 0;

  public pageSizes: number[] = [5, 10, 25, 50, 100];

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    const pageable: Pageable = {
      page: this.currentPage,
      size: this.pageSize,
    };

    this.employeeService.getAllEmployees(pageable).subscribe({
      next: (page: PageEmployeeSummaryResponse) => {
        this.employees = page.content ?? [];
        this.totalElements = page.totalElements ?? 0;
        this.totalPages = page.totalPages ?? 0;

        console.log('Załadowano stronę:', (page.number ?? 0) + 1);
      },
      error: (err) => {
        console.error('Błąd podczas ładowania pracowników', err);
        this.employees = [];
        this.totalElements = 0;
        this.totalPages = 0;
      }
    });
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
}

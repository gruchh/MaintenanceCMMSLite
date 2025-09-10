import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmployeeResponseDTO, EmployeesService, UpdateEmployeeDTO } from '../../../core/api/generated';

@Component({
  selector: 'app-employee-edit-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './employee-edit-modal.component.html',
})
export class EmployeeEditModalComponent implements OnChanges {
  private fb = inject(FormBuilder);
  private employeeService = inject(EmployeesService);

  @Input() isOpen = false;
  @Input()
  set employeeId(id: number | null) {
    this._employeeId = id;
    if (id && this.isOpen) {
      this.loadEmployeeDetails(id);
    }
  }
  get employeeId(): number | null {
    return this._employeeId;
  }
  private _employeeId: number | null = null;

  @Output() closeModal = new EventEmitter<void>();
  @Output() employeeUpdated = new EventEmitter<void>();

  public editForm: FormGroup;
  public educationLevels = Object.values(UpdateEmployeeDTO.EducationLevelEnum);
  public isLoading = false;
  public errorMessage: string | null = null;

  constructor() {
    this.editForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      hireDate: ['', Validators.required],
      street: ['', Validators.required],
      city: ['', Validators.required],
      postalCode: ['', Validators.required],
      country: ['', Validators.required],
      contractEndDate: [null],
      salary: [0, [Validators.required, Validators.min(0)]],
      educationLevel: [null, Validators.required],
      fieldOfStudy: [''],
      emergencyContactName: [''],
      emergencyContactPhone: [''],
      avatarUrl: [''],
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && !changes['isOpen'].currentValue) {
      this.resetModalState();
    }
    if (changes['isOpen'] && changes['isOpen'].currentValue && this.employeeId) {
      this.loadEmployeeDetails(this.employeeId);
    }
  }

  loadEmployeeDetails(id: number): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.editForm.reset();

    this.employeeService.getEmployeeById(id).subscribe({
      next: (employee: EmployeeResponseDTO) => {
        this.editForm.patchValue({
          firstName: employee.firstName,
          lastName: employee.lastName,
          phoneNumber: employee.phoneNumber,
          dateOfBirth: this.formatDate(employee.dateOfBirth),
          hireDate: this.formatDate(employee.hireDate),
          street: employee.street,
          city: employee.city,
          postalCode: employee.postalCode,
          country: employee.country,
          contractEndDate: this.formatDate(employee.contractEndDate),
          salary: employee.salary,
          educationLevel: employee.educationLevel,
          fieldOfStudy: employee.fieldOfStudy,
          emergencyContactName: employee.emergencyContactName,
          emergencyContactPhone: employee.emergencyContactPhone,
          avatarUrl: employee.avatarUrl,
        });
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Nie udało się pobrać danych pracownika.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  onSubmit(): void {
    if (this.editForm.invalid || !this.employeeId) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    const formValue = this.editForm.value;

    const payload: UpdateEmployeeDTO = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      phoneNumber: formValue.phoneNumber,
      dateOfBirth: formValue.dateOfBirth,
      hireDate: formValue.hireDate,
      contractEndDate: formValue.contractEndDate || null,
      salary: formValue.salary,
      educationLevel: formValue.educationLevel,
      fieldOfStudy: formValue.fieldOfStudy,
      emergencyContactName: formValue.emergencyContactName,
      emergencyContactPhone: formValue.emergencyContactPhone,
      address: {
        street: formValue.street,
        city: formValue.city,
        postalCode: formValue.postalCode,
        country: formValue.country
      }
    };

    this.employeeService.updateEmployee(this.employeeId, payload).subscribe({
      next: () => {
        this.employeeUpdated.emit();
        this.onClose();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Wystąpił błąd podczas aktualizacji. Sprawdź poprawność danych.';
        this.isLoading = false;
        console.error(err);
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  onClose(): void {
    this.closeModal.emit();
  }

  private resetModalState(): void {
    this.editForm.reset();
    this.errorMessage = null;
    this.isLoading = false;
    this._employeeId = null;
  }

  private formatDate(date: string | Date | undefined | null): string | null {
    if (!date) {
      return null;
    }
    const d = new Date(date);
    return !isNaN(d.getTime()) ? d.toISOString().substring(0, 10) : null;
  }
}

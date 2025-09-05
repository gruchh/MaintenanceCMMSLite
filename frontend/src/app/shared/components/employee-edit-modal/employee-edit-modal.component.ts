import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
// ZMIANA: Importujemy nowe DTO do aktualizacji
import { EmployeesService, EmployeeUpdateRequest, EmployeeResponse } from '../../../core/api/generated';

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
    if (id && this.isOpen) { // Ładuj dane tylko jeśli modal jest otwarty
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
  // ZMIANA: Enum pobierany z nowego DTO
  public educationLevels = Object.values(EmployeeUpdateRequest.EducationLevelEnum);
  public isLoading = false;
  public errorMessage: string | null = null;

  constructor() {
    this.editForm = this.fb.group({
      // Struktura formularza pozostaje bez zmian, ponieważ odzwierciedla pola w UI
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
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && !changes['isOpen'].currentValue) {
      this.resetModalState();
    }
    // Jeśli modal się otwiera i ma już ID, załaduj dane
    if (changes['isOpen'] && changes['isOpen'].currentValue && this.employeeId) {
      this.loadEmployeeDetails(this.employeeId);
    }
  }

  // Ta metoda pozostaje bez zmian - pobieranie danych jest oddzielone od ich aktualizacji
  loadEmployeeDetails(id: number): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.editForm.reset(); // Wyczyść formularz przed załadowaniem nowych danych

    this.employeeService.getEmployeeById(id).subscribe({
      next: (employee: EmployeeResponse) => {
        this.editForm.patchValue({
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

  // ZMIANA: Cała logika wysyłania danych została przepisana
  onSubmit(): void {
    if (this.editForm.invalid || !this.employeeId) {
      // Oznacz pola jako "dotknięte", aby pokazać błędy walidacji
      this.editForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    const formValue = this.editForm.value;

    // KROK 1: Transformacja danych z płaskiego formularza do zagnieżdżonej struktury DTO
    const payload: EmployeeUpdateRequest = {
      phoneNumber: formValue.phoneNumber,
      dateOfBirth: formValue.dateOfBirth,
      hireDate: formValue.hireDate,
      contractEndDate: formValue.contractEndDate || null,
      salary: formValue.salary,
      educationLevel: formValue.educationLevel,
      fieldOfStudy: formValue.fieldOfStudy,
      emergencyContactName: formValue.emergencyContactName,
      emergencyContactPhone: formValue.emergencyContactPhone,
      address: { // Tworzymy zagnieżdżony obiekt adresu
        street: formValue.street,
        city: formValue.city,
        postalCode: formValue.postalCode,
        country: formValue.country
      }
    };

    // KROK 2: Wywołanie nowej metody serwisu z nowym payloadem
    this.employeeService.updateEmployee(this.employeeId, payload).subscribe({
      next: () => {
        this.employeeUpdated.emit();
        this.onClose();
      },
      error: (err) => {
        // Lepsza obsługa błędów z API (jeśli backend je zwraca)
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
    // Bezpieczniejsza obsługa daty
    const d = new Date(date);
    return !isNaN(d.getTime()) ? d.toISOString().substring(0, 10) : null;
  }
}

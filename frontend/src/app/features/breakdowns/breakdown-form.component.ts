import { Component, computed, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  FormControl,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import {
  BreakdownService,
  BreakdownTypeResponseDTO,
  BreakdownTypesService,
  CreateBreakdownDTO,
  MachineResponseDTO,
  MachineService,
  BreakdownResponseDTO,
} from '../../core/api/generated';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-breakdown-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  templateUrl: './breakdown-form.component.html',
})
export class BreakdownFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private machineService = inject(MachineService);
  private breakdownTypesService = inject(BreakdownTypesService);
  private breakdownService = inject(BreakdownService);
  private toastr = inject(ToastrService);

  breakdownForm: FormGroup<{
    machineId: FormControl<number | null>;
    type: FormControl<string | null>;
    description: FormControl<string>;
  }>;

  machines$: Observable<MachineResponseDTO[]>;
  breakdownTypes$: Observable<BreakdownTypeResponseDTO[]>;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  canSubmit(): boolean {
    return this.breakdownForm.valid && !this.isLoading();
  }

  hasError = computed(() => this.errorMessage() !== null);

  constructor() {
    this.breakdownForm = this.fb.group({
      machineId: this.fb.control<number | null>(null, Validators.required),
      type: this.fb.control<string | null>(null, Validators.required),
      description: this.fb.control<string>('', {
        validators: [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(1000),
        ],
        nonNullable: true,
      }),
    });

    this.machines$ = this.machineService.getAllMachinesAsList();
    this.breakdownTypes$ = this.breakdownTypesService.getBreakdownTypes().pipe(
      tap((types) => {
        if (types && types.length > 0) {
          this.breakdownForm.patchValue({ type: types[0].value });
        }
      })
    );
  }

  onSubmit(): void {
    if (!this.canSubmit()) {
      this.breakdownForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const formValue = this.breakdownForm.getRawValue();

    const request: CreateBreakdownDTO = {
      machineId: formValue.machineId!,
      description: formValue.description,
      type: formValue.type as CreateBreakdownDTO.TypeEnum,
    };

    this.breakdownService.reportBreakdown(request).subscribe({
      next: (response: BreakdownResponseDTO) => {
        this.isLoading.set(false);
        this.toastr.success('Awaria została zgłoszona pomyślnie!');
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(
          'Wystąpił nieoczekiwany błąd podczas zgłaszania awarii. Spróbuj ponowie.'
        );
        this.toastr.error('Nie udało się zgłosić awarii. Spróbuj ponownie.');
        console.error('Wystąpił błąd podczas zgłaszania awarii', err);
      },
    });
  }

  getFieldError(fieldName: string): string | null {
    const control = this.breakdownForm.get(fieldName);
    if (control?.errors && (control.touched || control.dirty)) {
      if (control.errors['required']) return 'To pole jest wymagane.';
      if (control.errors['minlength'])
        return `Pole musi mieć minimum ${control.errors['minlength'].requiredLength} znaków.`;
      if (control.errors['maxlength'])
        return `Pole może mieć maksimum ${control.errors['maxlength'].requiredLength} znaków.`;
    }
    return null;
  }

  getSliderStyle(
    types: BreakdownTypeResponseDTO[] | null,
    selectedValue: string | null | undefined
  ): { [key: string]: string } {
    if (!types || types.length === 0 || !selectedValue) {
      return { opacity: '0' };
    }

    const selectedIndex = types.findIndex((t) => t.value === selectedValue);
    if (selectedIndex === -1) {
      return { opacity: '0' };
    }

    const percentPerItem = 100 / types.length;
    const leftPosition = selectedIndex * percentPerItem;

    return {
      opacity: '1',
      width: `${percentPerItem}%`,
      left: `${leftPosition}%`,
    };
  }
}

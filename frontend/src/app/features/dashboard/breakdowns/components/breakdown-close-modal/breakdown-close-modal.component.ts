import { Component, effect, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  FormControl,
} from '@angular/forms';
import { input, output } from '@angular/core';
import {
  BreakdownService,
  CloseBreakdownDTO,
} from '../../../../../core/api/generated';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-breakdown-close-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './breakdown-close-modal.component.html',
})
export class BreakdownCloseModalComponent {
  private fb = inject(FormBuilder);
  private breakdownService = inject(BreakdownService);
    private toastr = inject(ToastrService);

  isOpen = input<boolean>(false);
  breakdownId = input<number | null>(null);
  closeModal = output<void>();
  breakdownClosed = output<void>();

  closeForm: FormGroup<{
    closingNotes: FormControl<string>;
  }>;

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  canSubmit = computed(
    () =>
      this.closeForm?.valid && this.breakdownId() !== null && !this.isLoading()
  );

  hasError = computed(() => this.errorMessage() !== null);

  constructor() {
    this.closeForm = this.fb.group({
      closingNotes: this.fb.control<string>('', {
        validators: [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(500),
        ],
        nonNullable: true,
      }),
    });

    effect(() => {
      if (this.isOpen()) {
        this.resetForm();
      }
    });
  }

  private resetForm(): void {
    this.closeForm.reset();
    this.errorMessage.set(null);
    this.isLoading.set(false);
  }

  onClose(): void {
    this.closeModal.emit();
  }

  onSubmit(): void {
    if (!this.canSubmit()) {
      this.closeForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const request: CloseBreakdownDTO = {
      specialistComment: this.closeForm.value.closingNotes!,
    };

    this.breakdownService
      .closeBreakdown(this.breakdownId()!, request)
      .subscribe({
        next: () => {
          this.isLoading.set(false);
          this.toastr.success('Awaria została zamknięta pomyślnie!');
          this.breakdownClosed.emit();
          this.resetForm();
        },
        error: (err) => {
          this.isLoading.set(false);
          this.toastr.error("Nie udało się zamknąć awarii. Spróbuj ponownie.");
          this.errorMessage.set(
            'Wystąpił nieoczekiwany błąd. Spróbuj ponownie.'
          );
          console.error('Błąd podczas zamykania awarii:', err);
        },
      });
  }

  get closingNotesControl() {
    return this.closeForm.get('closingNotes');
  }

  getFieldError(fieldName: string): string | null {
    const control = this.closeForm.get(fieldName);
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'To pole jest wymagane';
      if (control.errors['minlength'])
        return `Minimum ${control.errors['minlength'].requiredLength} znaków`;
      if (control.errors['maxlength'])
        return `Maksimum ${control.errors['maxlength'].requiredLength} znaków`;
    }
    return null;
  }
}

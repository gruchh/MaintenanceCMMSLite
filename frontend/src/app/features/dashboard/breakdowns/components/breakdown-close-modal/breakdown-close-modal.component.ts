import { Component, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { input, output } from '@angular/core';
import { BreakdownService, CloseBreakdownDTO } from '../../../../../core/api/generated';

@Component({
  selector: 'app-breakdown-close-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './breakdown-close-modal.component.html',
})
export class BreakdownCloseModalComponent {
  isOpen = input<boolean>(false);
  breakdownId = input<number | null>(null);
  closeModal = output<void>();
  breakdownClosed = output<void>();

  closeForm: FormGroup<{
    closingNotes: import('@angular/forms').FormControl<string>;
  }>;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private breakdownService: BreakdownService
  ) {
    this.closeForm = this.fb.group({
      closingNotes: this.fb.control<string>('', {
        validators: [Validators.required, Validators.minLength(10), Validators.maxLength(500)],
        nonNullable: true,
      }),
    });

    effect(() => {
      if (this.isOpen()) {
        this.closeForm.reset();
        this.errorMessage.set(null);
        this.isLoading.set(false);
      }
    });
  }

  onClose(): void {
    this.closeModal.emit();
  }

  onSubmit(): void {
    if (this.closeForm.invalid || !this.breakdownId()) {
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const request: CloseBreakdownDTO = {
      specialistComment: this.closeForm.value.closingNotes!,
    };

    this.breakdownService.closeBreakdown(this.breakdownId()!, request).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.breakdownClosed.emit();
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set('Wystąpił nieoczekiwany błąd. Spróbuj ponownie.');
        console.error('Błąd podczas zamykania awarii:', err);
      },
    });
  }
}

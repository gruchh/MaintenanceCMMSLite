import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BreakdownService, CloseBreakdownDTO } from '../../../../../core/api/generated';

@Component({
  selector: 'app-breakdown-close-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './breakdown-close-modal.component.html',
})
export class BreakdownCloseModalComponent implements OnChanges {

  @Input() isOpen = false;
  @Input() breakdownId: number | null = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() breakdownClosed = new EventEmitter<void>();

  closeForm: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private breakdownService: BreakdownService
  ) {
    this.closeForm = this.fb.group({
      closingNotes: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.closeForm.reset();
      this.errorMessage = null;
      this.isLoading = false;
    }
  }

  onClose(): void {
    this.closeModal.emit();
  }

  onSubmit(): void {
    if (this.closeForm.invalid || !this.breakdownId) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const request: CloseBreakdownDTO = {
      specialistComment: this.closeForm.value.closingNotes,
    };

    this.breakdownService.closeBreakdown(this.breakdownId, request).subscribe({
      next: () => {
        this.isLoading = false;
        this.breakdownClosed.emit();
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie.';
        console.error("Błąd podczas zamykania awarii:", err);
      },
    });
  }
}

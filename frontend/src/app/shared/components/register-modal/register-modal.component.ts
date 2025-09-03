import { Component, signal, inject, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/api/auth.service';

@Component({
  selector: 'app-register-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-modal.component.html',
})
export class RegisterModalComponent {
  @Input({ required: true }) isOpen = false;
  @Output() close = new EventEmitter<boolean>();
  @Output() openLogin = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  public passwordVisible = signal(false);
  public isSubmitting = signal(false);
  public errorMessage = signal<string | null>(null);

  public registerForm: FormGroup;

  constructor() {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  togglePasswordVisibility(): void {
    this.passwordVisible.update(v => !v);
  }

  closeModal(success: boolean = false): void {
    this.registerForm.reset();
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
    this.close.emit(success);
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.closeModal(true);
      },
      error: (err: HttpErrorResponse) => {
        if (err.error && typeof err.error.message === 'string') {
          this.errorMessage.set(err.error.message);
        } else {
          this.errorMessage.set('Użytkownik o takiej nazwie lub e-mailu już istnieje.');
        }
        this.isSubmitting.set(false);
      }
    });
  }
}

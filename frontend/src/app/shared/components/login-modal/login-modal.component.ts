import { Component, signal, inject, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/api/auth.service';

@Component({
  selector: 'app-login-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-modal.component.html',
})
export class LoginModalComponent {
  @Input({ required: true }) isOpen = false;
  @Output() close = new EventEmitter<boolean>();

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  public passwordVisible = signal(false);
  public isSubmitting = signal(false);
  public errorMessage = signal<string | null>(null);

  public loginForm: FormGroup;

  constructor() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  togglePasswordVisibility(): void {
    this.passwordVisible.update(v => !v);
  }

  closeModal(success: boolean = false): void {
    this.loginForm.reset();
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
    this.close.emit(success);
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        console.log('Logowanie udane!');
        this.closeModal(true);
      },
      error: (err: HttpErrorResponse) => {
        if (err.error && typeof err.error.accessToken === 'string') {
          this.errorMessage.set(err.error.accessToken);
        } else {
          this.errorMessage.set('Wystąpił nieoczekiwany błąd. Spróbuj ponownie.');
        }
        this.isSubmitting.set(false);
      }
    });
  }
}

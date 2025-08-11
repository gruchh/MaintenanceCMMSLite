import { Component, signal, inject, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
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
  @Output() close = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

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

  closeModal(): void {
    this.loginForm.reset();
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
    this.close.emit();
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
        this.closeModal();
        this.router.navigate(['/dashboard']);
      },
      error: (err: HttpErrorResponse) => {
        if (err.error && typeof err.error.token === 'string') {
          this.errorMessage.set(err.error.token);
        } else {
          this.errorMessage.set('Wystąpił nieoczekiwany błąd serwera.');
        }
        this.isSubmitting.set(false);
      }
    });
  }
}

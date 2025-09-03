import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './core/api/auth.service';
import { CommonModule } from '@angular/common';
import { LoginModalComponent } from './shared/components/login-modal/login-modal.component';
import { RegisterModalComponent } from './shared/components/register-modal/register-modal.component';
import { ModalService } from './core/api/modal.service';


@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    CommonModule,
    LoginModalComponent,
    RegisterModalComponent
  ],
  templateUrl: './app.html',
})
export class App implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);
  private modalService = inject(ModalService);

  isLoginModalOpen = this.modalService.isLoginModalOpen;
  isRegisterModalOpen = this.modalService.isRegisterModalOpen;

  ngOnInit(): void {
    this.authService.initializeAuthState().subscribe();
  }

  switchToRegister(): void {
    this.modalService.openRegisterModal();
  }

  switchToLogin(): void {
    this.modalService.openLoginModal();
  }

  onLoginModalClose(isSuccess: boolean): void {
    this.modalService.closeAllModals();
    if (isSuccess) {
      this.router.navigate(['/dashboard']);
    }
  }

  onRegisterModalClose(isSuccess: boolean): void {
    this.modalService.closeAllModals();
    if (isSuccess) {
      this.router.navigate(['/dashboard']);
    }
  }
}

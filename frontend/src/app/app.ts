import { Component, computed, inject, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule, registerLocaleData } from '@angular/common';
import { LoginModalComponent } from './shared/components/login-modal/login-modal.component';
import { RegisterModalComponent } from './shared/components/register-modal/register-modal.component';
import { ModalService, ModalType } from './core/api/services/modal.service';
import { AuthService } from './core/api/services/auth.service';
import localePl from '@angular/common/locales/pl';

registerLocaleData(localePl, 'pl-PL');

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

  activeModal = this.modalService.activeModal;
  isLoginModalOpen = computed(() => this.activeModal() === 'login');
  isRegisterModalOpen = computed(() => this.activeModal() === 'register');

  ngOnInit(): void {
    this.authService.initializeAuthState().subscribe();
  }

  switchToModal(modalType: 'login' | 'register'): void {
    this.modalService.open(modalType);
  }

  onModalClose(isSuccess: boolean): void {
    this.modalService.close();
    if (isSuccess) {
      this.router.navigate(['/dashboard']);
    }
  }
}

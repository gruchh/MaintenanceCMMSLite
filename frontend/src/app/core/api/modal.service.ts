import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ModalService {
  isLoginModalOpen = signal(false);
  isRegisterModalOpen = signal(false);

  openLoginModal(): void {
    this.isRegisterModalOpen.set(false);
    this.isLoginModalOpen.set(true);
  }

  openRegisterModal(): void {
    this.isLoginModalOpen.set(false);
    this.isRegisterModalOpen.set(true);
  }

  closeAllModals(): void {
    this.isLoginModalOpen.set(false);
    this.isRegisterModalOpen.set(false);
  }
}

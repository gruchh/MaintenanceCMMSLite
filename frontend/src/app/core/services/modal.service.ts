import { Injectable, signal } from '@angular/core';

export type ModalType = 'login' | 'register' | null;

@Injectable({
  providedIn: 'root',
})
export class ModalService {
  activeModal = signal<ModalType>(null);

  open(modalType: 'login' | 'register'): void {
    this.activeModal.set(modalType);
  }

  close(): void {
    this.activeModal.set(null);
  }
}

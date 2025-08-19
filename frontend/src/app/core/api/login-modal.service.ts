import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginModalService {
  private modalState = signal(false);

  public readonly isModalOpen = this.modalState.asReadonly();

  open() {
    this.modalState.set(true);
  }

  close() {
    this.modalState.set(false);
  }
}

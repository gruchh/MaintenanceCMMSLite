import { inject } from "@angular/core";
import { CanActivateFn } from "@angular/router";
import { AuthService } from "../api/auth.service";
import { ModalService } from "../api/modal.service";

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const modalService = inject(ModalService);

  if (authService.isLoggedIn()) {
    return true; // Użytkownik jest zalogowany, więc kontynuujemy.
  }
  modalService.openLoginModal();
  return false;
};

import { inject } from "@angular/core";
import { CanActivateFn } from "@angular/router";
import { ModalService } from "../api/services/modal.service";
import { AuthService } from "../api/services/auth.service";

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const modalService = inject(ModalService);

  if (authService.isLoggedIn()) {
    return true;
  }

  modalService.open('login');

  return false;
};

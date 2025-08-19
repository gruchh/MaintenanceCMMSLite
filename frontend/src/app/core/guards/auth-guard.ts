import { inject } from "@angular/core";
import { CanActivateFn } from "@angular/router";
import { AuthService } from "../api/auth.service";
import { LoginModalService } from "../api/login-modal.service";

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const loginModalService = inject(LoginModalService);

  if (authService.isLoggedIn()) {
    return true;
  }

  loginModalService.open();
  return false;
};

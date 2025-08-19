import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/api/auth.service';
import { LoginModalService } from '../../core/api/login-modal.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private loginModalService = inject(LoginModalService);

  isLoggedIn = this.authService.isLoggedIn;
  currentUser = this.authService.currentUser;

  isMobileMenuOpen = signal(false);

  public navLinks = [
    { path: '/report-breakdown', label: 'Zgłoś awarię' },
    { path: '/dashboard', label: 'Dashboard' },
  ];

  toggleMobileMenu(): void {
    this.isMobileMenuOpen.update(v => !v);
  }

  openLoginModal(): void {
    this.loginModalService.open();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}

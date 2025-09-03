import { Component, signal, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/api/auth.service';
import { ModalService } from '../../core/api/modal.service';

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
  private modalService = inject(ModalService);

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
    this.modalService.openLoginModal();

    if (this.isMobileMenuOpen()) {
      this.isMobileMenuOpen.set(false);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
    if (this.isMobileMenuOpen()) {
      this.isMobileMenuOpen.set(false);
    }
  }
}

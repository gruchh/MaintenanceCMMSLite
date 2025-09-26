import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ModalService } from '../../core/services/modal.service';
import { AuthService } from '../../core/services/auth.service';
import { NAV_LINKS } from './navbar.constants';

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

  navLinks = NAV_LINKS;

  toggleMobileMenu(): void {
    this.isMobileMenuOpen.update(v => !v);
  }

  openAuthModal(): void {
    this.modalService.open('login');

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

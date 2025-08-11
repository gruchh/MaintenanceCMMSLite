import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { LoginModalComponent } from '../../shared/components/login-modal/login-modal.component';
import { AuthService } from '../../core/api/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    LoginModalComponent
  ],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  public authService = inject(AuthService);
  private router = inject(Router);
  public isMobileMenuOpen = signal(false);
  public isModalOpen = signal(false);

  public navLinks = [
    { path: '/report-breakdown', label: 'Zgłoś awarię' },
    { path: '/dashboard', label: 'Dashboard' },
  ];

  toggleMobileMenu(): void {
    this.isMobileMenuOpen.update(v => !v);
  }

  toggleModal(): void {
    this.isModalOpen.update(v => !v);
    if (this.isModalOpen()) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
    console.log('Wylogowano.');
  }
}

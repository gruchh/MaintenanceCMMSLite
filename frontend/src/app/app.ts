import { Component, inject, OnInit } from '@angular/core'; // <-- Dodaj OnInit
import { CommonModule } from '@angular/common';
import { LoginModalService } from './core/api/login-modal.service';
import { LoginModalComponent } from './shared/components/login-modal/login-modal.component';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './core/api/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, LoginModalComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private loginModalService = inject(LoginModalService);
  private router = inject(Router);
  private authService = inject(AuthService);

  isLoginModalOpen = this.loginModalService.isModalOpen;

  ngOnInit(): void {
    this.authService.initializeAuthState().subscribe();
  }

  onModalClose(isSuccess: boolean): void {
    this.loginModalService.close();

    if (isSuccess) {
      this.router.navigate(['/dashboard']);
    }
  }
}

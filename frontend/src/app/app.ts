import { Component, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoginModalService } from './core/api/login-modal.service';
import { LoginModalComponent } from './shared/components/login-modal/login-modal.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, LoginModalComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private loginModalService = inject(LoginModalService);
  private router = inject(Router);

  isLoginModalOpen = this.loginModalService.isModalOpen;

  onModalClose(isSuccess: boolean): void {
    this.loginModalService.close();

    if (isSuccess) {
      this.router.navigate(['/dashboard']);
    }
  }
}

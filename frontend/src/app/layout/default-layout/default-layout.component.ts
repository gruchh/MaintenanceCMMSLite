import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-default-layout',
  standalone: true,
  imports: [RouterModule, NavbarComponent],
  templateUrl: './default-layout.component.html',
})
export class DefaultLayoutComponent {}

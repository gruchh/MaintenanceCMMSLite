import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { BreakdownFormComponent } from './features/breakdown-form/breakdown-form.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  { path: 'report-breakdown', component: BreakdownFormComponent },
];

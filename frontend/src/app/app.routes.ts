import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { DefaultLayoutComponent } from './layout/default-layout/default-layout.component';
import { DashboardLayoutComponent } from './layout/dasboard-layout/dashboard-layout.component';
import { OverviewComponent } from './features/dashboard/overview/overview.component';
import { EmployeesComponent } from './features/dashboard/employees/employee.component';
import { SparePartsComponent } from './features/dashboard/spare-parts/spare-parts.component';
import { BreakdownsComponent } from './features/dashboard/breakdowns/breakdowns.component';
import { authGuard } from './core/guards/auth-guard';
import { BreakdownFormComponent } from './features/breakdowns/breakdown-form.component';
import { WorkScheduleComponent } from './features/dashboard/work-schedule/work-schedule.component';

export const routes: Routes = [
  {
    path: '',
    component: DefaultLayoutComponent,
    children: [
      {
        path: '',
        component: HomeComponent,
      },
      {
        path: 'report-breakdown',
        component: BreakdownFormComponent,
      },
    ],
  },
  {
    path: 'dashboard',
    component: DashboardLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: OverviewComponent,
      },
      {
        path: 'breakdowns',
        component: BreakdownsComponent,
      },
      {
        path: 'employees',
        component: EmployeesComponent,
      },
      {
        path: 'spare-parts',
        component: SparePartsComponent,
      },
      {
        path: 'work-schedule',
        component: WorkScheduleComponent,
      }
    ]
  }
];

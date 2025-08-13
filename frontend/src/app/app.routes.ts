import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { BreakdownFormComponent } from './features/breakdown-form/breakdown-form.component';
import { DefaultLayoutComponent } from './layout/default-layout/default-layout.component';
import { DashboardLayoutComponent } from './layout/dasboard-layout/dashboard-layout.component';
import { OverviewComponent } from './features/dashboard/overview/overview.component';
import { EmpolyeesComponent } from './features/dashboard/employees/employee.component';
import { SparePartsComponent } from './features/dashboard/spare-parts/spare-parts.component';
import { BreakdownsComponent } from './features/dashboard/breakdowns/breakdowns.component';

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
        component: EmpolyeesComponent,
      },
      {
        path: 'spare-parts',
        component: SparePartsComponent,
      }
    ]
  }
];

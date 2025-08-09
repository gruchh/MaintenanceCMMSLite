import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './overview.component.html',
})
export class OverviewComponent {
  statCards = [
    {
      label: 'Total Revenue',
      value: '$45,231.89',
      change: '+20.1% from last month'
    },
    {
      label: 'Subscriptions',
      value: '+2,350',
      change: '+180.1% from last month'
    },
    {
      label: 'Sales',
      value: '+12,234',
      change: '+19% from last month'
    },
    {
      label: 'Active Now',
      value: '+573',
      change: '+201 since last hour'
    }
  ];

  recentSales = [
    { name: 'Olivia Martin', email: 'olivia.martin@email.com', amount: '+$1,999.00' },
    { name: 'Jackson Lee', email: 'jackson.lee@email.com', amount: '+$39.00' },
    { name: 'Isabella Nguyen', email: 'isabella.nguyen@email.com', amount: '+$299.00' },
    { name: 'William Kim', email: 'will@email.com', amount: '+$99.00' },
    { name: 'Sofia Davis', email: 'sofia.davis@email.com', amount: '+$39.00' },
  ];
}

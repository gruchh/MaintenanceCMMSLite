import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { heroCalendar } from '@ng-icons/heroicons/outline';
import { heroExclamationTriangle } from '@ng-icons/heroicons/outline';
import { heroClock } from '@ng-icons/heroicons/outline';
import { heroChartBar } from '@ng-icons/heroicons/outline';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule, NgIcon],
  templateUrl: './stat-card.component.html',
  providers: [
    provideIcons({
      heroCalendar,
      heroExclamationTriangle,
      heroClock,
      heroChartBar
    })
  ]
})
export class StatCardComponent {
  @Input() icon: 'heroCalendar' | 'heroChartBar' | 'heroExclamationTriangle' | 'heroClock' = 'heroCalendar';
  @Input() value: string | number = '0';
  @Input() label: string = 'Etykieta';
  @Input() colorClass: string = 'text-gray-400';
}

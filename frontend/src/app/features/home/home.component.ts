import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DashboardService, DashboardFactoryStatsDTO } from '../../core/api/generated';
import { BreakdownService, BreakdownResponseDTO } from '../../core/api/generated';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, StatCardComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  private dashboardService = inject(DashboardService);
  private breakdownService = inject(BreakdownService);

  private state = signal({
    stats: [] as Array<any>,
    status: 'loading' as 'loading' | 'success' | 'error',
    lastBreakdown: null as BreakdownResponseDTO | null,
    error: null as unknown,
  });

  public stats = computed(() => this.state().stats);
  public status = computed(() => this.state().status);
  public lastBreakdown = computed(() => this.state().lastBreakdown);

  constructor() {
    this.loadData();
    this.loadLastBreakdown();
  }

  private loadData() {
    this.dashboardService.getFactoryStats().subscribe({
      next: (apiStats) => {
        this.state.update(s => ({
          ...s,
          stats: this.mapStatsToCardData(apiStats),
          status: 'success'
        }));
      },
      error: (err) => {
        console.error('Błąd pobierania danych:', err);
        this.state.update(s => ({ ...s, status: 'error', error: err }));
      }
    });
  }

  private loadLastBreakdown() {
    this.breakdownService.getLatestBreakdown().subscribe({
      next: (breakdown) => {
        this.state.update(s => ({ ...s, lastBreakdown: breakdown }));
      },
      error: (err) => {
        console.error('Błąd pobierania ostatniej awarii:', err);
      }
    });
  }

  private mapStatsToCardData(apiStats: DashboardFactoryStatsDTO) {
    const round = (v: number | null | undefined) => (typeof v === 'number' ? Math.round(v) : 'B/D');

    return [
      { icon: 'heroCalendar', value: round(apiStats.daysSinceLastBreakdown), label: 'Dni od ostatniej awarii', color: 'text-cyan-400' },
      { icon: 'heroExclamationTriangle', value: round(apiStats.breakdownsLastWeek), label: 'Awarie w ostatnim tygodniu', color: 'text-amber-400' },
      { icon: 'heroExclamationTriangle', value: round(apiStats.breakdownsLastMonth), label: 'Awarie w ostatnim miesiącu', color: 'text-orange-400' },
      { icon: 'heroExclamationTriangle', value: round(apiStats.breakdownsCurrentYear), label: 'Awarie w tym roku', color: 'text-red-400' },
      { icon: 'heroClock', value: typeof apiStats.averageBreakdownDurationMinutes === 'number' ? `${Math.round(apiStats.averageBreakdownDurationMinutes)} min` : 'B/D', label: 'Średni czas awarii', color: 'text-teal-400' },
      { icon: 'heroChartBar', value: typeof apiStats.averageEfficiencyPercentage === 'number' ? `${Math.round(apiStats.averageEfficiencyPercentage)}%` : 'B/D', label: 'Średnia wydajność (7 dni)', color: 'text-indigo-400' },
    ];
  }
}

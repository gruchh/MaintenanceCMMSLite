import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';

import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import {
  BreakdownService,
  BreakdownStats,
  BreakdownResponse,
} from '../../core/api/generated';

type StatCardData = {
  icon: 'heroCalendar' | 'heroExclamationTriangle' | 'heroClock';
  value: string | number;
  label: string;
  color: string;
};

type LastFailureData = {
  title: string;
  imageUrl: string;
  description: string;
};

type HomeData = {
  stats: StatCardData[];
  lastFailure: LastFailureData;
};

type ComponentState = {
  data: HomeData | null;
  status: 'loading' | 'success' | 'error';
  error?: unknown;
};

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, StatCardComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  private breakdownService = inject(BreakdownService);

  private state = signal<ComponentState>({
    data: null,
    status: 'loading',
  });

  public stats = computed(() => this.state().data?.stats);
  public lastFailure = computed(() => this.state().data?.lastFailure);
  public status = computed(() => this.state().status);

  constructor() {
    this.loadData();
  }

  private loadData(): void {
    forkJoin({
      stats: this.breakdownService.getBreakdownStats(),
      latestFailure: this.breakdownService.getLatestBreakdown(),
    }).subscribe({
      next: ({ stats, latestFailure }) => {
        const mappedData: HomeData = {
          stats: this.mapStatsToCardData(stats),
          lastFailure: this.mapLatestBreakdownToData(latestFailure),
        };
        this.state.set({ data: mappedData, status: 'success' });
      },
      error: (err) => {
        console.error(
          'Błąd podczas pobierania danych dla strony głównej:',
          err
        );
        this.state.set({ data: null, status: 'error', error: err });
      },
    });
  }

  private mapStatsToCardData(apiStats: BreakdownStats): StatCardData[] {
    const roundIfNumber = (value: number | null | undefined): number | 'B/D' => {
      return typeof value === 'number' ? Math.round(value) : 'B/D';
    };

    return [
      {
        icon: 'heroCalendar',
        value: roundIfNumber(apiStats.daysSinceLastBreakdown),
        label: 'Dni od ostatniej awarii',
        color: 'text-cyan-400',
      },
      {
        icon: 'heroExclamationTriangle',
        value: roundIfNumber(apiStats.breakdownsLastWeek),
        label: 'Awarie w ostatnim tygodniu',
        color: 'text-amber-400',
      },
      {
        icon: 'heroExclamationTriangle',
        value: roundIfNumber(apiStats.breakdownsLastMonth),
        label: 'Awarie w ostatnim miesiącu',
        color: 'text-orange-400',
      },
      {
        icon: 'heroExclamationTriangle',
        value: roundIfNumber(apiStats.breakdownsCurrentYear),
        label: 'Awarie w tym roku',
        color: 'text-red-400',
      },
      {
        icon: 'heroClock',
        value:
          typeof apiStats.averageBreakdownDurationMinutes === 'number'
            ? `${Math.round(apiStats.averageBreakdownDurationMinutes)} min`
            : 'B/D',
        label: 'Średni czas awarii',
        color: 'text-teal-400',
      },
    ];
  }

  private mapLatestBreakdownToData(
    latest: BreakdownResponse | null | undefined
  ): LastFailureData {
    if (!latest) {
      return {
        title: 'Brak zarejestrowanych awarii',
        description: 'System nie odnotował jeszcze żadnych awarii.',
        imageUrl:
          'https://cdn.pixabay.com/photo/2017/02/19/15/28/checklist-2080034_1280.jpg',
      };
    }

    return {
      title: `Awaria: ${latest.machine?.fullName ?? 'Nieznana maszyna'}`,
      description: latest.description ?? 'Brak szczegółowego opisu.',
      imageUrl:
        'https://cdn.pixabay.com/photo/2016/02/02/07/24/towing-service-1174901_1280.jpg',
    };
  }
}

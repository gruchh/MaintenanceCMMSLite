import {
  Component,
  OnInit,
  signal,
  computed,
  inject,
  DestroyRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ToastrService } from 'ngx-toastr';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import {
  heroBuildingOffice,
  heroAdjustmentsHorizontal,
  heroCalendarDays,
  heroChartBar,
  heroCog6Tooth,
  heroClock,
  heroWrenchScrewdriver,
  heroUserCircle,
  heroUsers,
  heroArrowsUpDown,
  heroExclamationTriangle,
  heroArrowPath,
} from '@ng-icons/heroicons/outline';
import {
  DashboardService,
  DashboardSnapshotDTO,
  DashboardPerformanceInditatorDTO,
  DashboardInfoAboutUser,
  DashboardWorkerBreakdownDTO,
} from '../../../core/api/generated';

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './overview.component.html',
  providers: [
    provideIcons({
      heroBuildingOffice,
      heroAdjustmentsHorizontal,
      heroCalendarDays,
      heroChartBar,
      heroCog6Tooth,
      heroClock,
      heroWrenchScrewdriver,
      heroUserCircle,
      heroUsers,
      heroArrowsUpDown,
      heroExclamationTriangle,
      heroArrowPath,
    }),
  ],
})
export class OverviewComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private destroyRef = inject(DestroyRef);
  private toastr = inject(ToastrService);

  isLoading = signal(true);
  error = signal<string | null>(null);

  performanceData = signal<number[]>([]);
  days = signal<string[]>([]);
  performanceSpotlightDate = signal('');
  performanceSpotlightValue = signal(0);
  performancePath = signal('');
  performanceDotCoords = signal<{ x: number; y: number; value: number }[]>([]);

  oeePercentage = signal<number | null>(null);
  mtbfYear = signal<number | null>(null);
  mtbfMonth = signal<number | null>(null);
  mttrYear = signal<number | null>(null);
  mttrMonth = signal<number | null>(null);

  workerBreakdownRanking = signal<DashboardWorkerBreakdownDTO[]>([]);

  employeeProfile = signal<DashboardInfoAboutUser | null>(null);

  readonly avgPerformance = computed(() => {
    const data = this.performanceData();
    if (!data.length) return 0;
    const sum = data.reduce((a, b) => a + b, 0);
    return Math.round(sum / data.length);
  });

  viewBoxWidth = 400;
  viewBoxHeight = 120;
  paddingX = 40;
  paddingTop = 20;
  paddingBottom = 30;

  ngOnInit(): void {
    this.loadDashboardData();
  }

  refreshData(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.dashboardService
      .getDashboardSnapshot()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (snapshot: DashboardSnapshotDTO) => {
          this.processApiData(snapshot.weeklyPerformance || []);

          if (snapshot.oeeStatsOverall) {
            this.oeePercentage.set(
              snapshot.oeeStatsOverall.oeePercentage ?? null
            );
            this.mtbfYear.set(snapshot.oeeStatsOverall.mtbfYear ?? null);
            this.mtbfMonth.set(snapshot.oeeStatsOverall.mtbfMonth ?? null);
            this.mttrYear.set(snapshot.oeeStatsOverall.mttrYear ?? null);
            this.mttrMonth.set(snapshot.oeeStatsOverall.mttrMonth ?? null);
          } else {
            this.setFallbackOee();
          }

          if (snapshot.workerBreakdownRanking) {
            this.workerBreakdownRanking.set(snapshot.workerBreakdownRanking);
          } else {
            this.setFallbackEmployees();
          }

          if (snapshot.userInfo) {
            this.employeeProfile.set(snapshot.userInfo);
          } else {
            this.setFallbackEmployeeProfile();
          }

          this.isLoading.set(false);
        },
        error: (error) => {
          console.error('Błąd podczas ładowania danych dashboard:', error);
          this.error.set('Nie udało się załadować danych z dashboardu.');
          this.toastr.error('Nie udało się załadować danych.', 'Błąd');
          this.isLoading.set(false);
          this.setFallbackData();
          this.setFallbackOee();
          this.setFallbackEmployees();
          this.setFallbackEmployeeProfile();
        },
      });
  }

  private processApiData(data: DashboardPerformanceInditatorDTO[]): void {
    if (!data?.length) {
      this.setFallbackData();
      return;
    }
    const sortedData = [...data].sort(
      (a, b) =>
        new Date(a.date || '').getTime() - new Date(b.date || '').getTime()
    );
    this.days.set(
      sortedData.map((item) => this.formatDateShort(item.date || ''))
    );
    this.performanceData.set(sortedData.map((item) => item.performance || 0));
    const maxPerformanceItem = sortedData.reduce((prev, current) =>
      (current.performance || 0) > (prev.performance || 0) ? current : prev
    );
    this.performanceSpotlightValue.set(maxPerformanceItem.performance || 0);
    this.performanceSpotlightDate.set(
      this.formatSpotlightDate(maxPerformanceItem.date || '')
    );
    this.rebuildChart();
  }

  private setFallbackData(): void {
    const today = new Date();
    const fallbackDays: string[] = [];
    const fallbackData = [85, 90, 78, 88, 92, 95, 80];
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(today.getDate() - i);
      fallbackDays.push(this.formatDateShort(date.toISOString()));
    }
    this.days.set(fallbackDays);
    this.performanceData.set(fallbackData);
    this.performanceSpotlightDate.set('18 Gru, 2024');
    this.performanceSpotlightValue.set(95);
    this.rebuildChart();
  }

  private setFallbackOee(): void {
    this.oeePercentage.set(85);
    this.mtbfYear.set(250);
    this.mtbfMonth.set(22);
    this.mttrYear.set(1.5);
    this.mttrMonth.set(0.9);
  }

  private setFallbackEmployees(): void {
    this.workerBreakdownRanking.set([
      {
        id: 1,
        firstName: 'Jan',
        lastName: 'Kowalski',
        role: 'Technik',
        brigade: 'A',
        breakdownCount: 10,
      },
      {
        id: 2,
        firstName: 'Anna',
        lastName: 'Nowak',
        role: 'Inżynier Procesu',
        brigade: 'B',
        breakdownCount: 8,
      },
      {
        id: 3,
        firstName: 'Piotr',
        lastName: 'Zieliński',
        role: 'Automatyk',
        brigade: 'A',
        breakdownCount: 6,
      },
    ]);
  }

  private setFallbackEmployeeProfile(): void {
    this.employeeProfile.set({
      firstName: 'Zalogowany',
      lastName: 'Użytkownik',
      breakdownCount: 5,
      avatarUrl: 'https://placehold.co/128x128',
      retirementDate: '2045-10-20',
    });
  }

  private rebuildChart(): void {
    const data = this.performanceData();
    if (!data?.length) {
      this.performancePath.set('');
      this.performanceDotCoords.set([]);
      return;
    }
    const n = data.length;
    const minVal = 0;
    const maxVal = 100;
    const innerWidth = this.viewBoxWidth - this.paddingX * 2;
    const innerHeight =
      this.viewBoxHeight - this.paddingTop - this.paddingBottom;
    const toY = (v: number) =>
      this.paddingTop + (1 - (v - minVal) / (maxVal - minVal)) * innerHeight;
    const toX = (i: number) => this.paddingX + i * (innerWidth / (n - 1 || 1));
    const points = data.map((v, i) => [toX(i), toY(v)] as [number, number]);
    const path = points
      .map(([x, y], i) => (i === 0 ? `M${x} ${y}` : `L${x} ${y}`))
      .join(' ');
    this.performancePath.set(path);
    this.performanceDotCoords.set(
      points.map(([x, y], i) => ({ x, y, value: data[i] }))
    );
  }

  private formatDateShort(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getDate().toString().padStart(2, '0')}.${(
      date.getMonth() + 1
    )
      .toString()
      .padStart(2, '0')}`;
  }

  private formatSpotlightDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const monthNames = [
      'Sty',
      'Lut',
      'Mar',
      'Kwi',
      'Maj',
      'Cze',
      'Lip',
      'Sie',
      'Wrz',
      'Paź',
      'Lis',
      'Gru',
    ];
    return `${date.getDate()} ${
      monthNames[date.getMonth()]
    }, ${date.getFullYear()}`;
  }
}

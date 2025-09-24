import { Component, OnInit, signal, computed, inject, DestroyRef } from '@angular/core';
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

  employeesByFailures = signal<
    { name: string; avatar?: string; failures: number; area?: string; role?: string }[]
  >([]);

  employeeProfile = signal<{
    name: string;
    avatar: string;
    position: string;
    remainingLeave: number;
    openWorkOrders: number;
    nextTraining: string;
  }>({
    name: 'Nieznany',
    avatar: 'https://placehold.co/128x128',
    position: 'Pracownik',
    remainingLeave: 0,
    openWorkOrders: 0,
    nextTraining: 'Brak danych',
  });

  readonly avgPerformance = computed(() => {
    const data = this.performanceData();
    if (!data.length) return 0;
    const sum = data.reduce((a, b) => a + b, 0);
    return Math.round(sum / data.length);
  });

  viewBoxWidth = 400;
  viewBoxHeight = 150; // zmniejszona wysokość wykresu
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
            this.oeePercentage.set(snapshot.oeeStatsOverall.oeePercentage ?? null);
            this.mtbfYear.set(snapshot.oeeStatsOverall.mtbfYear ?? null);
            this.mtbfMonth.set(snapshot.oeeStatsOverall.mtbfMonth ?? null);
            this.mttrYear.set(snapshot.oeeStatsOverall.mttrYear ?? null);
            this.mttrMonth.set(snapshot.oeeStatsOverall.mttrMonth ?? null);
          } else {
            this.setFallbackOee();
          }

          if (snapshot.employeeBreakdownRanking?.workers) {
            const mapped = snapshot.employeeBreakdownRanking.workers.map((w) => ({
              name: w.fullName ?? 'Nieznany',
              avatar: w.avatarUrl ?? 'https://placehold.co/64x64',
              failures: w.breakdownCount ?? 0,
              area: snapshot.employeeBreakdownRanking?.associatedArea,
              role: w.role,
            }));
            this.employeesByFailures.set(mapped);
          } else {
            this.setFallbackEmployees();
          }

          if (snapshot.userInfo) {
            this.employeeProfile.set({
              name:
                `${snapshot.userInfo.firstName || ''} ${snapshot.userInfo.lastName || ''}`.trim() ||
                'Nieznany',
              avatar: snapshot.userInfo.avatarUrl ?? 'https://placehold.co/128x128',
              openWorkOrders: snapshot.userInfo.breakdownCount ?? 0,
              position: 'Pracownik',
              remainingLeave: 0,
              nextTraining: 'Brak danych',
            });
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
      (a, b) => new Date(a.date || '').getTime() - new Date(b.date || '').getTime()
    );

    this.days.set(sortedData.map((item) => this.formatDateShort(item.date || '')));
    this.performanceData.set(sortedData.map((item) => item.performance || 0));

    const maxPerformanceItem = sortedData.reduce((prev, current) =>
      (current.performance || 0) > (prev.performance || 0) ? current : prev
    );

    this.performanceSpotlightValue.set(maxPerformanceItem.performance || 0);
    this.performanceSpotlightDate.set(this.formatSpotlightDate(maxPerformanceItem.date || ''));

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
    this.employeesByFailures.set([
      { name: 'Jan Kowalski', failures: 10, area: 'Linia A', avatar: 'https://placehold.co/64x64' },
      { name: 'Anna Nowak', failures: 8, area: 'Magazyn', avatar: 'https://placehold.co/64x64' },
      { name: 'Piotr Zieliński', failures: 6, area: 'Spawalnia', avatar: 'https://placehold.co/64x64' },
    ]);
  }

  private setFallbackEmployeeProfile(): void {
    this.employeeProfile.set({
      name: 'Nieznany',
      avatar: 'https://placehold.co/128x128',
      position: 'Pracownik',
      remainingLeave: 0,
      openWorkOrders: 0,
      nextTraining: 'Brak danych',
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
    const innerHeight = this.viewBoxHeight - this.paddingTop - this.paddingBottom;

    const toY = (v: number) =>
      this.paddingTop + (1 - (v - minVal) / (maxVal - minVal)) * innerHeight;
    const toX = (i: number) => this.paddingX + i * (innerWidth / (n - 1 || 1));

    const points = data.map((v, i) => [toX(i), toY(v)] as [number, number]);

    const path = points.map(([x, y], i) => (i === 0 ? `M${x} ${y}` : `L${x} ${y}`)).join(' ');

    this.performancePath.set(path);
    this.performanceDotCoords.set(points.map(([x, y], i) => ({ x, y, value: data[i] })));
  }

  private formatDateShort(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getDate().toString().padStart(2, '0')}.${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;
  }

  private formatSpotlightDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const monthNames = ['Sty', 'Lut', 'Mar', 'Kwi', 'Maj', 'Cze', 'Lip', 'Sie', 'Wrz', 'Paź', 'Lis', 'Gru'];
    return `${date.getDate()} ${monthNames[date.getMonth()]}, ${date.getFullYear()}`;
  }
}

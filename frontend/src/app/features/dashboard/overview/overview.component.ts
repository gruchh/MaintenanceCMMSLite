import { Component, OnInit, signal, computed, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

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
  heroArrowPath
} from '@ng-icons/heroicons/outline';
import { BreakdownPerformanceIndicatorDTO, BreakdownService } from '../../../core/api/generated';

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
      heroArrowPath
    })
  ]
})
export class OverviewComponent implements OnInit {
  private readonly breakdownService = inject(BreakdownService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(true);
  readonly error = signal<string | null>(null);
  readonly performanceData = signal<number[]>([]);
  readonly days = signal<string[]>([]);
  readonly performanceSpotlightDate = signal('');
  readonly performanceSpotlightValue = signal(0);

  readonly avgPerformance = computed(() => {
    const data = this.performanceData();
    if (!data.length) return 0;
    const sum = data.reduce((a, b) => a + b, 0);
    return Math.round(sum / data.length);
  });

  readonly performancePath = signal('');
  readonly performanceDotCoords = signal<{ x: number; y: number; value: number }[]>([]);

  readonly employeeProfile = {
    name: 'Adam Kowalski',
    position: 'Technik Utrzymania Ruchu',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=1974&auto=format&fit=crop',
    remainingLeave: 14,
    openWorkOrders: 3,
    nextTraining: 'Certyfikacja SEP G1 - 20.10.2025',
  };

  readonly employeesByFailures = [
    {
      name: 'Robert Malinowski',
      avatar: 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=1974&auto=format&fit=crop',
      failures: 21,
      area: 'Linia Montażowa A',
    },
    {
      name: 'Ewa Nowak',
      avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?q=80&w=1961&auto=format&fit=crop',
      failures: 18,
      area: 'Magazyn Wysokiego Składowania',
    },
    {
      name: 'Piotr Zieliński',
      avatar: 'https://images.unsplash.com/photo-1557862921-37829c790f19?q=80&w=2071&auto=format&fit=crop',
      failures: 15,
      area: 'Spawalnia',
    },
  ];

  readonly mtbfYear = '250 godzin';
  readonly mtbfMonth = '22 godziny';
  readonly mttrYear = '1.5 godziny';
  readonly mttrMonth = '0.9 godziny';

  readonly viewBoxWidth = 400;
  readonly viewBoxHeight = 200; // Zwiększone żeby zmieścić etykiety z procentami
  readonly paddingX = 30;
  readonly paddingTop = 25;
  readonly paddingBottom = 30; // Zwiększone dla dat na osi X

  ngOnInit(): void {
    this.loadWeeklyPerformance();
  }

  loadWeeklyPerformance(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.breakdownService.getWeeklyPerformance()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: BreakdownPerformanceIndicatorDTO[]) => {
          this.processApiData(data);
          this.isLoading.set(false);
        },
        error: (error) => {
          console.error('Error loading weekly performance:', error);
          this.error.set('Nie udało się załadować danych wydajności');
          this.isLoading.set(false);
          this.setFallbackData();
        }
      });
  }

  private processApiData(data: BreakdownPerformanceIndicatorDTO[]): void {
    if (!data || data.length === 0) {
      this.setFallbackData();
      return;
    }

    const sortedData = [...data].sort((a, b) => {
      const dateA = new Date(a.date || '').getTime();
      const dateB = new Date(b.date || '').getTime();
      return dateA - dateB;
    });

    // Formatuj daty jako "dd.MM" zamiast dni tygodnia
    const processedDays = sortedData.map(item => this.formatDateShort(item.date || ''));
    const processedPerformanceData = sortedData.map(item => item.performance || 0);

    this.days.set(processedDays);
    this.performanceData.set(processedPerformanceData);

    const maxPerformanceItem = sortedData.reduce((prev, current) =>
      (current.performance || 0) > (prev.performance || 0) ? current : prev
    );

    this.performanceSpotlightValue.set(maxPerformanceItem.performance || 0);
    this.performanceSpotlightDate.set(this.formatSpotlightDate(maxPerformanceItem.date || ''));

    this.rebuildChart();
  }

  private formatDateShort(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    return `${day}.${month}`;
  }

  private formatSpotlightDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const monthNames = [
      'Sty', 'Lut', 'Mar', 'Kwi', 'Maj', 'Cze',
      'Lip', 'Sie', 'Wrz', 'Paź', 'Lis', 'Gru'
    ];

    return `${date.getDate()} ${monthNames[date.getMonth()]}, ${date.getFullYear()}`;
  }

  private setFallbackData(): void {
    // Fallback data z datami z ostatnich 7 dni
    const today = new Date();
    const fallbackDays = [];
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

  private rebuildChart(): void {
    const data = this.performanceData();

    if (!data?.length) {
      this.performancePath.set('');
      this.performanceDotCoords.set([]);
      return;
    }

    const n = data.length;
    // Stałe wartości min/max dla osi Y (0-100%)
    const minVal = 0;
    const maxVal = 100;
    const innerWidth = this.viewBoxWidth - this.paddingX * 2;
    const innerHeight = this.viewBoxHeight - this.paddingTop - this.paddingBottom;

    const toY = (v: number) => {
      const t = (v - minVal) / (maxVal - minVal);
      return this.paddingTop + (1 - t) * innerHeight;
    };
    const toX = (i: number) => this.paddingX + i * xStep;

    const xStep = innerWidth / (n - 1 || 1);
    const points = data.map((v, i) => [toX(i), toY(v)] as [number, number]);

    const path = points
      .map(([x, y], i) => (i === 0 ? `M${x} ${y}` : `L${x} ${y}`))
      .join(' ');

    // Dodaj wartość do każdego punktu
    const dotCoords = points.map(([x, y], i) => ({
      x,
      y,
      value: data[i]
    }));

    this.performancePath.set(path);
    this.performanceDotCoords.set(dotCoords);
  }

  refreshData(): void {
    this.loadWeeklyPerformance();
  }
}

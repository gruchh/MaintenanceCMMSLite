import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

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
  heroExclamationTriangle
} from '@ng-icons/heroicons/outline';

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
      heroExclamationTriangle
    })
  ]
})
export class OverviewComponent implements OnInit {
  employeeProfile = {
    name: 'Adam Kowalski',
    position: 'Technik Utrzymania Ruchu',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=1974&auto=format&fit=crop',
    remainingLeave: 14,
    openWorkOrders: 3,
    nextTraining: 'Certyfikacja SEP G1 - 20.10.2025',
  };

  employeesByFailures = [
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

  // ====== NOWE: dane do wykresu wydajności ======
  days: string[] = ['Pon', 'Wto', 'Śro', 'Czw', 'Pią', 'Sob', 'Nie'];
  performanceData: number[] = [85, 90, 78, 88, 92, 95, 80];
  performanceSpotlightDate = '18 Gru, 2024';
  performanceSpotlightValue = 92;

  get avgPerformance(): number {
    const sum = this.performanceData.reduce((a, b) => a + b, 0);
    return Math.round((sum / this.performanceData.length));
  }

  // MTBF/MTTR – rok/miesiąc
  mtbfYear = '250 godzin';
  mtbfMonth = '22 godziny';
  mttrYear = '1.5 godziny';
  mttrMonth = '0.9 godziny';

  // ====== Rysowanie ścieżki dla SVG ======
  viewBoxWidth = 400;
  viewBoxHeight = 150;
  paddingX = 10;
  paddingTop = 16;
  paddingBottom = 26;
  performancePath = '';
  performanceDotCoords: { x: number; y: number }[] = [];

  ngOnInit(): void {
    this.rebuildChart();
  }

  private rebuildChart(): void {
    if (!this.performanceData?.length) {
      this.performancePath = '';
      this.performanceDotCoords = [];
      return;
    }

    const n = this.performanceData.length;
    const minVal = Math.min(...this.performanceData);
    const maxVal = Math.max(...this.performanceData);
    const innerWidth = this.viewBoxWidth - this.paddingX * 2;
    const innerHeight = this.viewBoxHeight - this.paddingTop - this.paddingBottom;

    const toY = (v: number) => {
      const t = (v - minVal) / (maxVal - minVal || 1);
      return this.paddingTop + (1 - t) * innerHeight;
    };
    const toX = (i: number) => this.paddingX + i * xStep;

    const xStep = innerWidth / (n - 1 || 1);
    const points = this.performanceData.map((v, i) => [toX(i), toY(v)] as [number, number]);

    this.performancePath = points
      .map(([x, y], i) => (i === 0 ? `M${x} ${y}` : `L${x} ${y}`))
      .join(' ');

    this.performanceDotCoords = points.map(([x, y]) => ({ x, y }));
  }
}

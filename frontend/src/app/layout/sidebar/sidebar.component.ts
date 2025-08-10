import { Component, inject, DestroyRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  heroHome, heroCog6Tooth, heroCubeTransparent, heroUsers,
  heroLifebuoy, heroArrowLeftOnRectangle, heroBars3, heroXMark
} from '@ng-icons/heroicons/outline';
import { CommonModule } from '@angular/common';

const CUSTOM_BREAKPOINTS = {
  mobile: '(max-width: 767.98px)',
  tablet: '(min-width: 768px) and (max-width: 1023.98px)',
  desktop: '(min-width: 1024px)',
};

interface MenuItem { path: string; iconName: string; label: string; }
interface ActionItem { action: () => void; iconName: string; label: string; }

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, NgIconComponent, CommonModule],
  templateUrl: './sidebar.component.html',
  providers: [
    provideIcons({
      heroHome, heroCog6Tooth, heroCubeTransparent, heroUsers,
      heroLifebuoy, heroArrowLeftOnRectangle, heroBars3, heroXMark,
    }),
  ],
})
export class SidebarComponent {
  private breakpointObserver = inject(BreakpointObserver);
  private readonly destroyRef = inject(DestroyRef);

  isSidebarOpen = false;
  isMobile = false;
  isTablet = false;
  isDesktop = false;

  readonly menuItems: MenuItem[] = [
    { path: '/', iconName: 'heroHome', label: 'Strona Główna' },
    { path: '/breakdowns', iconName: 'heroCog6Tooth', label: 'Awarie' },
    { path: '/spare-parts', iconName: 'heroCubeTransparent', label: 'Części zamienne' },
    { path: '/employees', iconName: 'heroUsers', label: 'Pracownicy' }, // Dodano nową pozycję
  ];

  readonly bottomItems: ActionItem[] = [
    { action: this.showSupport.bind(this), iconName: 'heroLifebuoy', label: 'Wsparcie' },
    { action: this.logout.bind(this), iconName: 'heroArrowLeftOnRectangle', label: 'Wyloguj' },
  ];

  constructor() {
    this.breakpointObserver
      .observe([
        CUSTOM_BREAKPOINTS.mobile,
        CUSTOM_BREAKPOINTS.tablet,
        CUSTOM_BREAKPOINTS.desktop,
      ])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((state) => {
        this.isMobile = state.breakpoints[CUSTOM_BREAKPOINTS.mobile];
        this.isTablet = state.breakpoints[CUSTOM_BREAKPOINTS.tablet];
        this.isDesktop = state.breakpoints[CUSTOM_BREAKPOINTS.desktop];

        if (!this.isMobile && this.isSidebarOpen) {
          this.isSidebarOpen = false;
        }
      });
  }

  toggleSidebar(): void {
    if (this.isMobile) {
      this.isSidebarOpen = !this.isSidebarOpen;
    }
  }

  onNavClick(): void {
    if (this.isMobile) {
      this.isSidebarOpen = false;
    }
  }

  showSupport(): void {
    console.log('Otwieranie okna wsparcia...');
    this.onNavClick();
  }

  logout(): void {
    console.log('Wylogowywanie...');
    this.onNavClick();
  }
}

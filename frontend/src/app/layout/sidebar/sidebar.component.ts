import { Component, inject, DestroyRef, signal, computed } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map } from 'rxjs/operators';
import {
  heroHome,
  heroCog6Tooth,
  heroCubeTransparent,
  heroUsers,
  heroLifebuoy,
  heroArrowLeftOnRectangle,
  heroBars3,
  heroXMark,
  heroCube,
  heroCalendarDateRange,
  heroCake,
} from '@ng-icons/heroicons/outline';
import { CommonModule, NgClass } from '@angular/common';
import { AuthService } from '../../core/api/services/auth.service';
import { ActionItem, MENU_ITEMS, MenuItem, SIDEBAR_ICONS } from './sidebar.constants';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, NgIconComponent, CommonModule, NgClass],
  templateUrl: './sidebar.component.html',
  providers: [
    provideIcons(SIDEBAR_ICONS),
  ],
})

export class SidebarComponent {
  private breakpointObserver = inject(BreakpointObserver);
  private destroyRef = inject(DestroyRef);
  private authService = inject(AuthService);
  private router = inject(Router);

  isSidebarOpen = signal(false);

  private breakpointState = signal({
    isMobile: false,
    isTablet: false,
    isDesktop: false,
  });

  isMobile = computed(() => this.breakpointState().isMobile);
  isTablet = computed(() => this.breakpointState().isTablet);
  isDesktop = computed(() => this.breakpointState().isDesktop);

  sidebarClasses = computed(() => {
    const mobile = this.isMobile();
    const tablet = this.isTablet();
    const desktop = this.isDesktop();
    const open = this.isSidebarOpen();

    return {
      'w-64': desktop || (mobile && open),
      'w-20': tablet,
      'translate-x-0': desktop || tablet || (mobile && open),
      '-translate-x-full': mobile && !open,
    };
  });

  readonly menuItems: MenuItem[] = MENU_ITEMS;

  readonly bottomItems: ActionItem[] = [
    {
      action: this.showSupport.bind(this),
      iconName: 'heroLifebuoy',
      label: 'Wsparcie',
    },
    {
      action: this.logout.bind(this),
      iconName: 'heroArrowLeftOnRectangle',
      label: 'Wyloguj',
    },
  ];

  constructor() {
    this.breakpointObserver
      .observe([
        Breakpoints.XSmall,
        Breakpoints.Small,
        Breakpoints.Medium,
        Breakpoints.Large,
        Breakpoints.XLarge,
      ])
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        map((state) => ({
          isMobile: state.breakpoints[Breakpoints.XSmall],
          isTablet: state.breakpoints[Breakpoints.Small],
          isDesktop:
            state.breakpoints[Breakpoints.Medium] ||
            state.breakpoints[Breakpoints.Large] ||
            state.breakpoints[Breakpoints.XLarge],
        }))
      )
      .subscribe((state) => {
        this.breakpointState.set(state);

        if (!this.isMobile() && this.isSidebarOpen()) {
          this.isSidebarOpen.set(false);
        }
      });
  }

  toggleSidebar(): void {
    if (this.isMobile()) {
      this.isSidebarOpen.update((isOpen) => !isOpen);
    }
  }

  onNavClick(): void {
    if (this.isMobile()) {
      this.isSidebarOpen.set(false);
    }
  }

  showSupport(): void {
    this.onNavClick();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
    this.onNavClick();
  }
}

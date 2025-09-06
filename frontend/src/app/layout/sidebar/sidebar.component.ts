import { Component, inject, DestroyRef, signal, computed } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  heroHome,
  heroCog6Tooth,
  heroCubeTransparent,
  heroUsers,
  heroLifebuoy,
  heroArrowLeftOnRectangle,
  heroBars3,
  heroXMark,
} from '@ng-icons/heroicons/outline';
import { CommonModule, NgClass } from '@angular/common';
import { AuthService } from '../../core/api/services/auth.service';

const CUSTOM_BREAKPOINTS = {
  mobile: '(max-width: 767.98px)',
  tablet: '(min-width: 768px) and (max-width: 1023.98px)',
  desktop: '(min-width: 1024px)',
};

interface MenuItem {
  path: string;
  iconName: string;
  label: string;
}
interface ActionItem {
  action: () => void;
  iconName: string;
  label: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, NgIconComponent, CommonModule, NgClass],
  templateUrl: './sidebar.component.html',
  providers: [
    provideIcons({
      heroHome,
      heroCog6Tooth,
      heroCubeTransparent,
      heroUsers,
      heroLifebuoy,
      heroArrowLeftOnRectangle,
      heroBars3,
      heroXMark,
    }),
  ],
})
export class SidebarComponent {
  private breakpointObserver = inject(BreakpointObserver);
  private readonly destroyRef = inject(DestroyRef);
  private authService = inject(AuthService);
  private router = inject(Router);

  isSidebarOpen = signal(false);
  isMobile = signal(false);
  isTablet = signal(false);
  isDesktop = signal(false);

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

  readonly menuItems: MenuItem[] = [
    { path: '/', iconName: 'heroHome', label: 'Strona Główna' },
    {
      path: '/dashboard/breakdowns',
      iconName: 'heroCog6Tooth',
      label: 'Awarie',
    },
    {
      path: '/dashboard/spare-parts',
      iconName: 'heroCubeTransparent',
      label: 'Części zamienne',
    },
    {
      path: '/dashboard/employees',
      iconName: 'heroUsers',
      label: 'Pracownicy',
    },
  ];

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
        CUSTOM_BREAKPOINTS.mobile,
        CUSTOM_BREAKPOINTS.tablet,
        CUSTOM_BREAKPOINTS.desktop,
      ])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((state) => {
        this.isMobile.set(state.breakpoints[CUSTOM_BREAKPOINTS.mobile]);
        this.isTablet.set(state.breakpoints[CUSTOM_BREAKPOINTS.tablet]);
        this.isDesktop.set(state.breakpoints[CUSTOM_BREAKPOINTS.desktop]);

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
    console.log('Otwieranie okna wsparcia...');
    this.onNavClick();
  }

logout(): void {
  this.authService.logout();
  this.router.navigate(['/']);
  this.onNavClick();
}
}

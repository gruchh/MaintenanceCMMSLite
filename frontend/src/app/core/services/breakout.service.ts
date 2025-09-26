import { Injectable, signal, computed, inject, DestroyRef } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map } from 'rxjs/operators';
import { BreakpointState } from '../models/breakout-status.model';

@Injectable({
  providedIn: 'root',
})
export class BreakpointService {
  private breakpointObserver = inject(BreakpointObserver);
  private destroyRef = inject(DestroyRef);

  private breakpointState = signal<BreakpointState>({
    isMobile: false,
    isTablet: false,
    isDesktop: false,
  });

  public isMobile = computed(() => this.breakpointState().isMobile);
  public isTablet = computed(() => this.breakpointState().isTablet);
  public isDesktop = computed(() => this.breakpointState().isDesktop);

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
        map((state): BreakpointState => ({
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
      });
  }
}

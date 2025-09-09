import { Component, inject, signal } from '@angular/core';
import {
  ShiftScheduleGenerateRequest,
  ShiftScheduleResponse,
  ShiftScheduleService,
} from '../../../core/api/generated';

@Component({
  selector: 'app-work-schedule',
  standalone: true,
  templateUrl: './work-schedule.html',
})
export class WorkScheduleComponent {
  private shiftScheduleService = inject(ShiftScheduleService);

  schedule = signal<ShiftScheduleResponse | null>(null);

  private getTodayFormatted(): string {
    return new Date().toISOString().slice(0, 10);
  }

  payload: ShiftScheduleGenerateRequest = {
    startDate: this.getTodayFormatted(),
    days: 28,
  };

  createSchedule(): void {
    this.shiftScheduleService.generate(this.payload).subscribe({
      next: (response) => {
        console.log('Schedule created successfully:', response);
        this.schedule.set(response);
      },
      error: (error) => {
        console.error('Error creating schedule:', error);
      },
    });
  }
}

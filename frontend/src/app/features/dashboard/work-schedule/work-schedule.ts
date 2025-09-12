import { GenerateShiftScheduleDTO } from './../../../core/api/generated/model/generateShiftScheduleDTO';
import { Component, inject, signal } from '@angular/core';
import {
  ShiftScheduleResponseDTO,
  ShiftScheduleService,
} from '../../../core/api/generated';

@Component({
  selector: 'app-work-schedule',
  standalone: true,
  templateUrl: './work-schedule.html',
})
export class WorkScheduleComponent {
  private shiftScheduleService = inject(ShiftScheduleService);

  schedule = signal<ShiftScheduleResponseDTO | null>(null);

  private getTodayFormatted(): string {
    return new Date().toISOString().slice(0, 10);
  }

  payload: GenerateShiftScheduleDTO = {
    startDate: this.getTodayFormatted(),
    days: 28,
  };

  createSchedule(): void {
    this.shiftScheduleService.createShiftSchedule(this.payload).subscribe({
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

import { FullCalendarModule } from '@fullcalendar/angular';
import { Component, computed, inject, signal } from "@angular/core";
import { GenerateShiftScheduleDTO, ShiftEntryResponseDTO, ShiftScheduleResponseDTO, ShiftScheduleService } from "../../../core/api/generated";
import { CommonModule } from "@angular/common";
import { CalendarOptions, EventClickArg, EventInput } from '@fullcalendar/core/index.js';
import dayGridPlugin from '@fullcalendar/daygrid';

@Component({
  selector: 'app-work-schedule',
  standalone: true,
  imports: [CommonModule, FullCalendarModule],
  templateUrl: './work-schedule.component.html',
})
export class WorkScheduleComponent {
  private shiftScheduleService = inject(ShiftScheduleService);
  schedule = signal<ShiftScheduleResponseDTO | null>(null);
  isLoading = signal<boolean>(false);
  error = signal<string | null>(null);

  private calendarEvents = computed(() => {
    const scheduleData = this.schedule();
    return scheduleData ? this.mapShiftsToEvents(scheduleData.entries ?? []) : [];
  });

  calendarOptions = computed<CalendarOptions>(() => ({
    plugins: [dayGridPlugin],
    initialView: 'dayGridMonth',
    events: this.calendarEvents(),
    eventClick: this.handleEventClick.bind(this),
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,dayGridWeek',
    },
    eventDisplay: 'block',
    eventDidMount: (info) => {
      const props = info.event.extendedProps as any;
      info.el.title = `${props.brigade} - ${props.shift} (${props.startTime} - ${props.endTime})`;
    },
    height: 'auto',
  }));

  private getTodayFormatted(): string {
    return new Date().toISOString().slice(0, 10);
  }

  payload: GenerateShiftScheduleDTO = {
    startDate: this.getTodayFormatted(),
    days: 28,
  };

  createSchedule(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.shiftScheduleService.createShiftSchedule(this.payload).subscribe({
      next: (response) => {
        console.log('Schedule created successfully:', response);
        this.schedule.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error creating schedule:', error);
        this.error.set(error.message || 'Failed to create schedule');
        this.isLoading.set(false);
      },
    });
  }

  private mapShiftsToEvents(entries: ShiftEntryResponseDTO[]): EventInput[] {
    return entries.map((entry) => ({
      id: entry.id?.toString(),
      title: `${entry.brigade} - ${entry.shift}`,
      start: entry.date,
      classNames: [`fc-event-${entry.shift?.toLowerCase()}`],
      extendedProps: {
        brigade: entry.brigade,
        shift: entry.shift,
        startTime: this.getShiftStartTime(entry.shift),
        endTime: this.getShiftEndTime(entry.shift),
      },
    }));
  }

  getShiftStartTime(shift: string | undefined): string {
    switch (shift) {
      case 'DAY': return '07:00';
      case 'NIGHT': return '19:00';
      default: return '-';
    }
  }

  getShiftEndTime(shift: string | undefined): string {
    switch (shift) {
      case 'DAY': return '19:00';
      case 'NIGHT': return '07:00';
      default: return '-';
    }
  }

  handleEventClick(info: EventClickArg): void {
    const event = info.event;
    const props = event.extendedProps as any;

    alert(
      `Brigade: ${props.brigade}\n` +
      `Shift: ${props.shift}\n` +
      `Date: ${event.start?.toISOString().split('T')[0]}\n` +
      `Time: ${props.startTime} - ${props.endTime}`
    );
  }
}

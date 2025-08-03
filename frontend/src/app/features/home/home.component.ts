import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, StatCardComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {

  stats = [
    { icon: 'heroCalendar', value: 7, label: 'Awarie w tym tyg.', color: 'text-cyan-400' },
    { icon: 'heroCalendar', value: 31, label: 'Awarie w tym msc.', color: 'text-cyan-400' },
    { icon: 'heroExclamationTriangle', value: 198, label: 'Awarie w tym roku', color: 'text-amber-400' },
    { icon: 'heroClock', value: '48 min', label: 'Średni czas naprawy', color: 'text-teal-400' }
  ] as const;

  lastFailure = {
    title: 'Awaria układu hydraulicznego w naczepie',
    imageUrl: 'https://cdn.pixabay.com/photo/2016/02/02/07/24/towing-service-1174901_1280.jpg',
    description: 'W dniu wczorajszym o godzinie 14:32 odnotowano krytyczny błąd systemu hydraulicznego w pojeździe o numerze WZ 12345. Zdiagnozowano wyciek płynu z głównego przewodu ciśnieniowego. Pojazd został unieruchomiony na autostradzie A2. Serwis jest w drodze na miejsce zdarzenia.'
  };
}

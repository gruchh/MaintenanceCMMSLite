import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Import CommonModule

// Definicja interfejsu dla obiektu pracownika dla lepszej kontroli typów
export interface Employee {
  id: number;
  name: string;
  email: string;
  position: string;
  status: 'Dostępny' | 'Niedostępny'; // Użycie typu unii dla precyzyjnego statusu
  imageUrl: string;
}

@Component({
  selector: 'app-employees', // Zmieniono selector dla spójności z nazwą (opcjonalne)
  standalone: true,
  imports: [CommonModule], // <-- Dodaj CommonModule do imports
  templateUrl: './employee.component.html',
})
export class EmpolyeesComponent {

  // Flaga do kontrolowania widoczności elementów dla administratora.
  // Ustaw na 'true', aby pokazać kolumnę "Akcja".
  public isAdmin: boolean = false;

  // Lista pracowników utrzymania ruchu
  public employees: Employee[] = [
    {
      id: 1,
      name: 'Neil Sims',
      email: 'neil.sims@example.com',
      position: 'Elektryk',
      status: 'Dostępny',
      imageUrl: 'https://flowbite.com/docs/images/people/profile-picture-1.jpg',
    },
    {
      id: 2,
      name: 'Bonnie Green',
      email: 'bonnie@example.com',
      position: 'Mechanik',
      status: 'Dostępny',
      imageUrl: 'https://flowbite.com/docs/images/people/profile-picture-3.jpg',
    },
    {
      id: 3,
      name: 'Jese Leos',
      email: 'jese@example.com',
      position: 'Automatyk',
      status: 'Dostępny',
      imageUrl: 'https://flowbite.com/docs/images/people/profile-picture-2.jpg',
    },
    {
      id: 4,
      name: 'Thomas Lean',
      email: 'thomas@example.com',
      position: 'Ślusarz',
      status: 'Dostępny',
      imageUrl: 'https://flowbite.com/docs/images/people/profile-picture-4.jpg',
    },
    {
      id: 5,
      name: 'Leslie Livingston',
      email: 'leslie@example.com',
      position: 'Operator wózka',
      status: 'Niedostępny',
      imageUrl: 'https://flowbite.com/docs/images/people/profile-picture-5.jpg',
    },
  ];

  constructor() {
    // Tutaj w przyszłości możesz umieścić logikę do pobierania danych z serwisu
    // lub sprawdzania uprawnień użytkownika.
  }
}

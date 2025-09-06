import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, tap } from 'rxjs/operators';
import {
  BreakdownCreateRequest,
  BreakdownService,
  BreakdownTypeResponse,
  BreakdownTypesService,
  MachineDetailsResponse,
  MachineService,
} from '../../core/api/generated';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-breakdown-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  templateUrl: './breakdown-form.component.html',
})
export class BreakdownFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private machineService = inject(MachineService);
  private breakdownTypesService = inject(BreakdownTypesService);
  private breakdownService = inject(BreakdownService);

  breakdownForm: FormGroup;
  machines$: Observable<MachineDetailsResponse[]>;
  breakdownTypes$: Observable<BreakdownTypeResponse[]>;
  isSubmitting = false;

  constructor() {
    this.breakdownForm = this.fb.group({
      machineId: [null, [Validators.required]],
      type: [null, [Validators.required]],
      description: [
        '',
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(1000),
        ],
      ],
    });

    this.machines$ = this.machineService.getAllMachinesAsList();
    this.breakdownTypes$ = this.breakdownTypesService.getBreakdownTypes().pipe(
      tap(types => {
        if (types && types.length > 0) {
          this.breakdownForm.patchValue({ type: types[0].value });
        }
      })
    );
  }

  get f() {
    return this.breakdownForm.controls;
  }

  onSubmit(): void {
    if (this.breakdownForm.invalid) {
      this.breakdownForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const request: BreakdownCreateRequest = this.breakdownForm.value;

    this.breakdownService
      .reportBreakdown(request)
      .pipe(finalize(() => (this.isSubmitting = false)))
      .subscribe({
        next: () => {
          console.log('Awaria zgłoszona pomyślnie!');
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Wystąpił błąd podczas zgłaszania awarii', err);
        },
      });
  }

  getSliderStyle(
    types: BreakdownTypeResponse[],
    selectedValue: string
  ): { [key: string]: string } {
    if (!types || types.length === 0 || !selectedValue) {
      return { opacity: '0' };
    }

    const selectedIndex = types.findIndex((t) => t.value === selectedValue);
    if (selectedIndex === -1) {
      return { opacity: '0' };
    }

    const percentPerItem = 100 / types.length;
    const leftPosition = selectedIndex * percentPerItem;

    return {
      opacity: '1',
      width: `${percentPerItem}%`,
      left: `${leftPosition}%`,
    };
  }
}

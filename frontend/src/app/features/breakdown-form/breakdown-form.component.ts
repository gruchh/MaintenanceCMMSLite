import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { BreakdownCreateRequest, BreakdownService, BreakdownTypeResponse, BreakdownTypesService, MachineDetailsResponse, MachineService } from '../../core/api';

@Component({
  selector: 'app-breakdown-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './breakdown-form.component.html',
  styleUrls: ['./breakdown-form.component.css']
})
export class BreakdownFormComponent implements OnInit {
  breakdownForm!: FormGroup;
  machines$!: Observable<MachineDetailsResponse[]>;
  breakdownTypes$!: Observable<BreakdownTypeResponse[]>;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private machineService: MachineService,
    private breakdownTypesService: BreakdownTypesService,
    private breakdownService: BreakdownService
  ) { }

  ngOnInit(): void {
    this.breakdownForm = this.fb.group({
      machineId: [null, [Validators.required]],
      type: [null, [Validators.required]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]]
    });

    this.loadDropdownData();
  }

  get f() {
    return this.breakdownForm.controls;
  }

  private loadDropdownData(): void {
    this.machines$ = this.machineService.getAllMachinesAsList();
    this.breakdownTypes$ = this.breakdownTypesService.getBreakdownTypes();
  }

  onSubmit(): void {
    if (this.breakdownForm.invalid) {
      this.breakdownForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    const request: BreakdownCreateRequest = this.breakdownForm.value;

    this.breakdownService.createBreakdown(request).pipe(
      finalize(() => this.isSubmitting = false)
    ).subscribe({
      next: () => {
       console.log('Awaria zgłoszona pomyślnie!');
       this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Wystąpił błąd podczas zgłaszania awarii', err);
      }
    });
  }
}

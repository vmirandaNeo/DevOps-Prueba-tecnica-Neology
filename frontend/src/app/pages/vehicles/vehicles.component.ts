import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ParkingApiService } from '../../core/parking-api.service';
import { Movement, Vehicle, VehicleType } from '../../core/models';

@Component({
  selector: 'app-vehicles',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressBarModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './vehicles.component.html',
  styleUrl: './vehicles.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VehiclesComponent {
  private readonly api = inject(ParkingApiService);
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly displayedColumns = ['placa', 'tipo', 'estado', 'acumulado', 'acciones'];
  readonly types: Array<{ value: VehicleType; label: string }> = [
    { value: 'OFICIAL', label: 'Oficial' },
    { value: 'RESIDENTE', label: 'Residente' },
    { value: 'NO_RESIDENTE', label: 'No residente' }
  ];
  readonly form = this.fb.nonNullable.group({
    placa: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(12), Validators.pattern(/^[A-Za-z0-9-]+$/)]],
    tipo: ['RESIDENTE' as VehicleType, Validators.required]
  });

  vehicles: Vehicle[] = [];
  filteredVehicles: Vehicle[] = [];
  loading = false;
  actionInProgress = false;
  errorMessage = '';

  constructor() {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.api.listVehicles()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: vehicles => {
          this.vehicles = vehicles;
          this.filteredVehicles = vehicles;
        },
        error: (error: Error) => this.errorMessage = error.message
      });
  }

  createVehicle(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { placa, tipo } = this.form.getRawValue();
    this.actionInProgress = true;
    this.api.createVehicle(placa.trim().toUpperCase(), tipo)
      .pipe(finalize(() => {
        this.actionInProgress = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: vehicle => {
          this.notify(`Vehículo ${vehicle.placa} registrado`);
          this.form.reset({ placa: '', tipo: 'RESIDENTE' });
          this.load();
        },
        error: (error: Error) => this.notify(error.message, true)
      });
  }

  applyFilter(value: string): void {
    const filter = value.trim().toUpperCase();
    this.filteredVehicles = filter
      ? this.vehicles.filter(vehicle => vehicle.placa.includes(filter))
      : this.vehicles;
  }

  registerMovement(vehicle: Vehicle): void {
    this.actionInProgress = true;
    const request = vehicle.estanciaAbierta
      ? this.api.registerExit(vehicle.placa)
      : this.api.registerEntry(vehicle.placa);

    request.pipe(finalize(() => {
      this.actionInProgress = false;
      this.cdr.markForCheck();
    })).subscribe({
      next: (movement: Movement) => {
        const charge = movement.importe !== null ? ` Importe: $${movement.importe.toFixed(2)}.` : '';
        this.notify(`${movement.mensaje}.${charge}`);
        this.load();
      },
      error: (error: Error) => this.notify(error.message, true)
    });
  }

  typeLabel(type: VehicleType): string {
    return this.types.find(item => item.value === type)?.label ?? type;
  }

  private notify(message: string, isError = false): void {
    this.snackBar.open(message, 'Cerrar', {
      duration: isError ? 6000 : 3500,
      panelClass: isError ? ['error-snackbar'] : undefined
    });
  }
}


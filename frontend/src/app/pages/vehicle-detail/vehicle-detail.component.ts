import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, map, switchMap } from 'rxjs';
import { VehicleDetail, VehicleType } from '../../core/models';
import { ParkingApiService } from '../../core/parking-api.service';

@Component({
  selector: 'app-vehicle-detail',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatCardModule, MatProgressBarModule, MatTableModule, RouterLink],
  templateUrl: './vehicle-detail.component.html',
  styleUrl: './vehicle-detail.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VehicleDetailComponent {
  private readonly api = inject(ParkingApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly displayedColumns = ['entrada', 'salida', 'minutos', 'importe', 'estado'];
  detail?: VehicleDetail;
  loading = true;
  errorMessage = '';

  constructor() {
    this.route.paramMap.pipe(
      map(params => params.get('placa') ?? ''),
      switchMap(plate => this.api.getVehicle(plate)),
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: detail => this.detail = detail,
      error: (error: Error) => this.errorMessage = error.message
    });
  }

  typeLabel(type: VehicleType): string {
    const labels: Record<VehicleType, string> = {
      OFICIAL: 'Oficial',
      RESIDENTE: 'Residente',
      NO_RESIDENTE: 'No residente'
    };
    return labels[type];
  }
}


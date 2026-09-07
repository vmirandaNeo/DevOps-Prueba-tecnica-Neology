import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import { ResidentPayment } from '../../core/models';
import { ParkingApiService } from '../../core/parking-api.service';

@Component({
  selector: 'app-resident-report',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatCardModule, MatProgressBarModule, MatSnackBarModule, MatTableModule],
  templateUrl: './resident-report.component.html',
  styleUrl: './resident-report.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResidentReportComponent {
  private readonly api = inject(ParkingApiService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly displayedColumns = ['placa', 'minutos', 'importe'];
  payments: ResidentPayment[] = [];
  loading = true;
  actionInProgress = false;
  errorMessage = '';

  constructor() {
    this.load();
  }

  get totalMinutes(): number {
    return this.payments.reduce((total, payment) => total + payment.minutosAcumulados, 0);
  }

  get totalAmount(): number {
    return this.payments.reduce((total, payment) => total + payment.importe, 0);
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.api.residentPayments()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: payments => this.payments = payments,
        error: (error: Error) => this.errorMessage = error.message
      });
  }

  startNewMonth(): void {
    const confirmed = window.confirm(
      'Se eliminarán las estancias cerradas y los acumulados del mes. ¿Deseas continuar?'
    );
    if (!confirmed) {
      return;
    }

    this.actionInProgress = true;
    this.api.startNewMonth()
      .pipe(finalize(() => {
        this.actionInProgress = false;
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: response => {
          this.snackBar.open(response.mensaje, 'Cerrar', { duration: 4500 });
          this.load();
        },
        error: (error: Error) => this.snackBar.open(error.message, 'Cerrar', { duration: 6000 })
      });
  }
}


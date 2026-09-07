import { Routes } from '@angular/router';
import { ResidentReportComponent } from './pages/resident-report/resident-report.component';
import { VehicleDetailComponent } from './pages/vehicle-detail/vehicle-detail.component';
import { VehiclesComponent } from './pages/vehicles/vehicles.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'vehiculos' },
  { path: 'vehiculos', component: VehiclesComponent, title: 'Vehículos | Neology' },
  { path: 'vehiculos/:placa', component: VehicleDetailComponent, title: 'Detalle | Neology' },
  { path: 'residentes', component: ResidentReportComponent, title: 'Reporte | Neology' },
  { path: '**', redirectTo: 'vehiculos' }
];


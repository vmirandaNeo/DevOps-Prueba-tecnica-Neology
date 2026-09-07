export type VehicleType = 'OFICIAL' | 'RESIDENTE' | 'NO_RESIDENTE';

export interface Vehicle {
  placa: string;
  tipo: VehicleType;
  minutosAcumulados: number;
  importeResidente: number;
  estanciaAbierta: boolean;
}

export interface Stay {
  id: number;
  fechaHoraEntrada: string;
  fechaHoraSalida: string | null;
  minutos: number | null;
  importe: number | null;
  abierta: boolean;
}

export interface VehicleDetail {
  vehiculo: Vehicle;
  estancias: Stay[];
}

export interface Movement {
  placa: string;
  tipo: VehicleType;
  fechaHora: string;
  minutos: number | null;
  importe: number | null;
  mensaje: string;
}

export interface ResidentPayment {
  placa: string;
  minutosAcumulados: number;
  importe: number;
}

export interface MessageResponse {
  mensaje: string;
}

export interface ApiError {
  mensaje?: string;
  campos?: Record<string, string>;
}


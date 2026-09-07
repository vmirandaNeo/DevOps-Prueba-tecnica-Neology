# Prueba técnica — DevOps Neology

## Introducción

Este repositorio contiene una aplicación web funcional para administrar el acceso de vehículos a un estacionamiento. La solución base incluye Angular, Spring Boot y PostgreSQL. El objetivo de la prueba no es desarrollar la funcionalidad del negocio, sino preparar su entrega y operación de forma segura, observable, reproducible y mantenible.

## Tiempo

- Tiempo efectivo máximo: **5 horas**.
- Modalidad: individual.
- Se recomienda entregar dentro de una ventana de tres días a partir de la recepción.
- Revisión posterior: 30 minutos de presentación y 15 minutos de incidente simulado.

No es indispensable completar todos los puntos. Se valoran la priorización, la calidad, las decisiones documentadas y una entrega parcial que funcione.

## Objetivo general

Tomar la aplicación base y proponer una ruta clara para llevarla desde un entorno local hasta un entorno productivo, demostrando automatización, seguridad, observabilidad y capacidad de recuperación.

## Actividades requeridas

### 1. Contenedores y ejecución local

- Auditar y mejorar los Dockerfiles existentes.
- Mantener compilaciones multi-stage.
- Ejecutar los procesos con usuarios sin privilegios.
- Reducir tamaño, superficie de ataque y tiempo de construcción.
- Mejorar health checks, manejo de señales y configuración.
- Mantener un comando reproducible para levantar el stack completo.

### 2. Integración y entrega continua

Implementar un pipeline en GitHub Actions que, como mínimo:

- Compile y pruebe el backend.
- Instale de forma reproducible y compile el frontend.
- Construya las imágenes.
- Ejecute al menos un análisis de seguridad o dependencias.
- Detenga la publicación cuando falle una validación obligatoria.
- Use caché sin comprometer la reproducibilidad.

Documentar cómo se versionarían y publicarían las imágenes. No es obligatorio utilizar un registro real.

### 3. Infraestructura como código

Crear una propuesta mínima con Terraform para AWS o Azure que contemple:

- Red y segmentación básica.
- Cómputo para frontend y backend, o una plataforma administrada equivalente.
- PostgreSQL administrado o una decisión alternativa justificada.
- Gestión de secretos y variables por ambiente.
- Salidas necesarias para despliegue u operación.

No es obligatorio aplicar la infraestructura en una cuenta real. El código debe ser coherente y validable.

### 4. Seguridad

- Evitar secretos en el repositorio y en las imágenes.
- Definir el mecanismo de inyección de secretos.
- Aplicar privilegio mínimo.
- Proponer escaneo de dependencias, imágenes e infraestructura.
- Documentar al menos tres riesgos detectados y su tratamiento.

### 5. Observabilidad

- Definir logs, métricas y alertas mínimas.
- Proponer un SLI y un SLO para la API.
- Explicar cómo correlacionar una petición del frontend con el backend.
- Incluir una consulta, panel o regla de alerta de ejemplo.

### 6. Recuperación

- Proponer respaldo y restauración de PostgreSQL.
- Documentar RPO y RTO objetivo.
- Incluir un procedimiento comprobable de restauración.
- Explicar el rollback de aplicación y configuración.

### 7. Incidente

Documentar la respuesta para este escenario:

> Después de un despliegue, el frontend responde, pero una parte de las solicitudes al backend devuelve error y la latencia aumenta. PostgreSQL reporta un crecimiento repentino de conexiones.

Indicar señales a revisar, hipótesis, pasos de contención, recuperación, comunicación y acciones preventivas.

## Entregables

- Cambios en una rama adicional del repositorio.
- `README.md` actualizado con instrucciones reproducibles.
- Pipeline de GitHub Actions.
- Dockerfiles y Compose mejorados.
- Código de infraestructura como código.
- Documentación de arquitectura y decisiones.
- Runbook de despliegue, rollback, respaldo, restauración e incidente.
- Evidencia breve de las validaciones ejecutadas.

## Criterios de evaluación

| Área | Puntos |
| --- | ---: |
| Contenedores y funcionamiento local | 20 |
| CI/CD y estrategia de entrega | 20 |
| Infraestructura como código | 15 |
| Seguridad | 15 |
| Observabilidad y SLO | 15 |
| Respaldo, restauración y respuesta a incidentes | 10 |
| Documentación, claridad y calidad general | 5 |
| **Total** | **100** |

## Consideraciones de entrega

1. Clonar este repositorio.
2. Crear una rama con el formato `candidato/nombre-apellido`.
3. Registrar commits pequeños y comprensibles.
4. No incluir contraseñas, tokens, archivos `.env` ni credenciales reales.
5. Enviar el enlace de la rama a la consultora, copiando a `vmiranda@neology.mx` y `lluna@neopartners.mx`.


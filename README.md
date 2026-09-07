# DevOps-Prueba-tecnica-Neology

## Instrucciones de la prueba técnica

La siguiente prueba tiene como objetivo evaluar a los postulantes para un perfil DevOps.

## Introducción

Este repositorio contiene una aplicación base para gestionar el acceso de vehículos a un estacionamiento:

* Frontend desarrollado en Angular.
* Backend desarrollado en Spring Boot.
* Base de datos PostgreSQL.
* Pruebas básicas incluidas en ambos proyectos.

El objetivo del candidato no es desarrollar nuevas funcionalidades, sino preparar una solución automatizada, segura, observable y reproducible para construir, validar y desplegar la aplicación.

## ¿Qué se busca evaluar?

Principalmente, los siguientes aspectos:

* Contenedores y buenas prácticas de construcción.
* Automatización mediante CI/CD.
* Uso de GitHub Actions.
* Infraestructura como código.
* Seguridad de imágenes, dependencias y secretos.
* Observabilidad y diagnóstico.
* Respaldo y recuperación.
* Conocimientos de Linux, redes y cloud.
* Capacidad para documentar decisiones y compromisos operativos.
* Enfoque DevSecOps y SRE.

## Consideraciones generales

* Ventana sugerida para realizar la prueba: 3 días.
* Tiempo máximo de trabajo efectivo: 5 horas.
* La aplicación funcional será proporcionada en el repositorio.
* No es necesario desplegar recursos reales en la nube.
* No deberán utilizarse cuentas productivas.
* No deberán incluirse credenciales, tokens o secretos reales.
* Si algún punto no puede completarse, deberá documentarse cómo se resolvería.
* Se valorará la priorización y calidad sobre la cantidad de componentes.

## Objetivo general

Preparar la aplicación de estacionamiento para que pueda construirse, probarse y desplegarse de forma automatizada, repetible y segura.

# Parte 1 — Contenedores

Crear contenedores para el frontend y backend.

## Backend

El Dockerfile deberá considerar:

* Construcción multi-stage.
* Java 17 o superior.
* Ejecución con usuario no privilegiado.
* Imagen final reducida.
* Variables de configuración externas.
* Health check.
* Manejo correcto de señales.
* Exclusión de archivos innecesarios.

## Frontend

El Dockerfile deberá considerar:

* Construcción multi-stage.
* Compilación de Angular.
* Servidor web para archivos estáticos.
* Ejecución con usuario no privilegiado cuando sea posible.
* Configuración para Single Page Application.
* Health check.
* Archivo `.dockerignore`.

## Entregables

* `backend/Dockerfile`
* `backend/.dockerignore`
* `frontend/Dockerfile`
* `frontend/.dockerignore`

# Parte 2 — Ejecución local

Crear un archivo `docker-compose.yml` que permita levantar:

* Frontend.
* Backend.
* PostgreSQL.

La solución deberá incluir:

* Red interna.
* Volumen persistente para PostgreSQL.
* Variables de ambiente.
* `.env.example`.
* Health checks.
* Dependencias basadas en salud.
* Reinicio controlado de contenedores.
* Configuración sin secretos reales.
* Acceso del frontend al backend.
* Migración o inicialización de la base de datos.

El siguiente comando deberá levantar la solución:

```bash
docker compose up --build
```

## Entregables

* `docker-compose.yml`
* `.env.example`

# Parte 3 — Integración continua con GitHub Actions

Crear un pipeline que se ejecute en Pull Requests y cambios sobre la rama principal.

El pipeline deberá incluir:

1. Validación del frontend.
2. Ejecución de pruebas del frontend.
3. Compilación del frontend.
4. Validación del backend.
5. Ejecución de pruebas del backend.
6. Compilación del backend.
7. Construcción de imágenes.
8. Análisis de vulnerabilidades.
9. Publicación de artefactos o imágenes cuando corresponda.
10. Evidencia clara cuando alguna etapa falle.

Se deberán considerar:

* Caché de dependencias.
* Ejecución paralela cuando sea conveniente.
* Versionado de imágenes.
* Permisos mínimos del workflow.
* Protección de secretos.
* Evitar publicar imágenes desde Pull Requests no confiables.

## Entregable

* `.github/workflows/ci.yml`

No es obligatorio publicar imágenes en un registro real. Puede documentarse o condicionarse el paso de publicación.

# Parte 4 — Infraestructura como código

Crear una propuesta en Terraform para desplegar la aplicación en AWS o Azure.

El candidato deberá seleccionar uno de los dos proveedores.

La infraestructura deberá contemplar, como mínimo:

* Red o integración de red.
* Servicio para ejecutar contenedores.
* Balanceo o exposición segura.
* Base de datos PostgreSQL administrada.
* Gestión de secretos.
* Registro de contenedores.
* Monitoreo y logs.
* Variables y outputs.
* Separación entre ambientes.

No es necesario ejecutar `terraform apply`.

La solución deberá permitir ejecutar:

```bash
terraform fmt -check
terraform validate
terraform plan
```

Se deberá documentar:

* Estrategia para el estado remoto.
* Separación de ambientes.
* Manejo de secretos.
* Alta disponibilidad.
* Escalamiento.
* Estimación general de los componentes con mayor impacto en costo.

## Estructura sugerida

```text
infra/
├── modules/
├── environments/
│   ├── dev/
│   └── prod/
├── versions.tf
├── providers.tf
├── variables.tf
└── outputs.tf
```

# Parte 5 — Seguridad

Incluir controles básicos de seguridad:

* Contenedores ejecutados sin privilegios.
* Imágenes base confiables y versionadas.
* Escaneo de vulnerabilidades.
* Secretos fuera del código.
* Permisos mínimos en GitHub Actions.
* Validación de dependencias.
* Comunicación segura entre componentes.
* Propuesta de rotación de secretos.
* Estrategia para parches de imágenes.

## Entregable

* `docs/security.md`

# Parte 6 — Observabilidad y operación

Diseñar una propuesta que incluya:

* Logs estructurados.
* Correlation ID.
* Métricas de frontend, backend y base de datos.
* Health, readiness y liveness checks.
* Trazas distribuidas.
* Dashboards principales.
* Alertas.
* Retención de logs.
* Indicadores de disponibilidad y rendimiento.

Definir, por lo menos:

* Dos indicadores o SLI.
* Un objetivo o SLO.
* Cuatro alertas operativas.
* Información necesaria para diagnosticar una petición fallida.

Puede utilizarse OpenTelemetry, Prometheus, Grafana o los servicios nativos del proveedor seleccionado.

No es obligatorio levantar toda la plataforma de observabilidad.

## Entregable

* `docs/observability.md`

# Parte 7 — Respaldo y recuperación

Crear scripts o comandos para:

* Generar un respaldo de PostgreSQL.
* Restaurar el respaldo.
* Validar que el archivo generado no esté vacío.
* Manejar errores.
* Evitar incluir contraseñas en el script.

También deberá documentarse:

* Frecuencia de respaldo.
* Retención.
* Cifrado.
* RPO y RTO.
* Pruebas periódicas de restauración.
* Responsables del proceso.

## Entregables

* `scripts/backup.sh`
* `scripts/restore.sh`
* `docs/backup-recovery.md`

# Parte 8 — Diagnóstico de incidente

Considere el siguiente escenario:

> Después de un despliegue, la API comienza a devolver errores HTTP 500. El tiempo de respuesta aumenta, PostgreSQL alcanza el límite de conexiones y algunos contenedores se reinician.

Documentar:

1. Validaciones iniciales.
2. Métricas y logs que revisaría.
3. Cómo determinaría si el problema proviene de la aplicación, infraestructura o base de datos.
4. Acciones inmediatas para estabilizar el servicio.
5. Criterios para realizar rollback.
6. Cómo evitar pérdida de información.
7. Acciones preventivas.
8. Mejoras que agregaría al pipeline y monitoreo.

## Entregable

* `docs/incident-response.md`

# Estructura esperada

```text
DevOps-Prueba-tecnica-Neology/
├── .github/
│   └── workflows/
│       └── ci.yml
├── backend/
│   ├── Dockerfile
│   └── .dockerignore
├── frontend/
│   ├── Dockerfile
│   └── .dockerignore
├── infra/
├── scripts/
│   ├── backup.sh
│   └── restore.sh
├── docs/
│   ├── architecture.md
│   ├── security.md
│   ├── observability.md
│   ├── backup-recovery.md
│   └── incident-response.md
├── docker-compose.yml
├── .env.example
└── README.md
```

# Entrega

El repositorio deberá incluir:

* Instrucciones para ejecutar la aplicación.
* Instrucciones para ejecutar las pruebas.
* Instrucciones para ejecutar el pipeline.
* Instrucciones para validar Terraform.
* Diagrama de la solución.
* Supuestos y decisiones.
* Limitaciones conocidas.
* Evidencias de ejecución.
* Historial de commits comprensible.

El candidato deberá crear una rama con el siguiente formato:

```text
entrega/nombre-apellido
```

Si no cuenta con permisos sobre el repositorio, podrá realizar un fork o generar un repositorio privado y compartir el acceso.

Enviar el enlace de la entrega a la consultora, copiando a:

* [vmiranda@neology.mx](mailto:vmiranda@neology.mx)
* [lluna@neopartners.mx](mailto:lluna@neopartners.mx)

# Criterios de evaluación

| Criterio                                   | Ponderación |
| ------------------------------------------ | ----------: |
| Contenedores y ejecución local             |        20 % |
| CI/CD y GitHub Actions                     |        25 % |
| Infraestructura como código                |        20 % |
| Seguridad y DevSecOps                      |        10 % |
| Observabilidad, resiliencia y recuperación |        15 % |
| Documentación y decisiones técnicas        |        10 % |

## Extras recomendados

* Kubernetes y Helm.
* OpenTelemetry funcional.
* Prometheus y Grafana.
* Generación de SBOM.
* Firma de imágenes.
* Políticas como código.
* Pruebas de infraestructura.
* Estrategia Blue/Green o Canary.
* Automatización de rollback.
* Análisis básico de costos o FinOps.

> No te preocupes si no puedes completar todos los requisitos dentro del tiempo establecido. Se valorará principalmente la calidad, la seguridad, la automatización, la capacidad de priorización y la claridad para explicar las decisiones.

## Revisión posterior

Se recomienda complementar la prueba con:

* Presentación técnica de 30 minutos.
* Simulación de incidente de 15 minutos.
* Explicación de decisiones y compromisos operativos.

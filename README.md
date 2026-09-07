# Aplicación base — Prueba técnica DevOps Neology

Aplicación funcional de estacionamiento compuesta por un backend en Spring Boot, un frontend en Angular y dos opciones de base de datos:

- **H2 en memoria** para ejecución local rápida.
- **PostgreSQL** al levantar el stack con Docker Compose.

Este código está pensado para publicarse dentro del repositorio `DevOps-Prueba-tecnica-Neology` como material inicial de la prueba DevOps. No conviene publicarlo como solución en la rama principal de `Full-Stack-Prueba-tecnica-Neology`, porque implementa buena parte del ejercicio que debe resolver el candidato Full Stack.

## Funcionalidad incluida

- Alta de vehículos oficiales, residentes y no residentes.
- Registro de entrada y salida.
- Prevención de dos entradas simultáneas para la misma placa.
- Cobro de residentes a `$0.05` por minuto acumulado.
- Cobro de no residentes a `$0.50` por minuto al salir.
- Vehículos oficiales sin cobro.
- Listado y filtro de vehículos.
- Detalle de vehículo y sus estancias.
- Reporte mensual de residentes.
- Inicio de un nuevo mes, siempre que no existan estancias abiertas.
- Endpoint de salud para validaciones operativas.

Cada minuto iniciado se cobra como un minuto completo. Los importes se manejan con `BigDecimal` para evitar errores de punto flotante.

## Estructura

```text
.
├── backend/                 API Spring Boot 3 + Java 17
├── frontend/                SPA Angular 18 + Angular Material
├── scripts/
│   ├── smoke-test.sh        Prueba funcional de la API
│   ├── verify.sh            Pruebas y compilación
│   └── verify-local-stack.sh
├── .env.example
├── docker-compose.yml
└── README.md
```

## Opción 1: levantar todo con Docker Compose

### Requisitos

- Docker Desktop o Docker Engine con Docker Compose v2.
- Puertos `4200` y `8080` disponibles.

### Ejecución

En Linux o macOS:

```bash
cp .env.example .env
docker compose up --build
```

En PowerShell:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Esperar hasta que los tres servicios aparezcan como iniciados. Después abrir:

- Frontend: <http://localhost:4200>
- Salud del backend: <http://localhost:8080/actuator/health>
- API de vehículos: <http://localhost:8080/neo/vehiculos>

Consultar el estado:

```bash
docker compose ps
docker compose logs --tail=100 backend frontend
```

Detener sin borrar los datos:

```bash
docker compose down
```

Detener y borrar exclusivamente el volumen de PostgreSQL de este proyecto:

```bash
docker compose down --volumes
```

## Opción 2: levantar localmente con H2

### Requisitos

- Java 17 o superior.
- Maven 3.9 o superior.
- Node.js 20 LTS y npm 10 o superior.
- Puertos `4200` y `8080` disponibles.

### Terminal 1 — backend

```bash
cd backend
mvn spring-boot:run
```

El perfil predeterminado usa H2 en memoria; no requiere instalar una base de datos. La consola H2 queda disponible en <http://localhost:8080/h2-console> con:

- JDBC URL: `jdbc:h2:mem:parking`
- Usuario: `sa`
- Contraseña: vacía

### Terminal 2 — frontend

```bash
cd frontend
npm ci
npm start
```

Abrir <http://localhost:4200>. El servidor de desarrollo redirige las llamadas `/neo` y `/actuator` hacia `http://localhost:8080`.

## Validación automática

### 1. Pruebas y compilación

Desde la raíz del repositorio:

```bash
./scripts/verify.sh
```

El comando falla inmediatamente si ocurre alguno de estos problemas:

- No compila Java.
- Falla una prueba de negocio del backend.
- No se pueden instalar de forma reproducible las dependencias del frontend.
- Angular no compila en modo producción.

### 2. Stack local integrado

Con los puertos `4200` y `8080` libres:

```bash
./scripts/verify-local-stack.sh
```

El script levanta temporalmente backend y frontend, espera que estén disponibles y ejecuta un recorrido real contra la API. Al terminar cierra ambos procesos.

### 3. Prueba funcional contra un stack ya levantado

```bash
./scripts/smoke-test.sh
```

Se valida:

1. Health check del backend.
2. Alta de un residente.
3. Registro de entrada.
4. Registro de salida.
5. Aparición de la placa en el reporte de residentes.
6. Respuesta HTTP del frontend, si está levantado.

## Validación manual recomendada

1. Abrir el frontend.
2. Registrar una placa como `RESIDENTE`.
3. Presionar **Registrar entrada**.
4. Confirmar que el estado cambia a **Dentro**.
5. Presionar **Registrar salida**.
6. Abrir **Reporte de residentes** y confirmar el tiempo y el importe.
7. Abrir el detalle del vehículo y revisar la estancia cerrada.
8. Iniciar un nuevo mes y confirmar que el acumulado queda en cero.

## API disponible

| Método | Ruta | Objetivo |
| --- | --- | --- |
| `GET` | `/neo/vehiculos` | Listar vehículos |
| `GET` | `/neo/vehiculos/{placa}` | Consultar vehículo y estancias |
| `POST` | `/neo/vehiculos/oficiales` | Alta de vehículo oficial |
| `POST` | `/neo/vehiculos/residentes` | Alta de vehículo residente |
| `POST` | `/neo/vehiculos/no-residentes` | Alta de vehículo no residente |
| `POST` | `/neo/estancias/entrada` | Registrar entrada |
| `POST` | `/neo/estancias/salida` | Registrar salida y calcular cobro |
| `GET` | `/neo/residentes/pagos` | Generar reporte de residentes |
| `POST` | `/neo/mes/iniciar` | Reiniciar el mes |
| `GET` | `/actuator/health` | Verificar salud del backend |

Ejemplo de alta:

```bash
curl -i -X POST http://localhost:8080/neo/vehiculos/residentes \
  -H 'Content-Type: application/json' \
  -d '{"placa":"ABC-123"}'
```

Ejemplo de entrada:

```bash
curl -i -X POST http://localhost:8080/neo/estancias/entrada \
  -H 'Content-Type: application/json' \
  -d '{"placa":"ABC-123"}'
```

## Criterio para considerar que la aplicación está lista

| Comprobación | Resultado esperado |
| --- | --- |
| `mvn test` | `BUILD SUCCESS` |
| `npm ci` | Instalación sin conflicto de dependencias |
| `npm run build` | Bundle de producción generado |
| `/actuator/health` | `{"status":"UP"}` |
| `smoke-test.sh` | Mensaje `SMOKE TEST CORRECTO` |
| Navegador | Pantalla de vehículos visible y acciones funcionales |

## Cómo publicarlo en GitHub

Crear en GitHub un repositorio vacío llamado `DevOps-Prueba-tecnica-Neology`, sin README ni `.gitignore` generados por GitHub. Después, desde esta carpeta:

```bash
git init
git add .
git commit -m "feat: agregar aplicación base para prueba DevOps"
git branch -M main
git remote add origin https://github.com/ORGANIZACION/DevOps-Prueba-tecnica-Neology.git
git push -u origin main
```

Se recomienda etiquetar la base entregada al candidato:

```bash
git tag -a app-base-v1.0.0 -m "Aplicación base validada"
git push origin app-base-v1.0.0
```

Cada candidato debe trabajar en una rama propia:

```bash
git switch -c candidato/nombre-apellido
git push -u origin candidato/nombre-apellido
```

La rama `main` conserva la aplicación base y permite comparar con claridad los cambios de Docker, pipeline, infraestructura, seguridad y observabilidad realizados por el candidato.

## Observaciones para la evaluación DevOps

La base ya contiene Dockerfiles y Compose únicamente para facilitar una referencia ejecutable. Antes de entregar la prueba puede elegirse una de estas dos modalidades:

- **Evaluación desde cero:** retirar los Dockerfiles y `docker-compose.yml`; el candidato debe crearlos.
- **Evaluación de mejora:** conservarlos y pedir al candidato que los audite, endurezca, optimice y prepare para producción.

Para una prueba de cinco horas recomiendo la modalidad de mejora: permite evaluar decisiones de seguridad, eficiencia, CI/CD, IaC, observabilidad y recuperación sin consumir la mayor parte del tiempo en configuración básica.


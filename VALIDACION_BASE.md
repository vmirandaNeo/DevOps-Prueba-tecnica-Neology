# Validación de la aplicación base

Fecha de validación: **7 de septiembre de 2026**.

## Resultado

| Comprobación | Resultado |
| --- | --- |
| Compilación Java 17 | Correcta |
| Pruebas unitarias Spring Boot | 4 ejecutadas, 0 fallas, 0 errores |
| Empaquetado JAR ejecutable | Correcto |
| Instalación limpia con `npm ci` | Correcta |
| Build Angular de producción | Correcto |
| Health check Spring Boot + H2 | `UP` |
| Alta de residente | Correcta |
| Registro de entrada y salida | Correcto |
| Reporte de residentes | Correcto |
| Respuesta del frontend | Correcta |
| Sintaxis YAML de Compose | Correcta |

El recorrido integrado fue ejecutado con `scripts/verify-local-stack.sh` y terminó con el mensaje:

```text
STACK LOCAL CORRECTO: Spring Boot, H2 y Angular funcionaron de manera integrada.
```

La construcción real con Docker Compose debe repetirse en una máquina con Docker Engine o Docker Desktop, tal como se indica en `GUIA_EVALUACION.md`.


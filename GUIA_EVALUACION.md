# Guía rápida para evaluar la entrega

## 1. Validar la aplicación base

Antes de revisar la rama del candidato, comprobar que la etiqueta `app-base-v1.0.0` funciona:

```bash
git switch main
git pull
git switch --detach app-base-v1.0.0
./scripts/verify.sh
./scripts/verify-local-stack.sh
```

## 2. Revisar el cambio del candidato

```bash
git fetch --all --tags
git switch candidato/nombre-apellido
git diff --stat app-base-v1.0.0...HEAD
git log --oneline app-base-v1.0.0..HEAD
```

Comprobar que no existan secretos o archivos locales versionados:

```bash
git ls-files | grep -E '(^|/)(\.env|id_rsa|.*\.pem)$' && echo "REVISAR POSIBLE SECRETO" || true
```

## 3. Verificación local obligatoria

```bash
./scripts/verify.sh
docker compose config
docker compose build --no-cache
docker compose up --detach
docker compose ps
./scripts/smoke-test.sh
docker compose logs --tail=150 backend frontend database
docker compose down
```

Resultado mínimo esperado:

- Pruebas Java aprobadas.
- Bundle Angular generado.
- Tres contenedores saludables.
- Flujo de alta, entrada, salida y reporte correcto.
- Sin credenciales reales en el repositorio.

## 4. Pipeline

Revisar que una ejecución desde cero:

- Se active con el evento documentado.
- Ejecute pruebas y compilaciones antes de publicar.
- No continúe si una etapa requerida falla.
- Produzca artefactos o imágenes identificables por versión/commit.
- No imprima secretos.
- Utilice caché de forma segura.

## 5. Infraestructura

Ejecutar en el directorio indicado por el candidato:

```bash
terraform fmt -check -recursive
terraform init -backend=false
terraform validate
terraform plan
```

No se debe aplicar infraestructura real para evaluar la prueba. Revisar aislamiento de red, secretos, estado remoto propuesto, variables por ambiente, permisos y estrategia de despliegue.

## 6. Preguntas de revisión

- ¿Qué cambiaría el candidato antes de llegar a producción y por qué?
- ¿Cómo evita una imagen vulnerable o una prueba fallida en producción?
- ¿Cuál es el rollback y cuánto tarda?
- ¿Cómo detectaría una degradación antes que el usuario?
- ¿Qué RPO y RTO propone y cómo los comprobó?
- ¿Cómo rota un secreto sin reconstruir todo el sistema?

## 7. Incidente simulado

Presentar el escenario incluido en `PRUEBA_TECNICA.md` sin información adicional durante los primeros cinco minutos. Evaluar:

- Orden y seguridad del diagnóstico.
- Uso de métricas, logs y trazas.
- Capacidad para contener impacto.
- Criterios de rollback.
- Comunicación y registro de decisiones.
- Acciones preventivas posteriores.


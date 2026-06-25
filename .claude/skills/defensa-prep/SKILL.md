---
name: defensa-prep
description: Dado un servicio (ej. /defensa-prep ms-pagos), lee su código real y genera preguntas orales de defensa con respuestas de referencia, gotchas frecuentes, y un ejercicio de live-coding tipo IE 2.2.5. Sin argumento, genera preguntas de arquitectura general. No modifica archivos.
disable-model-invocation: true
---

# Preparación para Defensa Oral — Evaluación Parcial 2

El argumento es el nombre del servicio a preparar (ej. `ms-pagos`, `auth-service`, `ms-accesos`).
Sin argumento → preguntas de arquitectura general del sistema completo.

## Contexto de la defensa

- **Fecha**: viernes 22/05/2026, sin internet
- **Proyecto corriendo localmente** con Postman listo
- **Indicador más pesado**: IE 2.2.5 (12%) — agregar una validación/regla en vivo y probarla en Postman
- **Criterio 30% grupal**: entrega del código; **70% individual**: defensa oral

## Pasos obligatorios antes de responder

1. Leer `CLAUDE.md` para arquitectura completa, convenciones y estado del proyecto
2. Leer **todos** los archivos `.java` del servicio indicado bajo `{servicio}/src/main/java/`
3. Leer el script SQL correspondiente en `db/` para verificar coherencia entidad-tabla
4. Si el servicio usa Feign, leer los clientes Feign y los servicios destino relevantes
5. Leer `{servicio}/src/main/resources/application.properties`

## Entregables (en este orden)

### 1. Preguntas de conceptos (3 preguntas)
Preguntas sobre decisiones de arquitectura y patrones usados:
- ¿Por qué usaste esta anotación / patrón?
- ¿Cómo funciona el flujo de X de extremo a extremo?
- ¿Qué ventaja tiene separar en capas Controller / Service / Repository?

Incluir respuesta de referencia basada en el código real del servicio.

### 2. Preguntas de código concreto (4 preguntas)
El profesor señala una clase o método y pregunta. Para cada pregunta:
- Citar el archivo y método real
- Explicar qué hace exactamente
- Mencionar qué pasaría si se eliminara esa línea/validación

Ejemplos de foco según servicio:
- `ms-pagos`: la fórmula BigDecimal, los 7 Feign clients, RoundingMode.HALF_UP
- `ms-accesos`: registrarEntrada vs registrarSalida, cálculo de minutos, llamada a EspacioClient
- `ms-reservas`: máquina de estados EstadoEnums, validaciones Feign en create()
- `auth-service`: flujo JWT completo, BCrypt strength 12, findByEmail vs findByUsername
- `ms-reportes`: que no tiene BD propia, solo Feign — cómo funciona sin @Entity

### 3. Preguntas de base de datos (2 preguntas)
- ¿Por qué no hay FK entre servicios en la BD?
- ¿Qué pasa si un `id_X_ref` apunta a un registro que ya no existe?
- ¿Por qué usaste `ddl-auto=validate` en producción y no `create`?

### 4. Ejercicio IE 2.2.5 — Live coding propuesto
Proponer UNA validación/regla de negocio que:
- Sea realista y útil para el servicio
- Se pueda implementar en 10 minutos durante la defensa
- Se pruebe con una sola request en Postman
- No rompa los 74 tests existentes del Newman

Formato del ejercicio:
- **Qué implementar**: descripción en una oración
- **Dónde**: archivo y método exacto donde se agrega
- **Enfoque**: descripción del patrón sin escribir el código completo
- **Cómo probar en Postman**: qué request enviar, qué response esperar (éxito y error)

### 5. Gotchas de este servicio (3–5 puntos)
Cosas que pueden salir mal durante la defensa:
- Partes del código que son confusas a primera vista
- Dependencias que deben estar corriendo primero (ej. Eureka, el servicio X)
- Errores comunes al explicar el flujo (ej. confundir Feign con REST directo)
- Diferencias entre este servicio y el patrón estándar (ej. ms-reportes sin BD)

## Restricción

NO modificar archivos del proyecto. Solo análisis y preparación oral.
Recordar al estudiante que practique respondiendo en voz alta — leer las respuestas no es suficiente para la defensa.

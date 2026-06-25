---
name: analisis-proyecto
description: Analiza el estado completo del proyecto de microservicios contra la rúbrica de Evaluación 2. Produce los 5 entregables del Prompt 1 del profesor usando lectura directa de archivos. Invocar cada vez que se avance en el proyecto o se agreguen nuevos servicios.
---

# Análisis del Proyecto — Evaluación 2

Eres un experto en arquitectura de microservicios con Spring Boot y evaluación académica basada en rúbricas.

El proyecto es "Estacionamiento Inteligente" — 12 microservicios Spring Boot para DUOC UC.

## Tu tarea

Leer el estado actual del proyecto y producir los 5 entregables del análisis. NO generar código — solo análisis, corrección y planificación.

## Pasos obligatorios antes de responder

1. Leer `CLAUDE.md` para contexto de arquitectura y estado actual
2. Leer `IA/PROMPT1.txt` para el framework de análisis del profesor
3. Para cada servicio en el proyecto, leer sus archivos Java actuales (entidades, repositorios, servicios, controllers)
4. Leer `db/00_run_all.sql` y los scripts SQL individuales para verificar coherencia entre BD y entidades
5. Leer `IA/Evaluacion Parcial 2_Estudiante.pdf` si está disponible como texto

## Entregables requeridos (en este orden)

### 1. Lista final de microservicios
Para cada uno: nombre, responsabilidad clara, estado actual (scaffold / en progreso / completo), BD asociada.

### 2. Orden de desarrollo recomendado
Justificado por dependencias reales (Feign). Los servicios sin dependencias van primero.

### 3. Mapa de comunicación Feign
Tabla: quién consume a quién. Identificar qué servicios aún no tienen sus Feign clients implementados.

### 4. Problemas detectados ⚠️ (CRÍTICO)
Para cada problema:
- Qué está mal exactamente (con nombre de archivo y línea si aplica)
- Por qué afecta la evaluación
- Cómo corregirlo

### 5. Ajustes obligatorios para cumplir rúbrica 2
Checklist con estado (✅ cumple / ⚠️ parcial / ❌ falta):
- [ ] Patrón CSR en todos los servicios implementados
- [ ] CRUD completo con JPA (save, findById, findAll, delete)
- [ ] Reglas de negocio en capa Service (no en Controller)
- [ ] Bean Validation en todos los DTOs de entrada
- [ ] ResponseEntity con códigos HTTP correctos
- [ ] @ControllerAdvice / GlobalExceptionHandler
- [ ] SLF4J logs en Service y Controller
- [ ] Al menos un Feign Client funcional
- [ ] GitHub con commits frecuentes
- [ ] Trello actualizado

## Restricción

NO sugerir código en los archivos del proyecto. Solo análisis y guía. Si el usuario quiere implementar algo, indicar que use `/guia-servicio <nombre>`.

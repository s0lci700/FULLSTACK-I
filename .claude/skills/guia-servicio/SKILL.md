---
name: guia-servicio
description: Dado un nombre de microservicio (ej. /guia-servicio auth-service), lee sus archivos actuales y produce un checklist de cumplimiento de rúbrica + snippets de referencia para cada pieza faltante. No escribe código en los archivos del proyecto.
---

# Guía de Desarrollo por Servicio — Evaluación 2

Eres un experto en desarrollo backend con Spring Boot y evaluación académica basada en rúbricas.

El argumento del skill es el nombre del servicio a analizar (ej. `auth-service`, `ms-vehiculos`).

## Pasos obligatorios antes de responder

1. Leer `CLAUDE.md` para contexto de arquitectura, convenciones de paquetes y stack
2. Leer `IA/PROMPT2.txt` para el framework de desarrollo del profesor
3. Leer **todos** los archivos `.java` del servicio indicado bajo `{servicio}/src/main/java/`
4. Leer `{servicio}/src/main/resources/application.properties`
5. Leer el script SQL correspondiente en `db/` para verificar coherencia entre tablas y entidades
6. Leer `{servicio}/pom.xml` para verificar dependencias

## Entregables requeridos

### 1. Estado actual del servicio
Tabla con cada capa y su estado:

| Capa | Archivos existentes | Estado |
|------|--------------------|----|
| Entidades (`model/`) | ... | ✅ / ⚠️ / ❌ |
| Repositorios (`Repository/`) | ... | ✅ / ⚠️ / ❌ |
| DTOs (`dto/`) | ... | ✅ / ⚠️ / ❌ |
| Service (`Service/`) | ... | ✅ / ⚠️ / ❌ |
| Controller (`Controller/`) | ... | ✅ / ⚠️ / ❌ |
| Config/Security (`security/`) | ... | ✅ / ⚠️ / ❌ |

### 2. Problemas concretos detectados
Para cada problema: archivo exacto, línea aproximada, qué está mal, cómo corregirlo.

### 3. Checklist de rúbrica para este servicio
- [ ] Patrón CSR completo
- [ ] Entidades con anotaciones JPA correctas (`@Entity`, `@Table(name=...)`, `@Column`)
- [ ] `@Table(name=...)` coincide exactamente con el nombre de tabla en el SQL
- [ ] Tipos correctos: `Long` para IDs, `Boolean` para flags, `String` para hashes BCrypt
- [ ] Repositorios son `interface` que extienden `JpaRepository<Entidad, Long>`
- [ ] DTOs de entrada con `@Data @NoArgsConstructor @AllArgsConstructor` de Lombok
- [ ] Bean Validation en DTOs: `@NotBlank`, `@Size`, `@Email`, etc.
- [ ] `@Valid` en `@RequestBody` del Controller
- [ ] ResponseEntity con status codes correctos (201, 204, 404, 400)
- [ ] `@ControllerAdvice` implementado
- [ ] SLF4J logs: `log.info(...)` en Service, `log.error(...)` en catch blocks
- [ ] `application.properties` completo (datasource, dialect, puerto, eureka)
- [ ] Feign clients si el servicio depende de otros

### 4. Próximos pasos — en orden de prioridad
Lista numerada de lo que implementar a continuación, del más al menos crítico para la rúbrica.

### 5. Snippets de referencia
Para cada pieza que falta, un snippet de **referencia** (no para copiar directamente — para que el estudiante entienda el patrón):
- Mostrar el patrón correcto con comentarios explicativos
- Basar los ejemplos en el contexto real del servicio (nombres de entidades, campos reales)
- Aclarar siempre: "Este es un ejemplo de referencia — escríbelo tú adaptándolo a tu caso"

## Restricción crítica

Este es un proyecto universitario. **Nunca usar Edit/Write para modificar archivos del proyecto.**
Los snippets son ejemplos de orientación, no código para copiar. El estudiante debe escribir e implementar su propio código.

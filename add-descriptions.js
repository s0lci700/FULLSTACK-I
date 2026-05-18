const fs = require('fs');
const c = JSON.parse(fs.readFileSync('estacionamiento.postman_collection.json', 'utf8'));

const descriptions = {
  // Fase 1 — Auth
  '#1 — Login admin':
    'Autentica al administrador con email y contraseña. El script de test guarda el token JWT en la variable de colección `token`, que es reutilizado por todos los requests siguientes como Bearer token.',
  '#2 — Login María (cliente)':
    'Autentica a la clienta María González (rol USER). Verifica que usuarios no-admin también pueden obtener un token JWT válido.',
  '#3 — Login credenciales inválidas':
    'Intenta autenticar con un usuario inexistente. Valida que el sistema retorna 401 Unauthorized ante credenciales incorrectas.',
  '#4 — Register nuevo usuario':
    'Registra una nueva credencial en auth-service con email único generado en pre-request (timestamp). Confirma 201 Created al crear el UserCredential.',

  // Fase 2 — Datos de referencia
  '#5 — GET tipos de cliente':
    'Lista todos los tipos de cliente disponibles (REGULAR, PREMIUM, CORPORATIVO). Cada tipo tiene un porcentaje de descuento que se aplica en el cálculo de cobros.',
  '#6 — GET tipos de vehículo':
    'Lista todos los tipos de vehículo con sus factores de multiplicación de tarifa (AUTO, MOTO, CAMIONETA, BUS). Usado por ms-pagos en la fórmula de billing.',
  '#7 — GET tipos de espacio':
    'Lista todos los tipos de espacio del estacionamiento (ESTÁNDAR, DISCAPACITADO, MOTO, BICICLETA) con sus factores tarifarios.',
  '#8 — GET tarifas':
    'Lista todas las tarifas configuradas en ms-tarifas. Cada tarifa tiene un precioBaseHora y puede estar activa o inactiva.',
  '#9 — GET tarifa vigente':
    'Retorna la tarifa con `activo=true`. Invocada internamente por ms-pagos vía Feign client al calcular el monto de un cobro.',
  '#10 — GET horarios de tarifa':
    'Lista todos los horarios-tarifa con sus multiplicadores, rangos horarios y tipo de día (LABORAL, FIN_DE_SEMANA, FESTIVO).',
  '#11 — GET horario vigente':
    'Retorna el horario-tarifa cuyo rango horario coincide con la hora actual del sistema. Acepta 200 o 404 si no hay horario configurado para ese rango.',
  '#12 — GET bancos':
    'Lista los bancos disponibles para métodos de pago (BancoEstado, Santander, BCI, Scotiabank). Cada banco tiene un porcentaje de descuento.',
  '#13 — GET tipos de tarjeta':
    'Lista los tipos de tarjeta soportados (DÉBITO, CRÉDITO) para registrar métodos de pago.',

  // Fase 3 — Clientes
  '#14 — GET todos los clientes':
    'Lista todos los clientes registrados en user-service con sus datos personales y tipo de cliente.',
  '#15 — GET cliente 1 (María)':
    'Obtiene el detalle completo del cliente seed María González (id=1). Verifica que el registro existe y tiene los datos correctos.',
  '#16 — GET cliente 999 (no existe → 404)':
    'Intenta obtener un cliente con ID inexistente. Valida que GlobalExceptionHandler retorna 404 Not Found con el formato estándar de error.',
  '#17 — POST crear cliente Ana':
    'Crea el cliente Ana Soto con RUT y email únicos generados dinámicamente en pre-request para garantizar idempotencia. Guarda el ID retornado en la variable `clienteId`.',
  '#18 — PUT actualizar Ana (apellido + tipo)':
    'Actualiza el apellido y tipo de cliente de Ana (reemplaza el recurso completo). Nota: se usa PUT, nunca PATCH, por limitación de Feign con HttpURLConnection.',
  '#19 — GET suscripciones cliente 1 (vacía)':
    'Verifica que María González no tiene suscripciones activas. La respuesta debe ser una lista vacía [].',
  '#20 — GET suscripciones cliente 2 (Carlos PREMIUM)':
    'Verifica que Carlos Pérez tiene suscripción PREMIUM activa con descuento del 10%, configurada en el seed data.',
  '#21 — DELETE cliente Ana':
    'Elimina el cliente creado en el request #17. Limpia los datos de prueba. Espera 204 No Content.',

  // Fase 4 — Vehículos
  '#22 — GET todos los vehículos':
    'Lista todos los vehículos registrados en ms-vehiculos con sus datos y estado activo/inactivo.',
  '#23 — GET vehículos de cliente 1 (María)':
    'Lista los vehículos de María González. El seed incluye AAAA11 y BBBB11, ambos activos.',
  '#24 — GET validar patente BBBB11 (existe)':
    'Verifica si la patente BBBB11 existe en el sistema. Retorna los datos del vehículo cuando se encuentra.',
  '#25 — GET validar patente ZZZZ99 (no existe)':
    'Verifica una patente inexistente. Retorna 200 con body indicando que no se encontró el vehículo.',
  '#26 — POST crear vehículo EEEE55':
    'Crea un nuevo vehículo con patente aleatoria (prefijo ZZ + 4 dígitos) para evitar conflictos de UNIQUE. Guarda el ID en `vehiculoId`.',
  '#27 — PUT actualizar color vehículo':
    'Actualiza el color del vehículo creado en #26 de Azul a Rojo. La patente es inmutable: VehiculoUpdateDTO no incluye el campo patente.',
  '#28 — DELETE vehículo EEEE55 (soft delete)':
    'Realiza soft delete del vehículo (establece activo=false). El registro permanece en la base de datos.',

  // Fase 5 — Espacios
  '#29 — GET todos los espacios':
    'Lista todos los espacios del estacionamiento con número, zona, piso, tipo y estado de disponibilidad.',
  '#30 — GET espacios disponibles':
    'Lista solo los espacios con disponible=true. Endpoint consultado por clientes para elegir dónde reservar.',
  '#31 — GET espacio 1 (A-01)':
    'Obtiene el detalle del espacio A-01 (zona A, piso 1, tipo ESTÁNDAR). Espacio seed del sistema.',
  '#32 — PUT espacio 1 disponibilidad=false':
    'Marca el espacio 1 como ocupado (disponible=false). Simula la llamada Feign que ejecuta ms-accesos al registrar una entrada.',
  '#33 — PUT espacio 1 disponibilidad=true':
    'Libera el espacio 1 (disponible=true). Simula la llamada Feign que ejecuta ms-accesos al registrar una salida. Restaura el espacio para pruebas posteriores.',
  '#34 — POST crear espacio A-07':
    'Crea un nuevo espacio con número aleatorio en zona Z. Guarda el ID en `espacioId` para el DELETE de cleanup.',
  '#35 — DELETE espacio A-07':
    'Elimina el espacio creado en #34. Limpia los datos de prueba.',

  // Fase 6 — Tarifas CRUD
  '#36 — POST crear TARIFA_TEST':
    'Crea una tarifa de prueba con nombre único (timestamp) y activo=false para no interferir con el sistema. Guarda el ID en `tarifaId`.',
  '#37 — PUT actualizar TARIFA_TEST':
    'Actualiza el precioBaseHora de la tarifa de prueba de 2000 a 2500.',
  '#38 — DELETE (desactivar) TARIFA_TEST':
    'Elimina la tarifa de prueba creada en #36. Espera 204 No Content.',
  '#39 — POST crear horario-tarifa':
    'Crea un horario-tarifa para días LABORALES (06:00–08:00) con multiplicador 0.80 (descuento madrugada). Guarda el ID en `horarioId`.',
  '#40 — DELETE horario-tarifa':
    'Elimina el horario-tarifa creado en #39. Limpia los datos de prueba.',

  // Fase 7 — Reservas
  '#41 — GET todas las reservas':
    'Lista todas las reservas del sistema con sus estados (PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA).',
  '#42 — GET reserva 1 (María, FINALIZADA)':
    'Obtiene la reserva seed de María González. Verifica que el estado es FINALIZADA (ciclo completo completado).',
  '#43 — GET reservas de cliente 2 (Carlos, CONFIRMADA)':
    'Lista las reservas de Carlos Pérez. Verifica que tiene una reserva en estado CONFIRMADA en el seed.',
  '#44 — POST crear reserva espacio 3':
    'Crea una reserva nueva para el espacio 3 (2026-05-20 10:00–12:00). Valida cliente activo, vehículo activo y espacio disponible vía Feign. Estado inicial: PENDIENTE. Guarda el ID en `reservaId1`.',
  '#45 — PUT confirmar reserva 1':
    'Transición de estado: PENDIENTE → CONFIRMADA. Válido solo desde estado PENDIENTE.',
  '#46 — PUT cancelar reserva 1':
    'Transición de estado: CONFIRMADA → CANCELADA. cancelar() solo cambia el estado — no llama a ms-espacios porque el espacio no fue bloqueado en la creación.',
  '#47 — POST crear reserva espacio 4 (para Fase 8)':
    'Crea la segunda reserva (espacio 4) que se usará en la Fase 8 para registrar un acceso real. Guarda el ID en `reservaId2`.',
  '#47b — PUT confirmar reserva 2 (prep acceso)':
    'Confirma la reserva 2 (PENDIENTE → CONFIRMADA). ms-accesos requiere que la reserva esté CONFIRMADA antes de registrar la entrada.',

  // Fase 8 — Accesos
  '#48 — GET acceso 1 (BBBB11, COMPLETADO, 150 min)':
    'Obtiene el acceso seed del vehículo BBBB11. Estado COMPLETADO con 150 minutos de duración. Usado como referencia para verificar el cálculo de cobros.',
  '#49 — GET acceso 2 (DDDD33, ACTIVO)':
    'Obtiene el acceso activo de Carlos Pérez (vehículo DDDD33 actualmente estacionado). Estado ACTIVO sin fecha de salida.',
  '#50 — GET acceso por reserva 1':
    'Busca el acceso vinculado a la reserva 1 de María. Permite obtener el acceso a partir del ID de reserva.',
  '#51 — POST registrar entrada (reserva 2)':
    'Registra la entrada del vehículo vinculado a la reserva 2. Internamente llama a ms-espacios para marcar el espacio 4 como no disponible. Estado inicial: ACTIVO. Guarda el ID en `accesoId`.',
  '#52 — PUT registrar salida':
    'Registra la salida del vehículo: establece fechaHoraSalida, calcula minutos totales, cambia estado a COMPLETADO y libera el espacio vía Feign a ms-espacios.',

  // Fase 9 — Cobros
  '#53 — GET cobro 1 (montoFinal: 3562.50)':
    'Obtiene el cobro seed con el monto final 3562.50 calculado por la fórmula completa de billing (precioBaseHora × multiplicadorHorario × factorVehiculo × factorEspacio × minutos/60, con descuentos aplicados).',
  '#54 — GET cobro por acceso 1':
    'Busca el cobro vinculado al acceso 1. La relación cobro–acceso es 1:1 (UNIQUE constraint en id_acceso_ref).',
  '#55 — GET cobros de cliente 1 (María)':
    'Lista el historial de cobros de María González (cliente id=1).',
  '#56 — POST crear cobro (acceso nuevo, fórmula completa)':
    'Genera el cobro para el acceso creado en la Fase 8 usando la fórmula completa: precioBaseHora × multiplicadorHorario × factorTipoVehiculo × factorTipoEspacio × (minutos/60), menos descuentos de tipo cliente, suscripción y banco. Usa BigDecimal con HALF_UP.',
  '#57 — POST cobro duplicado (→ 422)':
    'Intenta crear un segundo cobro para el mismo acceso. Debe retornar 422 Business Rule Violation por la constraint UNIQUE en id_acceso_ref. Valida la regla de negocio de cobro único por acceso.',

  // Fase 10 — Métodos de pago
  '#58 — GET todos los métodos de pago':
    'Lista todos los métodos de pago registrados en el sistema.',
  '#59 — GET método de pago 1 (Visa Banco Estado)':
    'Obtiene el método de pago seed (Visa Banco Estado, últimos 4 dígitos: 9999). Verificación de datos del seed.',
  '#60 — POST crear método de pago':
    'Registra un nuevo método de pago Visa Crédito para el cliente 1 con vencimiento 06/2028. Guarda el ID en `metodoPagoId`.',
  '#61 — DELETE método de pago':
    'Elimina el método de pago creado en #60. Limpia los datos de prueba.',

  // Fase 11 — Seguridad
  '#62 — GET todos los permisos':
    'Lista todos los permisos del sistema de control de acceso (security-service).',
  '#63 — POST crear permiso TEST_EXPORT':
    'Crea el permiso TEST_EXPORT (recurso: exportar, acción: POST). El pre-request verifica si ya existe y lo elimina para garantizar idempotencia al re-ejecutar la colección. Guarda el ID en `permisoId`.',
  '#64 — GET permiso por id':
    'Obtiene el permiso creado en #63 usando el ID guardado en `permisoId`.',
  '#65 — PUT actualizar descripción permiso':
    'Actualiza la acción del permiso de POST a GET usando el ID en `permisoId`.',
  '#66 — POST asignar permiso a rol 1':
    'Asigna el permiso TEST_EXPORT al rol ADMIN (idRol=1). Crea el vínculo en la tabla rol_permiso. Guarda el ID en `rolPermisoId`.',
  '#67 — GET roles-permisos de rol 1':
    'Lista todos los permisos asignados al rol ADMIN (rol id=1).',
  '#68 — DELETE permiso TEST_EXPORT (cleanup)':
    'Elimina el permiso TEST_EXPORT. El cascade delete en rol_permiso elimina también el rol-permiso creado en #66.',

  // Fase 12 — Reportes
  '#69 — GET reporte de ocupación':
    'Reporte agregado de ocupación del estacionamiento: total de espacios, cuántos están disponibles y cuántos accesos están activos. Datos obtenidos vía Feign desde ms-espacios y ms-accesos.',
  '#70 — GET acceso por reserva 1 (reporte)':
    'Reporte detallado del acceso vinculado a la reserva 1: datos del acceso enriquecidos con información del vehículo y espacio obtenidos vía Feign.',
  '#71 — GET cobros de cliente 1 (reporte)':
    'Reporte de historial de cobros del cliente 1 (María González): lista de cobros con detalles de vehículo y espacio de cada estadía.',
  '#72 — GET acceso reserva 999 (→ 404)':
    'Intenta obtener el reporte de una reserva inexistente. Valida que ms-reportes propaga el 404 recibido desde ms-accesos vía Feign.',
  '#73 — GET cobros cliente 999 (200 lista vacía o 404)':
    'Intenta obtener cobros de un cliente inexistente. Acepta 200 con lista vacía o 404, dependiendo del comportamiento de ms-pagos ante IDs sin registros.'
};

let found = 0;
let missing = [];

for (const fase of c.item) {
  if (!fase.item) continue;
  for (const req of fase.item) {
    if (descriptions[req.name]) {
      req.request.description = descriptions[req.name];
      found++;
    } else {
      missing.push(req.name);
    }
  }
}

console.log(`Descriptions added: ${found}`);
if (missing.length > 0) {
  console.log('Missing descriptions for:', missing);
}

fs.writeFileSync('estacionamiento.postman_collection.json', JSON.stringify(c, null, 2));
console.log('Done.');

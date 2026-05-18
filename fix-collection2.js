const fs = require('fs');
const c = JSON.parse(fs.readFileSync('estacionamiento.postman_collection.json', 'utf8'));

function findItem(folders, namePart) {
  for (const folder of folders) {
    for (const item of (folder.item || [])) {
      if (item.name && item.name.includes(namePart)) return item;
    }
  }
  return null;
}

// === #60 POST método de pago: full body with all required fields ===
const mp60 = findItem(c.item, 'POST crear método de pago');
if (mp60) {
  const body = {
    nombre: 'Visa Crédito de Ana',
    idClienteRef: 1,
    idBanco: 1,
    idTipoTarjeta: 1,
    ultimos4: '1234',
    mesVencimiento: 6,
    anioVencimiento: 2028
  };
  mp60.request.body.raw = JSON.stringify(body, null, 2);
  console.log('Fixed #60 método de pago body');
}

// === #63 POST permiso: recurso + accion instead of descripcion ===
const perm63 = findItem(c.item, 'POST crear permiso TEST_EXPORT');
if (perm63) {
  perm63.request.body.raw = JSON.stringify({nombre: 'TEST_EXPORT', recurso: 'exportar', accion: 'POST'}, null, 2);
  console.log('Fixed #63 permiso body');
}

// === #65 PUT actualizar permiso: recurso + accion ===
const perm65 = findItem(c.item, 'PUT actualizar descripción permiso');
if (perm65) {
  perm65.request.body.raw = JSON.stringify({nombre: 'TEST_EXPORT', recurso: 'exportar', accion: 'GET'}, null, 2);
  console.log('Fixed #65 permiso update body');
}

fs.writeFileSync('estacionamiento.postman_collection.json', JSON.stringify(c, null, 2));
console.log('Done.');

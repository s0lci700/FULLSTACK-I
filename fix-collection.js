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

function setPreRequest(item, code) {
  item.event = (item.event || []).filter(e => e.listen !== 'prerequest');
  item.event.push({ listen: 'prerequest', script: { type: 'text/javascript', exec: [code] } });
}

// === #4 Register: unique email via pre-request ===
const reg4 = findItem(c.item, 'Register nuevo');
if (reg4) {
  setPreRequest(reg4, "pm.collectionVariables.set('testEmail', 'nuevo_' + Date.now() + '@test.cl');");
  reg4.request.body.raw = JSON.stringify({email: '{{testEmail}}', password: 'Test1234!', nombreRol: 'USER'}, null, 2);
  console.log('Fixed #4');
}

// === #17 POST cliente Ana: unique email + rut via pre-request ===
const ana17 = findItem(c.item, 'POST crear cliente Ana');
if (ana17) {
  setPreRequest(ana17, [
    "const ts = Date.now();",
    "pm.collectionVariables.set('anaEmail', 'ana_' + ts + '@test.cl');",
    "pm.collectionVariables.set('anaRut', '9' + (Math.floor(Math.random()*8000000)+1000000) + '-' + Math.floor(Math.random()*9+1));"
  ].join('\n'));
  const body = {
    rut: '{{anaRut}}',
    nombre: 'Ana',
    apellido: 'Soto',
    email: '{{anaEmail}}',
    telefono: '+56911111111',
    idTipoCliente: 1
  };
  ana17.request.body.raw = JSON.stringify(body, null, 2);
  console.log('Fixed #17');
}

// === #26 POST vehículo: unique patente via pre-request ===
const veh26 = findItem(c.item, 'POST crear vehículo');
if (veh26) {
  setPreRequest(veh26, "pm.collectionVariables.set('patentePrueba', 'ZZ' + Math.floor(Math.random()*9000+1000));");
  const body = {patente: '{{patentePrueba}}', marca: 'Kia', modelo: 'Picanto', color: 'Azul', anio: 2023, idTipoVehiculo: 1, idClienteRef: 1};
  veh26.request.body.raw = JSON.stringify(body, null, 2);
  console.log('Fixed #26');
}

// === #34 POST espacio: unique numero via pre-request ===
const esp34 = findItem(c.item, 'POST crear espacio');
if (esp34) {
  setPreRequest(esp34, "pm.collectionVariables.set('espacioNumero', 'Z-' + Math.floor(Math.random()*90+10));");
  const body = {numero: '{{espacioNumero}}', zona: 'Z', piso: 1, idTipoEspacio: 1, disponible: true, activo: true};
  esp34.request.body.raw = JSON.stringify(body, null, 2);
  console.log('Fixed #34');
}

fs.writeFileSync('estacionamiento.postman_collection.json', JSON.stringify(c, null, 2));
console.log('Done.');

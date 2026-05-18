const fs = require('fs');
const c = JSON.parse(fs.readFileSync('estacionamiento.postman_collection.json', 'utf8'));

const fase11 = c.item.find(f => f.name && f.name.includes('11'));
const item63 = fase11 && fase11.item.find(i => i.name && i.name.includes('POST crear permiso TEST_EXPORT'));

if (item63) {
  // Add pre-request script that deletes existing TEST_EXPORT before creating it again
  const prereq = {
    listen: 'prerequest',
    script: {
      exec: [
        "pm.sendRequest({",
        "    url: pm.variables.get('base') + '/api/permisos',",
        "    method: 'GET',",
        "    header: [{ key: 'Authorization', value: 'Bearer ' + pm.collectionVariables.get('token') }]",
        "}, function(err, res) {",
        "    if (!err && res.code === 200) {",
        "        var existing = res.json().find(function(p) { return p.nombre === 'TEST_EXPORT'; });",
        "        if (existing) {",
        "            pm.sendRequest({",
        "                url: pm.variables.get('base') + '/api/permisos/' + existing.id,",
        "                method: 'DELETE',",
        "                header: [{ key: 'Authorization', value: 'Bearer ' + pm.collectionVariables.get('token') }]",
        "            }, function() {});",
        "        }",
        "    }",
        "});"
      ],
      type: 'text/javascript'
    }
  };

  if (!item63.event) item63.event = [];
  // Remove any existing prerequest event before adding new one
  item63.event = item63.event.filter(e => e.listen !== 'prerequest');
  item63.event.push(prereq);
  console.log('Added cleanup pre-request to #63');
} else {
  console.log('ERROR: could not find #63');
}

fs.writeFileSync('estacionamiento.postman_collection.json', JSON.stringify(c, null, 2));
console.log('Done.');

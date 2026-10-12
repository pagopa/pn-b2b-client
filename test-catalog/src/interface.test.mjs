import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync, existsSync} from 'node:fs';
import {fileURLToPath} from 'node:url';
import {JSDOM, VirtualConsole} from 'jsdom';
const htmlPath=new URL('../generated/index.html',import.meta.url);
const data=JSON.parse(readFileSync(new URL('../generated/catalog.json',import.meta.url),'utf8'));
const enabled=JSON.parse(readFileSync(new URL('../config/services.json',import.meta.url),'utf8'));
const consoleErrors=[];
const virtualConsole=new VirtualConsole();virtualConsole.on('jsdomError',e=>consoleErrors.push(e.message));
const dom=new JSDOM(readFileSync(htmlPath,'utf8'),{url:htmlPath.href,runScripts:'dangerously',virtualConsole,beforeParse(w){w.scrollTo=()=>{};}});
const w=dom.window,d=w.document;
function navigate(params){w.location.hash=new URLSearchParams(params).toString();w.dispatchEvent(new w.HashChangeEvent('hashchange'));}
function follow(selector){const a=d.querySelector(selector);assert.ok(a,selector);navigate(Object.fromEntries(new URLSearchParams(a.hash.slice(1))));}
test('output completo: ogni file è catalogato o esplicitamente segnalato',()=>{
 assert.equal(data.issues.length,0);
 assert.deepEqual(data.services.map(s=>s.id).sort(),Object.keys(enabled).filter(id=>enabled[id]).sort());
 for(const s of data.services) assert.equal(s.files,s.features.length+data.emptyFiles.filter(f=>f.service===s.name).length+data.issues.filter(f=>f.service===s.name).length);
 assert.equal(w.TEST_CATALOG.revision,data.revision);
 assert.equal(d.getElementById('catalog-data'),null);
});
test('navigazione da servizio a descrizione BDD per tutti i servizi',()=>{
 for(const service of data.services){navigate({s:service.id});assert.equal(d.querySelector('h1').textContent,service.name);
  follow('.grid .card');assert.match(d.querySelector('h2').textContent,/funzionalità con test/);
  follow('.grid .card');assert.ok(d.querySelector('.test-row'));
  follow('.test-row');assert.match(d.body.textContent,/Comportamento verificato/);assert.ok(d.querySelector('.bdd li'));
  const source=d.querySelector('a[href*="/blob/"]');assert.ok(source.href.includes('/blob/'+data.revision+'/'));assert.match(source.hash,/^#L[0-9]+$/);
 }
});
test('filtro locale configurabile su aree, funzionalità e scenari',()=>{
 navigate({});assert.equal(d.querySelector('input'),null);assert.equal(d.querySelector('select'),null);
 const service=data.services.find(s=>s.features.some(f=>f.scenarios.length));if(!service)return;
 navigate({s:service.id});
 for(let level=0;level<3;level++){
  const item=d.querySelector('#local-items a');assert.ok(item);
  const name=item.querySelector('h3').textContent;
  const input=d.querySelector('#name-filter');
  if (!data.ui?.nameFilterEnabled) {
   assert.equal(input,null);
   const params=Object.fromEntries(new URLSearchParams(w.location.hash.slice(1)));
   navigate({...params,name:'inesistente-9a773ccc'});
   assert.ok(d.querySelector('#local-items a'));
   if(level<2)follow('#local-items a');
   continue;
  }
  input.value='inesistente-9a773ccc';input.dispatchEvent(new w.Event('input'));
  assert.match(d.querySelector('#local-items').textContent,/Nessun nome corrisponde/);
  input.value=name.toUpperCase();input.dispatchEvent(new w.Event('input'));
  assert.ok(d.querySelector('#local-items a'));
  if(level<2)follow('#local-items a');
 }
});
test('dettaglio parametrizzato conserva esempi e precondizioni',()=>{
 const s=data.services.find(s=>s.id==='interop');if(!s) return;const f=s.features.find(f=>f.scenarios.some(t=>t.examples.length>1&&t.backgrounds.length));const t=f.scenarios.find(t=>t.examples.length>1&&t.backgrounds.length);
 navigate({s:s.id,a:t.area,f:f.id,t:t.id});assert.match(d.body.textContent,/Prima di iniziare/);assert.match(d.body.textContent,/Varianti del test/);
 assert.ok(d.body.textContent.includes(t.examples[0].rows[0][0]));assert.ok(d.body.textContent.includes(t.steps[0].text));
});
test('paginazione e collegamenti non validi',()=>{
 if(!data.services.some(s=>s.id==='send')) return;
 const service=data.services.find(s=>s.id==='send');
 const feature=service.features.find(f=>f.scenarios.length>24);
 navigate({s:service.id,f:feature.id});const first=d.querySelector('.test-row').href;
 follow('.pagination a');assert.notEqual(d.querySelector('.test-row').href,first);
 if(data.ui?.nameFilterEnabled){
  const target=feature.scenarios[0];const input=d.querySelector('#name-filter');input.value=target.name;input.dispatchEvent(new w.Event('input'));
  assert.ok([...d.querySelectorAll('.test-row')].some(a=>a.hash.includes(target.id)));
 }
 navigate({s:'send',f:'missing'});assert.match(d.body.textContent,/Pagina non disponibile/);
 assert.deepEqual(consoleErrors,[]);
});
test.after(()=>dom.window.close());

test('metadati nelle card e cronologia con runner nel dettaglio',()=>{
 navigate({});assert.ok(d.querySelector('.service-card .change-info'));
 const service=data.services.find(s=>s.features.some(f=>f.history?.length&&f.scenarios.some(t=>t.runners?.length)));assert.ok(service);
 const feature=service.features.find(f=>f.history?.length&&f.scenarios.some(t=>t.runners?.length));const scenario=feature.scenarios.find(t=>t.runners?.length);
 navigate({s:service.id});assert.ok(d.querySelector('.card .change-info'));
 navigate({s:service.id,a:scenario.area});assert.ok(d.querySelector('.card .change-info'));
 navigate({s:service.id,a:scenario.area,f:feature.id});assert.ok(d.querySelector('.test-row .change-info'));
 navigate({s:service.id,f:feature.id,t:scenario.legacyId});assert.ok(d.querySelector('#copy-test-link'));
 assert.equal(d.querySelectorAll('.timeline li').length,feature.history.length);
 assert.ok(d.body.textContent.includes(feature.history[0].author));assert.ok(d.querySelector('.runner-list a'));
});
test('copia link canonico e alternativa quando clipboard non disponibile',async()=>{
 const service=data.services[0], feature=service.features.find(f=>f.scenarios.length), scenario=feature.scenarios[0];
 navigate({s:service.id,f:feature.id,t:scenario.legacyId});
 let copied;
 Object.defineProperty(w.navigator,'clipboard',{configurable:true,value:{writeText:async value=>{copied=value;}}});
 await w.copyTestLink();assert.ok(copied.includes(scenario.id));assert.ok(d.querySelector('#copy-link-status').textContent.includes('copiato'));
 Object.defineProperty(w.navigator,'clipboard',{configurable:true,value:{writeText:async()=>{throw Error('Denied');}}});
 await w.copyTestLink();const field=d.querySelector('#copy-link-fallback');assert.equal(field.hidden,false);assert.equal(field.value,copied);assert.equal(d.querySelector('#copy-test-link').disabled,false);
});

test('card compatte: solo autore e data, coautori separati nei dettagli',()=>{
 const metadata={author:'Autore principale',date:'2026-10-11T12:00:00Z',coauthors:['Coautore esclusivo'],commit:'abc',subject:'Modifica',url:'https://example.com'};
 const card=w.changeInfo(metadata);assert.ok(card.includes('Autore principale'));assert.ok(!card.includes('Coautore esclusivo'));
 const details=w.historyDetails({history:[metadata],contributors:['Autore principale']});
 assert.ok(details.includes('Coautori dichiarati nel commit'));assert.ok(details.includes('Coautore esclusivo'));
 for(const service of data.services)for(const feature of service.features){
  assert.deepEqual(feature.contributors,[...new Set(feature.history.map(c=>c.author))]);
  for(const commit of feature.history)assert.equal(commit.coauthors.length,new Set(commit.coauthors).size);
 }
});

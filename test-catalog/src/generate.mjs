import { readFile, writeFile, mkdir } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { execFileSync } from 'node:child_process';
import { parseFeature, classify, safeJson } from './catalog.mjs';
import { fileHistory, latestChange } from './provenance.mjs';
import { discoverRunners, associateRunners } from './runners.mjs';
const home = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const root = resolve(home, '..');
const taxonomy = JSON.parse(await readFile(resolve(home, 'config/taxonomy.json'), 'utf8'));
const enabled = JSON.parse(await readFile(resolve(home, 'config/services.json'), 'utf8'));
for (const id of Object.keys(enabled)) {
  if (!taxonomy.services.some(s => s.id === id)) throw new Error(`Servizio sconosciuto in services.json: ${id}`);
}
for (const service of taxonomy.services) {
  if (typeof enabled[service.id] !== 'boolean') throw new Error(`Impostare ${service.id} a true o false in services.json`);
}
const activeServices = taxonomy.services.filter(s => enabled[s.id]);
const pagination = JSON.parse(await readFile(resolve(home, 'config/pagination.json'), 'utf8'));
const functionalAreasPerPage = pagination.functionalAreasPerPage ?? 100;
if (!Number.isSafeInteger(functionalAreasPerPage) || functionalAreasPerPage < 1) {
  throw new Error('functionalAreasPerPage deve essere un numero intero positivo in pagination.json');
}
const uiConfig = JSON.parse(await readFile(resolve(home, 'config/ui.json'), 'utf8'));
const nameFilterEnabled = uiConfig.nameFilterEnabled === undefined ? false : uiConfig.nameFilterEnabled;
if (typeof nameFilterEnabled !== 'boolean') {
  throw new Error('nameFilterEnabled deve essere true o false in ui.json');
}
const source = JSON.parse(await readFile(resolve(home, 'config/source.json'), 'utf8'));
const branch = source.branch;
if (typeof branch !== 'string' || !branch || branch.startsWith('-')) throw new Error('Branch sorgente non valido in source.json');
const git = args => execFileSync('git',args,{cwd:root,encoding:'utf8',maxBuffer:32*1024*1024});
// Resolve once so every file belongs to the same immutable snapshot.
let revision;
for (const ref of [`refs/heads/${branch}`, `refs/remotes/origin/${branch}`]) {
  try { revision = git(['rev-parse','--quiet','--verify',`${ref}^{commit}`]).trim(); break; }
  catch { /* Try the remote-tracking branch when no local branch exists. */ }
}
if (!revision) throw new Error(`Branch sorgente non trovato: ${branch}`);
const treePaths = git(['ls-tree','-r','--name-only','-z',revision]).split('\0');
const sourceUrl = path => `${source.repositoryUrl}/blob/${revision}/${path.split('/').map(encodeURIComponent).join('/')}`;
const paths = treePaths.filter(path => path.endsWith('.feature') && !path.split('/').some(p => ['target','node_modules','.git'].includes(p))).sort();
const historyComplete = git(['rev-parse','--is-shallow-repository']).trim() === 'false';
const catalog = { schemaVersion:2, historyComplete, branch, ui:{nameFilterEnabled}, pagination:{functionalAreasPerPage}, generatedAt:new Date().toISOString(), revision, sourceDirty:false, services:[], issues:[], emptyFiles:[] };
for (const cfg of activeServices) {
 console.log(`Analisi ${cfg.name}: cronologia e runner…`);
 const javaPaths=treePaths.filter(p=>p.startsWith(cfg.root+'/')&&p.includes('/test/java/')&&p.endsWith('.java')&&!p.includes('/steps/'));
 const runners=discoverRunners(javaPaths.map(path=>({path,source:git(['show',`${revision}:${path}`])})),sourceUrl);
 const service = {id:cfg.id, name:cfg.name, description:cfg.description, features:[], files:0, runners};
 for (const path of paths.filter(path => path.startsWith(cfg.root+'/'))) {
  service.files++;
  let feature;
  try { feature = parseFeature(git(['show',`${revision}:${path}`]),path); }
  catch(error) { catalog.issues.push({service:cfg.name,path,message:error.message}); continue; }
  if (!feature) {catalog.emptyFiles.push({service:cfg.name,path});continue;}
  feature.sourceUrl = sourceUrl(path);
  feature.history = fileHistory(git,revision,path,source.repositoryUrl);
  feature.contributors = [...new Set(feature.history.map(c=>c.author))];
  associateRunners(feature,runners);
  feature.displayName = cfg.featureNames?.[path.split('/').pop()] || feature.name;
  for (const scenario of feature.scenarios) scenario.area = classify(path,scenario,cfg);
  service.features.push(feature);
 }
 service.lastChange = latestChange(service.features);
 catalog.services.push(service);
}
const template = await readFile(resolve(home,'src/template.html'),'utf8');
const style = await readFile(resolve(home,'src/style.css'),'utf8');
const app = await readFile(resolve(home,'src/app.js'),'utf8');
await mkdir(resolve(home,'generated'),{recursive:true});
await writeFile(resolve(home,'generated/catalog.json'),JSON.stringify(catalog,null,2)+'\n');
await writeFile(resolve(home,'generated/index.html'),template.replace('/*STYLE*/',()=>style).replace('/*DATA*/',()=>safeJson(catalog)).replace('/*APP*/',()=>app));
for (const s of catalog.services) console.log(`${s.name}: ${s.files} file, ${s.features.length} feature, ${s.features.reduce((n,f)=>n+f.scenarios.length,0)} scenari`);
console.log(`Segnalazioni: ${catalog.issues.length}`);
for (const i of catalog.issues) console.log(`${i.path}: ${i.message}`);

console.log(`File senza Feature: ${catalog.emptyFiles.length}`);
if (catalog.issues.length) process.exitCode = 1;

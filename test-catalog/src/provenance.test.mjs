import test from 'node:test';
import assert from 'node:assert/strict';
import {mkdtempSync, writeFileSync, renameSync, rmSync} from 'node:fs';
import {tmpdir} from 'node:os';
import {join} from 'node:path';
import {execFileSync} from 'node:child_process';
import {fileHistory,latestChange} from './provenance.mjs';
import {parseFeature} from './catalog.mjs';
test('cronologia segue le rinomine, include coautori e rispetta la revisione',()=>{
 const cwd=mkdtempSync(join(tmpdir(),'catalog-history-'));
 const git=args=>execFileSync('git',['-c','user.name=Autore Test','-c','user.email=test@example.invalid','-c','commit.gpgsign=false','-c','core.hooksPath=/dev/null',...args],{cwd,encoding:'utf8'});
 try{
  git(['init','-q']);writeFileSync(join(cwd,'old.feature'),'Feature: Originale\n');git(['add','.']);git(['commit','-qm','Creazione\n\nCo-authored-by: Secondo Autore <second@example.invalid>']);
  renameSync(join(cwd,'old.feature'),join(cwd,'new.feature'));git(['add','.']);git(['commit','-qm','Rinomina']);const revision=git(['rev-parse','HEAD']).trim();
  writeFileSync(join(cwd,'new.feature'),'Feature: Futuro\n');git(['add','.']);git(['commit','-qm','Modifica successiva']);
  const history=fileHistory(git,revision,'new.feature','https://example.com/repo');assert.equal(history.length,2);assert.equal(history[0].subject,'Rinomina');assert.deepEqual(history[1].coauthors,['Secondo Autore']);assert.ok(history.every(c=>!JSON.stringify(c).includes('@example')));
  assert.equal(latestChange([{history}]).commit,revision);
 }finally{rmSync(cwd,{recursive:true,force:true});}
});
test('link scenario stabile con aggiunta righe; compatibilità ID precedente',()=>{
 const a=parseFeature('Feature: A\nScenario: [TC_1] Nome\nGiven a\n','a.feature');
 const b=parseFeature('# commento\n\nFeature: A\nScenario: [TC_1] Nome aggiornato\nGiven a\n','a.feature');
 assert.equal(a.scenarios[0].id,b.scenarios[0].id);assert.notEqual(a.scenarios[0].legacyId,b.scenarios[0].legacyId);
});

test('coautori deduplicati e distinti dall’autore principale',()=>{
 const git=()=> 'abc\x1fAutore\x1f2026-10-11T12:00:00Z\x1fModifica\x1fCo-authored-by: Autore <a@example.invalid>\nCo-authored-by: Collaboratore <b@example.invalid>\nCo-authored-by: Collaboratore <b@example.invalid>\x1e';
 assert.deepEqual(fileHistory(git,'revision','file.feature','https://example.com')[0].coauthors,['Collaboratore']);
});

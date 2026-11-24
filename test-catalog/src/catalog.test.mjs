import test from 'node:test';
import assert from 'node:assert/strict';
import {parseFeature, classify, safeJson} from './catalog.mjs';
test('preserva Background, Rule, tag ereditati, esempi, tabelle e docstring',()=>{
 const f=parseFeature(`@dominio
Feature: Prova
  Background: Comune
    Given un ente
  @regola
  Rule: Accesso
    Background: Ruolo
      Given un operatore
    @scenario
    Scenario Outline: Accesso <ruolo>
      descrizione su una seconda riga
      When accede <ruolo>
        | chiave | valore |
        | a | b |
      Then legge
        """
        testo <sicuro>
        """
      @positivo
      Examples: Consentiti
        | ruolo |
        | admin |
      @negativo
      Examples: Negati
        | ruolo |
        | ospite |
`, 'fixture.feature');
 const s=f.scenarios[0];assert.equal(f.scenarios.length,1);assert.equal(s.backgrounds.length,2);
 assert.deepEqual(s.tags,['@dominio','@regola','@scenario']);assert.deepEqual(s.examples[0].tags,['@dominio','@regola','@scenario','@positivo']);
 assert.equal(s.examples[1].rows[0][0],'ospite');assert.equal(s.steps[0].table[1][1],'b');assert.equal(s.steps[1].docString,'testo <sicuro>');assert.match(s.description,/seconda riga/);
});
test('legge Gherkin italiano e ignora scenari commentati',()=>{
 const f=parseFeature('# language: it\nFunzionalità: Prova\n  # Scenario: non presente\n  Scenario: Presente\n    Dato un ente\n    Quando accede\n    Allora legge\n','it.feature');assert.equal(f.scenarios.length,1);assert.equal(f.scenarios[0].steps.length,3);
});
test('segnala sintassi invalida senza restituire risultati parziali',()=>assert.throws(()=>parseFeature('Feature: A\nScenario: B\nGiven a\nExamples:\n| a | b |\n| uno |\n','bad.feature')));
test('classificazione esplicita con fallback e identificativi per sorgente',()=>{
 const cfg={rules:[{tags:['@dominio'],area:'Dominio'},{path:'/area/',area:'Area'}]};assert.equal(classify('/area/x',{tags:['@dominio']},cfg),'Dominio');assert.equal(classify('/area/x',{tags:[]},cfg),'Area');assert.equal(classify('/x',{tags:[]},cfg),'Da classificare');
 assert.notEqual(parseFeature('Feature: A','a').id,parseFeature('Feature: A','b').id);
});
test('JSON incorporato non può chiudere il tag script',()=>{const x={text:'</script><script>alert(1)</script>\u2028'};assert.ok(!safeJson(x).includes('<'));assert.deepEqual(JSON.parse(safeJson(x)),x);});

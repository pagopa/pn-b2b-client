import test from 'node:test';
import assert from 'node:assert/strict';
import {discoverRunners, associateRunners, tagMatches} from './runners.mjs';
const feature = () => ({path:'src/test/resources/domain/a.feature',scenarios:[{tags:['@domain'],examples:[{tags:['@domain','@yes'],rows:[['a'],['b']]},{tags:['@domain','@ignore'],rows:[['c']]}]}]});
test('tag expressions: precedenza, esclusioni, errori',()=>{
 assert.equal(tagMatches('domain & (yes | no) & !ignore',['@domain','@yes']),true);
 assert.equal(tagMatches('domain & !ignore',['@domain','@ignore']),false);
 assert.throws(()=>tagMatches('a &',[]));
});
test('runner ereditati con costanti, commenti, esclusioni ed esempi',()=>{
 const runners=discoverRunners([
  {path:'Base.java',source:'package qa; @Suite @SelectClasspathResource("domain") @ExcludeTags("ignore") public abstract class Base {}'},
  {path:'Child.java',source:'package qa; /* @IncludeTags("wrong") */ @IncludeTags(Child.TAG) public class Child extends Base { public static final String TAG = "domain"; }'},
  {path:'Other.java',source:'package qa; @Suite @SelectClasspathResource("other") public class Other {}'},
  {path:'Suite.java',source:'package qa; @Suite @SelectClasses({Child.class}) public class Group {}'},
 ],p=>'https://example.com/'+p);
 assert.equal(runners.length,3);assert.deepEqual(runners.find(r=>r.name==='Child').warnings,[]);
 const f=feature();associateRunners(f,runners);
 assert.deepEqual(f.scenarios[0].runners.map(r=>r.id),['qa.Child','qa.Group']);
 for(const r of f.scenarios[0].runners){assert.equal(r.includedVariants,2);assert.equal(r.totalVariants,3);assert.deepEqual(r.examples,[{example:1,row:1},{example:1,row:2}]);}
});
test('filtri dinamici e costanti non risolte non diventano inclusioni certe',()=>{
 const runners=discoverRunners([
 {path:'Runtime.java',source:'package qa; @Suite @SelectClasspathResource("domain") @IncludeTags("domain") @ConfigurationParameter(key=SCENARIO_IDS_PROPERTY,value="a") public class Runtime {}'},
 {path:'Unresolved.java',source:'package qa; @Suite @SelectClasspathResource("domain") @IncludeTags(Unknown.VALUE) public class Unresolved {}'},
 ],p=>p);
 const f=feature();associateRunners(f,runners);assert.equal(f.scenarios[0].runners.length,2);
 assert.ok(f.scenarios[0].runners.every(r=>r.status==='uncertain'));
});
test('tag del figlio sostituiscono i tag ereditati; risorse delimitate',()=>{
 const runners=discoverRunners([
 {path:'B.java',source:'package qa; @Suite @SelectClasspathResource("domain") @IncludeTags("wrong") public abstract class B {}'},
 {path:'C.java',source:'package qa; @IncludeTags("domain") public class C extends B {}'},
 ],p=>p);
 const f=feature();associateRunners(f,runners);assert.equal(f.scenarios[0].runners[0].status,'included');
 f.path='src/test/resources/domain-other/a.feature';associateRunners(f,runners);assert.deepEqual(f.scenarios[0].runners,[]);
});

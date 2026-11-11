// Conservative, static reader for the JUnit suite declarations in this repository.
// Unsupported selection constructs remain unresolved, never assumed to select all tests.
const stripComments = s => s.replace(/"(?:\\.|[^"\\])*"|\/\/[^\n]*|\/\*[\s\S]*?\*\//g, m => m.startsWith('"') ? m : ' ');
function annotations(source, name) {
  return [...source.matchAll(new RegExp('@'+name+'\\s*\\(((?:"(?:\\\\.|[^"\\\\])*"|[^)"])*)\\)', 'g'))].map(m => m[1].trim());
}
function splitValues(value) {
  return value.replace(/^value\s*=\s*/, '').replace(/^\{([\s\S]*)\}$/, '$1').match(/(?:"(?:\\.|[^"\\])*"|[^,])+/g)?.map(s=>s.trim()).filter(Boolean) || [];
}
export function tagMatches(expression, tags) {
  const tokens = expression.match(/\(|\)|!|&|\||[^\s()!&|]+/g) || [];
  let at = 0;
  function atom() {
    const token=tokens[at++];
    if(token==='!')return !atom();
    if(token==='('){const result=or();if(tokens[at++]!==')')throw Error('Parentesi non bilanciate');return result;}
    if(!token||[')', '&', '|'].includes(token))throw Error('Espressione tag non valida');
    if(token==='any')return tags.length>0;
    if(token==='none')return tags.length===0;
    return tags.includes('@'+token);
  }
  function and(){let result=atom();while(tokens[at]==='&'){at++;const right=atom();result=result&&right;}return result;}
  function or(){let result=and();while(tokens[at]==='|'){at++;const right=and();result=result||right;}return result;}
  const result=or();if(at!==tokens.length)throw Error('Espressione tag non valida');return result;
}
export function discoverRunners(files, sourceUrl) {
  const classes=[];
  for(const {path,source} of files){
    const text=stripComments(source);
    const declaration=text.match(/\b(public\s+)?(abstract\s+)?class\s+(\w+)(?:\s+extends\s+([\w.]+))?/);
    if(!declaration)continue;
    const pkg=text.match(/\bpackage\s+([\w.]+)\s*;/)?.[1]||'';
    const name=declaration[3];
    const constants=new Map([...text.matchAll(/\b(?:static\s+final|final\s+static)\s+String\s+(\w+)\s*=\s*([^;]+);/g)].map(m=>[m[1],m[2]]));
    classes.push({path,text,name,pkg,id:pkg+'.'+name,parent:declaration[4],abstract:!!declaration[2],constants,imports:[...text.matchAll(/import\s+(?!static)([\w.]+);/g)].map(m=>m[1])});
  }
  function lookup(name,owner){return classes.find(c=>c.id===name)||classes.find(c=>c.id===owner.pkg+'.'+name)||classes.find(c=>owner.imports.includes(c.id)&&c.name===name);}
  function stringValue(expr,owner,seen=new Set()){
    expr=expr.trim();
    if(/^"(?:\\.|[^"\\])*"$/.test(expr))return JSON.parse(expr);
    if(seen.has(owner.id+':'+expr))throw Error('Costante ricorsiva');seen.add(owner.id+':'+expr);
    const m=expr.match(/^(?:(.+)\.)?(\w+)$/);
    if(!m)throw Error('Valore non risolto: '+expr);
    const target=m[1]?lookup(m[1],owner):owner;
    if(!target?.constants.has(m[2]))throw Error('Costante non risolta: '+expr);
    return stringValue(target.constants.get(m[2]),target,seen);
  }
  function nearest(chain,name){for(const c of chain){const a=annotations(c.text,name);if(a.length)return a.flatMap(v=>splitValues(v).map(e=>stringValue(e,c)));}return [];}
  const runners=[];
  for(const c of classes){
    const chain=[c];let parent=c;const warnings=[];
    while(parent.parent){const next=lookup(parent.parent,parent);if(!next||chain.includes(next)){warnings.push('Classe base non risolta: '+parent.parent);break;}chain.push(next);parent=next;}
    if(!chain.some(c=>/@Suite\b/.test(c.text)))continue;
    if(c.abstract)continue;
    const runner={id:c.id,name:c.name,path:c.path,url:sourceUrl(c.path),resources:[],include:[],exclude:[],children:[],warnings};
    try{
      runner.resources=nearest(chain,'SelectClasspathResource');
      runner.include=nearest(chain,'IncludeTags');runner.exclude=nearest(chain,'ExcludeTags');
      for(const expression of [...runner.include,...runner.exclude])tagMatches(expression,[]);
      const selected=chain.find(c=>annotations(c.text,'SelectClasses').length);
      if(selected)runner.children=annotations(selected.text,'SelectClasses').flatMap(splitValues).map(value=>{
        const child=lookup(value.replace(/\.class$/,''),selected);if(!child)throw Error('Suite figlia non risolta: '+value);return child.id;
      });
      if(!runner.resources.length&&!runner.children.length)warnings.push('Nessuna selezione di risorse risolta');
      const engines=nearest(chain,'IncludeEngines');
      if(engines.length&&!engines.includes('cucumber')&&!runner.children.length)warnings.push('Engine non Cucumber');
      for(const node of chain){
        if(/@(SelectPackages|SelectDirectories|SelectFile|SelectClasspathResources|CucumberOptions|IncludePackages|ExcludePackages|ExcludeEngines|DisableParentConfigurationParameters)\b/.test(node.text))warnings.push('Selettori aggiuntivi da verificare');
        if(/FILTER_(TAGS|NAME)_PROPERTY_NAME|cucumber\.filter\.(tags|name)|SCENARIO_IDS_PROPERTY/.test(node.text))warnings.push('Filtri aggiuntivi a runtime');
        for(const name of ['IncludeTags','ExcludeTags','SelectClasspathResource']){
          if(new RegExp('@'+name+'\\b').test(node.text)&&!annotations(node.text,name).length)warnings.push('Annotazione non risolta: '+name);
        }
      }
    }catch(error){warnings.push(error.message);runner.include=[];runner.exclude=[];}
    runners.push(runner);
  }
  for(const r of runners)for(const id of r.children)if(!runners.some(c=>c.id===id))r.warnings.push('Suite figlia non disponibile: '+id);
  return runners;
}
export function associateRunners(feature, runners) {
  const resource=feature.path.split('/test/resources/')[1];
  const byId=new Map(runners.map(r=>[r.id,r]));
  function selected(r,tags,seen=new Set()){
    if(seen.has(r.id))return 'unknown';
    seen=new Set([...seen,r.id]);
    const scope=r.resources.some(p=>resource===p||resource?.startsWith(p.replace(/\/$/,'')+'/'));
    const children=r.children.map(id=>byId.has(id)?selected(byId.get(id),tags,seen):'unknown');
    if(!scope&&!children.some(v=>v!==false))return r.warnings.length&&!r.resources.length&&!r.children.length?'unknown':false;
    if(r.include.length&&!r.include.some(e=>tagMatches(e,tags)))return false;
    if(r.exclude.some(e=>tagMatches(e,tags)))return false;
    if(r.warnings.length)return 'unknown';
    return scope||children.includes(true)?true:children.includes('unknown')?'unknown':false;
  }
  for(const scenario of feature.scenarios){
    const variants=scenario.examples.length?scenario.examples.flatMap((e,i)=>e.rows.map((_,row)=>({tags:e.tags,example:i+1,row:row+1}))):[{tags:scenario.tags}];
    scenario.runners=[];
    for(const r of runners){
      const outcomes=variants.map(v=>selected(r,v.tags));
      const included=outcomes.flatMap((v,i)=>v===true?[i]:[]);
      const uncertain=outcomes.filter(v=>v==='unknown').length;
      if(included.length||uncertain)scenario.runners.push({id:r.id,status:included.length?'included':'uncertain',includedVariants:included.length,totalVariants:variants.length,uncertainVariants:uncertain,examples:included.filter(i=>variants[i].example).map(i=>({example:variants[i].example,row:variants[i].row}))});
    }
  }
}

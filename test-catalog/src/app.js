'use strict';
const catalog=window.TEST_CATALOG;
const main=document.getElementById('main');
const esc=x=>String(x??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const url=p=>'#'+new URLSearchParams(Object.entries(p).filter(([,v])=>v!==undefined&&v!==null&&v!=='')).toString();
const link=(label,p,cls='')=>`<a class="${cls}" href="${esc(url(p))}">${esc(label)}</a>`;
const all=catalog.services.flatMap(service=>service.features.flatMap(feature=>feature.scenarios.map(test=>({service,feature,test}))));
const date=new Intl.DateTimeFormat('it-IT',{dateStyle:'long',timeStyle:'short',timeZone:'Europe/Rome'}).format(new Date(catalog.generatedAt));
document.getElementById('footer').innerHTML=`Quality Engineering · Catalogo generato il ${esc(date)} (Europe/Rome)<br>Branch: ${esc(catalog.branch || "non disponibile (HEAD scollegato)")} · Revisione ${esc(catalog.revision.slice(0,12))}${catalog.sourceDirty?' · Sorgenti con modifiche locali':''}. I conteggi indicano scenari definiti, non esecuzioni o percentuali di copertura.${catalog.emptyFiles.length?`<details><summary>${catalog.emptyFiles.length} file vuoti o interamente commentati</summary>${catalog.emptyFiles.map(f=>`<div>${esc(f.service)} · ${esc(f.path)}</div>`).join('')}</details>`:''}`;
function table(header,rows){return `<div class="table-wrap" tabindex="0" role="region" aria-label="Tabella dati"><table>${header.length?`<thead><tr>${header.map(c=>`<th scope="col">${esc(c)}</th>`).join('')}</tr></thead>`:''}<tbody>${rows.map(r=>`<tr>${r.map(c=>`<td>${esc(c)}</td>`).join('')}</tr>`).join('')}</tbody></table></div>`;}
function bdd(steps){const labels={Given:'Dato',When:'Quando',Then:'Allora',And:'E',But:'Ma'};return `<ol class="bdd">${steps.map(s=>`<li><span class="keyword">${esc(labels[s.keyword]||s.keyword)}</span> ${esc(s.text)}${s.table?table([],s.table):''}${s.docString!==undefined?`<pre>${esc(s.docString)}</pre>`:''}</li>`).join('')}</ol>`;}
function badges(tags){return tags.map(t=>`<span class="badge">${esc(t)}</span>`).join('');}
function description(text){return text?`<p class="description">${esc(text)}</p>`:'';}
function pager(items,p,render,size=24){const pages=Math.max(1,Math.ceil(items.length/size));const page=Math.min(pages,Math.max(1,Math.floor(Number(p.page))||1));return items.slice((page-1)*size,page*size).map(render).join('')+(pages>1?`<nav class="pagination" aria-label="Pagine">${page>1?link('← Precedenti',{...p,page:page-1},'button'):''}<span>Pagina ${page} di ${pages}</span>${page<pages?link('Successivi →',{...p,page:page+1},'button'):''}</nav>`:'');}
function changeInfo(change,label='Ultima modifica del file') {
 if(!change)return '<p class="change-info">Cronologia non disponibile</p>';
 const date=new Intl.DateTimeFormat('it-IT',{dateStyle:'medium',timeZone:'Europe/Rome'}).format(new Date(change.date));
 return `<p class="change-info">${esc(label)}: <time datetime="${esc(change.date)}">${esc(date)}</time><br><strong>${esc(change.author)}</strong></p>`;
}
function latestFeatureChange(features) {
 return features.map(f=>f.history?.[0]).filter(Boolean).sort((a,b)=>Date.parse(b.date)-Date.parse(a.date))[0];
}
function runnerDetails(service,test) {
 const matches=test.runners||[];
 const included=matches.filter(r=>r.status==='included');
 const uncertain=matches.filter(r=>r.status==='uncertain');
 function entry(match){
  const runner=service.runners.find(r=>r.id===match.id);
  return `<li><a href="${esc(runner.url)}">${esc(runner.name)}</a>${test.examples.length?` · ${match.includedVariants} di ${match.totalVariants} varianti incluse`:''}${match.uncertainVariants?` · ${match.uncertainVariants} da verificare`:''}${match.examples?.length&&match.includedVariants<match.totalVariants?`<p>Righe incluse: ${match.examples.map(e=>`gruppo ${e.example}, riga ${e.row}`).join('; ')}</p>`:''}${runner.warnings.length?`<p>${runner.warnings.map(esc).join(' · ')}</p>`:''}</li>`;
 }
 return `<h3>Runner che includono questo test</h3>${included.length?`<ul class="runner-list">${included.map(entry).join('')}</ul>`:'<p>Nessuna associazione determinabile dalle dichiarazioni dei runner.</p>'}<p class="muted">Associazioni ricavate da percorsi e filtri dichiarati nei runner della revisione analizzata. Parametri Maven, CI e configurazioni a runtime possono cambiare la selezione.</p>${uncertain.length?`<details><summary>${uncertain.length} associazioni da verificare</summary><ul class="runner-list">${uncertain.map(entry).join('')}</ul></details>`:''}`;
}
function historyDetails(feature) {
 return `<h3>Ultima modifica e contributori</h3>${changeInfo(feature.history?.[0])}<h3>Autori dei commit che modificano il file</h3><p>${(feature.contributors||[]).map(esc).join(' · ')||'Non disponibili'}</p><details><summary>Cronologia del file · ${feature.history?.length||0} modifiche</summary>${catalog.historyComplete?'':'<p>Il clone contiene solo una parte della cronologia Git.</p>'}<ol class="timeline">${(feature.history||[]).map(c=>`<li>${changeInfo(c,'Modifica')}<a href="${esc(c.url)}">${esc(c.subject)}</a> <span class="muted">${esc(c.commit.slice(0,8))}</span>${c.coauthors?.length?`<details class="commit-coauthors"><summary>Coautori dichiarati nel commit · ${c.coauthors.length}</summary><p>Riferiti al commit complessivo, non necessariamente a questo file.</p><p>${c.coauthors.map(esc).join(' · ')}</p></details>`:''}</li>`).join('')}</ol></details>`;
}
async function copyTestLink() {
 const button=document.getElementById('copy-test-link');
 const status=document.getElementById('copy-link-status');
 const p=Object.fromEntries(new URLSearchParams(location.hash.slice(1)));
 const service=catalog.services.find(s=>s.id===p.s);
 const feature=service?.features.find(f=>f.id===p.f);
 const test=feature?.scenarios.find(t=>t.id===p.t||t.legacyId===p.t);
 if(!test)return;
 const target=new URL(location.href);
 target.hash=url({s:service.id,a:test.area,f:feature.id,t:test.id});
 const text=target.href;
 button.disabled=true;
 try{
  if(!navigator.clipboard?.writeText)throw Error('Clipboard non disponibile');
  await navigator.clipboard.writeText(text);
  status.textContent=target.protocol==='file:'?'Link locale copiato: chi lo riceve deve avere il catalogo allo stesso percorso.':'Link al test copiato';
 }catch{
  const field=document.getElementById('copy-link-fallback');
  field.hidden=false;field.value=text;field.focus();field.select();
  status.textContent='Copia il collegamento selezionato con il comando di copia del tuo dispositivo.';
 }finally{button.disabled=false;}
}
function testRow(r){const id=r.test.name.match(/^\[([^\]]+)\]/)?.[1];return `<a class="test-row" href="${esc(url({s:r.service.id,a:r.test.area,f:r.feature.id,t:r.test.id}))}"><span class="arrow" aria-hidden="true">›</span><span class="muted">${esc(r.service.name)} · ${esc(r.test.area)}</span><h3>${esc(r.test.name.replace(/^\[[^\]]+\]\s*/,''))}</h3>${id?`<span class="badge">${esc(id)}</span>`:''}${r.test.examples.length?'<span class="badge">Con varianti di esempio</span>':''}${changeInfo(r.feature.history?.[0])}${r.test.tags.includes('@ignore')?'<span class="badge">Tag @ignore nel sorgente</span>':''}<span class="test-action">Leggi la descrizione del test</span></a>`;}
let bindLocalFilter;
function localList(items,p,name,itemHtml,layout='grid',pageSize=24) {
 if (!catalog.ui?.nameFilterEnabled) {
  const {name: ignoredName, ...pageParams} = p;
  return `<div id="local-items" class="${layout}">${pager(items,pageParams,itemHtml,pageSize)}</div>`;
 }
 const matches=value=>items.filter(item=>name(item).toLocaleLowerCase('it').includes(value.trim().toLocaleLowerCase('it')));
 const body=value=>{const filtered=matches(value);return filtered.length?pager(filtered,{...p,name:value,page:value===(p.name||'')?p.page:1},itemHtml,pageSize):'<p class="empty">Nessun nome corrisponde. Prova con meno parole.</p>';};
 bindLocalFilter=()=>{
  const input=document.getElementById('name-filter');
  input.addEventListener('input',()=>{
   document.getElementById('local-items').innerHTML=body(input.value);
   document.getElementById('filter-count').textContent=`${matches(input.value).length} di ${items.length}`;
  });
 };
 return `<div class="local-filter"><label for="name-filter">Filtra per nome</label><input id="name-filter" type="search" placeholder="Scrivi un nome…" value="${esc(p.name||'')}" aria-controls="local-items"><span id="filter-count" class="muted" role="status">${matches(p.name||'').length} di ${items.length}</span></div><div id="local-items" class="${layout}">${body(p.name||'')}</div>`;
}
function render(){bindLocalFilter=null;const p=Object.fromEntries(new URLSearchParams(location.hash.slice(1)));const service=catalog.services.find(s=>s.id===p.s);const feature=service?.features.find(f=>f.id===p.f);const test=feature?.scenarios.find(t=>t.id===p.t||t.legacyId===p.t);let content='';
 let crumbs=link('Tutti i servizi',{});if(service)crumbs+=' <span>/</span> '+link(service.name,{s:service.id});if(p.a&&service)crumbs+=' <span>/</span> '+link(p.a,{s:p.s,a:p.a});if(feature)crumbs+=' <span>/</span> '+link(feature.displayName,{s:p.s,a:p.a,f:p.f});
 if((p.s&&!service)||(p.f&&!feature)||(p.t&&!test)){main.innerHTML=`<h1>Pagina non disponibile</h1><p>Il collegamento potrebbe riferirsi a una versione precedente del catalogo.</p>${link('Torna ai servizi',{},'button')}`;return;}
 if(test){const rows=test.examples.reduce((n,e)=>n+e.rows.length,0);content=`<span class="eyebrow">Descrizione del test</span><h1 class="test-title">${esc(test.name)}</h1><p class="intro">${esc(test.area)} · ${esc(service.name)}</p><div class="feedback-link"><button id="copy-test-link" type="button">Copia link al test</button><span id="copy-link-status" role="status" class="muted"></span><input id="copy-link-fallback" aria-label="Link al test da copiare" readonly hidden><p class="feedback-hint">Manca un caso o il comportamento atteso è cambiato? Condividi questo test con il team QA.</p></div>${description(test.description)}${test.rule?`<div class="note"><strong>${esc(test.rule.name)}</strong>${description(test.rule.description)}</div>`:''}${test.backgrounds.map(b=>`<section class="panel"><h2>Prima di iniziare${b.name?' · '+esc(b.name):''}</h2><p class="muted">Precondizioni comuni ai test di questa funzionalità.</p>${description(b.description)}${bdd(b.steps)}</section>`).join('')}<section class="panel"><h2>Comportamento verificato</h2>${bdd(test.steps)}</section>${test.examples.length?`<section class="panel"><h2>Varianti del test</h2><p class="muted">${rows} righe di esempio. Ogni riga fornisce i valori dei parametri tra &lt;parentesi angolari&gt; nella descrizione.</p>${test.examples.map((e,i)=>`<h3>${esc(e.name||'Esempi '+(i+1))}</h3>${description(e.description)}${table(e.header,e.rows)}<details class="tech"><summary>Tag di questi esempi</summary><div>${badges(e.tags)}</div></details>`).join('')}</section>`:''}<details class="panel tech"><summary>Dettagli tecnici e sorgente</summary><div>${badges(test.tags)}<p>File: <a href="${esc(feature.sourceUrl+'#L'+test.line)}">${esc(feature.path)}</a> · riga ${test.line}</p><p>Tipo: ${esc(test.keyword)}. I tag sono riportati come definiti nei sorgenti; la loro presenza non determina l’esito di esecuzione.</p><p>Nome originale della funzionalità: ${esc(feature.name)}</p>${runnerDetails(service,test)}${historyDetails(feature)}</div></details>${link('← Torna ai test della funzionalità',{s:p.s,a:p.a,f:p.f},'back')}`;
 }else if(feature){const scenarios=feature.scenarios.filter(t=>!p.a||t.area===p.a);content=`<span class="eyebrow">Funzionalità · ${esc(service.name)}</span><h1>${esc(feature.displayName)}</h1>${description(feature.description)}<p class="intro">${scenarios.length} scenari implementati. Seleziona un test per leggerne precondizioni, comportamento e risultato atteso.</p>${localList(scenarios,p,t=>t.name,t=>testRow({service,feature,test:t}),'list')}`;
 }else if(service&&p.a){const features=service.features.filter(f=>f.scenarios.some(t=>t.area===p.a));content=`<span class="eyebrow">Area funzionale · ${esc(service.name)}</span><h1>${esc(p.a)}</h1><p class="intro">Scegli una funzionalità per vedere i suoi test. Ogni scenario descrive una situazione e il risultato atteso.</p><div class="section-head"><h2>${features.length} funzionalità con test</h2></div>${localList(features,p,f=>f.displayName,f=>`<a class="card" href="${esc(url({s:p.s,a:p.a,f:f.id}))}"><span class="card-kind">Funzionalità</span><h3>${esc(f.displayName)}</h3><p class="context" title="${esc(f.path)}">${esc(f.path.split('/cucumber/').pop().replace(/\.feature$/,''))}</p>${changeInfo(f.history?.[0])}<span class="bottom">${f.scenarios.filter(t=>t.area===p.a).length} scenari · Vedi i test</span></a>`)}`;
 }else if(service){const tests=all.filter(r=>r.service.id===service.id);const areas=[...new Set(tests.map(r=>r.test.area))].sort((a,b)=>a.localeCompare(b,'it'));content=`<span class="eyebrow">Servizio</span><h1>${esc(service.name)}</h1><p class="intro">${esc(service.description)}. Scegli un’area per esplorare le funzionalità con test implementati.</p><div class="stat"><span><strong>${areas.length}</strong> aree funzionali</span><span><strong>${service.features.filter(f=>f.scenarios.length).length}</strong> funzionalità con test</span><span><strong>${tests.length}</strong> scenari</span></div><div class="section-head"><h2>Aree funzionali</h2></div>${localList(areas,p,a=>a,a=>{const entries=tests.filter(r=>r.test.area===a);return `<a class="card" href="${esc(url({s:p.s,a}))}"><span class="card-kind">Area funzionale</span><h3>${esc(a)}</h3><p>${new Set(entries.map(r=>r.feature.id)).size} funzionalità · ${entries.length} scenari</p>${changeInfo(latestFeatureChange([...new Set(entries.map(r=>r.feature))]),'Ultima modifica ai test')}<span class="bottom">Vedi le funzionalità</span></a>`;},'grid',catalog.pagination?.functionalAreasPerPage??100)}`;
 }else{content=`<span class="eyebrow">Il catalogo di SL QA&OPS</span><h1>Quali funzionalità<br>hanno test implementati?</h1><p class="intro">Scegli un servizio, esplora le sue funzionalità e scopri quali comportamenti vengono verificati dai test.</p><div class="section-head"><h2>Scegli un servizio</h2><span class="muted">${all.length.toLocaleString('it-IT')} scenari nel catalogo</span></div><div class="grid service-grid">${catalog.services.map(s=>{const n=all.filter(r=>r.service.id===s.id).length;return `<a class="card service-card" href="${esc(url({s:s.id}))}"><span class="card-kind">Servizio</span><h2>${esc(s.name)}</h2><p>${esc(s.description)}</p><p><strong>${s.features.filter(f=>f.scenarios.length).length}</strong> funzionalità con test · <strong>${n.toLocaleString('it-IT')}</strong> scenari</p>${changeInfo(s.lastChange,'Ultima modifica ai test')}<span class="bottom">Esplora le aree</span></a>`;}).join('')}</div><section class="panel glossary-panel" aria-labelledby="glossary-title"><h2 id="glossary-title">Come leggere il catalogo</h2><dl class="glossary">
<dt>Area funzionale</dt><dd>Raggruppamento di funzionalità relative allo stesso ambito del servizio.</dd>
<dt>Funzionalità</dt><dd>Comportamento o capacità del servizio per cui sono presenti test.</dd>
<dt>Scenario di test</dt><dd>Un caso che descrive le condizioni iniziali, un’azione e il risultato atteso.</dd>
<dt>BDD: Dato / Quando / Allora</dt><dd>Un modo di descrivere il test: <strong>Dato</strong> indica le condizioni iniziali, <strong>Quando</strong> l’azione, <strong>Allora</strong> il risultato atteso.</dd>
<dt>Precondizioni comuni</dt><dd>Condizioni iniziali condivise da più scenari, mostrate nella sezione “Prima di iniziare”.</dd>
<dt>Varianti del test</dt><dd>Dati diversi con cui viene ripetuto lo stesso scenario, per verificare più casistiche.</dd>
</dl></section>`;}
 const currentStep=feature?3:p.a?2:service?1:0;
 const journey=`<ol class="journey" aria-label="Percorso di esplorazione">${['Servizio','Area funzionale','Funzionalità','Test'].map((label,i)=>`<li ${i===currentStep?'aria-current="step"':''}><span class="step-number" aria-hidden="true">${i+1}</span>${label}</li>`).join('')}</ol>`;
 const issues=catalog.issues.filter(i=>!service||i.service===service.name);main.innerHTML=`<nav class="crumbs" aria-label="Percorso">${crumbs}</nav>${journey}${issues.length?`<details class="warning"><summary>${issues.length} file richiedono attenzione: il catalogo non comprende i loro scenari</summary><div>${issues.map(i=>`<p class="tech"><strong>${esc(i.path)}</strong><br>${esc(i.message)}</p>`).join('')}</div></details>`:''}${content}`;
 document.title=(test?.name||feature?.displayName||p.a||service?.name||'Tutti i servizi')+' · Catalogo dei test';
 document.getElementById('copy-test-link')?.addEventListener('click',copyTestLink);
 bindLocalFilter?.();
}
window.addEventListener('hashchange',()=>{render();window.scrollTo(0,0);main.focus({preventScroll:true});});render();

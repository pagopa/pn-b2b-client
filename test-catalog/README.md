# Catalogo dei test

Catalogo HTML navigabile per stakeholder di SEND, Interop e PARI. Risponde a tre domande:
quali funzionalità hanno test implementati, quali scenari sono presenti e qual è
la loro descrizione BDD.

Uno degli obiettivi prioritari è permettere ai referenti di prodotto di trovare
una funzionalità, comprendere cosa viene verificato e segnalare **casi mancanti**
o **comportamenti attesi da aggiornare**. Il catalogo serve al confronto tra
SL QA&OPS e team di prodotto; i conteggi aiutano a orientarsi.

## Stato della pubblicazione

Alla verifica dell’11 ottobre 2026, il catalogo e il workflow sono presenti nel
checkout locale sul branch `codex/test-catalog`. Le API GitHub per il sito Pages
e per `test-catalog-pages.yml` restituiscono 404: **non risultano configurati su
GitHub e l’aggiornamento giornaliero non è ancora attivo**. Non è stata eseguita
una pubblicazione di questo catalogo. Le istruzioni di attivazione sono in fondo
al documento; questo paragrafo va aggiornato dopo il primo deploy verificato.

## Consultare il catalogo

Aprire **generated/index.html** con un browser. Non richiede server, connessione,
CDN o installazioni per la consultazione; l'HTML contiene tutti i dati necessari.
È possibile copiare il solo HTML. I collegamenti ai sorgenti, ai runner e ai commit richiedono invece connessione
a GitHub. Il sorgente dello scenario si apre alla revisione analizzata e alla sua riga. GitHub non esegue l'HTML nella
vista del codice: occorre scaricarlo e aprirlo localmente.

## Branch dei test analizzati

Il branch è configurabile in `config/source.json`. La configurazione attuale è:

```json
{
  "branch": "develop",
  "repositoryUrl": "https://github.com/pagopa/pn-b2b-client"
}
```

`branch` è il nome di un branch locale; `repositoryUrl` è usato per costruire i
collegamenti GitHub, non per scegliere il repository da cui leggere i test.
Il generatore legge i file versionati da un unico commit di quel branch, senza
cambiare checkout. Puoi quindi lavorare al tool su `codex/test-catalog` mentre
i test provengono dal branch configurato, attualmente `develop`. Il footer indica branch e commit dei test.
Modifiche non committate e file non tracciati non entrano nel catalogo.
Il branch deve esistere localmente: se manca, la generazione si interrompe senza
ripiegare sul checkout corrente. La generazione locale non esegue fetch o pull: occorre aggiornare il branch
sorgente locale per includere nuovi commit. Il workflow GitHub Actions, invece,
aggiorna esplicitamente quel branch dal remoto prima di generare il catalogo.

## Attivare o disattivare un servizio

Modificare `config/services.json`:

```json
{
  "send": true,
  "interop": true,
  "pari": false
}
```

`true` include il servizio; con `false` non vengono analizzati il contenuto dei
suoi test, i runner o la cronologia, e il servizio non compare negli output. PARI è attualmente disattivato.
Dopo ogni modifica eseguire `npm run generate --prefix test-catalog` dalla root
e ricaricare l'HTML. Per riattivare PARI impostare `"pari": true`.
Tutti i servizi devono avere un valore booleano; nomi sconosciuti o valori
non validi interrompono la generazione prima di aggiornare gli output.

## Mostrare o nascondere “Filtra per nome”

Modificare `config/ui.json`:

```json
{
  "nameFilterEnabled": false
}
```

Il file deve esistere. Il default è `false` se la proprietà è omessa: il catalogo propone solo
la navigazione servizio → area → funzionalità → test. Con `true` compare il
filtro locale negli elenchi di aree, funzionalità e test. Quando è disattivato,
anche eventuali filtri presenti in vecchi collegamenti vengono ignorati.
Rigenerare il catalogo e ricaricare l’HTML dopo la modifica.

## Aree funzionali per pagina

Modificare `config/pagination.json`:

```json
{
  "functionalAreasPerPage": 100
}
```

Il valore deve essere un intero positivo. Il default è 100 se la proprietà
è omessa; il file deve comunque esistere. Si applica all’elenco delle aree funzionali di ogni servizio, anche
quando si filtra per nome. Gli elenchi di funzionalità e test restano a 24
voci per pagina. Rigenerare il catalogo e ricaricare l’HTML dopo la modifica.

## Rigenerazione

Dalla root del repository, con Git e Node.js **24** (la versione usata anche
nel workflow). Le dipendenze attuali richiedono almeno Node.js 22.13.0 nella
serie 22; è supportata anche la versione 24 o successiva:

```sh
npm ci --prefix test-catalog --ignore-scripts
npm run generate --prefix test-catalog
npm test --prefix test-catalog
```

Il generatore legge i `.feature` versionati in `src`, `interop-qa-tests` e `pari`
nel branch configurato, limitandosi ai servizi abilitati ed escludendo `target`,
`node_modules` e `.git`. Non esegue test backend né modifica i sorgenti.
L’installazione iniziale richiede accesso al registry npm; la generazione
successiva è locale. `npm test` legge anche gli output già generati: eseguirlo
dopo `generate`, così controlla il catalogo aggiornato.

Versionare insieme sorgenti del generatore, configurazione, lockfile e tutti gli
output in `generated/`: `index.html`, `catalog.json` e i file `service-*.js`.
La home carica solo un riepilogo; i dati completi vengono caricati aprendo un
servizio. Tenere i file nella stessa cartella, anche per la consultazione locale.
Rigenerare dopo aggiornamenti
del branch sorgente o della configurazione del catalogo.

## Organizzazione e classificazione

- `src/catalog.mjs`: lettura con il parser ufficiale Cucumber Gherkin.
- `src/generate.mjs`: estrazione e produzione degli output.
- `src/provenance.mjs`: cronologia Git e metadati delle modifiche.
- `src/runners.mjs`: associazioni statiche ai runner JUnit.
- `src/template.html`, `style.css`, `app.js`: interfaccia autonoma.
- `config/taxonomy.json`: servizi, regole di classificazione e titoli alternativi.
- `generated/catalog.json`: dati strutturati, utilizzabili anche da un futuro
  esportatore Confluence. Nessuna pubblicazione Confluence è inclusa.

La navigazione segue **servizio → area → funzionalità → scenario BDD**.
In questa prima versione una funzionalità corrisponde a una dichiarazione
`Feature` con almeno uno scenario. Feature omonime in file diversi restano
separate; il percorso ne permette la distinzione. 
Non è una tassonomia di prodotto già validata: i nomi delle aree sono basate sulla
struttura del repository.  
L'area funzionale "File di utility tecnica" è stata inserita per contenere scenari come _examples.feature che non implementano alcuna logica di testing reale.
La funzionalità di paginazione prima non classificata è stata inserita all'interno dell'area funzionale "Catalogo degli eservice"  tramite il tag piuttosto che il path per permettere eventuali future nuove aggregazioni differenti nel caso di aggiunta scenari al path /pagination.
Sono state aggiunte le aree "Consolidatore" "IO connector" e "funzionalità Delivery push" e unificate quelle di Comunicazione bonarie e Notifiche Bonarie.
I titoli delle Feature rimangono quelli originali, tranne le etichette esplicitamente definite in `featureNames` (attualmente PARI).

Le regole sono ordinate: la prima corrispondenza determina l'area dello scenario.
`tags` contiene nomi esatti con `@`; `path` è un'espressione regolare applicata al
percorso relativo alla root. Se una regola contiene entrambi, basta una delle
condizioni. I tag di Feature e Rule sono ereditati dagli scenari. Quelli su
Examples appartengono solo alle relative varianti: sono visibili nei dettagli tecnici,
ma non riclassificano tutto lo scenario. Una feature può apparire in più aree
senza duplicare gli scenari nel totale del servizio. Senza corrispondenza, il test
rimane in **Da classificare**. Tutti i tag originali restano consultabili.

Per affinare il catalogo, aggiungere regole specifiche prima di quelle generiche.
`featureNames` associa il nome del file a un titolo leggibile all'interno del
servizio; il nome Gherkin originale resta nei dettagli tecnici.

## Fedeltà e limiti

- Uno Scenario Outline conta come uno scenario; le righe Examples sono varianti
  mostrate separatamente, non esecuzioni effettuate.
- Background di Feature e Rule, descrizioni, tabelle e docstring sono conservati.
  Le parole chiave inglesi sono visualizzate come Dato/Quando/Allora/E/Ma; il testo
  dei passi non viene riscritto o tradotto.
- **Filtra per nome** restringe soltanto l’elenco visualizzato di aree, funzionalità
  o scenari, includendo tutte le sue pagine. Il filtro è immediato e ignora
  maiuscole/minuscole. I tag rimangono nei dettagli tecnici, senza ricerca globale.
- I test con `@ignore` o altri tag operativi rimangono nel catalogo. Le associazioni
  ai runner considerano i filtri dichiarati nelle suite (vedi sotto); non viene
  verificata l'associazione agli step Java né eseguito alcun test.
- File privi di Feature (vuoti o commentati) sono elencati a fondo pagina.
  Errori di parsing producono una segnalazione visibile e uscita non zero del
  generatore; l'output parziale indica esplicitamente i file non letti.
- I collegamenti interni sono mantenuti nell'hash dell'URL. Gli identificativi
  degli scenari dipendono dal percorso e dall’identificativo tra parentesi quadre,
  quando presente, oppure dal nome e dalla Rule. Le occorrenze duplicate vengono
  distinte per ordine. Inserire righe non cambia il link; rinominare/spostare file
  o cambiare l’identità del test può cambiarlo. Gli ID precedenti basati sulla riga
  sono accettati per la riga corrente, senza una mappa storica. Un collegamento non
  più valido mostra una pagina di recupero.
- I file `service-*.js` includono i dati di esempio contenuti nei `.feature`: conservarli negli
  stessi ambiti di accesso del repository.

## Cronologia, runner e feedback

Le card mostrano data (Europe/Rome) e autore Git dell’ultima modifica del file.
Per servizio e area si considera la modifica più recente fra i file presenti nel
catalogo. Sulle card scenario la dicitura **Ultima modifica del file** evita di
attribuire al singolo scenario una modifica che può riguardare altri test.
La data è quella di registrazione del commit (committer date); il nome è l’autore.

Nei dettagli tecnici si trovano contributori e timeline di tutti i commit restituiti
da `git log --follow`, fino alla revisione analizzata, comprese le rinomine rilevate
da Git. L’elenco dei contributori comprende gli autori dei commit che modificano il file.
I coautori dichiarati nei trailer `Co-authored-by` sono deduplicati, senza email,
e compaiono solo in un dettaglio espandibile del relativo commit: non sono
attribuiti automaticamente al file e non appaiono nelle card. Nomi diversi della
stessa persona non sono accorpati automaticamente. La cronologia segue la vista
del file restituita da Git: non è una ricostruzione dell’autore di ogni riga.
Con un clone shallow si segnala che la storia è parziale. Il workflow predisposto
richiede già una cronologia completa (`fetch-depth: 0`).

I runner sono letti dai sorgenti Java di test della stessa revisione e dello
stesso servizio (esclusi i package `steps`). Si risolvono suite JUnit, percorsi
`SelectClasspathResource`, `IncludeTags`/`ExcludeTags`, espressioni `!`, `&`, `|`,
parentesi, ereditarietà, costanti stringa semplici e suite `SelectClasses`.
Le inclusioni multiple sono alternative; le esclusioni prevalgono. Per gli
Outline si valutano separatamente i tag di ogni gruppo Examples e si mostrano le
righe selezionate quando l’inclusione è parziale. I runner sono collegati a GitHub.

È un’analisi statica delle dichiarazioni, non un’esecuzione JUnit/Maven: opzioni
CLI, CI, hook e configurazioni esterne possono cambiare la selezione effettiva.
Costanti non risolvibili e selettori aggiuntivi rilevati sono marcati **da verificare**;
non vengono considerati inclusioni certe. In particolare i filtri runtime dei
runner Delayer richiedono verifica. Le annotazioni Java personalizzate e i runner
non basati su queste suite JUnit non sono interpretati.
Riferimenti: [IncludeTags JUnit](https://docs.junit.org/5.11.0/api/org.junit.platform.suite.api/org/junit/platform/suite/api/IncludeTags.html)
e [Cucumber JUnit Platform](https://github.com/cucumber/cucumber-jvm/blob/main/cucumber-junit-platform-engine/README.md).

**Copia link al test** copia il collegamento alla pagina di dettaglio. Se il browser
non consente l’accesso agli appunti, mostra il link selezionato per la copia manuale.
In locale è un URL `file:` e funziona per chi dispone del file allo stesso percorso;
quando il catalogo sarà pubblicato copierà automaticamente l’URL del sito.

## Verifica

I test verificano ereditarietà dei tag, Background e Rule, esempi multipli,
Gherkin italiano, errori sintattici e incorporamento sicuro dei dati nell'HTML.
Le verifiche del documento generato controllano navigazione, impostazione del
filtro locale e contenuti BDD in un DOM simulato; non sostituiscono la verifica
visiva nel browser, una prova reale degli appunti o un deploy su GitHub. I test
del documento usano gli output presenti in `generated/`.

Sono inoltre verificati rinomine e coautori Git, isolamento della revisione,
associazioni ai runner (incluse esclusioni, ereditarietà e varianti), stabilità
degli ID rispetto alle righe e copia del link con alternativa manuale.

## Aggiornamento giornaliero su GitHub Pages

Il [workflow predisposto](../.github/workflows/test-catalog-pages.yml), **una volta
attivato**, è configurato per generare e pubblicare il catalogo ogni giorno alle **18:30 Europe/Rome**, anche durante l’ora legale. È disponibile
anche l’avvio manuale da Actions. GitHub può ritardare l’avvio pianificato.

Il flusso, passo per passo:

1. Si avvia all’orario previsto o tramite **Run workflow**.
2. Scarica il tool alla revisione dell’evento GitHub, con la cronologia completa.
3. Legge `source.json` e aggiorna dal remoto `origin` il branch dei test configurato.
4. Prepara Node.js 24 e installa le dipendenze dal lockfile.
5. Esegue `generate`: produce HTML e JSON secondo le configurazioni del catalogo.
6. Esegue `npm test`: verifica il tool e l’output, **non i test backend dei servizi**.
7. Carica la cartella generata e, solo dopo il successo dei passi precedenti,
   la pubblica su GitHub Pages. Non è necessario tenere acceso il Mac.

Per attivarlo:

1. Integrare il catalogo e il workflow nel branch predefinito (`develop`).
2. In **Settings → Pages → Build and deployment → Source** scegliere **GitHub Actions**.
3. Eseguire una prima volta **Test catalog · GitHub Pages → Run workflow** da `develop`.
   Il job di pubblicazione restituisce il link del sito.

Il workflow usa il tool del branch predefinito e aggiorna dal remoto il branch
sorgente indicato in `config/source.json`, con cronologia completa. Non cambia
la configurazione usata localmente. Genera i file, esegue le verifiche e pubblica
soltanto `test-catalog/generated`; non committa gli output nel repository.
Se generazione o verifiche falliscono, il sito già pubblicato rimane invariato.
Avvii manuali da altri branch non pubblicano. Eventuali protezioni dell’ambiente
`github-pages` devono consentire il deploy da `develop`; una richiesta di approvazione
manuale in quell’ambiente sospenderebbe la pubblicazione automatica.

La pubblicazione prevista è pubblica, coerentemente con questo repository
pubblico. Il workflow non abilita Pages automaticamente: è necessario il passaggio
in Settings descritto sopra. La pianificazione si attiva soltanto quando il workflow è
sul branch predefinito. Nei repository pubblici GitHub disabilita i workflow
schedulati dopo 60 giorni senza attività nel repository.

Riferimenti: [pianificazione GitHub Actions](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows#schedule)
e [pubblicazione Pages](https://docs.github.com/en/pages/getting-started-with-github-pages/using-custom-workflows-with-github-pages).

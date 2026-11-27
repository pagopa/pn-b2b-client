# sdp_generator

Genera il **Service Delivery Plan** dal manifest della release e lo pubblica
su Confluence. **La pubblicazione è il comportamento predefinito**, senza
`--apply`. Usare `--dry-run` per un'anteprima Markdown offline senza scritture.

## Esecuzione locale

Dalla radice del repository, con Python 3.10 o superiore:

```bash
python3 -m pip install -r tools/generators/sdp/requirements.txt
python3 tools/generators/sdp/sdp_generator.py releases/template.yaml --dry-run

# Pubblicazione di una release reale
python3 tools/generators/sdp/sdp_generator.py releases/send/2.26.0.yaml
```

Per una release reale sostituire il percorso con
`releases/<prodotto>/<versione>.yaml`. Usare il nuovo
[template](../../../releases/template.yaml) e le relative
[istruzioni](../../../releases/README.md): i manifest storici potrebbero non
contenere i dati richiesti e non vengono migrati automaticamente.

L'anteprima accetta tutti gli stati: `draft`, `planned`, `in-progress`,
`execution`, `closed`. Anche in `draft`, questa prima versione richiede i campi
obbligatori del nuovo schema per produrre una tabella completa.
Per pubblicare impostare le variabili di ambiente `JIRA_CLOUD_ID` e
`CONFLUENCE_API_TOKEN`. Non inserire credenziali nel manifest.
Il comando non modifica manifest, Jira o branch.

## Documento prodotto

- Prodotto, versione, trimestre, stato e descrizione della release.
- Calendario degli ambienti indicati in `delivery`.
- Una riga per attività QA, identificata da `id` e `sequence`: titolo, QA Epic,
  strategie, effort in giorni uomo, risorse e ruoli, priorità, stato QA,
  note, rischi e dipendenze.
- Tabella separata delle esclusioni, con work item e motivazione.

L'output usa l'ordine del manifest. Repository e branch servono ai generatori
e non sono colonne della tabella SDP. L'effort rimane numerico: la conversione
in T-Shirt size non viene introdotta senza averne definito le soglie.

L'anteprima `--dry-run` è **offline** e non richiede credenziali.
**La lettura Jira non è ancora implementata**, anche nella pagina pubblicata.
Se manca `qa_epic`, mostra “QA Epic da creare”; se è presente, lo stato viene
indicato come “Non disponibile (Jira non interrogato)”. Un titolo assente è
segnalato come “Titolo da recuperare da Jira”. Non vengono inventati stati o
titoli. La lettura Jira resta un passo successivo.

## Validazione ed errori

Prima di generare il documento vengono verificati campi obbligatori, stati,
strategie, priorità, ruoli, date, stime positive, coppie `(id, sequence)` e
`(id, qa_epic)` univoche, esclusioni univoche e separate dal perimetro QA.
Sono rifiutati anche chiavi YAML duplicate e `status` nei work item: lo stato
QA dovrà provenire dalla QA Epic. Le liste facoltative, se presenti, devono
essere liste; usare `[]` per una lista vuota. Sono accettate release con sole
esclusioni (`work-items: []`).

Gli errori vanno su stderr con exit code 1, senza SDP parziali su stdout.
In anteprima il documento va su stdout; in pubblicazione viene stampato
l'esito con l'ID pagina. Il generatore non salva file locali. I testi vengono
escapati in Markdown per l'anteprima e XHTML per Confluence.

## Pubblicazione Confluence

Le regole seguenti sono riportate in [config.yaml](config.yaml).
La CLI legge la configurazione per pubblicare. La pianificazione giornaliera
è concordata ma **non ancora attiva**: il workflow attuale è soltanto manuale.

| Aspetto | Regola |
| --- | --- |
| Cartella padre | `3459022926` (`parent_type: folder`, `parent_id` in configurazione) |
| Granularità | Una pagina per prodotto e versione della release |
| Titolo iniziale | `SDP — {product} — {version}` |
| Identificazione | Prodotto e versione come identità stabile, indipendente dal titolo |
| Prima esecuzione | Creazione della pagina se manca |
| Esecuzioni successive | Aggiornamento della stessa pagina, senza duplicati |
| Contenuto generato | Sostituito a ogni aggiornamento; usare aggiornamenti versionati per conservare la cronologia |
| Pianificazione futura | Ogni giorno alle 18:00, fuso `Europe/Rome`, rispettando ora solare e legale |
| Stati pubblicabili | `planned`, `in-progress`, `execution` |
| Release escluse | `draft` (anteprima disponibile) |
| Release `closed` | Una pubblicazione finale riuscita, poi esclusione dagli aggiornamenti automatici |

La pagina contiene macro Anchor generate con un'identità derivata da prodotto
e versione. La ricerca scorre le pagine visibili dello spazio con paginazione,
comprese quelle archiviate. Il titolo modificato manualmente viene conservato.
Non rimuovere le macro Anchor generate e mantenere l'accesso dell'utenza
tecnica alle pagine: il riconoscimento dipende da questi dati.

In presenza di identità duplicate, titolo occupato da una pagina non
riconosciuta, pagina spostata o archiviata, il comando si ferma senza scrivere.
Non adotta automaticamente pagine create a mano. Tutto il corpo della pagina
è generato; le modifiche manuali vengono sostituite e restano nella history.

Il marcatore di chiusura viene scritto insieme al contenuto: una scrittura
fallita non registra il successo. Se una release torna attiva e viene
pubblicata, il marcatore viene rimosso; una successiva chiusura produce
un nuovo aggiornamento finale. Una release chiusa mai pubblicata crea
direttamente la pagina finale.

In caso di timeout durante la scrittura, verificare la pagina prima di
riprovare: il server potrebbe aver completato l'operazione. Gli errori e
i conflitti di versione non vengono ritentati automaticamente.

## Workflow manuale

Da **Actions → sdp_generator → Run workflow**, selezionare il branch,
il prodotto (`send` o `interop`) e il nome del manifest senza `.yaml`.
Lasciare `dry_run` disattivato per pubblicare, attivarlo per l'anteprima.
Il workflow usa `vars.JIRA_CLOUD_ID` e `secrets.CONFLUENCE_API_TOKEN`.
Deve essere presente sul branch predefinito per essere avviabile dall'UI.

Le esecuzioni del workflow sono serializzate anche tra branch diversi.
Non avviare pubblicazioni concorrenti dalla CLI e da GitHub: Confluence
non offre una creazione atomica per identità SDP.

## Verifica

```bash
python3 -m unittest discover -s tools/generators/sdp/tests -v
```

## Verifica preliminare accesso Confluence

La Action manuale `sdp_check_confluence` esegue soltanto un GET del contenitore
padre configurato, usando `vars.JIRA_CLOUD_ID` e `secrets.CONFLUENCE_API_TOKEN`
con autenticazione Bearer sul gateway Atlassian, come il generatore Epic.
Il token dedicato deve essere abilitato per Confluence; il controllo richiede
`read:folder:confluence` per la cartella padre e `read:page:confluence` per
le pagine. Per la pubblicazione serve anche `write:page:confluence`.
Il Cloud ID resta quello del sito già configurato.
Non viene usato il token Jira come fallback. La prova verifica anche che
questa modalità di autenticazione sia accettata per il token creato.

Dopo commit e push, il workflow deve essere presente sul branch predefinito
per essere avviabile tramite `workflow_dispatch`. Da GitHub: **Actions →
sdp_check_confluence → Run workflow**, selezionando il ramo con il codice
del generatore. Non è una pubblicazione e non ha una pianificazione.

Il campo facoltativo `page_id` permette di verificare una pagina diversa:
per la pagina di test usare `3494707940`. Lasciandolo vuoto viene letta la
cartella padre in `config.yaml`. Questo parametro non cambia la destinazione
degli SDP. In locale il parametro corrisponde a `CONFLUENCE_CHECK_PAGE_ID`.

Il successo conferma solo la lettura del contenuto richiesto. HTTP 401/403 richiedono
la verifica dell'autenticazione, degli scope e dei permessi; HTTP 404 può
indicare anche contenuto non visibile. Non vengono stampati token o corpi
delle risposte. Nessuna scrittura viene eseguita.

Per la cartella il controllo usa `/wiki/api/v2/folders/3459022926`; per una
pagina usa `/wiki/api/v2/pages/{id}`.

Riferimento cartelle: [Confluence REST API v2 — Folder](https://developer.atlassian.com/cloud/confluence/rest/v2/api-group-folder/).

Endpoint pagine di riferimento: [Confluence REST API v2 — Get page by ID](https://developer.atlassian.com/cloud/confluence/rest/v2/api-group-page/#api-pages-id-get).

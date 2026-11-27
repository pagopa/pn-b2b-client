# sdp_generator

Genera un'anteprima locale del **Service Delivery Plan** dal manifest della
release e stampa il documento Markdown sul terminale. Serve a verificare i dati
e la struttura della tabella prima dell'integrazione con Confluence.

## Esecuzione locale

Dalla radice del repository, con Python 3.10 o superiore:

```bash
python3 -m pip install -r tools/generators/sdp/requirements.txt
python3 tools/generators/sdp/sdp_generator.py releases/template.yaml
```

Per una release reale sostituire il percorso con
`releases/<prodotto>/<versione>.yaml`. Usare il nuovo
[template](../../../releases/template.yaml) e le relative
[istruzioni](../../../releases/README.md): i manifest storici potrebbero non
contenere i dati richiesti e non vengono migrati automaticamente.

L'anteprima accetta tutti gli stati: `draft`, `planned`, `in-progress`,
`execution`, `closed`. Anche in `draft`, questa prima versione richiede i campi
obbligatori del nuovo schema per produrre una tabella completa.
Il comando è utilizzabile anche in CI, ma non viene aggiunta una Action in
questa fase. Non modifica manifest, Jira, branch o pagine Confluence.

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

Questa versione è **offline**: non richiede credenziali e non interroga Jira.
Se manca `qa_epic`, mostra “QA Epic da creare”; se è presente, lo stato viene
indicato come “Non disponibile (Jira non interrogato)”. Un titolo assente è
segnalato come “Titolo da recuperare da Jira”. Non vengono inventati stati o
titoli. La lettura Jira e la pubblicazione su Confluence restano da implementare.

## Validazione ed errori

Prima di stampare il documento vengono verificati campi obbligatori, stati,
strategie, priorità, ruoli, date, stime positive, coppie `(id, sequence)` e
`(id, qa_epic)` univoche, esclusioni univoche e separate dal perimetro QA.
Sono rifiutati anche chiavi YAML duplicate e `status` nei work item: lo stato
QA dovrà provenire dalla QA Epic. Le liste facoltative, se presenti, devono
essere liste; usare `[]` per una lista vuota. Sono accettate release con sole
esclusioni (`work-items: []`).

Gli errori vanno su stderr con exit code 1, senza SDP parziali su stdout.
Il documento completo va su stdout, con exit code 0. Il generatore non salva
file: l'output è separato dal codice e può essere rediretto dal chiamante.
I testi vengono escapati per non spezzare le celle Markdown.

## Pubblicazione Confluence concordata

Le regole seguenti sono riportate in [config.yaml](config.yaml).
La configurazione prepara la prossima fase: la CLI attuale non la legge e
non pubblica pagine né attiva esecuzioni pianificate.

| Aspetto | Regola |
| --- | --- |
| Cartella padre | `3459022926` (`parent_type: folder`, `parent_id` in configurazione) |
| Granularità | Una pagina per prodotto e versione della release |
| Titolo iniziale | `SDP — {product} — {version}` |
| Identificazione | Prodotto e versione come identità stabile, indipendente dal titolo |
| Prima esecuzione | Creazione della pagina se manca |
| Esecuzioni successive | Aggiornamento della stessa pagina, senza duplicati |
| Contenuto generato | Sostituito a ogni aggiornamento; usare aggiornamenti versionati per conservare la cronologia |
| Pianificazione | Ogni giorno alle 18:00, fuso `Europe/Rome`, rispettando ora solare e legale |
| Release elaborate ogni giorno | `planned`, `in-progress`, `execution` |
| Release escluse | `draft` |
| Release `closed` | Una pubblicazione finale riuscita, poi esclusione dagli aggiornamenti automatici |

La pubblicazione finale dovrà essere registrata in uno stato persistente solo
dopo il successo della scrittura su Confluence. Un errore non deve impedire
il tentativo successivo. Una release già chiusa e mai pubblicata deve poter
creare direttamente la propria pagina finale. Se una release torna attiva,
gli aggiornamenti riprendono; una successiva chiusura richiede un nuovo
aggiornamento finale.

La fase di integrazione dovrà prevedere un'anteprima senza scritture e
`--apply` per creare o aggiornare la pagina. La pianificazione sarà gestita
dal workflow del repository. Prima della pubblicazione andranno collegati
anche gli stati QA letti da Jira, attualmente assenti nell'anteprima offline.

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
le pagine. Per la futura pubblicazione serve anche `write:page:confluence`.
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

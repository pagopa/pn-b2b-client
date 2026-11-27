# Strumenti del processo QA/QE

Il processo Release as Code parte dai manifest YAML in `releases/<prodotto>/`.
Il manifest definisce il perimetro della release, i work item di prodotto e le
QA Epic che guidano il lavoro di validazione. Gli strumenti leggono questi dati
nelle fasi in cui servono; i risultati delle esecuzioni nascono invece nei
repository che eseguono le suite.

## Mappa del flusso

| Fase | Strumento e repository | Input | Risultato |
| --- | --- | --- | --- |
| Preparazione del lavoro QA | [epic_generator](generators/epic/README.md) in `pn-b2b-client` | Manifest di una release `in-progress` | QA Epic su Jira, branch QA e PR per riportare `qa_epic` e `branch` nel manifest |
| Pianificazione dei test | [pst_generator](generators/pst/README.md) in `pn-b2b-client` | Manifest, branch QA e scenari Gherkin | PST per QA Epic nel branch `pst-output` |
| Analisi degli esiti NRT | [RAE generator](https://github.com/pagopa/qe_suit/tree/develop/tools/rae-generator) in `qe_suit` | Report delle suite e analisi del QA | YAML RAE e Markdown, tramite Draft PR e revisione |
| Quadro della release | [sdp_generator](generators/sdp/README.md) in `pn-b2b-client` | Manifest della release | Pagina SDP su Confluence; anteprima Markdown con `--dry-run` |

La tabella descrive gli strumenti esistenti, inclusa la pubblicazione manuale SDP. Il catalogo tecnico dei generatori di questo repository è in
[generators/README.md](generators/README.md).

## Come si collegano

1. Il Principal Lead prepara il manifest della release. Quando la release è
   `in-progress`, può avviare manualmente `epic_generator` per i work item
   senza QA Epic. Il generatore propone una PR di aggiornamento del manifest.
2. Gli scenari `.feature` associati alla QA Epic descrivono cosa verificare.
   `pst_generator` produce il relativo PST e lo mantiene allineato agli
   scenari: il PST è un piano, non il risultato di un'esecuzione.
3. Le suite vengono eseguite nei repository QA. In `qe_suit`, il generatore RAE
   parte dai report NRT, apre una Draft PR e lascia al QA il completamento
   dell'analisi prima della revisione. La RAE documenta gli esiti osservati.
4. `sdp_generator` pubblica su Confluence la vista della release, con calendario,
   piano QA ed esclusioni. La lettura degli stati Jira e la pianificazione
   giornaliera saranno introdotte successivamente.

## Responsabilità dei dati

| Fonte o artefatto | Responsabilità |
| --- | --- |
| Manifest | Perimetro concordato della release e collegamenti fra work item, QA Epic, repository e branch |
| Scenari `.feature` | Comportamenti da verificare |
| PST | Piano di test della QA Epic, derivato dagli scenari |
| Report di esecuzione e RAE | Risultati osservati e analisi del QA |
| SDP | Quadro della release derivato dal manifest e pubblicato su Confluence; anteprima locale con `--dry-run` |

I comandi, le validazioni e la gestione degli errori sono nei README dei
singoli strumenti. Il collegamento formale fra una RAE, la release e le QA Epic
resta da definire per il processo 2027: la RAE contiene campi `release` e
`issue_jira`, ma questa mappa non assume ancora una corrispondenza automatica
con le QA Epic del manifest.

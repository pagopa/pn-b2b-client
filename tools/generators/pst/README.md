# Gherkin-to-PST Generator

Genera un PST Markdown per ciascuna QA Epic delle release in corso, raccogliendo
gli scenari Gherkin dai branch della release. Con `generate` e `generate-all`,
`--html` produce anche l'HTML;
può inoltre elaborare un singolo feature file in Markdown.

## Uso

Prerequisiti: Python 3.12 o successivo e Git con la storia completa dei branch
da consultare. Le dipendenze Python sono fissate in `requirements.txt`.
Dalla radice di `pn-b2b-client`:

```bash
cd tools/generators/pst
python3 -m venv .venv
.venv/bin/python -m pip install -r requirements.txt
```

Generare tutti i PST delle release attive, restando nella stessa directory:

```bash
.venv/bin/python -m pst_generator.cli generate-all --repo ../../.. --config config.yaml
```

Il percorso `../../..` indica la radice di `pn-b2b-client`: i documenti vengono
scritti nella sua cartella `pst/`. Le release vengono lette dal checkout corrente;
per utilizzare le definizioni di `develop`, eseguire il comando in un checkout
di quel branch. Il generatore non cambia branch e non esegue fetch.

Per generare un documento di esempio senza leggere le release:

```bash
.venv/bin/python -m pst_generator.cli tests/fixtures/example.feature --config config.yaml --output /tmp/pst-example.md
```

La configurazione definisce il pattern dell'ID, le traduzioni dei termini e la
presentazione del documento. L'ID viene letto dal titolo dello scenario, ad esempio
`[TIMELINE_REWORK_2]`. Ogni caso contiene precondizioni, dati rilevanti, operazione,
risultato atteso e tracciabilità.

I tre dizionari di `config.yaml` sono: `business_terms` per i nomi dei campi e le
intestazioni di `Examples`, `value_terms` per i valori delle tabelle e i valori
esatti tra virgolette nel testo, `status_terms` per i codici HTTP. Le sostituzioni
dei valori tra virgolette modificano solo il PST generato, non il feature sorgente.

## Test

Sempre da `tools/generators/pst/`:

```bash
.venv/bin/python -m unittest discover -s tests -v
```

I test di raccolta creano repository Git temporanei per verificare branch,
merge e conflitti. Non modificano i branch o i PST del repository di lavoro.

## Automazione su `pn-b2b-client`

Il workflow [`Generate PST`](../../../.github/workflows/generate-pst.yml) esegue
il generatore su richiesta (`workflow_dispatch`), esclusivamente su `develop`.
Le Action usate dal workflow sono fissate a commit specifici.
Il workflow e il codice devono prima essere integrati in quel branch.

Da GitHub: **Actions → Generate PST → Run workflow → develop**.
L'esecuzione recupera la storia completa di tutti i branch, installa le dipendenze,
esegue i test e genera Markdown e HTML delle QA Epic attive.

I documenti prodotti sono scaricabili come artifact `pst-<run_id>-<run_attempt>`,
con conservazione di 30 giorni. Il riepilogo dell'esecuzione mostra i risultati
e gli eventuali conflitti. Anche quando una QA Epic fallisce, i PST delle altre
sono disponibili nell'artifact; il workflow conserva l'esito negativo.

La Action genera in una directory temporanea pulita tramite `--output-dir`, così
l'artifact contiene solo documenti prodotti in quella esecuzione. Per questa fase
il risultato concordato resta l'artifact scaricabile: la Action non crea branch,
commit, PR o file di stato persistenti. Ogni esecuzione genera nuovamente i documenti.
La strategia per versionare i PST ed evitare rigenerazioni è fissata nella sezione
[Strategia futura — da implementare](#strategia-futura--da-implementare).

### Regole di individuazione

- Le release sono definite nei file YAML contenuti nelle sottocartelle di `releases/`, una per prodotto.
- Sono considerate solo le release con `status: in-progress`.
- Le QA Epic valide sono i valori `qa_epic` presenti nei relativi `work-items`, nel formato `QA-<numero>`.
- Per ogni QA Epic si raccolgono i `branch` espliciti dei work-item; se non ne esistono si usa `feature/<qa_epic>`.
- Un work-item può indicare `repo: qe_suit` accanto a `branch`. Se `repo` manca o vale `pn-b2b-client`, il branch appartiene a `pn-b2b-client`. Sono accettati solo questi due nomi; altri valori producono un errore. Per `qe_suit`, il generatore cerca il checkout in `source-repos/qe_suit/` dentro `pn-b2b-client`, poi in una directory sorella. `--source-repo qe_suit=percorso` consente di indicare un altro percorso locale.
- Tutti i branch così individuati nella stessa release sono fonti comuni per tutte le sue QA Epic, anche quando un tag si trova nel branch associato a un'altra QA Epic. Le release distinte mantengono fonti separate; una QA Epic presente in più release ne unisce le fonti.
- Gli scenari sono contenuti esclusivamente in file `.feature`.
- Un tag `@work-item:<qa_epic>` identifica l'associazione tra scenario e QA Epic.
- Uno scenario associato a più QA Epic viene incluso nel PST di ciascuna QA Epic.

### Esecuzione

Il comando disponibile per generare tutti i PST delle release attive è:

```bash
.venv/bin/python -m pst_generator.cli generate-all --repo ../../.. --config config.yaml
```

Aggiungere `--html` per produrre anche HTML e `--output-dir <directory>` per scegliere
una cartella diversa da `<repo>/pst`. `generate --qa-epic QA-123` usa la stessa
raccolta per una sola QA Epic; `--branch` forza una singola fonte per il debug.

Dalla root di `pn-b2b-client`, per generare la QA Epic `QA-17458` da un checkout
completo di `qe_suit`:

```bash
PYTHONPATH=tools/generators/pst python3 -m pst_generator.cli generate \
  --repo . --qa-epic QA-17458 \
  --source-repo qe_suit=/percorso/al/checkout/qe_suit \
  --config tools/generators/pst/config.yaml
```

Per elaborare tutte le QA Epic delle release attive, usare `generate-all` senza
`--qa-epic` e aggiungere `--html` se servono entrambi i formati:

```bash
PYTHONPATH=tools/generators/pst python3 -m pst_generator.cli generate-all \
  --repo . --source-repo qe_suit=/percorso/al/checkout/qe_suit \
  --config tools/generators/pst/config.yaml --html
```

Il comando controlla tutte le QA Epic attive e riscrive un Markdown solo se il
contenuto generato è cambiato.

La Action fa il checkout completo di `qe_suit` in `source-repos/qe_suit/`, quindi
non passa `--source-repo`: la chiave `repo` nello YAML sceglie la codebase.
`pagopa/qe_suit` è pubblico e non richiede un secret dedicato.

La Action legge le release dallo snapshot di `develop` dell'esecuzione. Il generatore
legge i riferimenti Git già disponibili, prima locali e poi `origin/`, e non esegue
fetch: il checkout della Action recupera la storia completa (`fetch-depth: 0`).
I clone shallow vengono rifiutati.

### Duplicati e conflitti

Gli scenari sono identificati dal loro ID. Copie con lo stesso contenuto Gherkin vengono deduplicate, ignorando posizione delle righe e metadati del parser. Il confronto comprende anche tag, Background, DocString e tutti i blocchi Examples.

Per versioni diverse si risale ai commit che hanno introdotto il contenuto dello scenario. Una versione viene scelta solo quando le altre la precedono nella storia Git. Commit su altri file e merge che trasportano copie invariate non cambiano questa precedenza; le date non decidono il vincitore.

Modifiche indipendenti producono un conflitto con QA Epic, ID, branch, commit e percorso. Il PST interessato rimane intatto; `generate-all` continua con gli altri PST e termina con codice 1. Il riepilogo viene scritto anche in `GITHUB_STEP_SUMMARY`, quando disponibile. Branch mancanti e storia non interpretabile producono un errore, senza pubblicare un PST parziale.

Markdown e HTML riportano le fonti della versione selezionata. La raccolta considera gli scenari presenti: l'assenza di uno scenario in un branch non viene interpretata come richiesta di cancellarlo dalle altre fonti.

### Autori dell'ultima modifica

Nei PST generati dalle release, ogni scenario riporta **Autori dell’ultima modifica**
in Markdown e HTML. Sono i nomi registrati nel campo autore dei commit che hanno
introdotto la versione selezionata dello scenario. Non vengono riportati indirizzi
email e non vengono effettuate chiamate a GitHub per cercare profili utente.

L'attribuzione usa il confronto del contenuto Gherkin: comprende anche la rimozione
di passi, mentre modifiche ad altri scenari nello stesso file, commit estranei e
merge che conservano il contenuto non cambiano gli autori. Il committer non
sostituisce l'autore. Copie identiche provenienti da modifiche indipendenti possono
riportare più nomi, senza duplicati; non si elencano tutti i contributori storici.

La ricerca segue la storia del percorso del file: una copia o rinomina in un nuovo
percorso può essere attribuita al commit che l'ha introdotta lì. Non viene ricostruita
la continuità tra percorsi rinominati. L'elaborazione di un singolo `.feature`
senza raccolta dal repository non dispone di questa attribuzione.

Il grafo e i metadati degli autori sono caricati insieme; versioni dei file e
risultati storici sono riutilizzati tra gli scenari della stessa QA Epic e dello
stesso repository.
Per i casi senza conflitti, ricavare gli autori richiede comunque una ricerca
storica aggiuntiva: il costo dipende dalla storia dei file coinvolti.

### Approccio di ricerca e aggregazione

Il generatore costruisce indici per ogni coppia QA Epic/repository. Branch
ripetuti per una QA Epic vengono deduplicati prima della raccolta.

1. **Costruzione delle fonti.** Dai work-item delle release attive si ricavano le
   QA Epic e i branch secondo le regole di individuazione. I branch ripetuti
   vengono deduplicati. Ogni QA Epic conserva l'elenco delle fonti delle proprie
   release.
2. **Risoluzione dei branch.** Ogni branch viene risolto a un commit, cercando
   prima il riferimento locale e poi quello di `origin`. Quel commit resta la
   fonte per tutta l'esecuzione, anche se il riferimento cambia successivamente.
3. **Una scansione per snapshot.** Per ogni commit distinto, `git grep` individua
   i file `.feature` contenenti `@work-item:`. I file vengono letti in blocco e
   analizzati per costruire un indice `QA Epic → scenari e percorsi`. I tag sono
   confrontati per intero: `QA-123` non corrisponde a `QA-1234`. Branch diversi
   sullo stesso commit condividono l'indice, mantenendo i propri nomi nelle fonti.
4. **Riuso dei contenuti.** I file sono identificati dal loro oggetto Git (blob).
   Se un contenuto compare in più branch, percorsi o commit, il parsing Gherkin
   viene eseguito una sola volta. Anche le firme usate per confrontare gli scenari
   sono calcolate e conservate in memoria.
5. **Aggregazione per QA Epic.** Si consultano gli indici dei soli branch ammessi
   per quella QA Epic e si raggruppano gli scenari per ID. Copie identiche vengono
   deduplicate. Le regole della sezione precedente risolvono le versioni diverse
   o segnalano il conflitto.
6. **Generazione del PST.** Dopo aver risolto tutti gli scenari della QA Epic, si
   producono il documento e la provenienza delle versioni selezionate. In caso di
   conflitto non si scrive il PST interessato; si prosegue con le altre QA Epic.

Per esempio, una release può associare `QA-123` a `feature/anna` e `QA-456` a
`feature/luca`. Uno scenario con
tag `@work-item:QA-123` trovato su `feature/luca` contribuisce al PST di `QA-123`;
la raccolta di `QA-456` resta indipendente.

### Ricostruzione della storia ed efficienza

La storia viene consultata per risolvere versioni diverse dello stesso ID tra
branch e per attribuire gli autori dell'ultima modifica. Due definizioni diverse
dello stesso ID già presenti nello stesso
branch costituiscono un'ambiguità e vengono segnalate direttamente.

Per i confronti storici, `git log` recupera in blocco il grafo dei commit, i nomi
degli autori e tutti i genitori dei merge. `git cat-file --batch-check` identifica le versioni
dei file e `git cat-file --batch` ne legge i contenuti distinti. Le richieste sono
raggruppate in blocchi; non viene avviato un processo Git per ogni commit.

La risalita delle versioni e i controlli di precedenza usano il grafo in memoria.
Un commit che modifica altro o un merge che conserva lo scenario invariato non
diventa una nuova origine dello scenario. Le parti del grafo già caricate e i
risultati dei confronti vengono riutilizzati durante la raccolta della stessa
QA Epic.

Le cache durano la raccolta di una QA Epic per repository e il checkout completo
rimane necessario.
Il numero di scansioni dipende dagli snapshot di branch distinti, mentre il
parsing dipende dai contenuti distinti effettivamente analizzati. La ricostruzione
storica richiede comunque lavoro e memoria proporzionati alla storia e ai file
coinvolti: l'ottimizzazione riduce i processi Git e le letture ripetute, senza
eliminare la necessità di esaminare i legami tra i commit.

I test verificano i risultati sui merge e sui conflitti, il riuso degli indici
tra branch della stessa QA Epic e il contenimento delle chiamate Git con storie lunghe.

### Output

Per ogni QA Epic viene generato o aggiornato il file:

```text
pst/pst_QA-<numero>.md
pst/pst_QA-<numero>.html  # con --html
```

La cartella `pst/` viene creata se non esiste, salvo uso di `--output-dir`.
In locale i documenti vengono scritti solo se il contenuto cambia, ma vengono comunque
ricostruiti; l'HTML riporta la data dell'esecuzione. Nella Action,
la directory temporanea è nuova a ogni esecuzione. Al momento non si recuperano
artifact precedenti e non si rimuovono automaticamente documenti obsoleti dal repository.

## Strategia futura — da implementare

Questa sezione registra la strategia concordata. La sua implementazione è rinviata;
il workflow attuale continua a produrre esclusivamente artifact scaricabili.

### Esecuzione su develop e PR dei risultati

1. La Action sarà eseguita sul branch di default `develop`, da cui leggerà codice,
   configurazione e release in corso.
2. I branch di lavoro indicati dalle release saranno consultati come fonti degli
   scenari, senza modificarli.
3. Per ogni QA Epic la Action creerà o aggiornerà un branch dedicato ai risultati,
   basato su `develop`, con nome `feature/pst_<qa_epic>`.
4. Su quel branch saranno salvati i PST e il relativo stato, nello stesso commit.
5. Sarà creata o aggiornata una sola PR Draft per QA Epic, con branch di destinazione
   `develop`. I documenti entreranno in `develop` dopo approvazione e merge secondo
   le regole del repository. La Action non approverà né eseguirà il merge.

Esempio di direzione della PR: `feature/pst_QA-123` → `develop`.
Il branch dei risultati è distinto dai branch di lavoro che contengono gli scenari.

### Stato persistente e documenti invariati

Il riferimento persistente sarà Git. Documenti e impronta saranno versionati insieme:

```text
pst/
├── pst_QA-123.md
├── pst_QA-123.html
└── .state/
    └── QA-123.json
```

Il file di stato conserverà l'impronta degli input rilevanti, le informazioni
dell'ultima generazione valida e le impronte dei documenti per verificarne l'integrità.
L'impronta degli input comprenderà gli scenari selezionati, le fonti rilevanti
(branch e percorsi), i work-item associati, la configurazione, i formati richiesti
e la versione del generatore e delle sue dipendenze.

La Action continuerà a cercare gli scenari e risolvere i conflitti prima di decidere
se riutilizzare un PST. Il confronto avverrà con lo stato sul branch della PR già
aperta, se presente; altrimenti con lo stato in `develop`. Questo eviterà di
riproporre documenti già aggiornati ma ancora in attesa di merge.

- Impronta uguale e documenti presenti e integri: riuso del PST, esito `invariato`,
  nessun nuovo commit o nuova PR.
- Input cambiati, stato assente o documenti mancanti/non integri: rigenerazione
  e aggiornamento di documenti e stato nello stesso commit.
- Conflitto: conservazione dell'ultima versione valida, nessun aggiornamento della
  PR interessata e segnalazione nel riepilogo; le altre QA Epic continueranno.

La data nel documento rappresenterà l'ultimo aggiornamento effettivo del PST.
Un commit su file estranei agli scenari non dovrà invalidare il documento:
l'impronta non userà semplicemente l'ultimo commit del branch. Nel PST riutilizzato
resteranno i commit di provenienza della sua generazione; l'esito della nuova
verifica comparirà nel riepilogo dell'esecuzione.

Gli artifact resteranno un download comodo dei risultati, mentre Git conserverà
lo stato necessario al confronto tra esecuzioni.

### Documenti obsoleti

Quando una QA Epic non sarà più attiva o non avrà più scenari associati, la rimozione
dei PST e dello stato già versionati dovrà passare da una PR verso `develop`.
L'eventuale chiusura di una precedente PR sarà limitata alle PR aperte, Draft e
create dal bot. L'eliminazione del relativo branch sarà consentita solo se contiene
esclusivamente modifiche generate automaticamente. Anche questa gestione è rinviata.

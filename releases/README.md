# Release as Code — Manifest delle release

Chiamiamo **Release as Code** l'approccio con cui descriviamo il perimetro e
il piano QA di una release in un manifest YAML versionato nel repository.
Le modifiche al piano passano attraverso pull request e revisione, mantenendo
traccia delle decisioni e fornendo dati strutturati alle automazioni.

Il manifest raccoglie i work item, le esclusioni dal perimetro QA, i collegamenti
alle QA Epic e i dati di pianificazione necessari al Service Delivery Plan (SDP).
È l'input condiviso dei generatori del [processo QA/QE](../tools/README.md):
alimenta già Epic generator e PST generator e predisporrà i dati per il futuro
generatore SDP. Gli stati operativi delle QA Epic restano gestiti in Jira;
gli esiti delle esecuzioni rimangono nei report e nelle RAE.

## Creare un manifest

1. Copiare [template.yaml](template.yaml) in `releases/send/<versione>.yaml`
   oppure `releases/interop/<versione>.yaml`.
2. Sostituire tutti i valori illustrativi: versione, prodotto, trimestre,
   date, issue, titolo, strategie, stima, persone e note.
3. Duplicare il blocco sotto `work-items` per ogni attività QA pianificata.
4. Elencare in `out_of_scope` i work item interamente esclusi dal perimetro QA,
   indicando per ciascuno la motivazione; usare `[]` se non ci sono esclusioni.
5. Aprire una PR per revisionare il manifest. Partire da `draft`, usare
   `planned` per il piano approvato, `in-progress` per progettazione e
   implementazione, `execution` per esecuzione e analisi esiti, `closed`
   per il lavoro QA concluso e consuntivato.

Il nome del file deve corrispondere a `release.version`. Il template originale
deve rimanere `draft`: il generatore PST considera solo le release
`in-progress`. Non usare `template.yaml` come manifest operativo.

## Template da copiare

Copia questo contenuto in `releases/<prodotto>/<versione>.yaml` e sostituisci
i valori di esempio. Lo snippet riproduce [template.yaml](template.yaml),
compresi i commenti di compilazione. Mantenere i due contenuti allineati
quando si modifica il template.

```yaml
# Copiare in releases/<prodotto>/<versione>.yaml e sostituire i valori di esempio.
# Mantenere questo template in draft: non è una release da elaborare.
# Istruzioni: releases/README.md.
# Obbligatorietà: convenzioni del nuovo manifest, non tutte validate dai generatori.
# Nei blocchi facoltativi, i campi obbligatori si applicano solo se il blocco è usato.
# Completare i valori obbligatori prima di portare la release a planned.
# OBBLIGATORIO.
release:
  # Testo: deve coincidere con il nome del file senza .yaml.
  # Per Epic generator serve una Fix Version omonima nel progetto Jira QA.
  # OBBLIGATORIO.
  version: ga26q4.b
  # Valori: draft | planned | in-progress | execution | closed.
  # draft: preparazione; planned: piano approvato;
  # in-progress: progettazione e implementazione dei test;
  # execution: esecuzione dei test e analisi degli esiti;
  # closed: lavoro QA concluso e consuntivato (non implica tutti i test passati).
  # Aggiornare tramite PR. La fase è quella prevalente della release.
  # Epic e PST elaborano solo in-progress. Passando a execution, i PST
  # esistenti rimangono disponibili ma non vengono più aggiornati.
  # execution non avvia automaticamente le suite: questo è uno sviluppo futuro.
  # OBBLIGATORIO.
  status: draft
  # Testo libero: breve descrizione del perimetro della release.
  # OBBLIGATORIO.
  description: Piano di delivery QA della release SEND GA26Q4.B
  # Valori: SEND | INTEROP, coerenti con la cartella send | interop.
  # OBBLIGATORIO.
  product: SEND
  # Formato AAAA-QN, con N = 1, 2, 3 o 4.
  # OBBLIGATORIO.
  quarter: 2026-Q4

  # Date previste di rilascio negli ambienti, non di consegna del lavoro QA.
  # Chiavi: dev, test, qa, uat, prod. Usare qa al posto di test se applicabile.
  # Valori: "AAAA-MM-GG" oppure vuoto se da definire.
  # Omettere gli ambienti non applicabili.
  # FACOLTATIVO.
  delivery:
    # FACOLTATIVO.
    dev: "2026-11-06"
    # FACOLTATIVO.
    test: "2026-11-16"
    # FACOLTATIVO.
    uat: "2026-12-01"
    # FACOLTATIVO.
    prod: "2026-12-14"

  # OBBLIGATORIO.
  work-items:
    # Chiave Jira del work item di prodotto (es. PN-12345 o PIN-12345).
    # OBBLIGATORIO.
    - id: PN-20853
      # Intero positivo stabile. La coppia (id, sequence) deve essere unica.
      # Per più attività dello stesso id usare sequenze diverse; non rinumerare.
      # OBBLIGATORIO.
      sequence: 1
      # Testo libero; se vuoto, Epic generator recupera il summary da Jira.
      # FACOLTATIVO.
      title: Recupero eventi di pagamento da piattaforma pagoPA

      # Valori: pn-b2b-client | qe_suit (repository degli scenari QA).
      # OBBLIGATORIO.
      repo: pn-b2b-client
      # Compilati dalla PR di epic_generator, oppure con riferimenti esistenti.
      # Chiave Jira QA-<numero> oppure vuoto; non rimuovere il campo.
      # La coppia (id, qa_epic) valorizzata deve essere unica.
      # PRESENZA OBBLIGATORIA; valore vuoto ammesso prima della generazione.
      qa_epic:
      # Nome del branch QA esistente oppure vuoto; non rimuovere il campo.
      # PRESENZA OBBLIGATORIA; valore vuoto ammesso prima della generazione.
      branch:
      # Non aggiungere status al work item: lo stato QA è quello della QA Epic.
      # Il futuro SDP lo leggerà da Jira; senza Epic mostrerà "QA Epic da creare".

      # Lista con uno o più dei seguenti valori:
      # NRT TA STANDARD | TA VERTICALE | NRT TA AD-HOC | NRT TM STANDARD |
      # TM VERTICALE | TM ESPLORATIVO | NRT TM AD-HOC | DESIGN ONLY |
      # TEST DI CARICO | TA FE VERTICALE.
      # Le esclusioni complete vanno in out_of_scope, non nelle strategie.
      # OBBLIGATORIO.
      strategies:
        - TA VERTICALE
        - TEST DI CARICO

      # Giorni uomo di analisi/progettazione e implementazione.
      # Numero positivo, anche frazionario (es. 2.5), oppure vuoto se non stimato.
      # Esclude esecuzione e analisi esiti. Non usare 0 o taglie S/M/L.
      # FACOLTATIVO.
      size: 7

      # Lista di persone; usare [] se non ancora assegnate.
      # FACOLTATIVO.
      resources:
        # name: nome della persona; role: TA (automatici) | TM (manuali).
        # OBBLIGATORIO.
        - name: Mario Rossi
          # OBBLIGATORIO.
          role: TA
        # OBBLIGATORIO.
        - name: Giuseppe Bianchi
          # OBBLIGATORIO.
          role: TM

      # Valori: Critical | High | Medium | Low. Priorità della pianificazione QA.
      # OBBLIGATORIO.
      priority: Critical

      # Testo libero oppure vuoto; includere anche eventuali esclusioni parziali
      # e complessità della fase di esecuzione. Usare > per testi su più righe.
      # FACOLTATIVO.
      notes: >
        Verificare anche la gestione degli eventi duplicati.
      # Testo libero sui rischi noti oppure vuoto.
      # FACOLTATIVO.
      risks: >
        Possibile indisponibilità di dati rappresentativi.
      # Lista di descrizioni testuali dei prerequisiti; usare [] se assenti.
      # FACOLTATIVO.
      dependencies:
        - Disponibilità delle API nell'ambiente di test.
        - Predisposizione dei dati per le prove di carico.

  # Work item interamente esclusi dal perimetro QA; usare [] se non presenti.
  # Ogni id è unico e non può comparire anche in work-items.
  # FACOLTATIVO.
  out_of_scope:
    # id: chiave Jira obbligatoria; reason: motivazione testuale non vuota.
    # OBBLIGATORIO.
    - id: PN-21118
      # OBBLIGATORIO.
      reason: Attività di monitoraggio operativo senza intervento QA previsto.
    # OBBLIGATORIO.
    - id: PN-20023
      # OBBLIGATORIO.
      reason: Aggiornamento della documentazione tecnica.
```

## Campi obbligatori e facoltativi

Le indicazioni nel template definiscono la convenzione di compilazione del
nuovo manifest. In `draft` i dati possono essere ancora in preparazione;
completare quelli obbligatori prima di passare a `planned`. Non tutti questi
vincoli sono già verificati automaticamente dai generatori.

| Ambito | Obbligatori con valore | Facoltativi |
| --- | --- | --- |
| Release | `version`, `status`, `description`, `product`, `quarter`, `work-items` | `delivery`, `out_of_scope` |
| Ogni work item QA | `id`, `sequence`, `repo`, `strategies` (almeno una), `priority` | `title`, `size`, `resources`, `notes`, `risks`, `dependencies` |
| Ogni persona in `resources` | `name`, `role` | — |
| Ogni esclusione in `out_of_scope` | `id`, `reason` non vuota | — |

**`qa_epic` e `branch` devono essere presenti in ogni work item, ma possono
avere valore vuoto** prima della generazione o dell'associazione dei riferimenti.
`title` è facoltativo perché Epic generator può recuperarlo da Jira.
Le date di `delivery` sono facoltative e possono restare vuote se da definire.
Per liste facoltative senza elementi usare `[]` oppure omettere il campo.

`work-items` deve essere presente; può essere `[]` se la release contiene
soltanto esclusioni, ma quel manifest non può essere elaborato da Epic generator,
che richiede almeno un work item. Ogni elemento eventualmente presente in una
lista facoltativa deve comunque avere tutti i suoi campi obbligatori.

## Dati della release

| Campo | Come compilarlo |
| --- | --- |
| `version` | Identificativo della release, ad esempio `ga26q4.b` o `2.26.0`. Per creare le QA Epic deve esistere una Fix Version omonima nel progetto Jira QA. |
| `status` | `draft`, `planned`, `in-progress`, `execution`, `closed`. Solo `in-progress` abilita l'elaborazione dei generatori attuali. |
| `description` | Breve descrizione del perimetro della release. |
| `product` | `SEND` per la cartella `send`, `INTEROP` per `interop`. Mantenerlo coerente con il percorso: Epic generator ricava oggi il prodotto dalla cartella. |
| `quarter` | Trimestre di riferimento dell'SDP nel formato `AAAA-QN`, ad esempio `2026-Q4`. |
| `delivery` | Date previste di rilascio per ambiente, in formato `"AAAA-MM-GG"`. Usare `dev`, `test`, `uat`, `prod`; per INTEROP usare `qa` al posto di `test` se questo è il nome dell'ambiente. Omettere gli ambienti non applicabili e lasciare vuote le date ancora da definire. |

Le date in `delivery` sono comuni alla release. Il manifest non prevede
`roadmap_url` né date di consegna del design, dell'implementazione o di fine
esecuzione del lavoro QA.

Lo stato della release indica la fase prevalente e si aggiorna tramite PR.
`closed` rappresenta la chiusura del lavoro QA, non garantisce che tutti i test
siano passati o che il rilascio in produzione sia avvenuto. Anomalie e rischi
residui devono essere documentati.

Il generatore PST elabora soltanto le release `in-progress`. Passando a
`execution`, i PST esistenti rimangono disponibili ma non vengono più
aggiornati: questo è il comportamento previsto per la fase di esecuzione.
Lo stato `execution` non avvia automaticamente le suite; eventuali meccanismi
di avvio saranno implementati in futuro.

## Dati di ciascun work item

| Campo | Come compilarlo |
| --- | --- |
| `id` | Chiave Jira del work item di prodotto da validare. |
| `sequence` | Intero positivo stabile. Se lo stesso `id` richiede più attività QA, usare sequenze diverse. Non rinumerarle quando cambia l'ordine delle righe. |
| `title` | Descrizione dell'attività. Compilarla per rendere leggibile l'SDP; Epic generator può recuperare il summary Jira quando il campo è assente o nullo. |
| `repo` | Repository degli scenari: `pn-b2b-client` oppure `qe_suit`. |
| `qa_epic` | Chiave della QA Epic, se già esistente; altrimenti lasciare il campo vuoto, senza rimuoverlo. |
| `branch` | Branch QA degli scenari, se già esistente; altrimenti lasciare il campo vuoto, senza rimuoverlo. |
| `strategies` | Lista delle strategie di test previste; vedere il vocabolario sotto. |
| `size` | Stima numerica positiva in giorni uomo, anche frazionaria (`2.5`). Comprende analisi/progettazione e implementazione, esclude esecuzione e analisi esiti. Non usare taglie come `S` o `M`. Se non stimata, lasciare vuoto. |
| `resources` | Lista delle persone pianificate, ognuna con `name` e `role`: `TA` per test automatici, `TM` per test manuali. Se non assegnate, usare `[]`. |
| `priority` | Priorità QA: `Critical`, `High`, `Medium`, `Low`. È un dato di pianificazione, non una sincronizzazione automatica della priorità Jira. |
| `notes` | Note operative, inclusa eventuale complessità della fase di esecuzione. |
| `risks` | Rischi noti per il lavoro QA. |
| `dependencies` | Lista di prerequisiti o dipendenze. Usare `[]` quando non presenti. |

Per i testi lunghi usare `>` come nel template. Per `notes` e `risks` senza
contenuto lasciare il valore vuoto. I campi mancanti non devono essere
interpretati come attività completate o stime pari a zero.

Non inserire `status` nei work item: la fonte dello stato del lavoro QA è la
QA Epic indicata da `qa_epic`. Il futuro generatore SDP leggerà lo stato da
Jira a ogni generazione o aggiornamento; senza QA Epic mostrerà
“QA Epic da creare”. Lo stato della issue di prodotto (`id`) è distinto.
L'SDP rifletterà l'ultima sincronizzazione, non uno stato in tempo reale,
e dovrà poter essere aggiornato anche in `execution` e `closed`.

La coppia `(id, sequence)` deve essere unica nella release. Anche la coppia
`(id, qa_epic)`, quando la QA Epic è valorizzata, deve essere unica.
`sequence` identifica stabilmente l'attività: non rappresenta una priorità.

### Strategie

Usare le denominazioni della legenda SDP:

- `NRT TA STANDARD`
- `TA VERTICALE`
- `NRT TA AD-HOC`
- `NRT TM STANDARD`
- `TM VERTICALE`
- `TM ESPLORATIVO`
- `NRT TM AD-HOC`
- `DESIGN ONLY`
- `TEST DI CARICO`
- `TA FE VERTICALE`

Una stessa attività può richiedere più strategie. Le esclusioni complete dal
perimetro QA si dichiarano in `out_of_scope`, non tra le strategie di test.

## Work item esclusi dal perimetro QA

La sezione `release.out_of_scope` è allo stesso livello di `work-items`.
Ogni voce contiene soltanto:

| Campo | Come compilarlo |
| --- | --- |
| `id` | Chiave Jira del work item di prodotto escluso. Obbligatoria. |
| `reason` | Motivazione non vuota dell'esclusione dal perimetro QA. Obbligatoria. |

Ogni `id` deve comparire una sola volta in `out_of_scope` e non può essere
presente anche in `work-items`. L'esclusione riguarda l'intero work item:
eventuali esclusioni parziali vanno descritte in `notes` dell'attività QA.
Non occorrono `sequence`, stime, risorse, QA Epic o branch per le esclusioni.
Quando non ci sono esclusioni, usare `out_of_scope: []`.

Epic generator e PST elaborano le voci in `work-items`, non quelle in
`out_of_scope`. Epic generator richiede comunque almeno un work item da
elaborare: un manifest con sole esclusioni non è un input valido per quel
generatore. Il futuro generatore SDP presenterà le esclusioni in una tabella
separata con le colonne **Work item** e **Motivo dell'esclusione**.
Queste regole di compilazione di `out_of_scope` sono documentate qui;
la loro validazione automatica resta da implementare.

## Collegamento con i generatori

| Generatore | Uso del manifest |
| --- | --- |
| [Epic generator](../tools/generators/epic/README.md) | Prepara QA Epic e branch; la Action propone una PR che valorizza `qa_epic` e `branch`. Usa già `id`, `sequence`, `title`, `repo`, `size` e i dati della release previsti dalla sua validazione. |
| [PST generator](../tools/generators/pst/README.md) | Legge le release attive e gli scenari nei branch QA per produrre il PST per QA Epic. |
| SDP generator, da realizzare | Userà i dati di release e pianificazione per generare la tabella del Service Delivery Plan. |

Questa modifica introduce il template e le convenzioni di compilazione;
non implementa il generatore SDP né nuove validazioni o sincronizzazioni Jira.
I vocabolari di pianificazione sopra sono convenzioni documentate, non ancora
vincoli applicati automaticamente. La T-Shirt size potrà essere derivata da
`size`: la conversione, inclusi i valori frazionari, resta da implementare.

Gli esiti di esecuzione rimangono nei report e nelle RAE. Il collegamento
formale RAE–release–QA Epic resta da definire; non copiare gli esiti nel manifest.

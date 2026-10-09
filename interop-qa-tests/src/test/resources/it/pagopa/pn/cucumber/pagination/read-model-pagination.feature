@pin7134 @no-parallel
Feature: PIN-7134 - Conteggio e paginazione del Read Model PostgreSQL
  La paginazione conserva il totale dei risultati filtrati anche oltre l'ultima pagina.
  La preparazione usa gli step API esistenti; il polling attende i dati senza verificare il totale.
  Gli step con prefisso PIN-7134 sono implementati in ReadModelPaginationSteps.java.
  Scope: Agreement, Catalogo e Deleghe.

  @agreement @happy-path
  Scenario Outline: Agreement - pagine complete, parziali e oltre il totale
    Given "PA1" ha già creato e pubblicato 3 e-services
    And "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "agreement-fruitore" espone 3 risultati con i filtri:
      | states | ACTIVE |
    When PIN-7134 l'utente interroga "agreement-fruitore" con offset <offset> e limit <limit> e i filtri:
      | states | ACTIVE |
    Then PIN-7134 la risposta contiene <risultati> risultati distinti e totalCount 3 con offset <offset> e limit <limit>

    Examples:
      | offset | limit | risultati |
      | 0      | 2     | 2         |
      | 2      | 2     | 1         |
      | 3      | 2     | 0         |
      | 10     | 2     | 0         |

  @agreement @happy-path
  Scenario: Agreement - riuso del listing consolidato con verifica reale del totale
    Given "PA1" ha già creato e pubblicato 3 e-services
    And "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "agreement-fruitore" espone 3 risultati con i filtri:
      | showOnlyUpgradeable | false |
    When l'utente richiede una operazione di listing limitata alle prime 2 richieste di fruizione
    Then PIN-7134 la risposta contiene 2 risultati distinti e totalCount 3 con offset 0 e limit 2

  @agreement @happy-path
  Scenario: Agreement - filtri combinati eservice, fruitore e stato con controllo negativo
    Given "PA1" ha già creato e pubblicato 3 e-services
    And "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    And "PA2" ha una richiesta di fruizione in stato "DRAFT" per ognuno di quegli e-services
    And l'utente è un "admin" di "PA1"
    And PIN-7134 il read model "agreement-erogatore" espone 3 risultati con i filtri:
      | consumer | PA2   |
      | states   | DRAFT |
    And PIN-7134 il read model "agreement-erogatore" espone 3 risultati con i filtri:
      | consumer | GSP    |
      | states   | ACTIVE |
    When l'utente richiede una operazione di listing delle richieste di fruizione di "GSP" che sono in stato "ACTIVE" e "SUSPENDED"
    Then PIN-7134 la risposta contiene 3 risultati distinti e totalCount 3 con offset 0 e limit 12
    When PIN-7134 l'utente interroga "agreement-erogatore" con offset 0 e limit 2 e i filtri:
      | consumer | GSP   |
      | states   | DRAFT |
    Then PIN-7134 la risposta contiene 0 risultati distinti e totalCount 0 con offset 0 e limit 2

  @agreement @happy-path
  Scenario: Agreement - nessuna richiesta per e-service nuovi e gia proiettati
    Given "PA1" ha già creato e pubblicato 2 e-services
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "catalogo" espone 2 risultati con i filtri:
      | producer | PA1 |
    When l'utente richiede una operazione di listing delle richieste di fruizione
    Then PIN-7134 la risposta contiene 0 risultati distinti e totalCount 0 con offset 0 e limit 12

  @agreement @happy-path
  Scenario Outline: Agreement aggiornabili - totale calcolato dopo il filtro in memoria
    Given "PA1" ha già creato e pubblicato 3 e-services
    And "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    And "PA1" ha già pubblicato una nuova versione per 2 di questi e-service
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "agreement-fruitore" espone 2 risultati con i filtri:
      | states              | ACTIVE |
      | showOnlyUpgradeable | true   |
    When PIN-7134 l'utente interroga "agreement-fruitore" con offset <offset> e limit <limit> e i filtri:
      | states              | ACTIVE |
      | showOnlyUpgradeable | true   |
    Then PIN-7134 la risposta contiene <risultati> risultati distinti e totalCount 2 con offset <offset> e limit <limit>

    Examples:
      | offset | limit | risultati |
      | 0      | 1     | 1         |
      | 1      | 1     | 1         |
      | 2      | 1     | 0         |
      | 10     | 2     | 0         |

  @agreement @happy-path
  Scenario: Agreement - nessun aggiornabile nonostante gli agreement attivi
    Given "PA1" ha già creato e pubblicato 2 e-services
    And "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "agreement-fruitore" espone 2 risultati con i filtri:
      | states | ACTIVE |
    When l'utente richiede una operazione di listing delle richieste di fruizione aggiornabili
    Then PIN-7134 la risposta contiene 0 risultati distinti e totalCount 0 con offset 0 e limit 12

  @catalog @happy-path
  Scenario Outline: Catalogo - ricerca e stati con esclusione dei draft
    Given "PA1" ha già creato 3 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "catalogo" espone 3 risultati con i filtri:
      | producer | PA1 |
    When PIN-7134 l'utente interroga "catalogo" con offset <offset> e limit <limit> e i filtri:
      | producer | PA1 |
    Then PIN-7134 la risposta contiene <risultati> risultati distinti e totalCount 3 con offset <offset> e limit <limit>

    Examples:
      | offset | limit | risultati |
      | 0      | 2     | 2         |
      | 2      | 2     | 1         |
      | 3      | 2     | 0         |
      | 10     | 2     | 0         |

  @catalog @happy-path
  Scenario: Catalogo - keyword, interfaccia e ricerca senza risultati
    Given "PA1" ha già creato e pubblicato un e-service contenente la keyword "Anagrafe Test QA"
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "catalogo" espone 1 risultati con i filtri:
      | keyword  | Anagrafe Test QA |
      | producer | PA1              |
    When l'utente richiede una operazione di listing sul catalogo filtrando per la keyword "Anagrafe Test QA"
    Then PIN-7134 la risposta contiene 1 risultati distinti e totalCount 1 con offset 0 e limit 12
    When l'utente richiede una operazione di listing sul catalogo filtrando per la keyword "PIN7134-NESSUNA-CORRISPONDENZA"
    Then PIN-7134 la risposta contiene 0 risultati distinti e totalCount 0 con offset 0 e limit 12

  @catalog @happy-path
  Scenario: Catalogo - piu documenti non moltiplicano gli e-service conteggiati
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e 3 documenti già caricati
    And l'utente è un "admin" di "GSP"
    And PIN-7134 il read model "catalogo" espone 1 risultati con i filtri:
      | keyword  | corrente |
      | producer | PA1      |
    When PIN-7134 l'utente interroga "catalogo" con offset 0 e limit 1 e i filtri:
      | keyword  | corrente |
      | producer | PA1      |
    Then PIN-7134 la risposta contiene 1 risultati distinti e totalCount 1 con offset 0 e limit 1
    When PIN-7134 l'utente interroga "catalogo" con offset 1 e limit 1 e i filtri:
      | keyword  | corrente |
      | producer | PA1      |
    Then PIN-7134 la risposta contiene 0 risultati distinti e totalCount 1 con offset 1 e limit 1

  @capofila @happy-path
  Scenario Outline: Deleghe in erogazione - filtri combinati e pagina vuota con totale positivo
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And PIN-7134 il read model "deleghe" espone 1 risultati con i filtri:
      | states | ACTIVE |
    When PIN-7134 l'utente interroga "deleghe" con offset <offset> e limit 1 e i filtri:
      | states | ACTIVE |
    Then PIN-7134 la risposta contiene <risultati> risultati distinti e totalCount 1 con offset <offset> e limit 1
    When PIN-7134 l'utente interroga "deleghe" con offset 0 e limit 1 e i filtri:
      | states | REJECTED |
    Then PIN-7134 la risposta contiene 0 risultati distinti e totalCount 0 con offset 0 e limit 1

    Examples:
      | offset | risultati |
      | 0      | 1         |
      | 1      | 0         |
      | 10     | 0         |

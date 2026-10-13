@agreement
Feature: Listing e-service con richieste di fruizione attive lato erogatore
  Tutti gli utenti autorizzati di enti PA e GSP possono ottenere la lista degli e-service con la quale hanno almeno una richiesta di fruizione attiva

  @happy-path
  @agreement_e_service_producer_listing1
  Scenario Outline: Restituisce gli e-service per i quali l’erogatore ha almeno una richiesta di fruizione in stato NON DRAFT da parte dei fruitori
    Given l'utente è un "<ruolo>" di "<ente>"
    And "<ente>" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<ente>" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente è un "<ruolo>" di "<ente>"
    And l'utente recupera la lista di e-service in erogazione e contiene il nuovo e-service creato
    Then l'utente recupera la lista di e-service in erogazione e contiene il vecchio e-service creato con filtro:
      | agreementState | ACTIVE |
    And l'utente recupera la lista di e-service in erogazione e non contiene il nuovo e-service creato con filtro:
      | agreementState | ACTIVE |

    Examples: 
      | ente | ruolo        |
      | GSP  | admin        |
      | GSP  | api          |
      | GSP  | security     |
      | GSP  | support      |
      | GSP  | api,security |
      | PA1  | admin        |
      | PA1  | api          |
      | PA1  | security     |
      | PA1  | support      |
      | PA1  | api,security |

  @happy-path
  @agreement_e_service_producer_listing2
  Scenario: A fronte di 5 e-service, restituisce solo i primi 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 5 e-services
    Given "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    When l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva limitata ai primi 3 e-services
    Then si ottiene status code 200 e la lista di 3 e-service

  @happy-path
  @agreement_e_service_producer_listing3
  Scenario: A fronte di 5 e-service in db e una richiesta di offset 2, restituisce solo 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 5 e-services
    Given "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    When l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva con offset 2
    Then si ottiene status code 200 e la lista di 3 e-service

  @happy-path
  @agreement_e_service_producer_listing4
  Scenario: Restituisce gli e-service il cui nome contiene la keyword "test" all'interno del nome, con ricerca case insensitive
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 2 e-services
    Given "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    Given "PA1" ha già creato e pubblicato un e-service contenente la keyword "test"
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva filtrando per la keyword "Test"
    Then si ottiene status code 200 e la lista di 1 e-service

  @happy-path
  @agreement_e_service_producer_listing5
  Scenario: Restituisce un insieme vuoto di e-service per una ricerca che non porta risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 2 e-services
    Given "GSP" ha un agreement attivo per ciascun e-service di "PA1"
    When l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva filtrando per la keyword "unknown"
    Then si ottiene status code 200 e la lista di 0 e-service

  @happy-path
  @agreement_e_service_producer_listing6
  Scenario Outline: Restituisce gli e-service con ciascun possibile stato nel filtro per l'agreement tranne stato requisiti mancanti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con prefisso "listing6" in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "GSP" ha una richiesta di fruizione in stato "<agreementState1>" per quell'e-service
    And "PA1" ha già creato un e-service con prefisso "listing6" in stato "PUBLISHED" con approvazione "<approvalPolicy2>"
    When "PA2" ha una richiesta di fruizione in stato "<agreementState2>" per quell'e-service
    And l'utente è un "admin" di "PA1"
    Then l'utente recupera la lista di e-service in erogazione e contiene il nuovo e-service creato con filtro:
      | agreementState | <agreementState2> |
      | namePart       | listing6          |
    And l'utente recupera la lista di e-service in erogazione e non contiene il vecchio e-service creato con filtro:
      | agreementState | <agreementState2> |
      | namePart       | listing6          |

    Examples:
      | agreementState1 | agreementState2 | approvalPolicy2 |
      | DRAFT           | ACTIVE          | AUTOMATIC       |
      | ACTIVE          | DRAFT           | AUTOMATIC       |
      | DRAFT           | PENDING         | MANUAL          |
      | ACTIVE          | SUSPENDED       | AUTOMATIC       |
      | SUSPENDED       | ARCHIVED        | AUTOMATIC       |
      | ACTIVE          | REJECTED        | MANUAL          |

  @happy-path
  @agreement_e_service_producer_listing7
  Scenario: Restituisce gli e-service con lo stato requisiti mancanti nel filtro per l'agreement
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA2" gli attributi certificati precedentemente creati
    And "PA1" ha già creato un e-service con prefisso "listing7" in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente revoca l'attributo precedentemente creato e assegnato
    And "PA1" ha già creato un e-service con prefisso "listing7" in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    Then l'utente recupera la lista di e-service in erogazione e contiene il vecchio e-service creato con filtro:
      | agreementState | MISSING_CERTIFIED_ATTRIBUTES |
      | namePart       | listing6 |
    And l'utente recupera la lista di e-service in erogazione e non contiene il nuovo e-service creato con filtro:
      | agreementState | MISSING_CERTIFIED_ATTRIBUTES |
      | namePart       | listing6 |

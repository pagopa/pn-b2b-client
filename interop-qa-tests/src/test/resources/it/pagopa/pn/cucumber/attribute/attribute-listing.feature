@attribute @wait_for_fix @IMN-347
Feature: Listing attributi
  Tutti gli utenti autenticati possono leggere la lista degli attributi

  # NOTE 16/04/2025 riproduzione dello scenario come presente nel repo interop-qa-tests
  @happy-path
  @nrt-minimal
  @attribute_listing1
  Scenario Outline: [ATTRIBUTE_LISTING_1] Restituisce gli attributi disponibili
    Given l'utente è un "<ruolo>" di "<ente>"
    Given PA1 ha già creato 5 attributi DECLARED
    When l'utente richiede una operazione di listing degli attributi
    Then si ottiene status code 200 e la lista di 5 attributi
    Examples:
      | ente    | ruolo        |
      | GSP     | admin        |
      | GSP     | api          |
      | GSP     | security     |
      | GSP     | support      |
      | GSP     | api,security |
      | PA1     | admin        |
      | PA1     | api          |
      | PA1     | security     |
      | PA1     | support      |
      | PA1     | api,security |
      | Privato | admin        |
      | Privato | api          |
      | Privato | security     |
      | Privato | support      |
      | Privato | api,security |

    @nuovi-operatori-update
    Examples:
      | ente    | ruolo        |
      | GSP     | viewer       |
      | Privato | viewer       |

  # NOTE 16/04/2025 adattamento dello scenario così che risultati funzionante nell'attuale
  # ambiente di QA
  #@attribute_listing1
  #Scenario Outline: Restituisce gli attributi disponibili
    # 16/04/2025 A ruoli diversi da ADMIN e API non è concessa la creazione di attributi
  #  Given l'utente è un "admin" di "<ente>"
  #  And PA1 ha già creato 5 attributi DECLARED

  #  When l'utente è un "<ruolo>" di "<ente>"
  #  And l'utente richiede una operazione di listing degli attributi
  #  Then si ottiene status code 200 e la lista di 5 attributi
  #  Examples:
  #    | ente    | ruolo        |
  #    | GSP     | admin        |
  #    | GSP     | api          |
  #    | GSP     | security     |
  #    | GSP     | support      |
  #    | GSP     | api,security |
  #    | PA1     | admin        |
  #    | PA1     | api          |
  #    | PA1     | security     |
  #    | PA1     | support      |
  #    | PA1     | api,security |
    # 16/04/2025 "Privato" non è abilitato, errore 403: "Requester origin IVASS is not allowed"

  @happy-path
  @nrt-minimal
  @attribute_listing2
  Scenario: [ATTRIBUTE_LISTING_2] A fronte di 5 attributi in db e una richiesta di 3 attributi, restituisce solo i primi 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given PA1 ha già creato 5 attributi DECLARED
    When l'utente richiede una operazione di listing degli attributi limitata ai primi 3 attributi
    Then si ottiene status code 200 e la lista di 3 attributi

  @happy-path
  @nrt-minimal
  @attribute_listing3
  Scenario: [ATTRIBUTE_LISTING_3] A fronte di 5 attributi in db e un offset di 2, restituisce solo 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given PA1 ha già creato 5 attributi DECLARED
    When l'utente richiede una operazione di listing degli attributi con offset 2
    Then si ottiene status code 200 e la lista di 3 attributi

  @happy-path
  @nrt-minimal
  @attribute_listing4
  Scenario: [ATTRIBUTE_LISTING_4] A fronte di 5 attributi in db dei quali 3 certificati, 2 verificati e 1 dichiarato, restituisce solo i 3 certificati e i 2 verificati
    Given l'utente è un "admin" di "PA2"
    Given PA2 ha già creato 3 attributi CERTIFIED
    Given PA2 ha già creato 2 attributi VERIFIED
    Given PA2 ha già creato 1 attributo DECLARED
    When l'utente richiede una operatione di listing degli attributi filtrando per tipo "certificato" e "verificato"
    Then si ottiene status code 200 e la lista di 5 attributi

  @happy-path
  @nrt-minimal
  @attribute_listing5
  Scenario: [ATTRIBUTE_LISTING_5] Restituisce gli attributi in db che contengono la keyword "test" all'interno del nome con ricerca case insensitive
    Given l'utente è un "admin" di "PA1"
    Given PA1 ha già creato 3 attributi DECLARED
    Given PA1 ha già creato un attributo DECLARED con nome che contiene "test"
    When l'utente richiede una operazione di listing degli attributi filtrando per keyword "test" all'interno del nome
    Then si ottiene status code 200 e la lista di 1 attributo

  @happy-path
  @nrt-minimal
  @attribute_listing6
  Scenario: [ATTRIBUTE_LISTING_6] Restituisce un insieme vuoto di attributi per una ricerca che non porta risultati
    Given l'utente è un "admin" di "PA1"
    Given PA1 ha già creato 3 attributi DECLARED
    When l'utente richiede una operazione di listing degli attributi filtrando per keyword "unknown" all'interno del nome
    Then si ottiene status code 200 e la lista di 0 attributi

  # https://pagopaspa.slack.com/archives/C0AQVEPGQ8L/p1782135024169989
  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOff
  Scenario: [CERT_DISCRETE_ATTR_NOT_AVAILABLE] Gli attributi certificati discreti non sono disponibili quando il feature flag è false.
    Given l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    Then l'utente "PA1" non possiede nessun attributo certificato discreto
    And si ottiene status code 200 e la lista di 0 attributi

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_AVAILABLE] Gli attributi certificati discreti sono disponibili quando il feature flag è true.
    Given l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    Then l'utente "PA1" possiede almeno un attributo certificato discreto
    And si ottiene response status code 200

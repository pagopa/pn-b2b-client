@tenants
Feature: Listing degli aderenti
  Tutti gli utenti autenticati possono leggere la lista dei aderenti

  @nrt-minimal
  @tenants_listing1
  Scenario Outline: [TENANTS_LISTING_1] A fronte di 3 aderenti in db, restituisce solo i primi 2 risultati (scopo del test è verificare il corretto funzionamento del parametro limit)
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente richiede una operazione di listing degli aderenti limitata a 2
    Then si ottiene status code <statusCode> e la lista di 2 tenant

    Examples:
      | ente    | ruolo        | statusCode |
      | GSP     | admin        |        200 |
      | GSP     | api          |        200 |
      | GSP     | security     |        200 |
      | GSP     | api,security |        200 |
      | GSP     | support      |        200 |
      | PA1     | api          |        200 |
      | PA1     | admin        |        200 |
      | PA1     | security     |        200 |
      | PA1     | support      |        200 |
      | PA1     | api,security |        200 |
      | Privato | admin        |        200 |
      | Privato | api          |        200 |
      | Privato | security     |        200 |
      | Privato | support      |        200 |
      | Privato | api,security |        200 |

  @nrt-minimal
  @tenants_listing2
  Scenario: [TENANTS_LISTING_2] Restituisce gli aderenti che contengono la keyword "comune di Milano" all'interno del nome, con ricerca case insensitive (scopo del test è verificare che funzioni il filtro name)
    Given l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di listing degli aderenti filtrando per la keyword "comune di Milano"
    Then si ottiene status code 200 e la lista di 1 tenant

  @nrt-minimal
  @tenants_listing3
  Scenario: [TENANTS_LISTING_3] Restituisce un insieme vuoto di aderenti per una ricerca che non porta risultati (scopo del test è verificare che, se non ci sono risultati, il server risponda con 200 e array vuoto e non con un errore)
    Given l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di listing degli aderenti filtrando per la keyword "unknown"
    Then si ottiene status code 200 e la lista di 0 tenant

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANTS_CERTIFIED_DISCRETE_ATTRIBUTE_LISTING_1a] Verifica che, dopo l'assegnazione effettuata da un ente certificatore,
  l'attributo discreto certificato sia incluso nell'elenco degli attributi certificati assegnati dallo stesso ente.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    When l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    And si ottiene lo status code 200
    Then l'utente è un "admin" di "GSP"
    And l'utente richiede una operazione di listing dei suoi attributi certificati discreti e l'attributo assegnato è presente

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANTS_CERTIFIED_DISCRETE_ATTRIBUTE_LISTING_1b] Verifica che un ente non certificatore non disponga delle
  autorizzazioni necessarie per elencare gli attributi del richiedente tramite API /tenants/attributes/certified.
    Given l'utente è un "admin" di "Privato"
    When l'utente richiede una operazione di listing dei suoi attributi certificati discreti
    Then si ottiene lo status code 403

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANTS_CERTIFIED_DISCRETE_ATTRIBUTE_LISTING_2] Verifica che, a seguito dell'assegnazione da parte di un ente
  certificatore, l'attributo certificato discreto risulti presente nella lista degli attributi del tenant.
    Given l'utente è un "admin" di "GSP"
    When GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    Then l'utente richiede una operazione di listing di tutti gli attributi certificati discreti e l'attributo creato è presente

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [TENANTS_CERTIFIED_DISCRETE_ATTRIBUTE_LISTING_3] Verifica che l'assegnazione dell'attributo certificato discreto
  vada a buon fine e che l'attributo risulti correttamente creato.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    When l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    And si ottiene lo status code 200
    Then l'utente è un "<ruolo>" di "<ente>"
    And l'attributo certificato discreto è stato creato correttamente
    And si ottiene lo status code <risultato>

    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | admin        | 200       |
      | GSP     | security     | 200       |
      | GSP     | api,security | 200       |
      | GSP     | reviewer     | 200       |
      | GSP     | viewer       | 200       |
      | Privato | admin        | 200       |
      | Privato | security     | 200       |
      | Privato | api,security | 200       |
      | Privato | reviewer     | 200       |
      | Privato | viewer       | 200       |
      | PA1     | admin        | 200       |
      | PA1     | security     | 200       |
      | PA2     | admin        | 200       |
      | PA2     | security     | 200       |

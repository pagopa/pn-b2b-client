@attribute
Feature: Lettura singolo attributo
  Tutti gli utenti autenticati possono leggere le informazioni di un singolo attributo

  # NOTE 16/04/2025 riproduzione dello scenario come presente nel repo interop-qa-tests
  @happy-path
  @nrt-minimal
  @attribute_read1
  Scenario Outline: [ATTRIBUTE_READ_1] Alla richiesta di un attributo presente in DB, restituisce il risultato
    Given l'utente è un "<ruolo>" di "<ente>"
    Given PA1 ha già creato 1 attributo DECLARED
    When l'utente richiede una operazione di lettura di quel attributo
    Then si ottiene status code 200
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
  #@attribute_read1
  #Scenario Outline: Alla richiesta di un attributo presente in DB, restituisce il risultato
    # 16/04/2025 A ruoli diversi da ADMIN e API non è concessa la creazione di attributi
  #  Given l'utente è un "admin" di "<ente>"
  #  And PA1 ha già creato 1 attributo DECLARED

  #  When l'utente è un "<ruolo>" di "<ente>"
  #  And l'utente richiede una operazione di lettura di quel attributo
  #  Then si ottiene status code 200
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


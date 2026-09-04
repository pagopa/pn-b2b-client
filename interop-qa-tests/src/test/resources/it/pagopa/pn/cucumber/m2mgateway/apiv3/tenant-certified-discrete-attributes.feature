Feature: Gestione di assegnazione degli attributi certificati discreti ai tenant attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_1] L'elenco di un attributo certificato discreto associato ad un ente va a buon fine.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then l'utente richiede l'elenco degli attributi certificati discreti di "PA1" e l'ultimo creato è associato con il valore discreto di 100

    Examples:
      | ruolo | ente    | ruoloM2M  |
      | admin | GSP     | m2m-admin |
      | admin | GSP     | m2m       |
      | admin | Privato | m2m-admin |
      | admin | Privato | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_2] Il tentativo di recuperare gli attributi certificati discreti associati a un ente fallisce se il relativo UUID non è valido.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    When l'utente richiede l'elenco degli attributi certificati discreti di "PA1" utilizzando un UUID invalido
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | GSP     | admin | m2m-admin |
      | GSP     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_3] La richiesta dell'elenco degli attributi certificati discreti associati all'utente non va a buon fine se i parametri utilizzati non sono validi.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    When l'utente tenta di recuperare la pagina -1 con un limite di -30 elementi della lista di attributi certificati discreti associati a "PA1"
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | GSP     | admin | m2m-admin |
      | GSP     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_4] Il tentativo di recuperare gli attributi certificati discreti associati a un ente fallisce se il relativo UUID non esiste.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    When l'utente richiede l'elenco degli attributi certificati discreti di "PA1" utilizzando un UUID inesistente
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | GSP     | admin | m2m-admin |
      | GSP     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_5] Il tentativo di recuperare gli attributi certificati discreti associati all'ente non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And viene impostato per l'utente un token m2m non valido
    Then l'utente tenta di recuperare la pagina 1 con un limite di 30 elementi della lista di attributi certificati discreti associati a "PA1"
    And si ottiene lo status code 400

    Examples:
      | ruolo | ente    | ruoloM2M  |
      | admin | GSP     | m2m-admin |
      | admin | GSP     | m2m       |
      | admin | Privato | m2m-admin |
      | admin | Privato | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_CREATE_1] Gli attributi certificati discreti possono essere associati solo dagli utenti autorizzati.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100
    Then si ottiene lo status code 403

    Examples:
      | ruolo | ente    | ruoloM2M  |
      | admin | GSP     | m2m       |
      | admin | Privato | m2m-admin |
      | admin | Privato | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_CREATE_2] L'assegnazione di un attributo certificato discreto non va a buon fine se l'UUID dell'attributo è invalido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100, utilizzando per l'ente un UUID invalido
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_CREATE_3] L'assegnazione di un attributo certificato discreto non va a buon fine se l'UUID dell'attributo è inesistente.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100, utilizzando per l'ente un UUID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_CREATE_4] L'assegnazione di un attributo certificato discreto non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And viene impostato per l'utente un token m2m non valido
    When l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_DELETE_1] L'operazione di revoca di un attributo certificato discreto va a buon fine se l'utente è autorizzato.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente revoca a "PA1" l'attributo certificato discreto precedentemente associato con successo
    Then si ottiene lo status code 200

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_DELETE_2] L'operazione di revoca di un attributo certificato discreto non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And viene impostato per l'utente un token m2m non valido
    And l'utente tenta di revocare a "PA1" l'attributo certificato discreto precedentemente associato
    Then si ottiene lo status code 403

    Examples:
      | ruolo | ente    | ruoloM2M  |
      | admin | GSP     | m2m       |
      | admin | Privato | m2m-admin |
      | admin | Privato | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_DELETE_3] La revoca di un attributo certificato discreto non va a buon fine se l'UUID dell'ente è invalido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta di revocare a "PA1" l'attributo certificato discreto precedentemente associato, utilizzando per l'ente un UUID invalido
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_DELETE_4] La revoca di un attributo certificato discreto non va a buon fine se l'UUID dell'attributo è invalido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta di revocare a "PA1" l'attributo certificato discreto precedentemente associato, utilizzando per l'attributo un UUID invalido
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_DELETE_5] La revoca di un attributo certificato discreto non va a buon fine se l'UUID dell'ente non esiste.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta di revocare a "PA1" l'attributo certificato discreto precedentemente associato, utilizzando per l'ente un UUID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_DELETE_6] La revoca di un attributo certificato discreto non va a buon fine se l'UUID dell'attributo non esiste.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta di revocare a "PA1" l'attributo certificato discreto precedentemente associato, utilizzando per l'attributo un UUID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_DELETE_7] La revoca di un attributo certificato discreto non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di revocare a "PA1" l'attributo certificato discreto precedentemente associato
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_PATCH_1] L'aggiornamento di un attributo certificato discreto va a buon fine se l'utente è autorizzato.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente modifica a "PA1" l'attributo certificato discreto precedentemente associato, impostando il valore discreto a 200 con successo
    Then si ottiene lo status code 200

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_PATCH_2] L'aggiornamento di un attributo certificato discreto non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
       | name | description | code |
       |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a "PA1", impostando il valore discreto a 200
    Then si ottiene lo status code 403

    Examples:
      | ruolo | ente    | ruoloM2M  |
      | admin | GSP     | m2m       |
      | admin | Privato | m2m-admin |
      | admin | Privato | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_PATCH_3] L'aggiornamento di un attributo certificato discreto non va a buon fine se l'UUID dell'ente è invalido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a "PA1", impostando il valore discreto a 200, utilizzando per l'ente un UUID invalido
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_PATCH_4] L'aggiornamento di un attributo certificato discreto non va a buon fine se l'UUID dell'attributo è invalido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a "PA1", impostando il valore discreto a 200, utilizzando un UUID invalido
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_PATCH_5] L'aggiornamento di un attributo certificato discreto non va a buon fine se l'UUID dell'ente non esiste.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a "PA1", impostando il valore discreto a 200, utilizzando per l'ente un UUID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_PATCH_6] L'aggiornamento di un attributo certificato discreto non va a buon fine se l'UUID dell'attributo non esiste.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a "PA1", impostando il valore discreto a 200, utilizzando un UUID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_PATCH_7] L'aggiornamento di un attributo certificato discreto non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a "PA1", impostando il valore discreto a 200
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_FUNC_ASSIGN] La revoca di un attributo certificato discreto assegnato a un ente non influisce sugli altri attributi.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione degli attributi certificati discreti
      | name | description | code |
      |      |             |      |
      |      |             |      |
      |      |             |      |
    And l'utente assegna a "PA1" gli attributi certificati discreti creati
    When l'utente tenta di revocare a "PA1" l'ultimo attributo certificato discreto precedentemente associato
    Then l'utente richiede l'elenco degli attributi certificati discreti di "PA1" e il sistema restituisce correttamente gli attributi associati e quelli revocati

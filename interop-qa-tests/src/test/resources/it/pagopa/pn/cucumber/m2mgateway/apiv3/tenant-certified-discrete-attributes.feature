Feature: Gestione di assegnazione degli attributi certificati discreti ai tenant attraverso APIs M2M V3

  ### CASO DI TEST 2.1 GET /tenants/{tenantId}/certifiedDiscreteAttributes

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

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_4] Il tentativo di recuperare gli attributi certificati discreti associati a un ente fallisce se il relativo UUID non è esiste.
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




  ### CASO DI TEST 2.2 Endpoint POST /tenants/{tenantId}/certifiedDiscreteAttributes

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_TENANTS_1] TODO
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente assegna a "PA1" l'attributo certificato discreto creato con un valore discreto di 100 con successo
    And l'utente richiede l'elenco degli attributi certificati discreti di "PA1" e l'ultimo creato è associato con il valore discreto di 100


  ### CASO DI TEST 2.3 - Endpoint DELETE /tenants/{tenantId}/certifiedDiscreteAttributes/{attributeId}

  ### CASO DI TEST 2.4 - Endpoint PUT /tenants/{tenantId}/certifiedDiscreteAttributes/{attributeId}

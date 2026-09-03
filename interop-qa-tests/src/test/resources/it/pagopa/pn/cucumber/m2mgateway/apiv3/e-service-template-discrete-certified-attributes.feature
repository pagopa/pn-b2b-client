Feature: Gestione degli attributi certificati discreti degli e-service template attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_1] Il recupero degli attributi certificati discreti assegnati al template e-service va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | PA1     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_2] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se l'ID dell'e-service non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del template e-service specificando un ID invalido per il template
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_3] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del template e-service specificando un ID inesistente per il template
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_4] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di recuperare gli attributi certificati discreti del template e-service
    Then si ottiene lo status code 403

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_1] L'operazione di associazione di un attributo certificato discreto su un nuovo gruppo di un e-service template non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo | ruoloM2M |
      | PA1     | admin | m2m      |
      | Privato | admin | m2m      |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_2] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    Then si ottiene lo status code 403

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_3] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    Then si ottiene lo status code 403

    Examples:
      | ente | ruolo | ruoloM2M  |
      | PA1  | admin | m2m       |
      | PA2  | admin | m2m-admin |
      | PA2  | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_4] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se l'ID del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service utilizzando per il template un ID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_5] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se l'ID della versione del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service utilizzando per la versione del template un ID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_6] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se la richiesta non contiene tutti i parametri richiesti.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi certificati discreti del template e-service senza specificare i parametri necessari
    Then si ottiene lo status code 403

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_1] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service va a buon fine se l'utente è autorizzato.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_2] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo | ruoloM2M |
      | PA1     | admin | m2m      |
      | Privato | admin | m2m      |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_3] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then si ottiene lo status code 403

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_4] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then si ottiene lo status code 403

    Examples:
      | ente | ruolo | ruoloM2M  |
      | PA1  | admin | m2m       |
      | PA2  | admin | m2m-admin |
      | PA2  | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_5] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'ID del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 di attributi certificati discreti del template e-service utilizzando per il template un ID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_6] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'ID della versione del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 di attributi certificati discreti del template e-service utilizzando per la versione del template un ID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_7] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se la richiesta non contiene tutti i parametri richiesti.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 degli attributi certificati discreti del template e-service senza specificare i parametri necessari
    Then si ottiene lo status code 403

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_1] La rimozione di un attributo certificato discreto da un gruppo di attributi di un tempate e-service va a buon fine se l'utente è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      | CAD1 |             | CAD1 |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      | CAD2 |             | CAD2 |
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente tenta di associare gli attributi certificati discreto creati al gruppo 0 del template e-service
    When l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_2] La rimozione di un attributo certificato discreto da un gruppo di attributi di un tempate e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      | CAD1 |             | CAD1 |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      | CAD2 |             | CAD2 |
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente tenta di associare gli attributi certificati discreto creati al gruppo 0 del template e-service
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m       |
      | Privato | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_3] La rimozione di un attributo certificato discreto da un gruppo di attributi di un tempate e-service non va a buon fine se l'indice del gruppo non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 25    |
    When l'utente tenta la rimozione di un attributo certificato discreto da un gruppo di attributi certificati discreti del template e-service non valido
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_4] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_5] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_6] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'ID del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service utilizzando per il template un ID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_7] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'ID della versione del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service utilizzando per la version del template un ID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_8] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'ID dell'attributo non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta la rimozione dell'attributo certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service utilizzando per l'attributo un ID inesistente
    Then si ottiene lo status code 403

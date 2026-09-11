@certifiedDiscreteAttributeM2Mv3
@certifiedDiscreteAttributeFlagOn
Feature: Gestione degli attributi certificati discreti degli e-service template attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_1] Il recupero degli attributi certificati discreti assegnati al template e-service va a buon fine se il template è in stato pubblicato.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    And "PA1" porta la versione dell'e-service template in stato PUBLISHED
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | PA1     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | PA2     | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_1b] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se il template è in stato bozza e il richiedente non ha l'ownership.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    Given l'utente è un "admin" di "PA1"
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di recuperare gli attributi certificati discreti del template e-service
    Then si ottiene lo status code 404

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_1c] Il recupero degli attributi certificati discreti assegnati al template e-service va a buon fine se il template è in stato bozza e il richiedente ha l'ownership.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

    Examples:
      | ente | ruolo | ruoloM2M  |
      | PA1  | admin | m2m-admin |
      | PA1  | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_2] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se l'ID dell'e-service non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del template e-service specificando un ID invalido per il template
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_3] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del template e-service specificando un ID inesistente per il template
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_4] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di recuperare gli attributi certificati discreti del template e-service
    Then si ottiene lo status code 401

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_FUNC_CREATE_1] L'operazione di associazione di un attributo certificato discreto su un nuovo gruppo di un e-service template non va a buon fine se il template non è in bozza.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And "PA1" porta la versione dell'e-service template in stato <statoDescrittore>
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    Then si ottiene lo status code 400

    Examples:
      | statoDescrittore |
      | PUBLISHED        |
      | SUSPENDED        |
      | DEPRECATED       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_FUNC_CREATE_2] L'operazione di associazione dello stesso attributo certificato discreto su un gruppo di un e-service template non va a buon fine se l'attributo è già associato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then si ottiene lo status code 400

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_1] L'operazione di associazione di un attributo certificato discreto ad un nuovo gruppo di un e-service template non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    When l'utente è un "admin" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    Then si ottiene lo status code <risultato>

    Examples:
      | ente | ruoloM2M  | risultato |
      | PA1  | m2m       | 403       |
      | PA2  | m2m       | 403       |
      | PA2  | m2m-admin | 404       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_2] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    Then si ottiene lo status code 401

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_4] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se l'ID del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service utilizzando per il template un ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_5] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se l'ID della versione del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service utilizzando per la versione del template un ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_6] L'operazione di associazione di un attributo certificato discreto ad un template e-service non va a buon fine se la richiesta non contiene tutti i parametri richiesti.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi certificati discreti del template e-service senza specificare i parametri necessari
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_1] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service va a buon fine se l'utente è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service con successo
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_FUNC_UPDATE_1] L'operazione di associazione di un attributo certificato discreto ad un gruppo esistente di un template e-service va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_2] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo | ruoloM2M |
      | PA1     | admin | m2m      |
      | Privato | admin | m2m      |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_3] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then si ottiene lo status code 401

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_4] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 del template e-service
    Then si ottiene lo status code <risultato>

    Examples:
      | ente | ruolo | ruoloM2M  | risultato |
      | PA1  | admin | m2m       | 403       |
      | PA2  | admin | m2m-admin | 404       |
      | PA2  | admin | m2m       | 403       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_5] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'ID del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 di attributi certificati discreti del template e-service utilizzando per il template un ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_6] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se l'ID della versione del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 di attributi certificati discreti del template e-service utilizzando per la versione del template un ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_UPDATE_7] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un template e-service non va a buon fine se la richiesta non contiene tutti i parametri richiesti.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 degli attributi certificati discreti del template e-service senza specificare i parametri necessari
    Then si ottiene lo status code 400

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_1] La rimozione di un attributo certificato discreto da un gruppo di attributi di un tempate e-service va a buon fine se l'utente è autorizzato.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    And si ottiene lo status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    And si ottiene lo status code 200
    When l'utente tenta la rimozione dell'attributo certificato discreto 0 dal gruppo di attributi certificati discreti 0 del template e-service
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_FUNC_DELETE_1] La rimozione dell'unico attributo certificato discreto presente in un gruppo di attributi di un template e-service avviene con successo e il gruppo corrispondente viene eliminato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente tenta di associare l'attributo certificato discreto creato ad un nuovo gruppo di attributi del template e-service
    When l'utente tenta la rimozione dell'attributo certificato discreto 0 dal gruppo di attributi certificati discreti 0 del template e-service
    Then nel template e-service non è presente alcun gruppo di attributi certificati discreti

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_FUNC_DELETE_2] La rimozione di un attributo certificato discreto associato ad un gruppo di attributi di un template e-service non va a buon fine se il template è pubblicato.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And "PA1" porta la versione dell'e-service template in stato PUBLISHED
    When l'utente tenta la rimozione dell'attributo certificato discreto 0 dal gruppo di attributi certificati discreti 0 del template e-service
    Then si ottiene lo status code 400

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_2] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione degli attributi certificati discreti
      | name | description | code |
      |      |             |      |
      |      |             |      |
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente tenta di associare gli attributi certificati discreti creati ad un nuovo gruppo del template e-service
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta la rimozione dell'attributo certificato discreto 0 dal gruppo di attributi certificati discreti 0 del template e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo | ruoloM2M |
      | PA1     | admin | m2m      |
      | Privato | admin | m2m      |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_3] La rimozione di un attributo certificato discreto da un gruppo di attributi di un tempate e-service non va a buon fine se l'indice del gruppo non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | LTE        | 25    |
    When l'utente tenta la rimozione di un attributo certificato discreto da un gruppo di attributi certificati discreti del template e-service non valido
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_4] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta la rimozione dell'attributo certificato discreto 0 dal gruppo di attributi certificati discreti 0 del template e-service
    Then si ottiene lo status code 401

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_5] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta la rimozione dell'attributo certificato discreto 0 dal gruppo di attributi certificati discreti 0 del template e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_6] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'ID del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service utilizzando per il template un ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_7] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'ID della versione del template non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta la rimozione dell'attibuto certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service utilizzando per la version del template un ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_DELETE_8] La rimozione di un attributo certificato discreto da un gruppo di attributi di un template e-service non va a buon fine se l'ID dell'attributo non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta la rimozione dell'attributo certificato 0 discreto dal gruppo di attributi certificati discreti 0 del template e-service utilizzando per l'attributo un ID inesistente
    Then si ottiene lo status code 404

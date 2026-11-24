@certifiedDiscreteAttributeM2Mv3
@certifiedDiscreteAttributeFlagOn
Feature: Gestione degli attributi certificati discreti degli e-service attraverso APIs M2M V3

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_GET_1] Il recupero degli attributi certificati discreti assegnati all'e-service va a buon fine se l'e-service è stato pubblicato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | EQ         | 100   |
      | 1     | CERTIFIED_DISCRETE | GT         | 80    |
      | 1     | CERTIFIED_DISCRETE | EQ         | 100   |
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente ha pubblicato l'e-service
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_GET_2] Il recupero degli attributi certificati discreti assegnati all'e-service va a buon fine se l'e-service è stato deprecato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    When l'utente è un "admin" di "PA1"
    And "PA1" porta il descrittore dell'e-service in stato "DEPRECATED"
    And il descrittore risulta in stato "DEPRECATED"
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_CREATE_GROUP_1] L'operazione di assegnazione ad un nuovo gruppo di un attributo certificato discreto va a buon fine per un e-service in stato DRAFT.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    And si ottiene lo status code 200
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_CREATE_GROUP_2] L'operazione di assegnazione ad un nuovo gruppo di un attributo certificato discreto non va a buon fine per un e-service in stato PUBLISHED.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente ha pubblicato l'e-service
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_UPDATE_GROUP_1] L'operazione di assegnazione ad un nuovo gruppo di un attributo certificato discreto va a buon fine per un e-service in stato DRAFT.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa


  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_1a] Il recupero degli attributi certificati discreti assegnati all'e-service va a buon fine per un e-service in stato DRAFT se l'utente possiede la ownership.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | EQ         | 100   |
      | 1     | CERTIFIED_DISCRETE | GT         | 80    |
      | 1     | CERTIFIED_DISCRETE | EQ         | 100   |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

    Examples:
      | ente | ruolo | ruoloM2M  |
      | PA1  | admin | m2m-admin |
      | PA1  | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_1b] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine per un e-service in stato DRAFT se l'utente non possiede la ownership.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | EQ         | 100   |
      | 1     | CERTIFIED_DISCRETE | GT         | 80    |
      | 1     | CERTIFIED_DISCRETE | EQ         | 100   |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service
    Then si ottiene lo status code 404

    Examples:
      | ente | ruolo | ruoloM2M  |
      | PA2  | admin | m2m-admin |
      | PA2  | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_2] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID dell'e-service non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID invalido per l'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_3] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID inesistente per l'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_4] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID del descrittore dell'e-service non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID invalido per il descrittore dell'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_5] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID del descrittore dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID inesistente per il descrittore dell'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_6] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service
    Then si ottiene lo status code 401

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_1] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine se non si specifica alcun attributo.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l’utente tenta di associare un attributo certificato discreto all'e-service senza specificarne alcuno
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_2] L'operazione di associazione di un attibuto certificato discreto ad un e-service non va a buon fine se non si specificano i parametri necessari.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato all'e-service senza specificare tutti i parametri necessari
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_3] L'operazione di associazione di un attibuto certificato discreto ad un e-service non va a buon fine se l'ID utilizzato per l'attibuto è inesistente.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare un attributo certificato discreto specificando un ID inesistente per l'attributo
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_4] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    Then si ottiene lo status code 401

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_5] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine se l'utente non possiede la ownership.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_6] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine l'ID specificato per l'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato specificando un e-service ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_7] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine l'ID specificato per il descrittore dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato specificando un descriptor ID inesistente
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_1a] L'operazione di associazione di un attributo certificato discreto ad un gruppo esistente di un e-service in stato DRAFT va a buon fine se l'utente è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_1b] L'operazione di associazione di un attributo certificato discreto ad un gruppo esistente di un e-service in stato PUBLISHED non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente ha pubblicato l'e-service
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 403

    Examples:
      | ente | ruolo | ruoloM2M  |
      | PA1  | admin | m2m       |
      | PA2  | admin | m2m-admin |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_2] L'operazione di associazione di un attributo certificato discreto ad un gruppo esistente di un e-service in stato DRAFT non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo | ruoloM2M |
      | PA1     | admin | m2m      |
      | Privato | admin | m2m      |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_3] L'operazione di associazione di un attributo certificato discreto su un gruppo con indice non valido non va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo <gruppo> dell'e-service
    Then si ottiene lo status code <risultato>

    Examples:
      | gruppo | risultato |
      | -1     | 400       |
      | 2      | 404       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_4] L'operazione di associazione di un attributo certificato discreto su un gruppo di un e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service specificando un ID inesistente per il descrittore dell'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_5] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 401

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_6] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un e-service non va a buon fine se l'utente non possiede la ownership.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_7] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un e-service non va a buon fine se l'attributo è già stato associato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 400

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_1] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service va a buon fine se l'utente è autorizzato.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "<ente>" con ruolo M2M m2m-admin
    And l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    And si ottiene lo status code 200
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_DELETE_1] La rimozione di un attributo certificato discreto da un gruppo di un e-service contenente più attributi va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_DELETE_2] La rimozione dell’unico attributo discreto certificato da un gruppo di un e-service comporta anche l’eliminazione del relativo gruppo di appartenenza.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then nel descrittore dell'e-service non è presente alcun gruppo di attributi certificati discreti

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_FUNC_DELETE_3] La rimozione di un attributo certificato discreto da un gruppo di un e-service che non è in stato bozza non va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    And "PA1" porta il descrittore dell'e-service in stato "<statoDescrittore>"
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then si ottiene lo status code 400

    Examples:
      | statoDescrittore |
      | PUBLISHED        |
      | SUSPENDED        |
      | DEPRECATED       |
      | ARCHIVED         |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_2] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "admin" di "<ente>" con ruolo M2M m2m-admin
    And "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    And l'utente è un "admin" di "<ente>" con ruolo M2M <ruoloM2M>
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruoloM2M |
      | PA1     | m2m      |
      | Privato | m2m      |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_3] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'indice del gruppo non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo <gruppo> dell'e-service
    Then si ottiene lo status code <risultato>

    Examples:
      | gruppo | risultato |
      | -1     | 400       |
      | 1      | 404       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_4] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then si ottiene lo status code 401

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_5] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'utente non possiede la ownership.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_6] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 10    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service specificando un ID inesistente per l'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_7] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'ID del descrittore dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service specificando un ID inesistente per il descrittore dell'e-service
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_8] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'ID dell'attributo associato non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | comparator | value |
      | 0     | CERTIFIED_DISCRETE | LTE        | 15    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service specificando un ID inesistente per l'attributo precedentemente associato
    Then si ottiene lo status code 404

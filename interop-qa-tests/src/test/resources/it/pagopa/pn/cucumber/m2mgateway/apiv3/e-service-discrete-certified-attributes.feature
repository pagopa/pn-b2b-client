Feature: Gestione degli attributi certificati discreti degli e-service attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_1] Il recupero degli attributi certificati discreti assegnati all'e-service va a buon fine.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | PA1     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_2] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID dell'e-service non è valido.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID invalido per l'e-service
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_3] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID inesistente per l'e-service
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_4] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID del descrittore dell'e-service non è valido.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID invalido per il descrittore dell'e-service
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_5] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID del descrittore dell'e-service non esiste.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID inesistente per il descrittore dell'e-service
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_6] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se il token di autenticazione non è valido.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_1] L'operazione di associazione di un attibuto certificato discreto ad un e-service non va a buon fine se non si specifica alcun parametro.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di associare un attributo certificato discreto all'e-service senza specificare alcun parametro
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_2] L'operazione di associazione di un attibuto certificato discreto ad un e-service non va a buon fine se non si specificano i parametri necessari.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato all'e-service senza specificare i parametri necessari
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_3] L'operazione di associazione di un attibuto certificato discreto ad un e-service  non va a buon fine se l'ID utilizzato per l'attibuto è inesistente.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare un attributo certificato discreto specificando un ID inesistente per l'attributo
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_4] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine se il token di autenticazione non è valido.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_5] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine se l'utente non possiede la ownership.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di associare l'attributo certificato discreto creato all'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_6] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine l'ID specificato per l'e-service non esiste.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato specificando un e-service ID inesistente
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_7] L'operazione di associazione di un attributo certificato discreto ad un e-service non va a buon fine l'ID specificato per il descrittore dell'e-service non esiste.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato specificando un descriptor ID inesistente
    Then si ottiene lo status code 403

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_1] L'operazione di associazione di un attributo certificato discreto su un nuovo gruppo di un e-service va a buon fine se l'utente è autorizzato.
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 1 dell'e-service
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_2] L'operazione di associazione di un attributo certificato discreto su un nuovo gruppo di un e-service non va a buon fine se l'utente non è autorizzato.
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 1 dell'e-service
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo | ruoloM2M |
      | PA1     | admin | m2m      |
      | Privato | admin | m2m      |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_3] L'operazione di associazione di un attributo certificato discreto su un gruppo con indice non valido di un e-service non va a buon fine.
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo <gruppo> dell'e-service
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M  | gruppo |
      | PA1     | admin | m2m-admin | -1     |
      | PA1     | admin | m2m-admin | 2      |
      | Privato | admin | m2m-admin | -1     |
      | Privato | admin | m2m-admin | 2      |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_4] L'operazione di associazione di un attributo certificato discreto su un gruppo di un e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service specificando un ID inesistente per il descrittore dell'e-service
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_5] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un e-service non va a buon fine se l'utente non è autorizzato.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_6] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un e-service non va a buon fine se l'utente non possiede la ownership.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_UPDATE_GROUP_7] L'operazione di associazione di un attributo certificato discreto ad un gruppo di un e-service non va a buon fine se l'attributo è già stato associato.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    When l'utente tenta di associare l'attributo certificato discreto creato al gruppo 0 dell'e-service
    Then si ottiene lo status code 403

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_1] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service va a buon fine se l'utente è autorizzato.
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_2] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'utente non è autorizzato.
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M |
      | PA1     | admin | m2m      |
      | Privato | admin | m2m      |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_3] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'indice del gruppo non è valido.
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo -1 dell'e-service
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | Privato | admin | m2m-admin |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_4] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'utente non è autorizzato.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo -1 dell'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_5] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'utente non possiede la ownership.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo -1 dell'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_6] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service specificando un ID inesistente per l'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_7] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'ID del descrittore dell'e-service non esiste.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service specificando un ID inesistente per il descrittore dell'e-service
    Then si ottiene lo status code 403

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_DELETE_8] L'operazione di rimozione di un attributo certificato discreto da un gruppo di un e-service non va a buon fine se l'ID dell'attributo associato non esiste.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
      | 0     | CERTIFIED_DISCRETE | CD002 | LTE        | 15    |
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di rimuovere l'attributo certificato discreto 0 associato al gruppo 0 dell'e-service specificando un ID inesistente per l'attributo precedentemente associato
    Then si ottiene lo status code 403

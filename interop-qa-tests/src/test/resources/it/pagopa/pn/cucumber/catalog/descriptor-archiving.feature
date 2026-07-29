@manual-archiving-eservice
Feature: Archiviazione manuale di un descrittore

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.1] Un ente erogatore di un e-service può avviare il processo di archiviazione manuale del primo e meno recente descrittore in stato DEPRECATED
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando <gracePeriod> giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | role         | gracePeriod |
      | admin        | 30          |
      | api          | 30          |
      | api,security | 30          |
      | admin        | 60          |
      | admin        | 90          |
      | admin        | 120         |

  @sad-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.2] Un utente con ruolo non autorizzato NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "<role>" di "PA2"
    And "PA2" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | role     |
      | security |
      | support  |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.3] Un utente con token non valido NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And viene impostato per l'utente un token non valido
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.4] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un descrittore se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "<descriptorId>" per l'e-service "<eserviceId>" impostando <gracePeriod> giorni di preavviso
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | descriptorId | eserviceId | gracePeriod | statusCode |
      | %null        | %actual    | 60          | 400        |
      | %actual      | %null      | 60          | 400        |
      | %null        | %null      | 60          | 400        |
      | %actual      | %actual    | %null       | 400        |
      | %actual      | %actual    | 10          | 400        |
      | %random      | %actual    | 60          | 404        |
      | %actual      | %random    | 60          | 404        |
      | %random      | %random    | 60          | 404        |

  @sad-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.5] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è già stato già archiviato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 400
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @sad-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.6] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è già in stato di archiviazione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 400
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @happy-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.7] Un ente erogatore di un e-service con 4 descrittori può avviare il processo di archiviazione manuale per i 3 descrittori meno recenti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA4" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.8] Un ente erogatore di un e-service può avviare il processo di archiviazione manuale del primo e meno recente descrittore in stato SUSPENDED
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando <gracePeriod> giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | role         | gracePeriod |
      | admin        | 30          |
      | api          | 30          |
      | api,security | 30          |
      | admin        | 60          |
      | admin        | 90          |
      | admin        | 120         |

  @sad-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.9] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è già in stato di archiviazione e sospeso allo stesso tempo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 400
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @sad-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_2.1] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è il più recente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della versione più recente dell'e-service impostando 60 giorni di preavviso
    Then si ottiene response status code 400
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.1] L'ente erogatore di un e-service può annullare l'archiviazione manuale in corso di un descrittore precedentemente in stato DEPRECATED
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'archiviazione manuale del singolo descrittore è stata annullata con successo

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @sad-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.2] Un utente con ruolo non autorizzato NON può annullare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente è un "<role>" di "PA2"
    And l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita

    Examples:
      | role     |
      | security |
      | support  |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.3] Un utente con token non valido NON può annullare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And viene impostato per l'utente un token non valido
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.4] Un ente erogatore di un e-service NON può annullare il processo di archiviazione manuale di un descrittore se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione della vecchia versione con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita

    Examples:
      | descriptorId | eserviceId | statusCode |
      | %null        | %actual    | 400        |
      | %actual      | %null      | 400        |
      | %null        | %null      | 400        |
      | %random      | %actual    | 404        |
      | %actual      | %random    | 404        |
      | %random      | %random    | 404        |

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.5] L'ente erogatore di un e-service può annullare l'archiviazione manuale in corso di un descrittore precedentemente in stato SUSPENDED
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "SUSPENDED"
    And l'archiviazione manuale del singolo descrittore è stata annullata con successo

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @sad-path
  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_1.1] Il processo di archiviazione dello specifico descrittore NON può essere eseguito da un ente differente dall'erogatore dell'e-service
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA3"
    And l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @sad-path
  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_1.2] Il processo di archiviazione dello specifico descrittore NON può essere annullato da un ente differente dall'erogatore dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente è un "admin" di "PA3"
    And l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita

  @sad-path
  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_1.3] Il processo di archiviazione dello specifico descrittore NON può essere effettuato da un ente differente dall'erogatore dell'e-service anche se questo è delegato all'erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @sad-path
  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_1.4] Il processo di archiviazione dello specifico descrittore NON può essere annullato da un ente differente dall'erogatore dell'e-service anche se questo è delegato all'erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita

  @sad-path
  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_2.1] Il processo di archiviazione dello specifico descrittore NON può essere effettuato dall'ente erogatore se l'e-service è in delega in erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    And l'utente è un "admin" di "PA1"
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 409
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  @happy-path
  Scenario Outline: [ARCHIVING_DESCRIPTOR_BY_JOB_1.1] Raggiunta la data finale del tempo di preavviso per l'archiviazione di un descrittore, questo risulterà correttamente archiviato
    Then l'utente è un "admin" di "PA1"
    #versione più recente
    And il descrittore con id "<secondDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "PUBLISHED"
    And il descrittore con id "<secondDescriptorId>" dell'e-service avente id "<eserviceId>" NON è stato archiviato tramite archiviazione manuale
    #versione meno recente
    And il descrittore con id "<firstDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "ARCHIVED"
    And il descrittore con id "<firstDescriptorId>" dell'e-service avente id "<eserviceId>" è stato correttamente archiviato tramite l'archiviazione manuale del singolo descrittore
    And la richiesta di fruizione con id "<agreementId>" assume lo stato "ACTIVE"

    Examples:
      | firstDescriptorId                    | secondDescriptorId                   | eserviceId                           | agreementId                          |
#      firstDescriptorId=descrittore in Archiving . secondDescriptorId=descrittore in Published
      | 4814138f-e28f-4c29-a0a0-255bf0f9ccdc | 737419b7-b8d5-4290-8d52-a0018adccc34 | ff738cf4-c8da-4b16-8b30-a988cdae5d6a | aae3c04a-f6f0-4e3b-b4aa-b25ed9030652 |
#      firstDescriptorId=descrittore in Archiving_Suspended . secondDescriptorId=descrittore in Published
      | e21fe7eb-e288-4280-a876-7eb8f8dc444a | 4b5f5dc0-d1e0-47f4-85fa-dcb6dbca63db | ef7d1661-caf6-4d8f-a9e0-234bdb0a80e7 | ef49786e-59ec-4ddf-81a1-2b789ea600e1 |

  @happy-path
  Scenario Outline: [ARCHIVING_DESCRIPTOR_BY_JOB_1.2] Se la data finale del tempo di preavviso per l'archiviazione di un descrittore non viene raggiunta, questo non risulterà ancora archiviato
    Then l'utente è un "admin" di "PA1"
    #versione più recente
    And il descrittore con id "<secondDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "PUBLISHED"
    And il descrittore con id "<secondDescriptorId>" dell'e-service avente id "<eserviceId>" NON è stato archiviato tramite archiviazione manuale
    #versione meno recente
    And il descrittore con id "<firstDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "<secondDescriptorState>"
#    utilizziamo questo step anche nel caso di archiviazione in corso poichè viene controllata la presenza del campo archivingSchedule
    And il descrittore con id "<firstDescriptorId>" dell'e-service avente id "<eserviceId>" è stato correttamente archiviato tramite l'archiviazione manuale del singolo descrittore
    And la richiesta di fruizione con id "<agreementId>" assume lo stato "ACTIVE"

    Examples:
      | firstDescriptorId                    | secondDescriptorId                   | eserviceId                           | secondDescriptorState | agreementId                          |
#      firstDescriptorId=descrittore in Archiving . secondDescriptorId=descrittore in Published
      | 39db89b0-7791-4e18-b5b0-8022947bceb1 | 6408e689-f901-44e1-836b-716f31668950 | e71d472d-0fdc-499e-a7fe-671ac453686c | ARCHIVING             | 9c12a5d1-15fa-4317-8710-20ae7cf6858c |
#      firstDescriptorId=descrittore in Archiving_Suspended . secondDescriptorId=descrittore in Published
      | 0fef54c8-611f-4787-b6ab-16c9955a6e64 | 0a2d71a3-7962-4ee2-a94f-c1166358406d | a35969dc-efa6-452b-8a2e-f3434d520fed | ARCHIVING_SUSPENDED   | 100ed036-3e48-498e-8263-83d40fcec0af |

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_TEMPLATE_INSTANCE_1.1] Un ente erogatore di un e-service creato da template può avviare il processo di archiviazione manuale del primo e meno recente descrittore in stato DEPRECATED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    When l'utente è un "<role>" di "PA1"
    And l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando <gracePeriod> giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | role         | gracePeriod |
      | admin        | 30          |
      | api          | 30          |
      | api,security | 30          |
      | admin        | 60          |
      | admin        | 90          |
      | admin        | 120         |

  @sad-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_TEMPLATE_INSTANCE_1.2] Un utente con ruolo non autorizzato NON può avviare il processo di archiviazione manuale del descrittore di un e-service creato da template
    Given l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA2"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    When l'utente è un "<role>" di "PA2"
    And l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | role     |
      | security |
      | support  |
      | reviewer |
      | viewer   |

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_TEMPLATE_INSTANCE_1.3] Un ente erogatore di un e-service creato da template può avviare il processo di archiviazione manuale del primo e meno recente descrittore in stato SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    When l'utente è un "<role>" di "PA1"
    And l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando <gracePeriod> giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | role         | gracePeriod |
      | admin        | 30          |
      | api          | 30          |
      | api,security | 30          |
      | admin        | 60          |
      | admin        | 90          |
      | admin        | 120         |

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_TEMPLATE_INSTANCE_ELIMINATION_1.1] L'ente erogatore di un e-service creato da template può annullare l'archiviazione manuale in corso di un descrittore precedentemente in stato DEPRECATED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'utente è un "<role>" di "PA1"
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'archiviazione manuale del singolo descrittore è stata annullata con successo

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_TEMPLATE_INSTANCE_ELIMINATION_1.2] L'ente erogatore di un e-service creato da template può annullare l'archiviazione manuale in corso di un descrittore precedentemente in stato SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'utente è un "<role>" di "PA1"
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "SUSPENDED"
    And l'archiviazione manuale del singolo descrittore è stata annullata con successo

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_CLONING_1.1] L'ente erogatore può clonare un descrittore in stato ARCHIVING di e-service diverso dal più recente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 200

  @happy-path
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_CLONING_1.2] L'ente erogatore può clonare un descrittore in stato ARCHIVING_SUSPENDED di e-service diverso dal più recente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 200
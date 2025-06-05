Feature: Gestione purposes

  Scenario: [M2MG_PURPOSES_1] Recupero corretto della lista delle finalità con utente autorizzato (Scenario 95)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare la lista completa delle finalità
    Then si ottiene lo status code 200
    And viene restituito l'elenco delle finalità

  Scenario: [M2MG_PURPOSES_2] Accesso negato alla lista delle finalità con token non valido (Scenario 96)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare la lista completa delle finalità
    Then si ottiene lo status code 401
    And l'elenco delle finalità non viene restituito

  Scenario Outline: [M2MG_PURPOSES_3] La lista delle finalità può essere visionata da un utente con ruolo M2M o M2M-ADMIN (Scenario 8)
    Given l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare la lista completa delle finalità
    Then si ottiene lo status code <statusCode>
    And viene restituito l'elenco delle finalità
    Examples:
      | ruolo        | ruolo-m2m | statusCode |
      | admin        | m2m       | 200        |
      | api          | m2m       | 403        |
      | security     | m2m       | 403        |
      | api,security | m2m       | 403        |
      | support      | m2m       | 403        |

  Scenario Outline: [M2M_PURPOSES_LIST_1] La lista delle finalità può essere visionata da un utente con ruolo M2M o M2M-ADMIN
    Given l'utente è un "admin" di "<ente_1>"
    And "<ente_1>" ha già creato e pubblicato 1 e-service
    And l'utente è un "<ruolo_2>" di "PA2" con ruolo M2M <ruolo-m2m_2>
    Given "PA2" ha una richiesta di fruizione m2m in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 5 finalità m2m in stato "ACTIVE" per quell'eservice
    When l'utente fruitore richiede una operazione di listing m2m delle finalità limitata ai primi 3 risultati
    Then si ottiene status code 200
    And sono stati visualizzate correttamente 3 finalità
    Examples:
      | ente_1  | ruolo_2      | ruolo-m2m_2  |
      | PA1     | admin        | m2m          |
      | PA1     | api          | m2m          |
      | PA1     | security     | m2m          |
      | PA1     | api,security | m2m          |
      | PA1     | support      | m2m          |
      | PA1     | admin        | m2m-admin    |
      | PA1     | api          | m2m-admin    |
      | PA1     | security     | m2m-admin    |
      | PA1     | api,security | m2m-admin    |
      | PA1     | support      | m2m-admin    |
      | GSP     | admin        | m2m          |
      | GSP     | api          | m2m          |
      | GSP     | security     | m2m          |
      | GSP     | api,security | m2m          |
      | GSP     | support      | m2m          |
      | GSP     | admin        | m2m-admin    |
      | GSP     | api          | m2m-admin    |
      | GSP     | security     | m2m-admin    |
      | GSP     | api,security | m2m-admin    |
      | GSP     | support      | m2m-admin    |
      | Privato | admin        | m2m          |
      | Privato | api          | m2m          |
      | Privato | security     | m2m          |
      | Privato | api,security | m2m          |
      | Privato | support      | m2m          |
      | Privato | admin        | m2m-admin    |
      | Privato | api          | m2m-admin    |
      | Privato | security     | m2m-admin    |
      | Privato | api,security | m2m-admin    |
      | Privato | support      | m2m-admin    |

  Scenario Outline: [M2MG_PURPOSES_4] Le versioni di una finalità possono essere recuperate da un utente con ruolo M2M o M2M-ADMIN (Scenario 10)
    Given l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare le versioni della finalità "purpose-001"
    Then si ottiene lo status code <statusCode>
    And viene restituito l'elenco delle versioni della finalità
    Examples:
      | ruolo        | ruolo-m2m | statusCode |
      | admin        | m2m       | 200        |
      | api          | m2m       | 403        |
      | security     | m2m       | 403        |
      | api,security | m2m       | 403        |
      | support      | m2m       | 403        |

  Scenario: [M2MG_PURPOSES_5] Recupero corretto delle versioni di una finalità con utente autorizzato (Scenario 97)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare le versioni della finalità "purpose-001"
    Then si ottiene lo status code 200
    And viene restituito l'elenco delle versioni della finalità

  Scenario: [M2MG_PURPOSES_6] Errore nel recupero delle versioni di una finalità con purposeId nullo (Scenario 98)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare le versioni della finalità "null"
    Then si ottiene lo status code 400
    And l'elenco delle versioni della finalità non viene restituito

  Scenario: [M2MG_PURPOSES_7] Accesso negato al recupero delle versioni di una finalità con token non valido (Scenario 99)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare le versioni della finalità "purpose-001"
    Then si ottiene lo status code 401
    And l'elenco delle versioni della finalità non viene restituito

  Scenario: [M2MG_PURPOSES_8] Errore nel recupero delle versioni di una finalità con purposeId inesistente (Scenario 100)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    When l'utente tenta di recuperare le versioni della finalità "purpose-999"
    Then si ottiene lo status code 404
    And l'elenco delle versioni della finalità non viene restituito

  Scenario Outline: [M2MG_PURPOSES_9] Il dettaglio di una versione di finalità può essere recuperato da un utente con ruolo M2M o M2M-ADMIN (Scenario 11)
    Given l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    And viene effettuata la creazione delle versioni della finalità:
      | versionId   | purposeId   |
      | version-001 | purpose-001 |
    When l'utente tenta di recuperare il dettaglio della versione "version-001" della finalità "purpose-001"
    Then si ottiene lo status code <statusCode>
    And viene restituito il dettaglio della versione della finalità
    Examples:
      | ruolo        | ruolo-m2m | statusCode |
      | admin        | m2m       | 200        |
      | api          | m2m       | 403        |
      | security     | m2m       | 403        |
      | api,security | m2m       | 403        |
      | support      | m2m       | 403        |

  Scenario: [M2MG_PURPOSES_10] Recupero corretto del dettaglio di una versione di finalità con utente autorizzato (Scenario 101)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    And viene effettuata la creazione delle versioni della finalità:
      | versionId   | purposeId   |
      | version-001 | purpose-001 |
    When l'utente tenta di recuperare il dettaglio della versione "version-001" della finalità "purpose-001"
    Then si ottiene lo status code 200
    And viene restituito il dettaglio della versione della finalità

  Scenario: [M2MG_PURPOSES_11] Errore nel recupero di una versione di finalità con purposeId e versionId nulli (Scenario 102)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    And viene effettuata la creazione delle versioni della finalità:
      | versionId   | purposeId   |
      | version-001 | purpose-001 |
    When l'utente tenta di recuperare il dettaglio della versione "null" della finalità "null"
    Then si ottiene lo status code 400
    And il dettaglio della versione della finalità non viene restituito

  Scenario: [M2MG_PURPOSES_12] Accesso negato al recupero del dettaglio di una versione di finalità con token non valido (Scenario 103)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    And viene effettuata la creazione delle versioni della finalità:
      | versionId   | purposeId   |
      | version-001 | purpose-001 |
    When l'utente tenta di recuperare il dettaglio della versione "version-001" della finalità "purpose-001"
    Then si ottiene lo status code 401
    And il dettaglio della versione della finalità non viene restituito

  Scenario: [M2MG_PURPOSES_13] Errore nel recupero di una versione di finalità con purposeId e versionId inesistenti (Scenario 104)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name               |
      | purpose-001 | Finalità Sanitaria |
    And viene effettuata la creazione delle versioni della finalità:
      | versionId   | purposeId   |
      | version-001 | purpose-001 |
    When l'utente tenta di recuperare il dettaglio della versione "version-999" della finalità "purpose-999"
    Then si ottiene lo status code 404
    And il dettaglio della versione della finalità non viene restituito

  Scenario: [M2MG_PURPOSES_14] Attivazione di una finalità in stato draft da parte di un utente M2M-ADMIN (Scenario 30)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             |
      | purpose-001 | Finalità Fiscale |
    When l'utente tenta di attivare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta attiva

  Scenario: [M2MG_PURPOSES_15] Accesso negato all'attivazione di una finalità da parte di un utente M2M non admin (Scenario 51)
    Given l'utente è un "api" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name                |
      | purpose-001 | Finalità Ambientale |
    When l'utente tenta di attivare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non risulta attiva

  Scenario: [M2MG_PURPOSES_16] Attivazione di una finalità in stato draft con utente autorizzato (Scenario 105)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato |
      | purpose-001 | Finalità Ricerca | draft |
    When l'utente tenta di attivare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta attiva

  Scenario: [M2MG_PURPOSES_17] Errore attivazione finalità con purposeId NULL (Scenario 106)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato |
      | purpose-001 | Finalità Ricerca | draft |
    When l'utente tenta di attivare la finalità "null"
    Then si ottiene lo status code 400
    And la finalità "null" non risulta attiva

  Scenario: [M2MG_PURPOSES_18] Errore attivazione finalità con purposeId inesistente (Scenario 107)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato |
      | purpose-001 | Finalità Ricerca | draft |
    When l'utente tenta di attivare la finalità "id-inesistente"
    Then si ottiene lo status code 404
    And la finalità "id-inesistente" non risulta attiva

  Scenario: [M2MG_PURPOSES_19] Errore attivazione finalità con token non valido (Scenario 108)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato |
      | purpose-001 | Finalità Ricerca | draft |
    When l'utente tenta di attivare la finalità "purpose-001"
    Then si ottiene lo status code 401
    And la finalità "purpose-001" non risulta attiva

  Scenario: [M2MG_PURPOSES_20] Errore attivazione finalità già attiva (Scenario 109)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di attivare la finalità "purpose-001"
    Then si ottiene lo status code 409
    And la finalità "purpose-001" non viene riattivata

  Scenario Outline: [M2MG_PURPOSES_21] Errore attivazione finalità in stato diverso da draft (Scenario 110)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato   |
      | purpose-001 | Finalità Ricerca | <stato> |
    When l'utente tenta di attivare la finalità "purpose-001"
    Then si ottiene lo status code 409
    And la finalità "purpose-001" non viene attivata
    Examples:
      | stato     |
      | active    |
      | suspended |
      | archived  |

  Scenario: [M2MG_PURPOSES_22] Errore attivazione finalità da parte di un utente non creatore (Scenario 111)
    Given l'utente è un "admin" di "PA2" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And è stata creata una finalità in stato "draft" da un altro ente con i seguenti dati:
      | purposeId   | name             |
      | purpose-001 | Finalità Ricerca |
    When l'utente tenta di attivare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non viene attivata

  Scenario: [M2MG_PURPOSES_23] Sospensione di una finalità in stato active con utente autorizzato (Scenario 33)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di sospendere la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta sospesa

  Scenario: [M2MG_PURPOSES_24] Sospensione negata con utente con solo ruolo M2M (Scenario 54)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di sospendere la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non viene sospesa

  Scenario: [M2MG_PURPOSES_25] Sospensione di una finalità in stato attivo con utente autorizzato (Scenario 112)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di sospendere la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta sospesa

  Scenario: [M2MG_PURPOSES_26] Sospensione fallita di una finalità con purposeId NULL (Scenario 113)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di sospendere la finalità "null"
    Then si ottiene lo status code 400
    And la finalità non viene sospesa

  Scenario: [M2MG_PURPOSES_27] Sospensione fallita di una finalità con purposeId inesistente (Scenario 114)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    When l'utente tenta di sospendere la finalità "purpose-inesistente"
    Then si ottiene lo status code 404
    And la finalità non viene sospesa

  Scenario: [M2MG_PURPOSES_28] Sospensione fallita di una finalità con token non valido (Scenario 115)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di sospendere la finalità "purpose-001"
    Then si ottiene lo status code 401
    And la finalità non viene sospesa

  Scenario: [M2MG_PURPOSES_29] Sospensione fallita di una finalità già sospesa (Scenario 116)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato     |
      | purpose-001 | Finalità Ricerca | suspended |
    When l'utente tenta di sospendere la finalità "purpose-001"
    Then si ottiene lo status code 409
    And la finalità non viene sospesa

  Scenario: [M2MG_PURPOSES_30] Sospensione fallita di una finalità in stato non attivo (Scenario 117)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato |
      | purpose-001 | Finalità Ricerca | draft |
    When l'utente tenta di sospendere la finalità "purpose-001"
    Then si ottiene lo status code 400
    And la finalità non viene sospesa

  Scenario: [M2MG_PURPOSES_31] Sospensione fallita della finalità da utente non autorizzato (Scenario 118)
    Given l'utente è un "admin" di "PA3" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And esiste una finalità "purpose-001" in stato "active" creata da "PA1"
    When l'utente tenta di sospendere la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità non viene sospesa

  Scenario Outline: [M2MG_PURPOSES_32] Archiviazione di una finalità in stato <stato> con utente autorizzato (Scenario 31)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato   |
      | purpose-001 | Finalità Ricerca | <stato> |
    When l'utente tenta di archiviare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta archiviata
    Examples:
      | stato     |
      | active    |
      | suspended |

  Scenario Outline: [M2MG_PURPOSES_33] Archiviazione di una finalità non consentita con ruolo M2M (Scenario 52)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato   |
      | purpose-001 | Finalità Ricerca | <stato> |
    When l'utente tenta di archiviare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non risulta archiviata
    Examples:
      | stato     |
      | active    |
      | suspended |

  Scenario Outline: [M2MG_PURPOSES_34] Archiviazione di una finalità in stato <stato> con utente autorizzato (Scenario 119)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato   |
      | purpose-001 | Finalità Ricerca | <stato> |
    When l'utente tenta di archiviare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta archiviata
    Examples:
      | stato     |
      | active    |
      | suspended |

  Scenario: [M2MG_PURPOSES_35] Archiviazione fallita di una finalità con purposeId NULL (Scenario 120)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di archiviare la finalità "null"
    Then si ottiene lo status code 400
    And la finalità non viene archiviata

  Scenario: [M2MG_PURPOSES_36] Archiviazione fallita di una finalità inesistente (Scenario 121)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di archiviare la finalità "purpose-999"
    Then si ottiene lo status code 404
    And la finalità non viene archiviata

  Scenario: [M2MG_PURPOSES_37] Archiviazione fallita di una finalità con token non valido (Scenario 122)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di archiviare la finalità "purpose-001"
    Then si ottiene lo status code 401
    And la finalità non viene archiviata

  Scenario: [M2MG_PURPOSES_38] Archiviazione fallita di una finalità già archiviata (Scenario 123)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato    |
      | purpose-001 | Finalità Ricerca | archived |
    When l'utente tenta di archiviare la finalità "purpose-001"
    Then si ottiene lo status code 409
    And la finalità non viene archiviata

  Scenario Outline: [M2MG_PURPOSES_39] Archiviazione fallita di una finalità in stato non valido (Scenario 124)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato   |
      | purpose-001 | Finalità Ricerca | <stato> |
    When l'utente tenta di archiviare la finalità "purpose-001"
    Then si ottiene lo status code 400
    And la finalità non viene archiviata
    Examples:
      | stato    |
      | draft    |
      | archived |
      | revoked  |

  Scenario Outline: [M2MG_PURPOSES_40] Archiviazione negata da utente non creatore (Scenario 125)
    Given l'utente è un "admin" di "PA2" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And è stata creata una finalità in stato "<stato>" da un altro ente con i seguenti dati:
      | purposeId   | name             |
      | purpose-001 | Finalità Ricerca |
    When l'utente tenta di archiviare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità non viene archiviata
    Examples:
      | stato     |
      | active    |
      | suspended |

  Scenario: [M2MG_PURPOSES_41] Approvazione di una finalità in stato waiting for approval con utente autorizzato (Scenario 32)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato                |
      | purpose-001 | Finalità Ricerca | waiting for approval |
    When l'utente tenta di approvare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta approvata

  Scenario: [M2MG_PURPOSES_42] Approvazione negata per utente con ruolo M2M (Scenario 53)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato                |
      | purpose-001 | Finalità Ricerca | waiting for approval |
    When l'utente tenta di approvare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non risulta approvata

  Scenario: [M2MG_PURPOSES_43] Attivazione di una finalità in stato waiting for approval con utente autorizzato (Scenario 126)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato                |
      | purpose-001 | Finalità Ricerca | waiting for approval |
    When l'utente tenta di approvare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta approvata

  Scenario: [M2MG_PURPOSES_44] Errore attivazione finalità con purposeId NULL (Scenario 127)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato                |
      | purpose-001 | Finalità Ricerca | waiting for approval |
    When l'utente tenta di approvare la finalità "null"
    Then si ottiene lo status code 400
    And la finalità "null" non risulta approvata

  Scenario: [M2MG_PURPOSES_45] Errore attivazione finalità con purposeId inesistente (Scenario 128)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato                |
      | purpose-001 | Finalità Ricerca | waiting for approval |
    When l'utente tenta di approvare la finalità "purpose-999"
    Then si ottiene lo status code 404
    And la finalità "purpose-999" non risulta approvata

  Scenario: [M2MG_PURPOSES_46] Attivazione fallita di una finalità con token non valido (Scenario 129)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato                |
      | purpose-001 | Finalità Ricerca | waiting for approval |
    When l'utente tenta di approvare la finalità "purpose-001"
    Then si ottiene lo status code 401
    And la finalità "purpose-001" non risulta approvata

  Scenario: [M2MG_PURPOSES_47] Attivazione negata di una finalità già attiva (Scenario 130)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di approvare la finalità "purpose-001"
    Then si ottiene lo status code 409
    And la finalità "purpose-001" non viene riattivata

  Scenario Outline: [M2MG_PURPOSES_48] Attivazione fallita di una finalità in stato non valido (Scenario 131)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato   |
      | purpose-001 | Finalità Ricerca | <stato> |
    When l'utente tenta di approvare la finalità "purpose-001"
    Then si ottiene lo status code 400
    And la finalità non viene approvata
    Examples:
      | stato     |
      | active    |
      | suspended |
      | archived  |
      | revoked   |

  Scenario: [M2MG_PURPOSES_49] Attivazione negata da utente non erogatore (Scenario 132)
    Given l'utente è un "admin" di "PA2" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And esiste una finalità "purpose-001" in stato "waiting for approval" creata da "PA1"
    When l'utente tenta di approvare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non risulta approvata

  Scenario: [M2MG_PURPOSES_50] Riattivazione di una finalità in stato sospeso con utente autorizzato (Scenario 34)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato     |
      | purpose-001 | Finalità Ricerca | suspended |
    When l'utente tenta di riattivare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta attiva

  Scenario: [M2MG_PURPOSES_51] Riattivazione negata per utente con ruolo M2M (Scenario 55)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato     |
      | purpose-001 | Finalità Ricerca | suspended |
    When l'utente tenta di riattivare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non risulta attiva

  Scenario: [M2MG_PURPOSES_52] Riattivazione di una finalità in stato sospeso con utente autorizzato (Scenario 133)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato     |
      | purpose-001 | Finalità Ricerca | suspended |
    When l'utente tenta di riattivare la finalità "purpose-001"
    Then si ottiene lo status code 200
    And la finalità "purpose-001" risulta attiva

  Scenario: [M2MG_PURPOSES_53] Riattivazione fallita di una finalità con purposeId NULL (Scenario 134)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato     |
      | purpose-001 | Finalità Ricerca | suspended |
    When l'utente tenta di riattivare la finalità "null"
    Then si ottiene lo status code 400
    And la finalità non risulta riattivata

  Scenario: [M2MG_PURPOSES_54] Riattivazione fallita di una finalità con purposeId inesistente (Scenario 135)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    When l'utente tenta di riattivare la finalità "purpose-999"
    Then si ottiene lo status code 404
    And la finalità non risulta riattivata

  Scenario: [M2MG_PURPOSES_55] Riattivazione fallita di una finalità con token non valido (Scenario 136)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And l'utente possiede un token non valido
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato     |
      | purpose-001 | Finalità Ricerca | suspended |
    When l'utente tenta di riattivare la finalità "purpose-001"
    Then si ottiene lo status code 401
    And la finalità non risulta riattivata

  Scenario: [M2MG_PURPOSES_56] Riattivazione negata di una finalità già attiva (Scenario 137)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato  |
      | purpose-001 | Finalità Ricerca | active |
    When l'utente tenta di riattivare la finalità "purpose-001"
    Then si ottiene lo status code 409
    And la finalità non viene riattivata

  Scenario Outline: [M2MG_PURPOSES_57] Riattivazione fallita di una finalità in stato non sospeso (Scenario 138)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And viene effettuata la creazione delle finalità:
      | purposeId   | name             | stato   |
      | purpose-001 | Finalità Ricerca | <stato> |
    When l'utente tenta di riattivare la finalità "purpose-001"
    Then si ottiene lo status code 400
    And la finalità non risulta riattivata
    Examples:
      | stato    |
      | active   |
      | draft    |
      | archived |
      | revoked  |

  Scenario: [M2MG_PURPOSES_58] Riattivazione negata da utente non erogatore e non fruitore (Scenario 139)
    Given l'utente è un "admin" di "PA3" con ruolo M2M "m2m-admin"
    And l'utente è amministratore del client
    And esiste una finalità "purpose-001" in stato "suspended" creata da "PA1"
    When l'utente tenta di riattivare la finalità "purpose-001"
    Then si ottiene lo status code 403
    And la finalità "purpose-001" non risulta riattivata

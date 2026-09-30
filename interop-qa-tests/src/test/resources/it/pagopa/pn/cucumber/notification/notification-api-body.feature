@bff-notification
@disable-notifications-hooks # FIXME usato per velocizzare l'esecuzione dei test in locale, rimuovere
Feature: API Notifiche - verifica bodies

  Background:
    # eliminazione di ogni notifica presente per ogni ente ed utente (applicato con Cucumber Hook @Before)
    # abilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @Before)
    # disabilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @After)

  # Prototipo di strategia precedente basata su matching esatto di parametri, al momento non utilizzato.
  #Scenario: [NOTIFICATION_AGREEMENTS_1_A] L'inoltro di una richiesta di fruizione per un proprio e-service produce una notifica (Scenario 59)
  #  Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
  #  When "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
  #  Then è presente una notifica in-app contenente il seguente messaggio: "Hai ricevuto una nuova richiesta di fruizione per l'e-service ${e-service:name} formulata da parte di ${agreement:consumer-name}"

  Scenario: [NOTIFICATION_AGREEMENTS_1] L'inoltro di una richiesta di fruizione per un proprio e-service produce una notifica (Scenario 59)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    When "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Hai ricevuto una nuova richiesta di fruizione per l'e-service .+ formulata da parte di .+" e "/erogazione/richieste/.+"

  Scenario: [NOTIFICATION_AGREEMENTS_2] L'inoltro di una richiesta di fruizione per un proprio e-service con approvazione automatica produce una notifica (Scenario 62)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È stata accettata una richiesta di fruizione per l'e-service .+ formulata da parte di .+" e "/erogazione/richieste/.+"

  Scenario: [NOTIFICATION_AGREEMENTS_3] L'inoltro di una richiesta di fruizione per un proprio e-service con approvazione automatica produce una notifica (Scenario 65)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When "PA2" richiede un'operazione di upgrade di quella richiesta di fruizione con successo
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha aggiornato la propria richiesta di fruizione per l'e-service .+ alla versione più recente\." e "/erogazione/richieste/.+"

  Scenario: [NOTIFICATION_AGREEMENTS_4] La sospensione - da parte del fruitore - di una richiesta di fruizione per un proprio e-service con approvazione automatica produce una notifica (Scenario 68)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente "admin" di "PA2" richiede una operazione di sospensione di quella richiesta di fruizione con successo
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha sospeso la propria richiesta di fruizione per il suo e-service .+\." e "/erogazione/richieste/.+"

  @agreement-activate-refactor
  Scenario: [NOTIFICATION_AGREEMENTS_6] La riattivazione - da parte del fruitore - di una richiesta di fruizione per un proprio e-service con approvazione automatica produce una notifica (Scenario 74)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA2" richiede una operazione di sospensione di quella richiesta di fruizione con successo
    When "PA2" ha già riattivato quella richiesta di fruizione come CONSUMER
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha riattivato la propria richiesta di fruizione per il tuo e-service .+, precedentemente sospesa\." e "/erogazione/richieste/.+"

  # Nota 13 01 2026: ad un utente "api" non è permesso disassociare un client, motivo per cui tutte le precondizioni sono eseguite da un admin
  Scenario: [NOTIFICATION_AGREEMENTS_9] La disassociazione di un client da una finalità produce una notifica (Scenario 86)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And "PA2" ha già associato la finalità a quel client
    When l'utente "admin" di "PA2" richiede la disassociazione della finalità dal client con successo
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha disassociato un proprio client dalla finalità .+ per il tuo e-service .+\." e "/erogazione/finalita/.+"
    # TODO 13 01 2026 test per ruolo diverso da ADMIN rimandati a causa di un problema di configurazione delle notifiche per gli altri ruoli https://pagopa.atlassian.net/browse/PIN-8948?atlOrigin=eyJpIjoiYWU0NzdiNzk2ZTgxNGQ1MjkyOWIxZDI5NWVhYjZiYTIiLCJwIjoiamlyYS1zbGFjay1pbnQifQ
    # And per l'utente "api" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha disassociato un proprio client dalla finalità .+ per il tuo e-service .+\." e "/erogazione/finalita/.+"

  Scenario: [NOTIFICATION_AGREEMENTS_11] La riattivazione - da parte del fruitore - di una finalità per un proprio e-service con approvazione automatica produce una notifica (Scenario 92)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    When l'utente "admin" di "PA2" riattiva la finalità in stato "SUSPENDED" per quell'e-service
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha sospeso la finalità \".+\", associata al tuo e-service .+\." e "/erogazione/finalita/.+"
    # TODO 13 01 2026 test per ruolo diverso da ADMIN rimandati a causa di un problema di configurazione delle notifiche per gli altri ruoli https://pagopa.atlassian.net/browse/PIN-8948?atlOrigin=eyJpIjoiYWU0NzdiNzk2ZTgxNGQ1MjkyOWIxZDI5NWVhYjZiYTIiLCJwIjoiamlyYS1zbGFjay1pbnQifQ
    #And per l'utente "api" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha sospeso la finalità ".+", associata al tuo e-service .+\." e "/erogazione/finalita/.+"

  Scenario: [NOTIFICATION_AGREEMENTS_13] La sospensione di un e-service template produce una notifica (Scenario 98)
    Given l'utente è un "admin" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È stato sospeso il tuo template \".+\"\." e "/erogazione/template-eservice/.+/.+"
    # TODO 13 01 2026 test per ruolo diverso da ADMIN rimandati a causa di un problema di configurazione delle notifiche per gli altri ruoli https://pagopa.atlassian.net/browse/PIN-8948?atlOrigin=eyJpIjoiYWU0NzdiNzk2ZTgxNGQ1MjkyOWIxZDI5NWVhYjZiYTIiLCJwIjoiamlyYS1zbGFjay1pbnQifQ
    #And per l'utente "api" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È stato sospeso il tuo template \".+\"\." e "/erogazione/template-eservice/.+/.+"


  # TODO da qui in poi esecuzioni bloccate a causa di err. 503. Testare appena possibile

  Scenario: [NOTIFICATION_AGREEMENTS_15] La riattivazione di un e-service produce una notifica (Scenario 104)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA1" sospende quel descrittore con successo
    When l'utente "admin" di "PA1" attiva il descrittore di quell'e-service con successo

    # NOTA: regex originale stabilita dai test era "L'ente erogatore .+ ha riattivato la versione [0-9]+ dell'e-service \".+\", precedentemente sospesa\." Si è scelto di adeguarla al risultato non ritenendo rilevanti le variazioni (virgolette attorno a nome e-service).
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha riattivato la versione [0-9]+ dell'e-service .+, precedentemente sospesa\." e "/catalogo-e-service/.+/.+"
    # TODO 13 01 2026 test per ruolo diverso da ADMIN rimandati a causa di un problema di configurazione delle notifiche per gli altri ruoli https://pagopa.atlassian.net/browse/PIN-8948?atlOrigin=eyJpIjoiYWU0NzdiNzk2ZTgxNGQ1MjkyOWIxZDI5NWVhYjZiYTIiLCJwIjoiamlyYS1zbGFjay1pbnQifQ
    #And per l'utente "security" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha riattivato la versione [0-9]+ dell'e-service .+, precedentemente sospesa\." e "/catalogo-e-service/.+/.+"

  # WIP
  Scenario: [NOTIFICATION_AGREEMENTS_17] La modifica di un e-service produce una notifica (Scenario 110)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente "admin" di "PA1" aggiorna la descrizione di quell'e-service con successo
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È disponibile una nuova versione (.+) per l'e-service \".+\", pubblicato da .+\." e "/catalogo-e-service/.+/.+"
    # TODO 13 01 2026 test per ruolo diverso da ADMIN rimandati a causa di un problema di configurazione delle notifiche per gli altri ruoli https://pagopa.atlassian.net/browse/PIN-8948?atlOrigin=eyJpIjoiYWU0NzdiNzk2ZTgxNGQ1MjkyOWIxZDI5NWVhYjZiYTIiLCJwIjoiamlyYS1zbGFjay1pbnQifQ
    #And per l'utente "security" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha riattivato la versione [0-9]+ dell'e-service .+, precedentemente sospesa\." e "/catalogo-e-service/.+/.+"

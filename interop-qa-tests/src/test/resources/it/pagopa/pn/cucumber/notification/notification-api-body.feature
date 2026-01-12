@bff-notification
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
    Then per "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Hai ricevuto una nuova richiesta di fruizione per l'e-service .+ formulata da parte di .+" e "/erogazione/richieste/.+"

  Scenario: [NOTIFICATION_AGREEMENTS_2] L'inoltro di una richiesta di fruizione per un proprio e-service con approvazione automatica produce una notifica (Scenario 62)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Then per "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È stata accettata una richiesta di fruizione per l'e-service .+ formulata da parte di .+" e "/erogazione/richieste/.+"

  Scenario: [NOTIFICATION_AGREEMENTS_3] L'inoltro di una richiesta di fruizione per un proprio e-service con approvazione automatica produce una notifica (Scenario 65)
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When "PA2" richiede un'operazione di upgrade di quella richiesta di fruizione con successo
    Then per "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha aggiornato la propria richiesta di fruizione per l'e-service .+ alla versione più recente\." e "/erogazione/richieste/.+"
Feature: API Notifiche - verifica bodies

  Scenario: [NOTIFICATION_AGREEMENTS_1_A] L'inoltro di una richiesta di fruizione per un proprio e-service produce una notifica
    Given "PA1" ha già creato e pubblicato 1 e-service
    When "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Then è presente una notifica in-app contenente il seguente messaggio: "Hai ricevuto una nuova richiesta di fruizione per l'e-service ${e-service:name} formulata da parte di ${agreement:consumer-name}"

  # FIXME 09/01/2026 alternativa di [NOTIFICATION_AGREEMENTS_1_A], sceglierne 1
  Scenario: [NOTIFICATION_AGREEMENTS_1_B] L'inoltro di una richiesta di fruizione per un proprio e-service produce una notifica
    Given "PA1" ha già creato e pubblicato 1 e-service
    When "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Then è presente una notifica in-app il cui messaggio aderisce al seguente pattern: "Hai ricevuto una nuova richiesta di fruizione per l'e-service .+ formulata da parte di .+"


  # FIXME 09/01/2026 ad uso interno temporaneo, rimuovere
  Scenario: [TEST_PROPERTY_RESOLVER]
    When "PA1" ha già creato e pubblicato 1 e-service
    Then la property "${e-service:id}" estratta è coerente con l'id dell'e-service creato

  # FIXME 09/01/2026 ad uso interno temporaneo, rimuovere
  Scenario: [TEST_NOTIFICATION_REGEX]
    When pongo il seguente template di body in forma di regex "Hai ricevuto una nuova richiesta di fruizione per l'e-service .+ formulata da parte di .+"
    Then riesco a fare il match con la seguente stringa "Hai ricevuto una nuova richiesta di fruizione per l'e-service PincoPanco formulata da parte di Mario Gherkin"
@purpose
Feature: Verifica soglie differenziate
  Tutti gli utenti autorizzati possono usufruire della soglia differenziata specificata nell'attributo certificato

  # @nrt-minimal
  # @purpose_suspend1
  # test 2.1
  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_1] Per la creazione di una finalità il sistema attribuisce la soglia maggiore degli attributi certificati
    Given l'utente è un "admin" di "PA2"

    And PA2 ha già creato 2 attributi CERTIFIED
    And l'utente assegna a "PA1" gli attributi certificati precedentemente creati

    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    # TODO Threshold
    # And l'utente modifica dailyCallsPerConsumer con 100 per l'0-esimo attributo certificato creato
    # And l'utente modifica dailyCallsPerConsumer con 50 per l'1-esimo attributo certificato creato

    # Che differenza c'è tra "ha una richiesta di fruizione" e "ha già creato 1 finalità"?

    # And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente è un "admin" di "PA1"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    # And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 10

    # NEXT
    # When "PA1" fa una richiesta di fruizione per quell'e-service con dailyCallsPerConsumer uguale a 100

    # see: "{string} ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione")
    # Given "<ente>" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice

    # Then la finalità è in stato "ACTIVE"
    And si ottiene status code 200

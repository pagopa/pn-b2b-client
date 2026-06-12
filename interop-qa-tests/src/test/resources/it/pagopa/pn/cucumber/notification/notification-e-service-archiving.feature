@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di un e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.1]
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'e-service $DA_CONTESTO(nome_e_service) è in fase di archiviazione ma è ancora attivo. L'archiviazione avverrà il giorno $DA_CONTESTO(data_archiviazione).
    Dopo questa data, l'e-service sarà archiviato definitivamente e non risulterà più attivo. Puoi annullare l'archiviazione solo entro questo periodo di preavviso.
    """

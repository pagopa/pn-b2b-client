Feature: verifica audit-log su delivery

  @NotificationDetailsError @work-item:QA-18841
  Scenario Outline: [NOTIFICHE_LEGALI_AUDIT_LOG_400_ERROR] Verifico che su pn-delivery sia presente un audit log di errore con status code 502 quando external-registries è spento e viene inviata una notifica legale verso una PF o PG indicando un gruppo
    Given viene generata una nuova notifica
      | subject            | invio notifica con gruppo verso <persona> |
      | senderDenomination | comune di Palermo                         |
      | document           | SI                                        |
      | payment_f24        | PAYMENT_F24_FLAT                          |
      | group              | prova3                                    |
    And destinatario <destinatario>
    When la notifica viene inviata dal "Comune_Multi" con downstream spento
    Then la notifica fallisce con status code "502"
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | error | Could not get groups for senderId {} |
    And viene restituito uno status code 200 in accesso al dettaglio notifica tramite API per le notifiche legali
    Examples:
      | destinatario   | persona |
      | Mario Cucumber | PF      |
      | CucumberSpa    | PG      |






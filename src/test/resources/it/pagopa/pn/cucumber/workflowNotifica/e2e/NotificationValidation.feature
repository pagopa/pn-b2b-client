Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_1] validazione fallita allegati notifica
    Given viene generata una nuova notifica
       | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then si verifica che la notifica non viene accettata causa "ALLEGATO"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_REFUSED"
    And viene verificato che nell'elemento di timeline della notifica "REQUEST_REFUSED" sia presente il campo "refusalReasons"
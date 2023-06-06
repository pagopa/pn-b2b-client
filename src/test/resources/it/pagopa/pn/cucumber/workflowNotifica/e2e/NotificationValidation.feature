Feature: Validazione notifica e2e
    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_1] validazione fallita allegati notifica - file non caricato su SafeStorage
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "ALLEGATO"
        # And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_REFUSED"
        # And viene verificato che nell'elemento di timeline della notifica "REQUEST_REFUSED" sia presente il campo "refusalReasons"

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_2] validazione fallita allegati notifica - Sha256 differenti
        Given viene generata una nuova notifica
           | subject | invio notifica con cucumber |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b con sha256 differente dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "SHA_256"

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_3] validazione fallita allegati notifica - estensione errata
        Given viene generata una nuova notifica
           | subject | invio notifica con cucumber |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b con estensione errata dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "EXTENSION"

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_TAXID] Invio notifica mono destinatario con taxId non valido scenario negativo
        Given viene generata una nuova notifica
        | subject | invio notifica con cucumber |
        And destinatario
        | taxId        | LNALNI80A01H501T |
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_REFUSED"
        Then si verifica che la notifica non viene accettata causa "TAX_ID"

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_PHYSICAL_ADDRESS] Invio notifica mono destinatario con indirizzo fisisco non valido scenario negativo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | physicalAddress_foreignState        | ZZZZZZ     |
            | physicalAddress_municipality | xxxxxx     |
            | physicalAddress_zip          | 111111111111 |
            | physicalAddress_province     | yyyyy      |
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_REFUSED"
        Then l'operazione ha prodotto un errore con status code "400"
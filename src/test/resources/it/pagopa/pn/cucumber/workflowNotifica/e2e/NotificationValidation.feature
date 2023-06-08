Feature: Validazione notifica e2e
    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_1] validazione fallita allegati notifica - file non caricato su SafeStorage
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "ALLEGATO"
        And viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista e che abbia details
            | refusalReasons | [{"errorCode": "FILE_NOTFOUND"}] |

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_2] validazione fallita allegati notifica - Sha256 differenti
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b con sha256 differente dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "SHA_256"
        And viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista e che abbia details
            | refusalReasons | [{"errorCode": "FILE_SHA_ERROR"}] |

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_3] validazione fallita allegati notifica - estensione errata
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b con estensione errata dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "EXTENSION"
        And viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista e che abbia details
            | refusalReasons | [{"errorCode": "FILE_PDF_INVALID_ERROR"}] |

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_TAXID] Invio notifica mono destinatario con taxId non valido scenario negativo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | taxId        | LNALNI80A01H501T |
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "TAX_ID"
        And viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista e che abbia details
            | refusalReasons | [{"errorCode": "TAXID_NOT_VALID"}] |

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_PHYSICAL_ADDRESS] Invio notifica mono destinatario con indirizzo fisico non valido scenario negativo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | taxId | CLMCST42R12D969Z |
            | physicalAddress_zip          | 00000 |
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "ADDRESS"
        And viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista e che abbia details
            | refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |


    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_ASINC_OK] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED e controllo che sia presente nel campo legalFactsIds l'atto opponibile a terzi con category SENDER_ACK positivo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
            | senderDenomination | Comune di milano |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
        And viene verificato che nell'elemento di timeline della notifica "REQUEST_ACCEPTED" sia presente nel campo legalFactsIds l'atto opponibile a terzi con category SENDER_ACK

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_AAR_GENERATION] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION sia presente il campo generatedAarUrl valorizzato positivo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
            | senderDenomination | Comune di milano |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then viene verificato che l'elemento di timeline "AAR_GENERATION" esista e che abbia details
            | generatedAarUrl | NOT_NULL |
        
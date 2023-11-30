Feature: Validazione notifica e2e

    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_1] validazione fallita allegati notifica - file non caricato su SafeStorage
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
        When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        #Then si verifica che la notifica non viene accettata causa "ALLEGATO"
        Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "FILE_NOTFOUND"}] |

    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_2] validazione fallita allegati notifica - Sha256 differenti
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
        When la notifica viene inviata tramite api b2b con sha256 differente dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        #Then si verifica che la notifica non viene accettata causa "SHA_256"
        Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "FILE_SHA_ERROR"}] |

    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_3] validazione fallita allegati notifica - estensione errata
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
        When la notifica viene inviata tramite api b2b con estensione errata dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        #Then si verifica che la notifica non viene accettata causa "EXTENSION"
        Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "FILE_PDF_INVALID_ERROR"}] |


    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_4] validazione fallita allegati notifica - file non caricato su SafeStorage
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
        When la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
       #Then si verifica che la notifica non viene accettata causa "ALLEGATO"
        Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "FILE_NOTFOUND"}] |

    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_5] validazione fallita allegati notifica - file json non caricato su SafeStorage
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
            | feePolicy | DELIVERY_MODE |
        And destinatario Mario Gherkin e:
            | payment_pagoPaForm | NULL |
            | payment_f24flatRate | NULL |
            | payment_f24standard | SI |
            | apply_cost_f24 | SI |
            | payment_multy_number | 1 |
        When la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato json dal "Comune_Multi" e si attende che lo stato diventi REFUSED
       # Then si verifica che la notifica non viene accettata causa "ALLEGATO"
        Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "F24_METADATA_NOT_VALID"}] |


    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_ATTACHMENT_6] validazione fallita allegati notifica - Sha256 Json differenti
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
            | feePolicy | DELIVERY_MODE |
        And destinatario Mario Gherkin e:
            | payment_pagoPaForm | NULL |
            | payment_f24flatRate | NULL |
            | payment_f24standard | SI |
            | apply_cost_f24 | SI |
            | payment_multy_number | 1 |
        When la notifica viene inviata tramite api b2b con sha256 Json differente dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        #Then si verifica che la notifica non viene accettata causa "SHA_256"
        Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "F24_METADATA_NOT_VALID"}] |

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_TAXID] Invio notifica mono destinatario con taxId non valido scenario negativo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | taxId        | LNALNI80A01H501T |
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "TAX_ID"
        And viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "TAXID_NOT_VALID"}] |

    @e2e @ignore
    Scenario: [E2E-NOTIFICATION_VALIDATION_PHYSICAL_ADDRESS] Invio notifica mono destinatario con indirizzo fisico non valido scenario negativo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
            | physicalAddress_zip          | 00000 |
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
        Then si verifica che la notifica non viene accettata causa "ADDRESS"
        And viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |

    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_ASINC_OK] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED e controllo che sia presente nel campo legalFactsIds l'atto opponibile a terzi con category SENDER_ACK positivo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
            | senderDenomination | Comune di milano |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        And viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
            | loadTimeline | true |
            | legalFactsIds | [{"category": "SENDER_ACK"}] |

    @e2e
    Scenario: [E2E-NOTIFICATION_VALIDATION_AAR_GENERATION] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION sia presente il campo generatedAarUrl valorizzato positivo
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
            | senderDenomination | Comune di milano |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then viene verificato che l'elemento di timeline "AAR_GENERATION" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_recIndex | 0 |
            | details_generatedAarUrl | NOT_NULL |
        
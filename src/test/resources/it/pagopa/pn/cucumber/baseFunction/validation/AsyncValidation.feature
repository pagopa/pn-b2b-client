Feature: verifica validazione asincrona

  @dev
  Scenario: [B2B-PA-ASYNC_VALIDATION_1] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage  scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    Then la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @validation @realNormalizzatore
  Scenario Outline: [B2B-PA-ASYNC_VALIDATION_2] invio notifiche digitali mono destinatario con  con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province errati scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_zip          | <zip_code>     |
      | physicalAddress_province     | <province>     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Examples:
      | municipality | zip_code | province |
      | Palermo      | 20019    | MI       |
      | Milano       | 90121    | PA       |
      | Milano       | 90121    | MI       |
      | Milano       | 90121    | RM       |


  @e2e1  @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_3] validazione fallita allegati notifica - file non caricato su SafeStorage
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId        | CLMCST42R12D969Z   |
    When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                             |
      | details                | NOT_NULL                         |
      | details_refusalReasons | [{"errorCode": "FILE_NOTFOUND"}] |

  @e2e1 @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_4] validazione fallita allegati notifica - Sha256 differenti
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId        | CLMCST42R12D969Z   |
    When la notifica viene inviata tramite api b2b con sha256 differente dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                              |
      | details                | NOT_NULL                          |
      | details_refusalReasons | [{"errorCode": "FILE_SHA_ERROR"}] |

  @e2e1 @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_5] validazione fallita allegati notifica - estensione errata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId        | CLMCST42R12D969Z   |
    When la notifica viene inviata tramite api b2b con estensione errata dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                      |
      | details                | NOT_NULL                                  |
      | details_refusalReasons | [{"errorCode": "FILE_PDF_INVALID_ERROR"}] |


  @e2e1 @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_6] validazione fallita allegati notifica - file non caricato su SafeStorage
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId        | CLMCST42R12D969Z   |
    When la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
       #Then si verifica che la notifica non viene accettata causa "ALLEGATO"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                             |
      | details                | NOT_NULL                         |
      | details_refusalReasons | [{"errorCode": "FILE_NOTFOUND"}] |

  @e2e1 @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_7] validazione fallita allegati notifica - file json non caricato su SafeStorage
    Given viene generata una nuova notifica
      | subject   | invio notifica con cucumber |
      | feePolicy | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    When la notifica viene inviata tramite api b2b effettuando la preload ma senza caricare nessun allegato json dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                      |
      | details                | NOT_NULL                                  |
      | details_refusalReasons | [{"errorCode": "F24_METADATA_NOT_VALID"}] |


  @e2e1 @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_8] validazione fallita allegati notifica - Sha256 Json differenti
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    Given viene generata una nuova notifica
      | subject   | invio notifica con cucumber |
      | feePolicy | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    When la notifica viene inviata tramite api b2b con sha256 Json differente dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                      |
      | details                | NOT_NULL                                  |
      | details_refusalReasons | [{"errorCode": "F24_METADATA_NOT_VALID"}] |



  @e2e @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_9] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED e controllo che sia presente nel campo legalFactsIds l'atto opponibile a terzi con category SENDER_ACK positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId        | CLMCST42R12D969Z   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
      | loadTimeline  | true                         |
      | legalFactsIds | [{"category": "SENDER_ACK"}] |

  @e2e @validation
  Scenario: [B2B-PA-ASYNC_VALIDATION_9] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION sia presente il campo generatedAarUrl valorizzato positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId        | CLMCST42R12D969Z   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "AAR_GENERATION" esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_generatedAarUrl | NOT_NULL |


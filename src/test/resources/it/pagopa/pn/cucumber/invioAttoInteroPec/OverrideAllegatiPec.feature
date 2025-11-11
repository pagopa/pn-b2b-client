Feature: Override allegati pec in funzione della copertura RADD

  @overrideAttiFlagAttivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_1] PF - Invarianza degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario NON coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BORGO SANTA MARIA    |
      | physicalAddress_province     | RM                   |
      | physicalAddress_zip          | 00010                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
      | payment_multy_number         | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "F24"

  @overrideAttiFlagAttivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_2] PF - Override degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
      | payment_multy_number         | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 1 allegati
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"

  @overrideAttiFlagAttivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_3] PG - Invarianza degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario NON coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | document           | DOC_1_PG; DOC_2_PG          |
    And destinatario Cucumber Society e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BORGO SANTA MARIA    |
      | physicalAddress_province     | RM                   |
      | physicalAddress_zip          | 00010                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "F24"

  @overrideAttiFlagAttivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_4] PG - Invarianza degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | document           | DOC_1_PG; DOC_2_PG          |
    And destinatario Cucumber Society e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "F24"

  @overrideAttiFlagDisattivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_5] PF - Invarianza degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario NON coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BORGO SANTA MARIA    |
      | physicalAddress_province     | RM                   |
      | physicalAddress_zip          | 00010                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
      | payment_multy_number         | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "F24"

  @overrideAttiFlagDisattivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_6] PF - Invarianza degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
      | payment_multy_number         | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "F24"

  @overrideAttiFlagDisattivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_7] PG - Invarianza degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario NON coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | document           | DOC_1_PG; DOC_2_PG          |
    And destinatario Cucumber Society e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BORGO SANTA MARIA    |
      | physicalAddress_province     | RM                   |
      | physicalAddress_zip          | 00010                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "F24"

  @overrideAttiFlagDisattivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_8] PG - Invarianza degli allegati PEC (Atto, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | document           | DOC_1_PG; DOC_2_PG          |
    And destinatario Cucumber Society e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "F24"

  @overrideAttiFlagAttivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_9] PF - Override degli allegati PEC (Atto intero >= 30Mb, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario NON coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber  |
      | senderDenomination | Comune di Palermo            |
      | document           | DOC_1_PG; DOC_2_PG; DOC_30MB |
    And destinatario Cucumber Society e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BORGO SANTA MARIA    |
      | physicalAddress_province     | RM                   |
      | physicalAddress_zip          | 00010                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 1 allegati
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"

  @overrideAttiFlagAttivo
  Scenario: [OVERRIDE-ALLEGATI-PEC_10] PG - Override degli allegati PEC  (Atto intero >= 30Mb, AAR, Avviso PagoPA,  F24) in una notifica digitale mono-destinatario NON coperto da RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber  |
      | senderDenomination | Comune di Palermo            |
      | document           | DOC_1_PG; DOC_2_PG; DOC_30MB |
    And destinatario Cucumber Society e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_address      | Via nazionale 66     |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 1 allegati
    Then si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"





Feature: Invio atto intero via PEC (fase 2 - estensione F24)

  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_1] PF - Verifica PEC contenente allegati (solo un atto, AAR) di una notifica mono destinatario digitale
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it |
      | payment                 | NULL          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 2 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "NOTIFICATION_ATTACHMENTS"

  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_2] PG - Verifica PEC contenente allegati (Più Atti, AAR) di una notifica mono destinatario digitale
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | document           | DOC_1_PG; DOC_2_PG          |
    And destinatario Cucumber Society e:
      | digitalDomicile_address | test@pecOk.it |
      | payment                 | NULL          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 3 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"

  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_3] PF - Verifica PEC contenente allegati (Atto, AAR, Avviso PagoPA) di una notifica mono destinatario digitale con solo avviso PagoPa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 3 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"

  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_4] PF/PG - Verifica PEC contenente allegati (Atto, AAR, Avviso PagoPA, no F24) di una notifica multi destinatario digitale con più avvisi PagoPA e senza modelli F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it |
    And destinatario Cucumber Society e:
      | digitalDomicile_address | test@pecOk.it |
      | payment     | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 3 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 1 con 2 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "NOTIFICATION_ATTACHMENTS"


  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_5] Verifica presenza e correttezza SHA dei documenti allegati alla PEC in ricevuta consegna PEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 3 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica lo SHA degli attachment inseriti nella pec del destinatario 0 di tipo "NOTIFICATION_ATTACHMENTS"


  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_5_1] Verifica presenza e correttezza SHA dei documenti allegati alla PEC in ricevuta consegna PEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it |
    And destinatario Cucumber Society e:
      | digitalDomicile_address | test@pecOk.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 3 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica lo SHA degli attachment inseriti nella pec del destinatario 0 di tipo "NOTIFICATION_ATTACHMENTS"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And si verifica il contenuto degli attacchment da inviare nella pec del destinatario 1 con 3 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica lo SHA degli attachment inseriti nella pec del destinatario 1 di tipo "NOTIFICATION_ATTACHMENTS"



  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_6] PF - Verifica PEC contenente allegati (Atto, AAR, Avviso PagoPA,  F24) di una notifica mono destinatario digitale con solo avviso PagoPa e modello F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it        |
      | payment_pagoPaForm      | SI                   |
      | payment_f24             | PAYMENT_F24_STANDARD |
      | title_payment           | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa       | SI                   |
      | apply_cost_f24          | SI                   |
      | payment_multy_number    | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "F24"


  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_7] PF/PG - Verifica PEC contenente allegati (Atto, AAR, Avviso PagoPA,  F24) di una notifica multi destinatario digitale con più avvisi PagoPA e modelli F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it        |
      | payment_pagoPaForm      | SI                   |
      | payment_f24             | PAYMENT_F24_STANDARD |
      | title_payment           | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa       | SI                   |
      | apply_cost_f24          | SI                   |
      | payment_multy_number    | 3                    |
    And destinatario Cucumber Society e:
      | digitalDomicile_address | test@pecOk.it        |
      | payment_pagoPaForm      | SI                   |
      | payment_f24             | PAYMENT_F24_STANDARD |
      | title_payment           | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa       | SI                   |
      | apply_cost_f24          | SI                   |
      | payment_multy_number    | 2                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 8 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 4 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 3 attachment di tipo "F24"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 1 con 6 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 3 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "F24"


  @invioAttoInteroPec
  Scenario: [ALLEGATI-PEC_WI-2_8] PF/PG - Verifica PEC contenente i propri allegati per ogni destinatario di una notifica multi destinatario digitale per il primo destinatario contente solamente più avvisi PagoPA e per il secondo destinatario contenente solamente più modelli F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it |
      | payment_pagoPaForm      | SI            |
      | apply_cost_pagopa       | SI            |
      | payment_multy_number    | 5             |
    And destinatario Cucumber Society e:
      | digitalDomicile_address | test@pecOk.it        |
      | payment_pagoPaForm      | NULL                 |
      | payment_f24             | PAYMENT_F24_STANDARD |
      | title_payment           | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa       | NO                   |
      | apply_cost_f24          | SI                   |
      | payment_multy_number    | 4                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 7 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 6 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 0 attachment di tipo "F24"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 1 con 6 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 4 attachment di tipo "F24"




Feature: Allegati notifica

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_15] verifica retention time dei documenti pre-caricati
    Given viene effettuato il pre-caricamento di un documento
    Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_16] verifica retention time  pagopaForm pre-caricato
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention di "PAGOPA" precaricato

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_PG-CF_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_PG-CF_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_PG_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @SmokeTest @RetentionAllegati
  Scenario: [B2B-PA-SEND_PG_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"


   #Test introdotto come regression del bug 8120 GA2.1
  @RetentionAllegati
  Scenario: [B2B_PN8120_1] Analizzando una notifica digitale perfezionata, verificare che la retention degli allegati non venga modificata anche post visualizzazione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@pecOk.it |
      | payment_pagoPaForm      | SI            |
      | payment_f24             | NULL          |
      | apply_cost_pagopa       | SI            |
      | payment_multy_number    | 1             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline     | true                               |
      | details          | NOT_NULL                           |
      | details_recIndex | 0                                  |
      | legalFactsIds    | [{"category": "RECIPIENT_ACCESS"}] |
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "NOTIFICATION_VIEWED"
      | details          | NOT_NULL |
      | details_recIndex | 0        |

    #Test introdotto come regression del bug 8120 GA2.1
  @RetentionAllegati
  Scenario: [B2B_PN8120_2] Analizzando una notifica analogica perfezionata, verificare che la retention degli allegati non venga modificata anche post visualizzazione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm      | SI        |
      | payment_f24             | NULL      |
      | apply_cost_pagopa       | SI        |
      | payment_multy_number    | 1         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline     | true                               |
      | details          | NOT_NULL                           |
      | details_recIndex | 0                                  |
      | legalFactsIds    | [{"category": "RECIPIENT_ACCESS"}] |
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "NOTIFICATION_VIEWED"
      | details          | NOT_NULL |
      | details_recIndex | 0        |


  #Test introdotto come regression del bug 8120 GA2.1
  @RetentionAllegati
  Scenario: [B2B_PN8120_3] Visualizzazione da parte del destinatario della notifica non perfezionata e verifica che la retenion dell'allegato non cambi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@pecOk.it |
      | payment_pagoPaForm      | SI            |
      | payment_f24             | NULL          |
      | apply_cost_pagopa       | SI            |
      | payment_multy_number    | 1             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline     | true                               |
      | details          | NOT_NULL                           |
      | details_recIndex | 0                                  |
      | legalFactsIds    | [{"category": "RECIPIENT_ACCESS"}] |
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "NOTIFICATION_VIEWED"
      | details          | NOT_NULL |
      | details_recIndex | 0        |


  @hotfix-11281
  Scenario: [ALLEGATI-PEC_HOTFIX_1] si verifica che la notifica analogica non contenga nel url degli allegati docTag - PN-11281
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@ok_AR            |
      | payment_pagoPaForm      | SI                   |
      | payment_f24             | PAYMENT_F24_STANDARD |
      | title_payment           | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa       | SI                   |
      | apply_cost_f24          | SI                   |
      | payment_multy_number    | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 da "data vault"
    And si verifica che negli url non contenga il docTag nel "analogico"
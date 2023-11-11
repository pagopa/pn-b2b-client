Feature: deleghe test new feature

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_17] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO @patch
  Scenario: [WEB-PF-MANDATE_18] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_19] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 0                          |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_pagopa    | NO                 |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO @patch
  Scenario: [WEB-PF-MANDATE_20] Invio notifica da parte di ente padre e lettura da delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu                |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_21] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Son"
    Then l'operazione ha generato un errore con status code "422"

  @Annullamento @deleghe1 @patch
  Scenario:  [B2B-PF-ANNULLAMENTO_26] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @Annullamento @deleghe1 @patch
  Scenario:  [B2B-PF-ANNULLAMENTO_27] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega


  @Annullamento @deleghe1 @ignore
  Scenario: [B2B-PA-ANNULLAMENTO_32] Invio notifica digitale mono destinatario e recupero documento notificato_scenario negativo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then il documento notificato non può essere correttamente recuperato da "Mario Gherkin" con delega restituendo un errore "404"

  @Annullamento @deleghe1 @patch
  Scenario: [B2B-PA-ANNULLAMENTO_33] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario negativo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then il documento notificato non può essere correttamente recuperato da "Mario Gherkin" con delega restituendo un errore "404"

  @deleghe2 @AOO_UO @patch
  Scenario: [WEB-PG-MANDATE_19] Invio notifica digitale altro destinatario e recupero_scenario positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

  @deleghe2 @AOO_UO
  Scenario: [WEB-PG-MANDATE_20] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Ufficio per la transizione al Digitale         |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega

  @deleghe2 @AOO_UO @patch
  Scenario: [WEB-PG-MANDATE_21] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Ufficio per la transizione al Digitale          |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24flatRate  | NULL                 |
      | payment_f24standard  | SI                   |
      | title_payment        | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa    | NO                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    And l'allegato "F24" può essere correttamente recuperato da "CucumberSpa" con delega

  @deleghe2 @AOO_UO
  Scenario: [WEB-PG-MANDATE_22] Invio notifica digitale altro destinatario per ente figlio e fallimento invio
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Ufficio per la transizione al Digitale         |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

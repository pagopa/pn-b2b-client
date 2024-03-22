Feature: verifica feature aoo/uo con deleghe

  @deleghe1 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_2] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_3] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 0                          |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_MARIO   |
      | apply_cost_pagopa    | NO                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_4] Invio notifica da parte di ente padre e lettura da delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_5] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    Given "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Son"
    Then l'operazione ha generato un errore con status code "422"


  @deleghe2 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_6] Invio notifica digitale altro destinatario e recupero_scenario positivo da parte di ente radice
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
  Scenario: [B2B-AOO-UO_MANDATE_7] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber             |
      | senderDenomination | Ufficio per la transizione al Digitale |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega

  @deleghe2 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_8] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber             |
      | senderDenomination | Ufficio per la transizione al Digitale |
      | feePolicy          | DELIVERY_MODE                          |
      | paFee              | 0                                      |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa    | NO                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    And l'allegato "F24" può essere correttamente recuperato da "CucumberSpa" con delega

  @deleghe2 @AOO_UO
  Scenario: [B2B-AOO-UO_MANDATE_9] Invio notifica digitale altro destinatario per ente figlio e fallimento invio
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber             |
      | senderDenomination | Ufficio per la transizione al Digitale |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

Feature: Ricezione notifiche destinate al delegante

  Background:
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1
  Scenario: [WEB-PF-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1
  Scenario: [WEB-PF-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1
  Scenario: [WEB-PF-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1
  Scenario: [WEB-PF-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI                   |
      | payment_f24        | PAYMENT_F24_STANDARD |
      | apply_cost_pagopa  | SI                   |
      | apply_cost_f24     | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1
  Scenario: [WEB-PF-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" revoca la delega a "Mario Gherkin"
    Then si tenta la lettura della notifica da parte del delegato "Mario Gherkin" che produce un errore con status code "404"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" rifiuta la delega ricevuta da "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si tenta la lettura della notifica da parte del delegato "Mario Gherkin" che produce un errore con status code "404"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_8] Delega a se stesso _scenario negativo
    Given "Mario Gherkin" viene delegato da "Mario Gherkin"
    Then l'operazione di delega ha prodotto un errore con status code "409"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_9] delega duplicata_scenario negativo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    Then l'operazione di delega ha prodotto un errore con status code "409"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And la notifica può essere correttamente letta da "Mario Cucumber"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_11] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1
  Scenario: [WEB-PF-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And si verifica che l'elemento di timeline della lettura riporti i dati di "Mario Gherkin"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_13] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And si verifica che l'elemento di timeline della lettura non riporti i dati del delegato

  @deleghe1
  Scenario: [WEB-PF-MULTI-MANDATE_14] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1
  Scenario: [WEB-PF-MULTI-MANDATE_15] Invio notifica digitale a destinatario non reperibile
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario
      | denomination    | Dino Sauro       |
      | taxId           | DSRDNI00A01A225I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED

  @deleghe1
  Scenario: [WEB-PF-MULTI-MANDATE_16] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And download attestazione opponibile AAR
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

  @deleghe1
  Scenario: [WEB-PF-MANDATE_17] Invio notifica digitale altro destinatario e recupero_scenario positivo PN-9059
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And l'utente "Mario Cucumber" controlla che la data di refinement sia corretta

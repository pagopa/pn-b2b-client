Feature: Ricezione notifiche destinate al delegante

  Background:
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "Mario Cucumber"

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl" con delega

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato da "GherkinSrl" con delega

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "GherkinSrl" con delega

  @ignore
  Scenario: [WEB-PFPG-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "GherkinSrl" con delega

  @ignore
  Scenario: [WEB-PFPG-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "GherkinSrl" con delega

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" revoca la delega a "GherkinSrl"
    Then si tenta la lettura della notifica da parte del delegato "GherkinSrl" che produce un errore con status code "404"

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" rifiuta la delega ricevuta da "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si tenta la lettura della notifica da parte del delegato "GherkinSrl" che produce un errore con status code "404"

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_8] delega duplicata_scenario negativo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    And "GherkinSrl" viene delegato da "Mario Cucumber"
    Then l'operazione di delega ha prodotto un errore con status code "409"

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_9] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl" con delega
    And la notifica può essere correttamente letta da "Mario Cucumber"

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And la notifica può essere correttamente letta da "GherkinSrl" con delega

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_11] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl" con delega
    And si verifica che l'elemento di timeline della lettura riporti i dati di "GherkinSrl"

  @deleghe3
  Scenario: [WEB-PFPG-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And si verifica che l'elemento di timeline della lettura non riporti i dati del delegato

  @deleghe3
  Scenario: [WEB-PFPG-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario GherkinSrl
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And la notifica può essere correttamente letta da "GherkinSrl" con delega

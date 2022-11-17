Feature: Ricezione notifiche destinate al delegante

  Background:
    Given Mario Gherkin rifiuta se presente la delega ricevuta Mario Cucumber

  #@SmokeTest
  Scenario: [WEB-PF-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal delegato

  #@SmokeTest
  Scenario: [WEB-PF-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato dal delegato

  Scenario: [WEB-PF-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato dal delegato


  Scenario: [WEB-PF-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato dal delegato

  Scenario: [WEB-PF-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato dal delegato

  Scenario: [WEB-PF-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And Mario Cucumber revoca la delega a Mario Gherkin
    Then si tenta la lettura della notifica da parte del delegato che produce un errore con status code "404"

  Scenario: [WEB-PF-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin rifiuta la delega ricevuta da Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si tenta la lettura della notifica da parte del delegato che produce un errore con status code "404"


  Scenario: [WEB-PF-MANDATE_8] Delega a se stesso _scenario negativo
    Given Mario Gherkin viene delegato da Mario Gherkin
    Then l'operazione di delega ha prodotto un errore con status code "409"

  Scenario: [WEB-PF-MANDATE_9] delega duplicata_scenario negativo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    And Mario Gherkin viene delegato da Mario Cucumber
    Then l'operazione di delega ha prodotto un errore con status code "409"

  Scenario: [WEB-PF-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal delegato
    And la notifica può essere correttamente letta dal destinatario "Mario Cucumber"

  Scenario: [WEB-PF-MANDATE_11] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal destinatario "Mario Cucumber"
    And la notifica può essere correttamente letta dal delegato

  Scenario: [WEB-PF-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Mario Gherkin viene delegato da Mario Cucumber
    And Mario Gherkin accetta la delega Mario Cucumber
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal destinatario "Mario Cucumber"
    And la notifica può essere correttamente letta dal delegato



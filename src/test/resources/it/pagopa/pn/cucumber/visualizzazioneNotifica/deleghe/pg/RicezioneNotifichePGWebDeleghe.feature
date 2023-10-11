Feature: Ricezione notifiche destinate al delegante

  Background:
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"


  Scenario: [WEB-PG-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

  Scenario: [WEB-PG-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato da "CucumberSpa" con delega

  Scenario: [WEB-PG-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega

  @ignore
  Scenario: [WEB-PG-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "CucumberSpa" con delega

  @ignore
  Scenario: [WEB-PG-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "CucumberSpa" con delega

  Scenario: [WEB-PG-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "GherkinSrl" revoca la delega a "CucumberSpa"
    Then si tenta la lettura della notifica da parte del delegato "CucumberSpa" che produce un errore con status code "404"

  Scenario: [WEB-PG-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" rifiuta la delega ricevuta da "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si tenta la lettura della notifica da parte del delegato "CucumberSpa" che produce un errore con status code "404"

  @ignore
  Scenario: [WEB-PG-MANDATE_8] Delega a se stesso _scenario negativo
    Given "GherkinSrl" viene delegato da "GherkinSrl"
    Then l'operazione di delega ha prodotto un errore con status code "409"

  Scenario: [WEB-PG-MANDATE_9] delega duplicata_scenario negativo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    Then l'operazione di delega ha prodotto un errore con status code "409"

  Scenario: [WEB-PG-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And la notifica può essere correttamente letta da "GherkinSrl"

  Scenario: [WEB-PG-MANDATE_11] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl"
    And la notifica può essere correttamente letta da "CucumberSpa" con delega

  Scenario: [WEB-PG-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And si verifica che l'elemento di timeline della lettura riporti i dati di "CucumberSpa"

  Scenario: [WEB-PG-MANDATE_13] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl"
    And si verifica che l'elemento di timeline della lettura non riporti i dati del delegato

  Scenario: [WEB-PG-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario GherkinSrl
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl"
    And la notifica può essere correttamente letta da "CucumberSpa" con delega

  @dev
  Scenario: [WEB-PG-MANDATE_14] Invio notifica digitale con delega senza gruppo e assegnazione di un gruppo alla delega da parte del PG amministratore  positivo PN-5962
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
   # Then la notifica può essere correttamente modificata da "GherkinSrl" con delega
    Then come amministratore "GherkinSrl" associa alla delega il primo gruppo disponibile attivo per il delegato "CucumberSpa"

  Scenario: [WEB-PF-PG-MANDATE_15] Invio notifica digitale con delega ad un PG amministratore e recupero della stessa positivo
    Given "CucumberSpa" viene delegato da "Mario Cucumber"
    And "CucumberSpa" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    #Then come amministratore "CucumberSpa" associa alla delega il primo gruppo disponibile attivo

  Scenario: [WEB-PF-PG-MANDATE_16] Invio notifica digitale con delega senza gruppo e assegnazione di un gruppo alla delega da parte del PG amministratore e recupero della stessa positivo
    Given "CucumberSpa" viene delegato da "Mario Cucumber"
    And "CucumberSpa" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And come amministratore "Mario Cucumber" associa alla delega il primo gruppo disponibile attivo per il delegato "CucumberSpa"
    And la notifica può essere correttamente letta da "CucumberSpa" con delega

  Scenario: [WEB-PF-PG-MANDATE_17] Invio notifica digitale con delega senza gruppo ad un PG amministratore e altro destinatario e recupero_scenario positivo
    Given "CucumberSpa" viene delegato da "Mario Cucumber"
    And "CucumberSpa" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | comune di milano          |
    And destinatario Mario Cucumber
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And la notifica può essere correttamente letta da "CucumberSpa" con delega

  Scenario: [WEB-PF-PG-MANDATE_18] Invio notifica digitale con delega senza gruppo ad un PG amministratore e altro destinatario e assegnazione di un gruppo con recupero_scenario positivo
    Given "CucumberSpa" viene delegato da "Mario Cucumber"
    And "CucumberSpa" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | comune di milano          |
    And destinatario Mario Cucumber
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Cucumber"
    And la notifica può essere correttamente letta da "CucumberSpa" con delega
    And come amministratore "Mario Cucumber" associa alla delega il primo gruppo disponibile attivo per il delegato "CucumberSpa"
    And la notifica può essere correttamente letta da "CucumberSpa" con delega


  @AOO_UO
  Scenario: [WEB-PG-MANDATE_19] Invio notifica digitale altro destinatario e recupero_scenario positivo da parte di ente radice
    Given "CucumberSpa" viene delegato da "GherkinSrl" per comune "comune_1"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl"

  @AOO_UO
  Scenario: [WEB-PG-MANDATE_20] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" viene delegato da "GherkinSrl" per comune "comune_1"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "GherkinSrl"

  @AOO_UO
  Scenario: [WEB-PG-MANDATE_21] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" viene delegato da "GherkinSrl" per comune "comune_1"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And l'allegato "F24" può essere correttamente recuperato da "GherkinSrl"

  @AOO_UO
  Scenario: [WEB-PG-MANDATE_22] Invio notifica digitale altro destinatario per ente figlio e fallimento invio
    Given "CucumberSpa" viene delegato da "GherkinSrl" per comune "comune_1"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED

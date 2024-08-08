Feature: Adeguamento RADD alle modifiche dell’allegato tecnico - Throttling

  @raddTechnicalThrottle
    #startAORTransaction
  Scenario: [RADD-ALT_START-AOR-THROTTLE-KO] Vengono recuperati gli atti per un eccessivo numero di volte - Caso di errore
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
      | taxId                   | FLPCPT69A65Z336P                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "UPLOADER" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD "UPLOADER"
    Then Si recuperano gli atti su radd alternative per un numero di volte superiore al limite definito

  @raddTechnicalThrottle
    #startAORTransaction
  Scenario: [RADD-ALT_START-AOR-THROTTLE-OK] Vengono recuperati gli atti più volte - Caso di successo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | digitalDomicile         | NULL                                         |
      | taxId                   | GLLGLL64B15G702I                             |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "UPLOADER" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD "UPLOADER"
    Then Si recuperano gli atti 100 volte su radd alternative da operatore radd "UPLOADER"
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative

  @raddTechnicalThrottle
    #aorInquiry
  Scenario: [ADEG-RADD-AOR-INQ-THROTTLE-KO] Inserimento più tentativi al minuto per un eccessivo numero di volte - Caso di errore
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | digitalDomicile         | NULL                                         |
      | taxId                   | RMSLSO31M04Z404R                             |
      | physicalAddress_address | Via NationalRegistries @FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then Viene visualizzata la presenza di notifiche un numero di volte superiore al limite definito

  @raddTechnicalThrottle
    #aorInquiry
  Scenario: [ADEG-RADD-AOR-INQ-THROTTLE-OK] Inserimento più tentativi al minuto per più volte - Caso di successo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | taxId                   | LVLDAA85T50G702B                             |
      | physicalAddress_address | Via NationalRegistries @FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then Viene visualizzata la presenza di notifiche per la persona fisica "Signor casuale" 100 volte dal operatore radd "UPLOADER"
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative

  @raddTechnicalThrottle
    #actTransaction
  Scenario: [RADD-ALT_START-ACT-THROTTLE-KO] PF - Visualizzazione di atti e attestazioni opponibili per un eccessivo numero di volte - Caso di errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
      | feePolicy          | DELIVERY_MODE                                |
      | paFee              | 0                                            |
    And destinatario
      | denomination            | Galileo Galilei               |
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via @ok_890                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | SI                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 15                            |
      | taxId                   | DVNLRD52D15M059P              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si controlla con check rapidi che lo stato diventi ACCEPTED
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber"
    Then la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti e le attestazioni riferiti alla notifica un numero di volte superiore al limite definito

  @raddTechnicalThrottle
  #actTransaction
  Scenario: [RADD-ALT_START-ACT-THROTTLE-OK] PF - Visualizzazione di atti e attestazioni opponibili per più volte - Caso di successo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
      | feePolicy          | DELIVERY_MODE                                |
      | paFee              | 0                                            |
    And destinatario
      | denomination            | Galileo Galilei               |
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via @ok_890                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | SI                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 15                            |
      | taxId                   | PLOMRC01P30L736Y              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si controlla con check rapidi che lo stato diventi ACCEPTED
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber"
    Then la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti e le attestazioni riferiti alla notifica 100 volte
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative

  @raddTechnicalThrottle
    #actInquiry
  Scenario: [RADD-ALT_ACT-INQ-THROTTLE-KO] Inserimento più tentativi al minuto (actInquiry) - Caso di errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | MNDLCU98T68C933T |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber" un numero di volte superiore al limite definito

  @raddTechnicalThrottle
  #actInquiry
  Scenario: [RADD-ALT_ACT-INQ-THROTTLE-OK] Inserimento più tentativi al minuto (actInquiry) - Caso di successo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | DSRDNI00A01A225I |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber" 100 volte
    And la lettura si conclude correttamente su radd alternative
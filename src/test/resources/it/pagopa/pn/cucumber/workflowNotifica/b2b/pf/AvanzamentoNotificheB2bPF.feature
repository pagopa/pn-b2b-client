Feature: avanzamento notifiche b2b persona fisica

  @mockPec
  Scenario: [TEST_PAPER_NEW]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@OK-WO-011B |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"

  @mockPec @giacenza890Complex
  Scenario: [TEST_PAPER_NEW_2]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@OK-Giacenza-lte10_890-2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_3]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @sequence.5s-CON080.5s-RECRN001A.15m-RECRN001B[DOC:AR;DELAY:15m].5s-RECRN001C[DELAY:15m] |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_4]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @sequence.5s-CON080.5s-RECRN001A.15m-RECRN001B[DOC:AR;DELAY:15m].5s-RECRN001C[DELAY:15m] |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"

  Scenario: [B2B_TIMELINE_27] Invio notifica digitale e lettura notifica da un utente con token scaduto PN-9110
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber con credenziali non valide" tenta di leggere la notifica ricevuta
    Then l'operazione ha prodotto un errore con status code "403"


  @mockPec
  Scenario: [TEST_PAPER_NEW_5]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_6]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile_address | DSRDNI00A01A225I@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_7]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_8]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile_address | pectest@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_9]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | via@fail-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_10]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | via@fail-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    And destinatario
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile_address | pectest@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_11]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | via@fail-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    And destinatario
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile_address | DSRDNI00A01A225I@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_NEW_12]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@fail_890  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"

  @mockPec
  Scenario: [TEST_PAPER_NEW_13]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK_890 |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | via @sequence.5s-CON080.5s-RECRN002D[DISCOVERY;FAILCAUSE:M01].5s-RECRN002E[DOC:Plico;DOC:Indagine].5s-RECRN002F@discovered.10m-CON080.5s-RECRN001A.5m-RECRN001B[DOC:AR].5m-RECRN001C |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"



  @mockPec
  Scenario: [TEST_PAPER_NEW_14]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | via @FAIL-Irreperibile_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"



  @mockPec
  Scenario: [TEST_PAPER_ASSOCIAZIONE_1] evento RECRS002A non valido KO
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @FAIL-EVENTO-INESISTENTE |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_ASSOCIAZIONE_2] statusCode presente con deliveryFailureCause non presente OK
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-SEQUENCE-NORMALE |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"

  @mockPec
  Scenario: [TEST_PAPER_ASSOCIAZIONE_3] statusCode presente con deliveryFailureCause presente e appartenente a lista, ma non presente in mappa: non deve passare
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @FAIL-ASSOCIAZIONE-ERRATA |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"

  @mockPec
  Scenario: [TEST_PAPER_ASSOCIAZIONE_4] statusCode presente con deliveryFailureCause presente e appartenente a lista, e presente in mappa: deve passare
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-ASSOCIAZIONE-CORRETTA |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  @mockPec
  Scenario: [TEST_PAPER_ASSOCIAZIONE_5] statusCode valido ma non presente nella mappa con deliveryFailureCause presente in lista: passa
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @OK-CAUSE-EVENTO-NO-MAPPA |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"

  @mockPec
  Scenario: [TEST_PAPER_ASSOCIAZIONE_6] statusCode valido ma non presente nella mappa con deliveryFailureCause non valido: KO
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via  @FAIL-CAUSE-EVENTO-NO-LISTA |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"


  Scenario: [B2B-TEST_1] PA mittente: annullamento notifica in stato “irreperibile totale”
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di MILANO |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | MNTMRA03M71C615V |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowDigitale
  Scenario: [B2B_TIMELINE_HOTFIX-BINGQ2.2_1] Invio notifica digitale ed attesa stato DELIVERED con invio mail a pec mittente (bug HOTFIX-BINGQ2.2)
    Given si invia una email alla pec mittente e si attendono 6 minuti
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"


  Scenario: [B2B-TIMELINE_HOTFIX-BUG-PEC_1] notifica con allegato oltre i 30MB e 60 allegati pagoPa - PN-11212  (verifica manuale nella pec del solo AAR)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_1_PG;DOC_30MB           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI |
      | apply_cost_pagopa    | SI |
      | payment_multy_number | 60 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


  Scenario: [B2B-TIMELINE_HOTFIX-BUG-PEC_2] notifica con allegato di 30MB e 1 allegato  - PN-11212 (verifica manuale nella pec del solo AAR)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_1_PG;DOC_30MB           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
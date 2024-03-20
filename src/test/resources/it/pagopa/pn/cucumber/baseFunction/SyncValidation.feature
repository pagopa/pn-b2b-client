Feature: verifica validazione sincrona

  @OldMemory
  Scenario: [B2B-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @B2Btest @testLite
  Scenario: [B2B-PA-SYNC_VALIDATION_1] verifica validazione paProtocolNumber e idempotenceToken (p.fisica)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | idempotenceToken   | AME2E3626070001.1           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.1"
    And la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "409"



  @B2Btest
  Scenario: [B2B-PA-SYNC_VALIDATION_2] verifica validazione codice fiscale del creditore e codice avviso
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    And la notifica viene inviata dal "Comune_1"
    And l'operazione ha prodotto un errore con status code "409"


  Scenario: [B2B-PA-SYNC_VALIDATION_3] verifica validazione assenza physicalAddress
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SYNC_VALIDATION_4] verifica validazione assenza taxonomyCode
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | taxonomyCode       | NULL                        |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    And l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SYNC_VALIDATION_5] Invio notifica digitale mono destinatario e controllo paProtocolNumber con diverse pa_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber
    And la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "409"
    And viene generata una nuova notifica con uguale paProtocolNumber
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica

  @dev
  Scenario: [B2B-PA-SYNC_VALIDATION_6] Invio notifica mono destinatario con taxId non valido scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | taxId | LNALNI80A01H501X |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SYNC_VALIDATION_7] Invio notifica mono destinatario con max numero allegati scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber
    And aggiungo 16 numero allegati
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @SmokeTest @testLite
  Scenario: [B2B-PA-SYNC_VALIDATION_8] Invio notifica digitale mono destinatario con noticeCode ripetuto prima notifica rifiutata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene generata una nuova notifica valida con uguale codice fiscale del creditore e uguale codice avviso
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @SmokeTest  @testLite
  Scenario: [B2B-PA-SYNC_VALIDATION_9] Invio notifica multi destinatario senza pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
      | payment | NULL |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | FRMTTR76M06B715E@pec.pagopa.it |
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"


  Scenario: [B2B-PA-SYNC_VALIDATION_10] Invio notifica multi destinatario uguale codice avviso_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa
    And destinatario "Mario Cucumber" con uguale codice avviso del destinario numero 1
      | digitalDomicile_address | FRMTTR76M06B715E@pec.pagopa.it |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SYNC_VALIDATION_11] Invio notifica multi destinatario destinatario duplicato_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Gherkin
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-SYNC_VALIDATION_12] Invion notifica multidestinatario max recipient_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | taxId        | CSRGGL44L13H501E |
    And destinatario
      | taxId        | LNALNI80A01H501T |
    And destinatario
      | taxId        | GRBGPP87L04L741X |
    And destinatario
      | taxId        | LVLDAA85T50G702B |
    And destinatario
      | taxId        | BRGLRZ80D58H501Q |
    And destinatario
      | taxId        | CLMCST42R12D969Z |
    And destinatario
      | taxId        | DRCGNN12A46A326K |
    And destinatario
      | taxId        | FRMTTR76M06B715E |
    And destinatario
      | taxId        | FLPCPT69A65Z336P |
    And destinatario
      | taxId        | PLOMRC01P30L736Y |
    And destinatario
      | taxId        | MNDLCU98T68C933T |
    And destinatario
      | taxId        | MNZLSN99E05F205J |
    And destinatario
      | taxId        | RMSLSO31M04Z404R |
    And destinatario
      | taxId        | MNTMRA03M71C615V |
    And destinatario
      | taxId        | LTTSRT16T12H501Y |
    And destinatario
      | taxId        | DSRDNI00A01A225I |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-SYNC_VALIDATION_13] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  Scenario: [B2B-PA-SYNC_VALIDATION_14] verifica errore PG con CF alfanumerico
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination  | PgErrore          |
      | taxId         | GLLGLL64B15G702I  |
      |recipientType  | PG                |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-SYNC_VALIDATION_15] Invio notifica  mono destinatario con Piva errata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | recipientType    | PG                  |
      | taxId            | 1266681029H    |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica ha sollevato un errore "400"
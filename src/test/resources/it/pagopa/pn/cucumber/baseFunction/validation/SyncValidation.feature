Feature: verifica validazione sincrona

  @OldMemory @authFleet
  Scenario: [B2B-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @B2Btest @testLite @syncValidation
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



  @B2Btest @syncValidation
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

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_3] verifica validazione assenza physicalAddress
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null)"

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_4] verifica validazione sync taxonomyCode
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | taxonomyCode       | <taxonomyCode>                        |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | taxonomyCode | error                |
      | NULL         | instance type (null) |
      | 6_CHAR       | too short            |
      | 8_CHAR       | too long             |
      | ĄŁŚŠŻą˛łľś   | ECMA 262 regex       |

  @syncValidation
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

  @dev @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_6] Invio notifica mono destinatario con taxId non valido scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | taxId | LNALNI80A01H501X |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Invalid taxId"

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_7] Invio notifica mono destinatario con max numero allegati scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber
    And aggiungo 16 numero allegati
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Max attachment count reached"

  @SmokeTest @testLite @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_8] Invio notifica digitale mono destinatario con noticeCode ripetuto prima notifica rifiutata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene generata una nuova notifica valida con uguale codice fiscale del creditore e uguale codice avviso
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @SmokeTest  @testLite @syncValidation
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

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_10] Invio notifica multi destinatario uguale codice avviso_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa
    And destinatario "Mario Cucumber" con uguale codice avviso del destinario numero 1
      | digitalDomicile_address | FRMTTR76M06B715E@pec.pagopa.it |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Duplicated iuv"

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_11] Invio notifica multi destinatario destinatario duplicato_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Gherkin
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Duplicated recipient taxId"

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_12] Invion notifica multidestinatario max recipient_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | taxId        | DVNLRD52D15M059P |
    And destinatario
      | taxId        | LNALNI80A01H501T |
    And destinatario
      | taxId        | GRBGPP87L04L741X |
    And destinatario
      | taxId        | LVLDAA85T50G702B |
    And destinatario
      | taxId        | FNTLCU80T25F205R |
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
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Max recipient count reached"

  @syncValidation
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

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_14] verifica errore PG con CF alfanumerico
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination  | PgErrore          |
      | taxId         | GLLGLL64B15G702I  |
      |recipientType  | PG                |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "SEND accepts only numerical taxId for PG"

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_15] Invio notifica  mono destinatario con Piva errata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | recipientType    | PG                  |
      | taxId            | 1266681029H    |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "ECMA 262 regex"

  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_16] validazione sincrona campo denomination
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | <denomination>   |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori
    Examples:
      | denomination                     |
      | ãsåéØaìnò dæonà ñ'di pi`aève     |
      | SALVAtor DALI Colombo 0123456789 |
      | Ilaria-D'Amico/.@_               |


  @testLite @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_17] validazione sincrona campo subject
    Given viene generata una nuova notifica
      | subject            | <name>           |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori
    Examples:
      | name                         |
      | ãsåéØaìnò dæonà ñ'di pi`aève |


  #@validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_18] validazione sincrona campo physicalAddress_municipality
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipality | <comune> |
      | physicalAddress_zip          | 20121    |
      | physicalAddress_province     | MILANO   |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori
    Examples:
      | comune                       |
      | ãsåéØaìnò dæonà ñ'di pi`aève |
      | Milano '-/.@_                |
      | MILANO                       |
      | MILANO 01234 56789           |


  @validation @authFleet @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_19] validazione sincrona campo physicalAddress_municipalityDetails
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori
    Examples:
      | localita                     |
      #| ãsåéØaìnò dæonà ñ'di pi`aève |
      | Milano '-/.@_                |
      | PARIGI                       |
      | MILANO 01234 56789           |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_20] validazione sincrona campo taxId
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | taxId | <taxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | taxId             | error                |
      | FRMTTR76F06B715E  | Invalid taxId        |
      | FRRTTR76M0MB715H  | Invalid taxId        |
      | 1000000000        | too short            |
      | 17000000000000000 | too long             |
      | FRMTTR76M06B715   | ECMA 262 regex       |
      | FRMTTRZ6M06B715E  | ECMA 262 regex       |
      | FRMTTR76M0YB715E  | ECMA 262 regex       |
      | FRMTTR76M06B7W5E  | ECMA 262 regex       |
      | 20517460320       | Invalid taxId        |
      | NULL              | instance type (null) |
      #1-2) codice fiscale malformato
      #3) 10 numeri (min 11)
      #4) 17 numeri (max 16)
      #5) CF non valido (lettera finale mancante)
      #6) Lettera omocodia non contemplata (primi 2 numeri)
      #7) Lettera omocodia non contemplata (seconda serie di 2 numeri)
      #8) Lettera omocodia non contemplata (serie di 3 numeri finale)
      #9) CF solo numerico
      #10) CF non presente

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_21] validazione sincrona campo payment_creditorTaxId
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | <creditorTaxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | creditorTaxId |
      | 1000000000    |
      | 120000000000  |
      | 11_CHAR       |
      #1) 10 numeri (min 11)
      #2) 12 numeri (max 11)
      #3) 11 lettere (ammessi solo numeri)

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_22] validazione sincrona campo senderTaxId
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | senderTaxId        | <senderTaxId>               |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | senderTaxId  |
      | 1000000000   |
      | 120000000000 |
      | 11_CHAR      |
      | é*ç°.,@#˝ž[  |
      #1) 10 numeri (min 11)
      #2) 12 numeri (max 11)
      #3) 11 lettere (ammessi solo numeri)

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_23] validazione sincrona campo subject
    Given viene generata una nuova notifica
      | subject            | <subject>        |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | subject  |
      | 135_CHAR |
      | \n       |
      | NULL     |


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_24] validazione sincrona campo physicalAddress_State
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_State | <stato> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori
    Examples:
      | stato                       |
      | COSTA D'AVORIO              |
      | spagna                      |
      | Italia '-/.@_ ãåéØò æàñ'`aè |
      | FRANCIA                     |
      | ITALIA 01234 56789          |

    #AGGIUNGERE STATI E VERIFICARE COPERTURA TEST


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_25] validazione sincrona campo physicalAddress_address
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori
    Examples:
      | indirizzo                    |
      | ãsåéØaìnò dæonà ñ'di pi`aève |


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_26] validazione sincrona campo denomination
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | <denomination> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination |
      | NULL         |
      | 0_CHAR       |
      | 81_CHAR      |
      | \n           |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_27] validazione sincrona campo abstract
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | abstract           | <abstract>                  |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | abstract  |
      | 1025_CHAR |
      | \n        |


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_28] validazione sincrona campo abstract physicalAddress_zip
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | <state>    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | <zip_code> |
      | physicalAddress_province     | Paris      |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori
    Examples:
      | state   | zip_code |
      | FRANCIA | 06000    |
      | FRANCIA | 06200    |
      | IRAN    | 13       |


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_29] validazione sincrona max length campo abstract physicalAddress_zip
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | FRANCIA    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | <zip_code> |
      | physicalAddress_province     | Paris      |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | zip_code         |
      | 7500777500779987 |
      | 1                |
      | []!=+àèùòì'^*+   |
    #1) 15 max Length

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_30] invio notifiche digitali mono destinatario con provincia non presente e Stato Italia scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | ITALIA    |
      | physicalAddress_province | <province> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | province |
      | NULL     |
      | 0_CHAR   |

    #1) 15 max Length
  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_31] invio notifiche digitali mono destinatario con provincia non presente e Stato Estero scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | FRANCIA    |
      | physicalAddress_province | NULL |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori


  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_32] Invio notifica digitale con mono destinatario con denomination errata scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano               |
    And destinatario
      | denomination | <denomination>   |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination          |
      | ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝ |


  @validation @syncValidation #Possibile mergiarlo con altri test
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_33] Invio notifica digitale mono destinatario con physicalAddress_address e physicalAddress_addressDetails  corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_address        | <indirizzo> |
      | physicalAddress_addressDetails | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | indirizzo                       |
      | via dell'adige- via torino/.@_  |
      | VIA ADIGE VIA TORINO            |
      | via adige 01234 via adige 56789 |


  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_34] Invio notifica digitale mono destinatario con physicalAddress_zip corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <comune>   |
      | physicalAddress_zip          | <zip_code> |
      | physicalAddress_province     | <province> |
      | physicalAddress_State        | <state>    |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | zip_code   | comune               | province | state   |
      | 1212_      | Paris                | Paris    | FRANCIA |
      | ZONA 1     | Paris                | Paris    | FRANCIA |
      | 0123456789 | Paris                | Paris    | FRANCIA |
      | 20019      |  SETTIMO MILANESE    | MI       | ITALIA  |
      | 20121      | Milano               | MI       | ITALIA  |
      | 87076      | VILLAPIANA LIDO      | CS       | ITALIA  |

  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_35] Invio notifica digitale mono destinatario con physicalAddress_province corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | Milano     |
      | physicalAddress_zip          | 20121      |
      | physicalAddress_province     | <province> |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | province       |
      | MI '-/.@_      |
      | MI             |
      | MI 01234 56789 |


  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_36] validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_address
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                                                    |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |


  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_37] validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_addressDetails
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_addressDetails | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                                                    |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_38] validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_municipality
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <comune> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | comune                                                       |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_39] validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_municipalityDetails
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | localita                            |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂ       |
      | via ĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_40] validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_State
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_State | <state> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | state                                                        |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |


  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_41] validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_zip
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_zip | <zip_code> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | zip_code                                                 |
      | ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |


  @validation @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_42] validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_province
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_province | <province> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | province                                                     |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_43]  validazione errore caratteri NON iso-latin-1 sul campo physicalAddress_at
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | at | ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_44] validazione sincrona campo idempotenceToken
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | idempotenceToken   | <idempotenceToken>          |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | idempotenceToken | error          |
      | 257_CHAR         | too long       |
      | \n               | ECMA 262 regex |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_45] validazione sincrona campo paProtocolNumber
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | paProtocolNumber   | <paProtocolNumber>          |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | paProtocolNumber | error                                                          |
      | 257_CHAR         | too long                                                       |
      | \n               | ECMA 262 regex                                                 |
      | NULL             | instance type (null) does not match any allowed primitive type |

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_46] validazione sincrona campo notificationFeePolicy
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | feePolicy          | NULL                            |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_47] validazione sincrona campo cancelledIun
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | cancelledIun       | <cancelledIun>              |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | cancelledIun              | error          |
      | 24_CHAR                   | too short      |
      | 26_CHAR                   | too long       |
      | 12A4-1234-1234-abcdef-1-a | ECMA 262 regex |

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_48] validazione sincrona campo physicalCommunicationType
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | comune di milano            |
      | physicalCommunication | NULL    |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_49] validazione sincrona campo senderDenomination
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | <senderDenomination>        |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | senderDenomination | error                                                          |
      | NULL               | instance type (null) does not match any allowed primitive type |
      | 0_CHAR             | too short                                                      |
      | 81_CHAR            | too long                                                       |
      | \n                 | ECMA 262 regex                                                 |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_50] validazione sincrona campo group
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | group              | <group>                     |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | group                                                    | error          |
      | 1025_CHAR                                                | too long       |
      | ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ | ECMA 262 regex |


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_51] validazione sincrona campo paymentExpirationDate
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | comune di milano            |
      | paymentExpirationDate | <paymentExpirationDate>     |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | paymentExpirationDate | error          |
      | 9_CHAR                | too short      |
      | 11_CHAR               | too long       |
      | ĄŁŚŠŻą˛łľś            | ECMA 262 regex |
      | abcd-ab-ab            | ECMA 262 regex |

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_52] validazione sincrona campo recipents
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    #bug associato 10407

  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_53] validazione sincrona campo documents
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | NULL                        |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"


  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_54] validazione sincrona campo recipientType
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | recipientType | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"


  @syncValidation
  Scenario: [B2B-PA-SYNC_VALIDATION_55] validazione sincrona campo digitalDomicile_type
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile_type | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_56] validazione sincrona campo digitalDomicile_address
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | <digitalDomicileAddress> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | digitalDomicileAddress | error                                                          |
      | NULL                   | instance type (null) does not match any allowed primitive type |
      | 321_CHAR               | too long                                                       |
      | %&£"(=.*é°@é.ç°é*"žżŔ  | ECMA 262 regex                                                 |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_57] validazione sincrona campo at del physicalAddress
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | at | <at> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | at       | error          |
      | 257_CHAR | too long       |
      | \n       | ECMA 262 regex |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_58] validazione sincrona campo physicalAddress_address
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | <address> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | address   | error                                                          |
      | NULL      | instance type (null) does not match any allowed primitive type |
      | 1_CHAR    | too short                                                      |
      | 1025_CHAR | too long                                                       |
      | \n  a     | ECMA 262 regex                                                 |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_59] validazione sincrona campo physicalAddress_addressDetails
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_addressDetails | <addressDetails> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | addressDetails | error                    |
      | 1025_CHAR      | too long                 |
      | \n  a          | ECMA 262 regex too short |


  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_60] validazione sincrona campo physicalAddress_municipality
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <municipality> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | municipality | error                                                          |
      | NULL         | instance type (null) does not match any allowed primitive type |
      | 1_CHAR       | too short                                                      |
      | 257_CHAR     | too long                                                       |
      | \n  a        | ECMA 262 regex                                                 |

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_61] validazione sincrona campo physicalAddress_municipalityDetails
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipalityDetails | <municipalityDetails> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | municipalityDetails | error          |
      | 257_CHAR            | too long       |
      | \n                  | ECMA 262 regex |

  @syncValidation #@validation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_62] validazione sincrona campo physicalAddress_province
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_province | <province> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | province | error          |
      | 257_CHAR | too long       |
      | \n       | ECMA 262 regex |

  @syncValidation #@validation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_63] validazione sincrona campo physicalAddress_State
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_State | <state> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | state    | error          |
      | 257_CHAR | too long       |
      | \n       | ECMA 262 regex |

  @syncValidation #@validation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_64] validazione sincrona campo noticeCode
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | payment_noticeCode | <noticeCode> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | noticeCode          | error                                                          |
      | NULL                | instance type (null) does not match any allowed primitive type |
      | 12345678912345678   | too short                                                      |
      | 1234567891234567891 | too long                                                       |
      | #@è^?é*arasc(+*é?A  | ECMA 262 regex                                                 |

  @syncValidation #@validation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_65] validazione sincrona campo creditorTaxId
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId | <creditorTaxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | creditorTaxId | error                                                          |
      | NULL          | instance type (null) does not match any allowed primitive type |
      | 1234567891    | too short                                                      |
      | 123456789123  | too long                                                       |
      | #@è^?asc(?A   | ECMA 262 regex                                                 |

  @syncValidation #@validation
  Scenario: [B2B-PA-SYNC_VALIDATION_66] validazione sincrona campo applyCost pagamento pagoPa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | apply_cost_pagopa | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"

  @syncValidation #@validation
  Scenario: [B2B-PA-SYNC_VALIDATION_67] validazione sincrona campo title pagamento F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | payment_f24        | PAYMENT_F24_FLAT |
      | title_payment      | NULL             |
      | apply_cost_f24     | NO               |
      | payment_pagoPaForm | NO               |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"

  @syncValidation #@validation
  Scenario: [B2B-PA-SYNC_VALIDATION_68] validazione sincrona campo applyCost pagamento F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | payment_f24    | PAYMENT_F24_FLAT |
      | apply_cost_f24 | NULL             |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"

  @syncValidation #@validation
  Scenario: [B2B-PA-SYNC_VALIDATION_69] validazione sincrona campo metadataAttachment pagamento F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | payment_f24 | NO_METADATA_ATTACHMENT |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "instance type (null) does not match any allowed primitive type"

  @validation
  Scenario: [B2B-PA-SYNC_VALIDATION_70] Invio notifica con allegato uguale al allegato di pagamento - PN-10162
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" con allegato uguale al allegato di pagamento
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Same attachment compares more then once in the same request"


  Scenario: [B2B-PA-SYNC_VALIDATION_71] Invio notifica multidestinatario con allegato uguale al allegato di pagamento - PN-10162
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" con allegato uguale al allegato di pagamento
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Same attachment compares more then once in the same request"

  @validation
  Scenario: [B2B-PA-SYNC_VALIDATION_72] validazione sincrona campo denomination contenente pipe
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | Fieramosca pipe\| |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "Field denomination in recipient 0 contains invalid characters."

  @validation
  Scenario: [B2B-PA-SYNC_VALIDATION_73] validazione sincrona campo denomination non contenente il carattere pipe
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | Ettore Fieramosca  |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio della notifica non ha prodotto errori

  @validation
  Scenario: [B2B-PA-SYNC_VALIDATION_74] validazione sincrona campo denomination contenente più di 44 caratteri
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | Fieramosca aaaaaaaaaaaaaaa aaaaaaaaaaaaa aaaaaaaa |
      | taxId        | FRMTTR76M06B715E                                  |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @validation
  Scenario: [B2B-PA-SYNC_VALIDATION_75] validazione sincrona campo denomination contenente euro
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | Fieramosca €     |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @validation
  Scenario: [B2B-PA-SYNC_VALIDATION_76] validazione sincrona campo denomination contenente a capo e inizio riga
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | Fieramosca\n\rEttore |
      | taxId        | FRMTTR76M06B715E     |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @syncValidation
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_77] validazione sincrona campo digitalDomicile_address domicilio digitale speciale -PN-11485
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | <email> |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Examples:
      | email            |
      | V.S.-SRL@pecOk.it|

    #Test utilizzato per verificare l'errore prima della fix
  @syncValidation @ignore
  Scenario Outline: [B2B-PA-SYNC_VALIDATION_78] validazione sincrona campo digitalDomicile_address domicilio digitale speciale -PN-11485
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | <email> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "<error>"
    Examples:
      | email            | error          |
      | V.S.-SRL@pecOk.it | ECMA 262 regex |
Feature: recupero notifiche tramite api AppIO b2b

  @SmokeTest @letturaDestinatario  @appIo
  Scenario: [B2B-PA-APP-IO_1] Invio notifica con api b2b e recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then la notifica può essere recuperata tramite AppIO

  @SmokeTest @letturaDestinatario  @appIo
  Scenario: [B2B-PA-APP-IO_2] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | idempotenceToken   | AME2E3626070001.1           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere recuperata tramite AppIO
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then la notifica può essere recuperata tramite AppIO

  @SmokeTest @letturaDestinatario  @appIo
  Scenario: [B2B-PA-APP-IO_3] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere recuperata tramite AppIO
    And viene generata una nuova notifica con uguale codice fiscale del creditore e codice avviso differente
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then la notifica può essere recuperata tramite AppIO

  @ignore @tbc
  Scenario: [B2B-PA-APP-IO_4] Invio notifica con api b2b e recupero documento notificato con AppIO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then il documento notificato può essere recuperata tramite AppIO

  @appIo
  Scenario: [B2B-PA-APP-IO_5] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" tenta il recupero della notifica tramite AppIO
    Then il tentativo di recupero con appIO ha prodotto un errore con status code "400"
  #viene richiesto il codice QR per lo IUN {string}

  #[TC_1]
  @appIo
  Scenario: [QR_CODE_1] Viene scansionato il QR Code sull'AAR per recuperare i dettagli della notifica tramite appIO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
#    [TC_5]
    And a seguito della scansione del QR Code, la notifica può essere recuperata da: Mario Cucumber tramite AppIO
    # SI VERIFICA CHE UN UTENTE CHE NON è DESTINATARIO O DELEGATO NON POSSA LEGGERE IL QRCODE
    And l'utente Galileo Galilei scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    And si verifica che la chiamata abbia ritornato uno status code: 403

    #[TC_3] SI PROVA A RIACCEDERE ALLA NOTIFICA SCANSIONANDO UN QR CODE NON VALIDO
    And viene generato il QR Code "malformato" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    And si verifica che la chiamata abbia ritornato uno status code: 404
    
  #[TC_2]
  @appIo
  Scenario Outline: [QR_CODE_2] Si verificano gli status code ritornati per il caso negativo dell'API: checkQRCode
    And viene chiamato l'endpoint "checkQRCode" con i seguenti params:
      | taxId          | <PARAM_1> |
      | aarQrCodeValue | <PARAM_2> |
    Then si verifica che la chiamata abbia ritornato uno status code: 400
    Examples:
      | PARAM_1          | PARAM_2                                                                                                                                  |
      |                  | UFlEQS1RR05LLURWUlUtMjAyNTA4LU4tMV9QRi00ZmM3NWRmMy0wOTEzLTQwN2UtYmRhYS1lNTAzMjk3MDhiN2RfM2Y2MWQ1N2QtMmJmOC00NGU4LWFhMmMtNjBlZmNmODY3YTVh |
      | FRMTTR76M06B715E |                                                                                                                                          |

  #[TC_6] [TC_7]
  @appIo
  Scenario Outline: [QR_CODE_3] Si verificano gli status code ritornati per il caso negativo dell'API: getReceivedNotification
    And viene chiamato l'endpoint "getReceivedNotification" con i seguenti params:
      | iun   | <PARAM_1> |
      | taxId | <PARAM_2> |
    Then si verifica che la chiamata abbia ritornato uno status code: 400
    Examples:
      | PARAM_1                   | PARAM_2          |
      |                           | FRMTTR76M06B715E |
      | ERRA-T000-0000-ERRATO-0-0 | FRMTTR76M06B715E |
      | NAUZ-WNPH-WQZE-202508-Y-1 |                  |

  @appIo
  Scenario: [QR_CODE_4] Viene generata una notifica e invocato l'endpoint per il recupero del documento tramite docIdx (/delivery/notifications/received/{iun}/attachments/documents/{docIdx})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    Then a seguito della scansione del QR Code, il documento notificato può essere recuperata tramite AppIO

  #[TC_9]
  @appIo
  Scenario Outline: [QR_CODE_5] Si verificano gli status code ritornati per il caso negativo dell'API: getSentNotificationDocument
    And viene chiamato l'endpoint "getSentNotificationDocument" con i seguenti params:
      | iun    | <PARAM_1> |
      | docIdx | <PARAM_2> |
      | taxId  | <PARAM_3> |
    Then si verifica che la chiamata abbia ritornato uno status code: <PARAM_4>
    Examples:
      | PARAM_1                   | PARAM_2 | PARAM_3          | PARAM_4 |
      | PKMK-AAAA-WJDK-202509-A-1 | 0       | FRMTTR76M06B715E | 404     |
      |                           | 0       | FRMTTR76M06B715E | 400     |
      | ERRA-T000-0000-ERRATO-0-0 |         | FRMTTR76M06B715E | 400     |
      | NAUZ-WNPH-WQZE-202508-Y-1 | 0       |                  | 400     |


  #[TC_12]
  @appIo
  Scenario: [QR_CODE_6] Viene creata una notifica e recuperato il documento di pagamento PAGOPA tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  @appIo
  Scenario: [QR_CODE_6] Viene creata una notifica e recuperato l'F24 tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | NAPOLI               |
      | physicalAddress_province     | NA                   |
      | physicalAddress_zip          | 80124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Gherkin scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    Then a seguito della scansione del QR Code, il documento di pagamento "F24" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  #[TC_13] [TC_14]
  @appIo
  Scenario Outline: [QR_CODE_7] Si verificano gli status code ritornati per il caso negativo dell'API: getReceivedNotificationAttachment
    And viene chiamato l'endpoint "getReceivedNotificationAttachment" con i seguenti params:
      | iun            | <PARAM_1> |
      | attachmentName | <PARAM_2> |
      | taxId          | <PARAM_3> |
      | attachmentIdx  | 0         |
    Then si verifica che la chiamata abbia ritornato uno status code: <PARAM_4>
    Examples:
      | PARAM_1                   | PARAM_2 | PARAM_3          | PARAM_4 |
      | AAAA-AAAA-WQZE-202508-Y-1 | PAGOPA  | FRMTTR76M06B715E | 404     |
      |                           | F24     | FRMTTR76M06B715E | 400     |
      | ERRA-T000-0000-ERRATO-0-0 | F24     | FRMTTR76M06B715E | 400     |
      | NAUZ-WNPH-WQZE-202508-Y-1 |         | FRMTTR76M06B715E | 400     |
      | NAUZ-WNPH-WQZE-202508-Y-1 | PAGOPA  |                  | 400     |


  @appIo
  Scenario: [QR_CODE_8] Lettura tramite AppIO di una notifica da parte di un delegato PF da un delegatore PF
    Given "Mario Cucumber" rifiuta se presente la delega ricevuta "Mario Gherkin"
    And "Mario Cucumber" viene delegato da "Mario Gherkin" per comune "Comune_Root"
    And "Mario Cucumber" accetta la delega "Mario Gherkin"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi "ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    And a seguito della scansione del QR Code, la notifica può essere recuperata tramite AppIO dal delegato: Mario Cucumber
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO dal delegato: Mario Cucumber
    And il download non ha prodotto errori
    Then a seguito della scansione del QR Code, la notifica può essere recuperata da: Mario Gherkin tramite AppIO
#    [TC_18]
    And a seguito della scansione del QR Code, la notifica non può essere recuperata da: Mario Cucumber tramite AppIO senza passare l'id della delega

  @appIo @deleghe1
  Scenario: [QR_CODE_9] Lettura tramite AppIO di una notifica da parte di un PF delegato da una PG
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "Mario Gherkin" viene delegato da "CucumberSpa" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "CucumberSpa"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi "ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Gherkin scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    And a seguito della scansione del QR Code, la notifica può essere recuperata tramite AppIO dal delegato: Mario Gherkin
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO dal delegato: Mario Gherkin
    And il download non ha prodotto errori

    #https://pagopa.atlassian.net/browse/PN-15758
  @appIo
  Scenario: [QR_CODE_10] Lettura tramite AppIO di una notifica da parte di un PF ma con header lollipop diverso da quello atteso del destinatario della notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Leonardo da Vinci e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Leonardo da Vinci scansiona il QR Code per recuperare i dettagli della notifica e viene passato l'header lollipop
    Then si verifica che la chiamata abbia ritornato uno status code: 403
    #https://pagopa.atlassian.net/browse/PN-16249
    And a seguito della scansione del QR Code, la notifica può essere recuperata da: Leonardo da Vinci tramite AppIO passando un header src non valido
    Then si verifica che la chiamata abbia ritornato uno status code: 403

  @appIo
  Scenario: [QR_CODE_11] Viene recuperato il qrcode da notifica di 60 giorni
    And viene generato il QR Code "corretto" per la notifica di 60 giorni
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    And l'operazione non ha prodotti errori
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO
    And il download non ha prodotto errori


  @appIo
  Scenario: [QR_CODE_HF_12] Viene recuperato il qrcode da notifica di 60 giorni
    And viene generato il QR Code "corretto" per la notifica di 60 giorni
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "1.0"
    And l'operazione non ha prodotti errori
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  @appIo
  Scenario: [QR_CODE_13] Viene creata una notifica e recuperato il documento di pagamento PAGOPA tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "1.0"
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  @appIo
  Scenario: [QR_CODE_14] Viene creata una notifica e recuperato l'F24 tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | NAPOLI               |
      | physicalAddress_province     | NA                   |
      | physicalAddress_zip          | 80124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Gherkin scansiona il QR Code per recuperare i dettagli della notifica con versione "1.0"
    Then a seguito della scansione del QR Code, il documento di pagamento "F24" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  @appIo
  Scenario: [QR_CODE_HF_1] Viene creata una notifica e recuperato il documento di pagamento PAGOPA tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "esteso" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  @appIo
  Scenario: [QR_CODE_HF_2] Viene creata una notifica e recuperato l'F24 tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | NAPOLI               |
      | physicalAddress_province     | NA                   |
      | physicalAddress_zip          | 80124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "esteso" per la notifica appena creata
    And l'utente Mario Gherkin scansiona il QR Code per recuperare i dettagli della notifica con versione "0.9"
    Then a seguito della scansione del QR Code, il documento di pagamento "F24" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  @appIo
  Scenario: [QR_CODE_HF_3] Viene creata una notifica e recuperato il documento di pagamento PAGOPA tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "esteso" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica con versione "1.0"
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  @appIo
  Scenario: [QR_CODE_HF_4] Viene creata una notifica e recuperato il documento di pagamento PAGOPA tramite AppIO (/delivery/notifications/received/{iun}/attachments/payment/{attachmentName})
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | NAPOLI               |
      | physicalAddress_province     | NA                   |
      | physicalAddress_zip          | 80124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene generato il QR Code "esteso" per la notifica appena creata
    And l'utente Mario Gherkin scansiona il QR Code per recuperare i dettagli della notifica con versione "1.0"
    Then a seguito della scansione del QR Code, il documento di pagamento "F24" può essere recuperata tramite AppIO
    And il download non ha prodotto errori
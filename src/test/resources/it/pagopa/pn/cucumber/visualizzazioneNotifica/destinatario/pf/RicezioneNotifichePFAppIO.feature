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
    Then il tentativo di recupero con appIO ha prodotto un errore con status code "404"
  #viene richiesto il codice QR per lo IUN {string}

  #[TC_1]
  @appIo
  Scenario: [QR_CODE_1] Viene scansionato il QR Code sull'AAR per recuperare i dettagli della notifica tramite appIO
    And l'utente "Mario Cucumber" "ACCETTA" i termini di servizio di tipo: TOS
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica
#    [TC_5]
    And a seguito della scansione del QR Code, la notifica può essere recuperata da: Mario Cucumber tramite AppIO
    # [TC_8] SI PROVA A RIFIUTARE I TOS E RIACCEDERE SENZA SUCCESSO AL DETTAGLIO DELLA NOTIFICA
    And l'utente "Mario Cucumber" "NON ACCETTA" i termini di servizio di tipo: TOS
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica
    And a seguito della scansione del QR Code, la notifica può essere recuperata da: Mario Cucumber tramite AppIO
    #[TC_3] SI PROVA A RIACCEDERE ALLA NOTIFICA SCANSIONANDO UN QR CODE NON VALIDO
    And l'utente "Mario Cucumber" "ACCETTA" i termini di servizio di tipo: TOS
    And viene generato il QR Code "malformato" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica
    And si verifica che la chiamata abbia ritornato uno status code: 404

    
  #[TC_2]
  @appIo
  Scenario Outline: [QR_CODE_2] Viene scansionato il QR Code sull'AAR per recuperare i dettagli della notifica tramite appIO
    And l'utente "Mario Cucumber" "ACCETTA" i termini di servizio di tipo: TOS
    And viene chiamato l'endpoint "checkQRCode" con i seguenti params:
    | taxId             | <PARAM_1>   |
    | aarQrCodeValue    | <PARAM_2>   |
    Then si verifica che la chiamata abbia ritornato uno status code: 400
    Examples:
    | PARAM_1           | PARAM_2 |
    |                   | UFlEQS1RR05LLURWUlUtMjAyNTA4LU4tMV9QRi00ZmM3NWRmMy0wOTEzLTQwN2UtYmRhYS1lNTAzMjk3MDhiN2RfM2Y2MWQ1N2QtMmJmOC00NGU4LWFhMmMtNjBlZmNmODY3YTVh  |
    | FRMTTR76M06B715E  |                                                                                                                                           |

  #[TC_6] [TC_7]
  @appIo
  Scenario Outline: [QR_CODE_3] Viene scansionato il QR Code sull'AAR per recuperare i dettagli della notifica tramite appIO
    And l'utente "Mario Cucumber" "ACCETTA" i termini di servizio di tipo: TOS
    And viene chiamato l'endpoint "getReceivedNotification" con i seguenti params:
      | iun             | <PARAM_1>   |
      | taxId           | <PARAM_2>   |
    Then si verifica che la chiamata abbia ritornato uno status code: 400
    Examples:
      | PARAM_1                     | PARAM_2           |
      |                             | FRMTTR76M06B715E  |
      | ERRA-T000-0000-ERRATO-0-0   | FRMTTR76M06B715E  |
      | NAUZ-WNPH-WQZE-202508-Y-1   |                   |

  @appIo
  Scenario: [QR_CODE_4]
    And l'utente "Mario Cucumber" "ACCETTA" i termini di servizio di tipo: TOS
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And viene generato il QR Code "corretto" per la notifica appena creata
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica
    Then a seguito della scansione del QR Code, il documento notificato può essere recuperata tramite AppIO

  #[TC_11]
  @appIo
  Scenario Outline: [QR_CODE_5]
    And l'utente "Mario Cucumber" "ACCETTA" i termini di servizio di tipo: TOS
    And viene chiamato l'endpoint "getSentNotificationDocument" con i seguenti params:
      | iun             | <PARAM_1>   |
      | docIdx          | <PARAM_2>   |
      | taxId           | <PARAM_3>   |
    Then si verifica che la chiamata abbia ritornato uno status code: 400
    Examples:
      | PARAM_1                     | PARAM_2     | PARAM_3                    |
      |                             | 0           | FRMTTR76M06B715E           |
      | ERRA-T000-0000-ERRATO-0-0   |             | FRMTTR76M06B715E           |
      | NAUZ-WNPH-WQZE-202508-Y-1   | 0           |                            |

  #[TC_12]
  @appIo
  Scenario: [QR_CODE_6]
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
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica
    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO
    And il download non ha prodotto errori

  #[TC_13] [TC_14]
  @appIo
  Scenario Outline: [QR_CODE_7]
    And l'utente "Mario Cucumber" "ACCETTA" i termini di servizio di tipo: TOS
    And viene chiamato l'endpoint "getSentNotificationDocument" con i seguenti params:
      | iun             | <PARAM_1>   |
      | attachmentName  | <PARAM_2>   |
      | taxId           | <PARAM_3>   |
      | attachmentIdx   | <PARAM_4>   |
    Then si verifica che la chiamata abbia ritornato uno status code: 404
    Examples:
      | PARAM_1                     | PARAM_2     | PARAM_3                    | PARAM_4  |
      |                             | F24         | FRMTTR76M06B715E           | 0        |
      | ERRA-T000-0000-ERRATO-0-0   | F24         | FRMTTR76M06B715E           | 0        |
      | NAUZ-WNPH-WQZE-202508-Y-1   |             | FRMTTR76M06B715E           | 0        |
      | NAUZ-WNPH-WQZE-202508-Y-1   | PAGOPA      |                            | 0        |
    #considera che PARAM_4 non è obbligatorio

  @appIo
  Scenario: [QR_CODE_8] Lettura tramite AppIO di una notifica da parte di un delegato
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
    And l'utente Mario Cucumber scansiona il QR Code per recuperare i dettagli della notifica
    And a seguito della scansione del QR Code, la notifica può essere recuperata tramite AppIO dal delegato: Mario Cucumber

    Then a seguito della scansione del QR Code, il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO dal delegato: Mario Cucumber
    And il download non ha prodotto errori

    Then a seguito della scansione del QR Code, la notifica può essere recuperata da: Mario Gherkin tramite AppIO
#    [TC_18]
    And a seguito della scansione del QR Code, la notifica può essere recuperata da: Mario Cucumber tramite AppIO senza passare l'id della delega




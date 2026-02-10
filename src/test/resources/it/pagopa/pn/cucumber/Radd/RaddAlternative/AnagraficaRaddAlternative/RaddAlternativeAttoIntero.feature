Feature: Radd Alternative Atto Intero

  # [RADD_FILTRO_ATTO-INTERO_4] - [RADD_FILTRO_ATTO-INTERO_6] - [RADD_FILTRO_ATTO-INTERO_7]: I seguenti test
  # sono stati rimossi poiché la logica sui filtri dei documenti per i CAP coperto da RADD è stata deprecata

  @raddAttoIntero
  Scenario: [RADD_FILTRO_ATTO-INTERO_1] invio notifica 890 coperto da RADD e controllo diminuzione costi filtro base (eseguire controllo manuale costi del F24)
    Given viene generata una nuova notifica
      | subject               | notifica analogica filtro base |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | REGISTERED_LETTER_890          |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_3_PG;                      |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_890           |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30121                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 873 e il peso di 10 nei details dell'elemento di timeline letto

  @raddAttoIntero
  Scenario: [RADD_FILTRO_ATTO-INTERO_2] invio notifica AR coperto da RADD e controllo diminuzione costi filtro base (eseguire controllo manuale costi del F24)
    Given viene generata una nuova notifica
      | subject               | notifica analogica filtro base |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | AR_REGISTERED_LETTER           |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_3_PG;                      |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30121                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 364 e il peso di 10 nei details dell'elemento di timeline letto

  @raddAttoIntero
  Scenario: [RADD_FILTRO_ATTO-INTERO_3] invio notifica RS coperto da RADD e controllo diminuzione costi filtro base (eseguire controllo manuale costi del F24)
    Given viene generata una nuova notifica
      | subject            | notifica analogica filtro base |
      | senderDenomination | Comune di palermo              |
      | feePolicy          | DELIVERY_MODE                  |
      | document           | DOC_3_PG;                      |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it         |
      | physicalAddress_address      | Via@ok_RS            |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30121                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo di 224 e il peso di 10 nei details dell'elemento di timeline letto

  @raddAttoIntero
  Scenario: [RADD_FILTRO_ATTO-INTERO_5] invio notifica RS coperto da RADD e controllo diminuzione costi filtro con rule typeWithNextResult DOCUMENT e AAR
    Given viene generata una nuova notifica
      | subject            | notifica analogica filtro AAR e DOCUMENT |
      | senderDenomination | Comune di palermo                        |
      | feePolicy          | DELIVERY_MODE                            |
      | document           | DOC_3_PG;                                |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it         |
      | physicalAddress_address      | Via@ok_RS            |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30122                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo di 224 e il peso di 10 nei details dell'elemento di timeline letto

  @raddAttoIntero
  Scenario: [RADD_FILTRO_ATTO-INTERO_8] invio notifica 890 coperto da RADD con cap con 2 configurazione
    Given viene generata una nuova notifica
      | subject               | notifica analogica filtro base |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | REGISTERED_LETTER_890          |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_3_PG;                      |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_890           |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 873 e il peso di 10 nei details dell'elemento di timeline letto


  @raddAttoIntero @uatEnvCondition
  Scenario: [RADD_FILTRO_ATTO-INTERO_9] invio notifica 890 con 2 tentativi coperto da RADD e controllo diminuzione costi filtro base (eseguire controllo manuale costi del F24)
    Given viene generata una nuova notifica
      | subject               | notifica analogica filtro base |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | REGISTERED_LETTER_890          |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_3_PG;                      |
    And destinatario
      | denomination                 | Alessandro Manzoni        |
      | taxId                        | MNZLSN99E05F205J          |
      | digitalDomicile              | NULL                      |
      | physicalAddress_address      | Via@FAIL-IRREPERIBILE_890 |
      | physicalAddress_municipality | VENEZIA                   |
      | physicalAddress_province     | VE                        |
      | physicalAddress_zip          | 30121                     |
      | payment_f24                  | PAYMENT_F24_STANDARD      |
      | title_payment                | F24_STANDARD_GHERKIN      |
      | apply_cost_f24               | SI                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 873 e il peso di 10 nei details dell'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 853 e il peso di 10 nei details dell'elemento di timeline letto


  @raddAttoIntero @mockEnvCondition
  Scenario: [RADD_FILTRO_ATTO-INTERO_9_UAT] invio notifica 890 con 2 tentativi coperto da RADD e controllo diminuzione costi filtro base (eseguire controllo manuale costi del F24)
    Given viene generata una nuova notifica
      | subject               | notifica analogica filtro base |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | REGISTERED_LETTER_890          |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_3_PG;                      |
    And destinatario
      | denomination                 | utenza radd               |
      | taxId                        | STTSGT90A01H501J          |
      | digitalDomicile              | NULL                      |
      | physicalAddress_address      | Via@FAIL-IRREPERIBILE_890 |
      | physicalAddress_municipality | VENEZIA                   |
      | physicalAddress_province     | VE                        |
      | physicalAddress_zip          | 30121                     |
      | payment_f24                  | PAYMENT_F24_STANDARD      |
      | title_payment                | F24_STANDARD_GHERKIN      |
      | apply_cost_f24               | SI                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 880 e il peso di 10 nei details dell'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 853 e il peso di 10 nei details dell'elemento di timeline letto


  @raddAttoIntero @uatEnvCondition
  Scenario: [RADD_FILTRO_ATTO-INTERO_10] invio notifica AR con 2 tentativi coperto da RADD e controllo diminuzione costi filtro base (eseguire controllo manuale costi del F24)
    Given viene generata una nuova notifica
      | subject               | notifica analogica filtro base |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | AR_REGISTERED_LETTER           |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_3_PG;                      |
    And destinatario
      | denomination                 | Alessandro Manzoni       |
      | taxId                        | MNZLSN99E05F205J         |
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL-IRREPERIBILE_AR |
      | physicalAddress_municipality | VENEZIA                  |
      | physicalAddress_province     | VE                       |
      | physicalAddress_zip          | 30121                    |
      | payment_f24                  | PAYMENT_F24_STANDARD     |
      | title_payment                | F24_STANDARD_GHERKIN     |
      | apply_cost_f24               | SI                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 364 e il peso di 10 nei details dell'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 364 e il peso di 10 nei details dell'elemento di timeline letto


  @raddAttoIntero @mockEnvCondition
  Scenario: [RADD_FILTRO_ATTO-INTERO_10_UAT] invio notifica AR con 2 tentativi coperto da RADD e controllo diminuzione costi filtro base (eseguire controllo manuale costi del F24)
    Given viene generata una nuova notifica
      | subject               | notifica analogica filtro base |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | AR_REGISTERED_LETTER           |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_3_PG;                      |
    And destinatario
      | denomination                 | utenza radd              |
      | taxId                        | STTSGT90A01H501J         |
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL-IRREPERIBILE_AR |
      | physicalAddress_municipality | VENEZIA                  |
      | physicalAddress_province     | VE                       |
      | physicalAddress_zip          | 30121                    |
      | payment_f24                  | PAYMENT_F24_STANDARD     |
      | title_payment                | F24_STANDARD_GHERKIN     |
      | apply_cost_f24               | SI                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 366 e il peso di 10 nei details dell'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 460 e il peso di 10 nei details dell'elemento di timeline letto

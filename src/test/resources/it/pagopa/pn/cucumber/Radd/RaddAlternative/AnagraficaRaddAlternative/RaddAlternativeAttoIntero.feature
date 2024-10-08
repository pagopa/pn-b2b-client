Feature: Radd Alternative Atto Intero

  #TODO aggiungere integrazione per prendere i dati dal DynamoDb di aws per il controllo del campo del filtro

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 818 e il peso di 10 nei details del'elemento di timeline letto

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 352 e il peso di 10 nei details del'elemento di timeline letto

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo di 214 e il peso di 10 nei details del'elemento di timeline letto

  @raddAttoIntero
  Scenario Outline: [RADD_FILTRO_ATTO-INTERO_4] invio notifica 890 coperto da RADD e controllo diminuzione costi con filtro con rule typeWithNextResult
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>             |
      | senderDenomination    | Comune di palermo     |
      | physicalCommunication | REGISTERED_LETTER_890 |
      | feePolicy             | DELIVERY_MODE         |
      | document              | DOC_3_PG;             |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_890           |
      | physicalAddress_municipality | <MUNICIPALITY>       |
      | physicalAddress_province     | <PROVINCE>           |
      | physicalAddress_zip          | <CAP>                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di <COSTO> e il peso di <PESO> nei details del'elemento di timeline letto
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                                                       | PESO |
      | 30133 | 822   | VENEZIA      | VE       | notifica filtro con AAR, ATTACHMENT_PAGOPA e LEGAL_FACT       | 15   |
      | 30135 | 989   | VENEZIA      | VE       | notifica filtro con AAR, ATTACHMENT_F24 e LEGAL_FACT_EXTERNAL | 25   |
      | 30122 | 825   | VENEZIA      | VE       | notifica filtro con DOCUMENT e AAR                            | 20   |

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo di 214 e il peso di 10 nei details del'elemento di timeline letto

  @raddAttoIntero
  Scenario Outline: [RADD_FILTRO_ATTO-INTERO_6] invio notifica AR coperto da RADD e controllo diminuzione costi in base al filtro con typeWithSuccessResult
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | feePolicy             | DELIVERY_MODE        |
      | document              | DOC_3_PG;            |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | <MUNICIPALITY>       |
      | physicalAddress_province     | <PROVINCE>           |
      | physicalAddress_zip          | <CAP>                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di <COSTO> e il peso di <PESO> nei details del'elemento di timeline letto
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                                                               | PESO |
      | 30141 | 355   | VENEZIA      | VE       | notifica filtro accetazione AAR, ATTACHMENT_PAGOPA e LEGAL_FACT       | 15   |
      | 30142 | 408   | VENEZIA      | VE       | notifica filtro accetazione AAR, ATTACHMENT_F24 e LEGAL_FACT_EXTERNAL | 25   |
      | 30171 | 358   | VENEZIA      | VE       | notifica filtro accetazione DOCUMENT e AAR                            | 20   |

  @raddAttoIntero
  Scenario: [RADD_FILTRO_ATTO-INTERO_7] invio notifica AR coperto da RADD e controllo diminuzione costi in base al filtro acceptAttachment e discardAttachment settato
    Given viene generata una nuova notifica
      | subject               | notifica analogica con filtro accetazione e scarto |
      | senderDenomination    | Comune di palermo                                  |
      | physicalCommunication | AR_REGISTERED_LETTER                               |
      | feePolicy             | DELIVERY_MODE                                      |
      | document              | DOC_3_PG;                                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30172                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 355 e il peso di 15 nei details del'elemento di timeline letto

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 818 e il peso di 10 nei details del'elemento di timeline letto


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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 819 e il peso di 10 nei details del'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 800 e il peso di 10 nei details del'elemento di timeline letto


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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 818 e il peso di 10 nei details del'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 800 e il peso di 10 nei details del'elemento di timeline letto


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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 352 e il peso di 10 nei details del'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 352 e il peso di 10 nei details del'elemento di timeline letto


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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato il costo di 351 e il peso di 10 nei details del'elemento di timeline letto
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato il costo di 440 e il peso di 10 nei details del'elemento di timeline letto


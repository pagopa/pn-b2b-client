Feature: Sperimentazione Radd wave 1

  #Configurazioni Prepare:
  # Parameter Store:
      #radd-experimentation-zip-1
      #radd-experimentation-zip-2
      #radd-experimentation-zip-3
      #radd-experimentation-zip-4
      #radd-experimentation-zip-5



#il cap deve essere dentro il parameterStore (coperto da sperimentazione radd) e non presente nella tabella pn-AttachmentsConfig (quindi non coperto da sportello radd)
  @raddWave
  Scenario: [RADD_WAVE_1] - Invio notifica digitale (1° tentativo OK) a destinatario con CAP in fase di sperimentazione
    Given viene generata una nuova notifica
      | subject            | notifica digitale |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
      | document           | DOC_1_PG;         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "F24"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_1_1] - Invio notifica digitale (1° tentativo OK) a destinatario con CAP in fase di sperimentazione - test per manuale (con pec reale)
    Given viene generata una nuova notifica
      | subject            | notifica digitale |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
      | document           | DOC_1_PG;         |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_2] - Invio notifica analogica (1° tentativo OK) a destinatario con CAP in fase di sperimentazione ma non coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject               | notifica analogica   |
      | senderDenomination    | Comune di palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | feePolicy             | DELIVERY_MODE        |
      | document              | DOC_1_PG;            |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70128                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato che il peso della busta cartacea sia di 35
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_3] - Invio notifica digitale (fallimento invii, quindi RS) a destinatario con CAP in fase di sperimentazione ma non coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica fallimento digitale |
      | senderDenomination | Comune di palermo            |
      | feePolicy          | DELIVERY_MODE                |
      | document           | DOC_1_PG;                    |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it         |
      | physicalAddress_address      | Via@ok_RS            |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70121                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato che il peso della busta cartacea sia di 10
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
Scenario: [RADD_WAVE_4] - Invio notifica digitale (1° tentativo OK) a destinatario con CAP in fase di sperimentazione ma non coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica digitale |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
      | document           | DOC_1_PG;         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70123                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "F24"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_4_1] - Invio notifica digitale (1° tentativo OK) a destinatario con CAP in fase di sperimentazione ma non coperto dai servizi RADD - test per manuale (con pec reale)
    Given viene generata una nuova notifica
      | subject            | notifica digitale |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
      | document           | DOC_1_PG;         |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70123                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "F24"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_5] - Invio notifica analogica (1° tentativo OK) a destinatario con CAP in fase di sperimentazione, coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica analogica |
      | senderDenomination | Comune di palermo  |
      | feePolicy          | FLAT_RATE          |
      | document           | DOC_1_PG;          |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_890           |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_f24                  | PAYMENT_F24_FLAT     |
      | title_payment                | F24_STANDARD_GHERKIN |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato che il peso della busta cartacea sia di 10
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_6] - Invio notifica digitale (fallimento invii, quindi RS) a destinatario con CAP in fase di sperimentazione, coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica fallimento digitale |
      | senderDenomination | Comune di palermo            |
      | feePolicy          | DELIVERY_MODE                |
      | document           | DOC_1_PG;                    |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it         |
      | physicalAddress_address      | Via@ok_RS            |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato che il peso della busta cartacea sia di 10
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_7] - Invio notifica analogica (1° tentativo OK) a destinatario con CAP non in fase di sperimentazione ma coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica analogica |
      | senderDenomination | Comune di palermo  |
      | feePolicy          | DELIVERY_MODE      |
      | document           | DOC_1_PG;          |
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
    And viene verificato che il peso della busta cartacea sia di 35
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"


  @raddWave
  Scenario: [RADD_WAVE_8] - Invio notifica digitale (1° tentativo OK) a destinatario con CAP non in fase di sperimentazione ma coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica digitale |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "F24"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"


  @raddWave
  Scenario: [RADD_WAVE_8_1] - Invio notifica digitale (1° tentativo OK) a destinatario con CAP non in fase di sperimentazione ma coperto dai servizi RADD - test per manuale (con pec reale)
    Given viene generata una nuova notifica
      | subject            | notifica digitale |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    Then si verifica il contenuto degli attacchment da inviare nella pec del destinatario 0 con 4 allegati
    And si verifica il contenuto della pec abbia 1 attachment di tipo "AAR"
    And si verifica il contenuto della pec abbia 2 attachment di tipo "NOTIFICATION_ATTACHMENTS"
    And si verifica il contenuto della pec abbia 1 attachment di tipo "F24"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"


  @raddWave
  Scenario: [RADD_WAVE_9] - Invio notifica digitale (fallimento invii, quindi RS) a destinatario con CAP non in fase di sperimentazione ma coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica fallimento digitale |
      | senderDenomination | Comune di palermo            |
      | feePolicy          | DELIVERY_MODE                |
      | document           | DOC_1_PG;                    |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it         |
      | physicalAddress_address      | Via@ok_RS            |
      | physicalAddress_municipality | VENEZIA              |
      | physicalAddress_province     | VE                   |
      | physicalAddress_zip          | 30124                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato che il peso della busta cartacea sia di 10
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"


  @raddWave
  Scenario: [RADD_WAVE_10] - Invio notifica analogica a destinatario con stato estero ma CAP coincidente a uno di quelli presenti in sperimentazione
    Given viene generata una nuova notifica
      | subject               | notifica analogica estero |
      | senderDenomination    | Comune di palermo         |
      | physicalCommunication | AR_REGISTERED_LETTER      |
      | feePolicy             | DELIVERY_MODE             |
      | document              | DOC_1_PG;                 |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                 |
      | physicalAddress_address      | Via@ok_AR            |
      | physicalAddress_municipality | PARIGI               |
      | physicalAddress_province     | Paris                |
      | physicalAddress_zip          | 70129                |
      | physicalAddress_State        | FRANCIA              |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato che il peso della busta cartacea sia di 35
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"


  @raddWave
  Scenario: [RADD_WAVE_11] - Invio notifica analogica (che implica un 2° tentativo) a destinatario con CAP della prima spedizione in fase di sperimentazione non coperto dai servizi RADD
                                                                                    e CAP della seconda spedizione coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject            | notifica analogica 2 tentativi |
      | senderDenomination | Comune di palermo              |
      | feePolicy          | DELIVERY_MODE                  |
      | document           | DOC_1_PG;                      |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@FAIL-Discovery_890 |
      | physicalAddress_municipality | BARI                   |
      | physicalAddress_province     | BA                     |
      | physicalAddress_zip          | 70125                  |
      | payment_f24                  | PAYMENT_F24_STANDARD   |
      | title_payment                | F24_STANDARD_GHERKIN   |
      | apply_cost_f24               | SI                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato che il peso della busta cartacea sia di 35
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato che il peso della busta cartacea sia di 10
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_12] - Invio notifica analogica (che implica un 2° tentativo) a destinatario con CAP della prima spedizione in fase di sperimentazione coperto dai servizi RADD
                                                                                    e CAP della seconda spedizione NON coperto dai servizi RADD
    Given viene generata una nuova notifica
      | subject               | notifica analogica 2 tentativi |
      | senderDenomination    | Comune di palermo              |
      | physicalCommunication | AR_REGISTERED_LETTER           |
      | feePolicy             | DELIVERY_MODE                  |
      | document              | DOC_1_PG;                      |
    And destinatario
      | denomination                 | Giovanna D'Arco       |
      | taxId                        | DRCGNN12A46A326K      |
      | digitalDomicile              | NULL                  |
      | physicalAddress_address      | Via@FAIL-Discovery_AR |
      | physicalAddress_municipality | BARI                  |
      | physicalAddress_province     | BA                    |
      | physicalAddress_zip          | 70129                 |
      | payment_f24                  | PAYMENT_F24_STANDARD  |
      | title_payment                | F24_STANDARD_GHERKIN  |
      | apply_cost_f24               | SI                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato che il peso della busta cartacea sia di 10
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato che il peso della busta cartacea sia di 35
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"


  @raddWave
  Scenario: [RADD_WAVE_13] - Invio notifica analogica (che implica un 2° tentativo) a destinatario con CAP della prima spedizione non in fase di sperimentazione
    Given viene generata una nuova notifica
      | subject            | notifica analogica 2 tentativi |
      | senderDenomination | Comune di palermo              |
      | feePolicy          | DELIVERY_MODE                  |
      | document           | DOC_1_PG;                      |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@FAIL-Discovery_890 |
      | physicalAddress_municipality | COSENZA                |
      | physicalAddress_province     | CS                     |
      | physicalAddress_zip          | 87100                  |
      | payment_f24                  | PAYMENT_F24_STANDARD   |
      | title_payment                | F24_STANDARD_GHERKIN   |
      | apply_cost_f24               | SI                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato che il peso della busta cartacea sia di 35
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And viene verificato che il peso della busta cartacea sia di 35
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"

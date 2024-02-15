Feature: calcolo costo notifica in base hai grammi con notifiche 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-20GR_1] Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>             |
      | senderDenomination    | Comune di palermo     |
      | physicalCommunication | REGISTERED_LETTER_890 |
      | feePolicy             | DELIVERY_MODE         |
      | document              | DOC_4_PG;             |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1103  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 957   | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 953   | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 906   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 860   | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 924   | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 978   | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-21GR_2] Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>             |
      | senderDenomination    | Comune di palermo     |
      | physicalCommunication | REGISTERED_LETTER_890 |
      | feePolicy             | DELIVERY_MODE         |
      | document              | DOC_5_PG;             |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1200  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1043  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1101  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1066  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1012  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1088  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1153  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-50GR_3] Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>             |
      | senderDenomination    | Comune di palermo     |
      | physicalCommunication | REGISTERED_LETTER_890 |
      | feePolicy             | DELIVERY_MODE         |
      | document              | DOC_8_PG;DOC_8_PG;    |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1215  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1058  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1116  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1081  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1027  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1103  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1168     | MONTERADO      | AN       | notifica analogica RECAPITISTA |

  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-51GR_4] Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                  |
      | senderDenomination    | Comune di palermo          |
      | physicalCommunication | REGISTERED_LETTER_890      |
      | feePolicy             | DELIVERY_MODE              |
      | document              | DOC_8_PG;DOC_8_PG;DOC_1_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1218  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1061  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1119  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1084  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1030  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1106  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1171  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-100GR_5] Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                     |
      | senderDenomination    | Comune di palermo                             |
      | physicalCommunication | REGISTERED_LETTER_890                         |
      | feePolicy             | DELIVERY_MODE                                 |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG; |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1245  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1088  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1146  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1111  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1057  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1133  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1198     | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-101GR_6] Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                    |
      | senderDenomination    | Comune di palermo                            |
      | physicalCommunication | REGISTERED_LETTER_890                        |
      | feePolicy             | DELIVERY_MODE                                |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1337  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1167  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1229  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1189  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1130  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1213  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1284  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-250GR_7] Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                       |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | REGISTERED_LETTER_890                                           |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1424  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1254  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1316  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1276  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1217  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1300  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1371     | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-251GR_8] Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                       |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | REGISTERED_LETTER_890                                           |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1427  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1257  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1319  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1279  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1220  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1303  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1374     | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-350GR_9] Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                        |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | REGISTERED_LETTER_890                                            |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1484  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1314  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1376  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1336  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1277  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1360  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1431     | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-351GR_10] Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                        |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | REGISTERED_LETTER_890                                            |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1602  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1414  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1483  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1437  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1372  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1463  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1541  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-1000GR_11] Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                                                        |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | REGISTERED_LETTER_890                                                                            |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1989  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1801  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1870  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1824  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1759  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1850  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1928  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-1001GR_12] Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                                                        |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | REGISTERED_LETTER_890                                                                            |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1992  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1804  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1873  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1827  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1762  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1853  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1931  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_890-2000GR_13] Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                                                        |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | REGISTERED_LETTER_890                                                                            |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_300_PG;DOC_300_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 2589  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 2401  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 2470  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 2424  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 2359  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 2450  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2528  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


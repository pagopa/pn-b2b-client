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
      | 05010 | 1111  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 963   | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 960   | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 912   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 867   | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 931   | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 985   | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 1109  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1051  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1109  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1074  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1019  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1096  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1162  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 1224  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1066  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1124  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1089   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1034  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1111  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1177  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

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
      | 05010 | 1227  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1069  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1127  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1092   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1037  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1114  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1180  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 1254  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1096  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1154  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1119   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1064  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1141  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1207  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 1346  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1175  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1238  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1198   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1138  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1222  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1293  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 1433  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1262  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1325  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1285   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1225  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1309  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1380  | MONTERADO      | AN       | notifica analogica RECAPITISTA |



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
      | 05010 | 1436  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1265  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1328  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1288  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1228  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1312  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1383  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 1493  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1322  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1385  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1345   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1285  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1369  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1440  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 1613  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1423  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1492  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1446   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1381  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1473  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1551  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 2000  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1810  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1879  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1833   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1768  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1860  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1938  | MONTERADO      | AN       | notifica analogica RECAPITISTA |



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
      | 05010 | 2003  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1813  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1882  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1836   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1771  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1863  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1941  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


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
      | 05010 | 2600  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 2410  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 2479  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 2433   | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 2368  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 2460  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2538  | MONTERADO      | AN       | notifica analogica RECAPITISTA |


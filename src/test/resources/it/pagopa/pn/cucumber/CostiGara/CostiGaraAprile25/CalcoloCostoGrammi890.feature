Feature: calcolo costo notifica in base hai grammi con notifiche 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-20GR_1] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY          | PROVINCE | SUBJECT                        |
      | 05010 | 1092  | COLLELUNGO            | TR       | notifica analogica FSU         |
      | 06031 | 1092  | BEVAGNA               | PG       | notifica analogica RECAPITISTA |
      | 64011 | 945   | ALBA ADRIATICA        | TE       | notifica analogica RECAPITISTA |
      | 00010 | 967   | CASAPE                | RM       | notifica analogica RECAPITISTA |
      | 70010 | 918   | ADELFIA               | BA       | notifica analogica RECAPITISTA |
      | 10010 | 987   | ANDRATE               | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1044  | MONTERADO             | AN       | notifica analogica RECAPITISTA |
      | 35049 | 987   | SANTA CATERINA D'ESTE | PD       | notifica analogica RECAPITISTA |
      | 90052 | 918   | CERDA                 | PA       | notifica analogica RECAPITISTA |
      | 06012 | 1092  | CERBARA               | PG       | notifica analogica RECAPITISTA |
      | 17011 | 1092  | ALBISOLA CAPO         | SV       | notifica analogica RECAPITISTA |
      | 87020 | 1092  | ACQUAPPESA            | CS       | notifica analogica RECAPITISTA |

      | 21009 | 967   | BARDELLO              | VA       | notifica analogica RECAPITISTA |
      | 14027 | 987   | TONENGO               | AT       | notifica analogica RECAPITISTA |
      | 33100 | 1092  | UDINE                 | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1092  | ROSARNO               | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1092  | BERGHIDDA             | SS       | notifica analogica RECAPITISTA |
      | 06135  | 1092 | PERUGIA               | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-21GR_2] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1195  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1195  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1090  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1068  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1013  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1090  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1155  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1195  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 11100 | 1195  | AOSTA          | AO       | notifica analogica RECAPITISTA |
      | 33100 | 1195  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1195  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1195  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1195  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-50GR_3] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1212  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1212  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1106  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1084  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1030  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1106  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1171  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 90054 | 1030  | GERACI SICULO  | PA       | notifica analogica RECAPITISTA |
      | 88071 | 1212  | STALETTI       | CZ       | notifica analogica RECAPITISTA |
      | 06012 | 1212  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 38097 | 1212  | TERRE D'ADIGE  | TN       | notifica analogica RECAPITISTA |
      | 33100 | 1212  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1212  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1212  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1212  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-51GR_4] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1215  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1215  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1109  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1087  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1033  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1110  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1174  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1215  | CERBARA        | PG       | notifica analogica RECAPITISTA |
      | 33100 | 1215  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1215  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1215  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1215  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-100GR_5] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1244  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1244  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1139  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1117  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1062  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1139  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1204  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 90072 | 1062  | ALTOFONTE      | PA       | notifica analogica RECAPITISTA |

      | 06012 | 1244  | CERBARA        | PG       | notifica analogica RECAPITISTA |
      | 33100 | 1244  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1244  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1244  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1244  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-101GR_6] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1335  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1335  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1221  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1195  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1137  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1220  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1290  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1335  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 86081 | 1221  | AGNONE         | IS       | notifica analogica RECAPITISTA |
      | 33100 | 1335  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1335  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1335  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1335  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-250GR_7] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY        | PROVINCE | SUBJECT                        |
      | 05010 | 1429  | COLLELUNGO          | TR       | notifica analogica FSU         |
      | 06031 | 1429  | BEVAGNA             | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1315  | ALBA ADRIATICA      | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1290  | CASAPE              | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1232  | ADELFIA             | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1314  | ANDRATE             | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1384  | MONTERADO           | AN       | notifica analogica RECAPITISTA |
      | 90082 | 1232  | SANTA CRISTINA GELA | PA       | notifica analogica RECAPITISTA |

      | 06012 | 1429  | CERBARA             | PG       | notifica analogica RECAPITISTA |
      | 33100 | 1429  | UDINE               | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1429  | ROSARNO             | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1429  | BERGHIDDA           | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1429  | PERUGIA             | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-251GR_8] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1433  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1433  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1319  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1293  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1235  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1318  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1388  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1433  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 34129 | 1433  | TRIESTE        | TS       | notifica analogica RECAPITISTA |
      | 33100 | 1433  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1433  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1433  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1433  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-350GR_9] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1495  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1495  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1381  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1355  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1297  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1380  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1450  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 94028 | 1297  | VILLAROSA      | EN       | notifica analogica RECAPITISTA |

      | 06012 | 1495  | CERBARA        | PG       | notifica analogica RECAPITISTA |
      | 33100 | 1495  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1495  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1495  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1495  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-351GR_10] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 1611  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1611  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1486  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1458  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1393  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1484  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1561  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1611  | CERBARA        | PG       | notifica analogica RECAPITISTA |
      | 19126 | 1611  | LA SPEZIA      | SP       | notifica analogica RECAPITISTA |
      | 33100 | 1611  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 1611  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 1611  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 1611  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-1000GR_11] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY        | PROVINCE | SUBJECT                        |
      | 05010 | 2033  | COLLELUNGO          | TR       | notifica analogica FSU         |
      | 06031 | 2033  | BEVAGNA             | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1908  | ALBA ADRIATICA      | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1880  | CASAPE              | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1814  | ADELFIA             | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1905  | ANDRATE             | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1983  | MONTERADO           | AN       | notifica analogica RECAPITISTA |
      | 95055 | 1814  | SAN PIETRO CLARENZA | CT       | notifica analogica RECAPITISTA |
      | 06012 | 2033  | CERBARA             | PG       | notifica analogica RECAPITISTA |
      | 33100 | 2033  | UDINE               | UD       | notifica analogica RECAPITISTA |
      | 89025 | 2033  | ROSARNO             | RC       | notifica analogica RECAPITISTA |
      | 07022 | 2033  | BERGHIDDA           | SS       | notifica analogica RECAPITISTA |
      | 06135 | 2033  | PERUGIA             | PG       | notifica analogica RECAPITISTA |

  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-1001GR_12] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 2036  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 2036  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1911  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1883  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1817  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1909  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1986  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 2036  | CERBARA        | PG       | notifica analogica RECAPITISTA |
      | 33100 | 2036  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 2036  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 2036  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 2036  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_890-2000GR_13] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE | SUBJECT                        |
      | 05010 | 2686  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 2686  | BEVAGNA        | PG       | notifica analogica RECAPITISTA |
      | 64011 | 2561  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 2533  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 2467  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 2559  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2636  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 2686  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 09121 | 2686  | CAGLIARI       | CA       | notifica analogica RECAPITISTA |
      | 33100 | 2686  | UDINE          | UD       | notifica analogica RECAPITISTA |
      | 89025 | 2686  | ROSARNO        | RC       | notifica analogica RECAPITISTA |
      | 07022 | 2686  | BERGHIDDA      | SS       | notifica analogica RECAPITISTA |
      | 06135 | 2686  | PERUGIA        | PG       | notifica analogica RECAPITISTA |


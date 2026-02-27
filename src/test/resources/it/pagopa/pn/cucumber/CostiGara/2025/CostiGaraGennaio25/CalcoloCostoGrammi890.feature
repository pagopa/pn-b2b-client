Feature: calcolo costo notifica in base hai grammi con notifiche 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-20GR_1] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
      | 00010 | 898   | CASAPE                | RM       | notifica analogica RECAPITISTA |
      | 70010 | 853   | ADELFIA               | BA       | notifica analogica RECAPITISTA |
      | 10010 | 916   | ANDRATE               | TO       | notifica analogica RECAPITISTA |
      | 60012 | 969   | MONTERADO             | AN       | notifica analogica RECAPITISTA |
      | 35049 | 916   | SANTA CATERINA D'ESTE | PD       | notifica analogica RECAPITISTA |
      | 90052 | 853   | CERDA                 | PA       | notifica analogica RECAPITISTA |
      | 06012 | 948   | CERBARA               | PG       | notifica analogica RECAPITISTA |
      | 17011 | 1092  | ALBISOLA CAPO         | SV       | notifica analogica RECAPITISTA |
      | 87020 | 948   | ACQUAPPESA            | CS       | notifica analogica RECAPITISTA |

      | 21009 | 898   | BARDELLO              | VA       | notifica analogica RECAPITISTA |
      | 14027 | 916   | TONENGO               | AT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-21GR_2] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
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
      | 00010 | 1056  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1003  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1078  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1142  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1036  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 11100 | 1036  | AOSTA          | AO       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-50GR_3] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
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
      | 00010 | 1073  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1019  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1095  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1159  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 90054 | 1019  | GERACI SICULO  | PA       | notifica analogica RECAPITISTA |
      | 88071 | 1053  | STALETTI       | CZ       | notifica analogica RECAPITISTA |
      | 06012 | 1053  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 38097 | 1053  | TERRE D'ADIGE  | TN       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-51GR_4] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
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
      | 00010 | 1076  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1022  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1098  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1162  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1056  | CERBARA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-100GR_5] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
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
      | 00010 | 1105  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1052  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1127  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1191  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 90072 | 1052  | ALTOFONTE      | PA       | notifica analogica RECAPITISTA |

      | 06012 | 1085  | CERBARA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-101GR_6] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
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
      | 00010 | 1183  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1124  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1206  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1276  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1163  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 86081 | 1221  | AGNONE         | IS       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-250GR_7] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
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
      | 00010 | 1277  | CASAPE              | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1219  | ADELFIA             | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1301  | ANDRATE             | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1370  | MONTERADO           | AN       | notifica analogica RECAPITISTA |
      | 90082 | 1219  | SANTA CRISTINA GELA | PA       | notifica analogica RECAPITISTA |

      | 06012 | 1257  | CERBARA             | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-251GR_8] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
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
      | 00010 | 1281  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1222  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1304  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1374  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1261  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 34129 | 1261  | TRIESTE        | TS       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-350GR_9] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
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
      | 00010 | 1343  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1285  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1366  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1436  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 94028 | 1285  | VILLAROSA      | EN       | notifica analogica RECAPITISTA |

      | 06012 | 1323  | CERBARA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-351GR_10] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
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
      | 00010 | 1442  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1378  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1468  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1544  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 1422  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 19126 | 1422  | LA SPEZIA      | SP       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-1000GR_11] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
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
      | 00010 | 1864  | CASAPE              | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1800  | ADELFIA             | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1890  | ANDRATE             | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1966  | MONTERADO           | AN       | notifica analogica RECAPITISTA |
      | 95055 | 1800  | SAN PIETRO CLARENZA | CT       | notifica analogica RECAPITISTA |

      | 06012 | 1843  | CERBARA             | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-1001GR_12] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
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
      | 00010 | 1867  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1803  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1893  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1969  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1847  | CERBARA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
  Scenario Outline: [CALCOLO-COSTO_890-2000GR_13] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
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
      | 00010 | 2517  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 2453  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 2543  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2619  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 06012 | 2497  | CERBARA        | PG       | notifica analogica RECAPITISTA |

      | 09121 | 2497  | CAGLIARI       | CA       | notifica analogica RECAPITISTA |


Feature: calcolo costo notifica in base hai grammi con notifiche 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-20GR_1] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1103  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1103  | CANTALUPO               | PG       | notifica analogica RECAPITISTA |
      | 64011 | 953   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 906   | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 861   | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 924   | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 978   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 35049 | 924   | SANTA CATERINA D'ESTE   | PD       | notifica analogica RECAPITISTA |
      | 90052 | 861   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 06012 | 957   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 17011 | 1103  | ALBISOLA CAPO           | SV       | notifica analogica RECAPITISTA |
      | 87020 | 957   | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-21GR_2] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
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
      | 05010 | 1207  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1207  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1100  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1067  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1012  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1089  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1154  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1046   | CERBARA       | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-50GR_3] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
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
      | 05010 | 1222  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1222  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1115  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1082  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1027  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1104  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1169  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 90054 | 1027  | GERACI SICULO  | PA       | notifica analogica RECAPITISTA |
      | 88071 | 1061  | STALETTI       | CZ       | notifica analogica RECAPITISTA |

      | 06012 | 1061   | CERBARA       | PG       | notifica analogica RECAPITISTA |

  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-51GR_4] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
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
      | 05010 | 1225  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1225  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1118  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1085  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1030  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1107  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1172  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1064   | CERBARA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-100GR_5] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
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
      | 05010 | 1252  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1252  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1145  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1112  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1057  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1134  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1199  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 90072 | 1057  | ALTOFONTE      | PA       | notifica analogica RECAPITISTA |

      | 06012 | 1091   | CERBARA       | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-101GR_6] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
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
      | 05010 | 1344  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1344  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1228  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1190  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1131  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1214  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1284  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1169   | CERBARA       | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-250GR_7] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
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
      | CAP   | COSTO | MUNICIPALITY          | PROVINCE | SUBJECT                        |
      | 05010 | 1431  | COLLELUNGO            | TR       | notifica analogica FSU         |
      | 06031 | 1431  | CANTALUPO             | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1315  | ALBA ADRIATICA        | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1277  | CASAPE                | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1218  | ADELFIA               | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1301  | ANDRATE               | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1371  | MONTERADO             | AN       | notifica analogica RECAPITISTA |
      | 90082 | 1218  | SANTA CRISTINA GELA   | PA       | notifica analogica RECAPITISTA |

      | 06012 | 1256   | CERBARA               | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-251GR_8] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
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
      | 05010 | 1434  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1434  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1318  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1280  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1221  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1304  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1374  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1259   | CERBARA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-350GR_9] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
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
      | 05010 | 1491  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1491  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1375  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1337  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1278  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1361  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1431  | MONTERADO      | AN       | notifica analogica RECAPITISTA |
      | 94028 | 1278  | VILLAROSA      | EN       | notifica analogica RECAPITISTA |

      | 06012 | 1316   | CERBARA        | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-351GR_10] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
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
      | 05010 | 1609  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1609  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1482  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1437  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1372  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1464  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1541  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1417  | CERBARA       | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-1000GR_11] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
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
      | CAP   | COSTO | MUNICIPALITY          | PROVINCE | SUBJECT                        |
      | 05010 | 1996  | COLLELUNGO            | TR       | notifica analogica FSU         |
      | 06031 | 1996  | CANTALUPO             | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1869  | ALBA ADRIATICA        | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1824  | CASAPE                | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1759  | ADELFIA               | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1851  | ANDRATE               | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1928  | MONTERADO             | AN       | notifica analogica RECAPITISTA |
      | 95055 | 1759  | SAN PIETRO CLARENZA   | CT       | notifica analogica RECAPITISTA |

      | 06012 | 1804  | CERBARA               | PG       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-1001GR_12] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
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
      | 05010 | 1999  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 1999  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 1872  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1827  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1762  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1854  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1931  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 1807   | CERBARA        | PG       | notifica analogica RECAPITISTA |



  @costoAnalogicoSettembre24
  Scenario Outline: [CALCOLO-COSTO_890-2000GR_13] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
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
      | 05010 | 2596  | COLLELUNGO     | TR       | notifica analogica FSU         |
      | 06031 | 2596  | CANTALUPO      | PG       | notifica analogica RECAPITISTA |
      | 64011 | 2469  | ALBA ADRIATICA | TE       | notifica analogica RECAPITISTA |
      | 00010 | 2424  | CASAPE         | RM       | notifica analogica RECAPITISTA |
      | 70010 | 2359  | ADELFIA        | BA       | notifica analogica RECAPITISTA |
      | 10010 | 2451  | ANDRATE        | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2528  | MONTERADO      | AN       | notifica analogica RECAPITISTA |

      | 06012 | 2404   | CERBARA        | PG       | notifica analogica RECAPITISTA |



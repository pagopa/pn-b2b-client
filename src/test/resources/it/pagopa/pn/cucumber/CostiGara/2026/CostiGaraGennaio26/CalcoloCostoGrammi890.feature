Feature: calcolo costo notifica in base hai grammi con notifiche 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoGennaio26
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
      | 05010 | 1084  | COLLELUNGO            | TR       | notifica analogica FSU         |
      | 06031 | 1084  | BEVAGNA               | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA        | TE       | notifica analogica RECAPITISTA |
      | 00010 | 960   | CASAPE                | RM       | notifica analogica RECAPITISTA |
      | 70010 | 911   | ADELFIA               | BA       | notifica analogica RECAPITISTA |
      | 10010 | 980   | ANDRATE               | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1037  | MONTERADO             | AN       | notifica analogica RECAPITISTA |
      | 35049 | 980   | SANTA CATERINA D'ESTE | PD       | notifica analogica RECAPITISTA |
      | 90052 | 911   | CERDA                 | PA       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA               | PG       | notifica analogica RECAPITISTA |
      | 17011 | 1084  | ALBISOLA CAPO         | SV       | notifica analogica RECAPITISTA |
      | 87020 | 1084  | ACQUAPPESA            | CS       | notifica analogica RECAPITISTA |

      | 21009 | 960   | BARDELLO              | VA       | notifica analogica RECAPITISTA |
      | 14027 | 980   | TONENGO               | AT       | notifica analogica RECAPITISTA |
      | 07011 | 1084  | BONO                  | SS       | notifica analogica RECAPITISTA |
      | 33012 | 941   | SAPPADA               | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO       | PG       | notifica analogica RECAPITISTA |
      | 09050 | 941   | PULA                  | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1084  | ACCIANO               | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1084  | CAMPOCHIARO           | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1084  | VALFABBRICA           | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1084  | DERUTA                | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA               | PG       | notifica analogica RECAPITISTA |
      | 09045 | 941   | QUARTU SANT'ELENA     | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1186  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1186  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1060  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1006  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1082  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1146  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |

      | 11100 | 1077  | AOSTA             | AO       | notifica analogica RECAPITISTA |
      | 87020 | 1186  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1186  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1029  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1029  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1186  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1186  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1186  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1186  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1029  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1203  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1203  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1076  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1023  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1098  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1163  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 90054 | 1023  | GERACI SICULO     | PA       | notifica analogica RECAPITISTA |
      | 88071 | 1203  | STALETTI          | CZ       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |

      | 38097 | 1203  | TERRE D'ADIGE     | TN       | notifica analogica RECAPITISTA |
      | 87020 | 1203  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1203  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1045  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1045  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1203  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1203  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1203  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1203  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1045  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1206  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1206  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1079  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1026  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1102  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1166  | MONTERADO         | AN       | notifica analogica RECAPITISTA |

      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1206  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1206  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1048  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1048  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1206  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1206  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1206  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1206  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1048  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1235  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1235  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1109  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1055  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1131  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1195  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 90072 | 1055  | ALTOFONTE         | PA       | notifica analogica RECAPITISTA |

      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1235  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1235  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1078  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1078  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1235  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1235  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1235  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1235  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1078  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1325  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1325  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1187  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1129  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1211  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1281  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |

      | 86081 | 1212  | AGNONE            | IS       | notifica analogica RECAPITISTA |
      | 87020 | 1325  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1325  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1154  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1154  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1325  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1325  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1325  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1325  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1154  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | 05010 | 1420  | COLLELUNGO          | TR       | notifica analogica FSU         |
      | 06031 | 1420  | BEVAGNA             | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA      | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1282  | CASAPE              | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1224  | ADELFIA             | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1306  | ANDRATE             | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1375  | MONTERADO           | AN       | notifica analogica RECAPITISTA |
      | 90082 | 1224  | SANTA CRISTINA GELA | PA       | notifica analogica RECAPITISTA |

      | 06012 | 934   | CERBARA             | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1420  | ACQUAPPESA          | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1420  | BONO                | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1249  | SAPPADA             | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO     | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1249  | PULA                | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1420  | ACCIANO             | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1420  | CAMPOCHIARO         | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1420  | VALFABBRICA         | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1420  | DERUTA              | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA             | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1249  | QUARTU SANT'ELENA   | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1423  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1423  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1285  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1227  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1309  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1379  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |

      | 34129 | 934   | TRIESTE           | TS       | notifica analogica RECAPITISTA |
      | 87020 | 1423  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1423  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1252  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1252  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1423  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1423  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1423  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1423  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1252  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1485  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1485  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1347  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1289  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1371  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1441  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 94028 | 1289  | VILLAROSA         | EN       | notifica analogica RECAPITISTA |

      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1485  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1485  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1314  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1314  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1485  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1485  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1485  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1485  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1314  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 1601  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 1601  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1449  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1384  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1474  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1551  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |
      | 19126 | 1077  | LA SPEZIA         | SP       | notifica analogica RECAPITISTA |
      | 87020 | 1601  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1601  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1413  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1413  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1601  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1601  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1601  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1601  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1413  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | 05010 | 2022  | COLLELUNGO          | TR       | notifica analogica FSU         |
      | 06031 | 2022  | BEVAGNA             | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA      | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1871  | CASAPE              | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1805  | ADELFIA             | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1896  | ANDRATE             | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1973  | MONTERADO           | AN       | notifica analogica RECAPITISTA |
      | 95055 | 1805  | SAN PIETRO CLARENZA | CT       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA             | PG       | notifica analogica RECAPITISTA |
      | 87020 | 2022  | ACQUAPPESA          | CS       | notifica analogica RECAPITISTA |
      | 07011 | 2022  | BONO                | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1834  | SAPPADA             | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO     | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1834  | PULA                | CA       | notifica analogica RECAPITISTA |
      | 67020 | 2022  | ACCIANO             | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 2022  | CAMPOCHIARO         | CB       | notifica analogica RECAPITISTA |

      | 06029 | 2022  | VALFABBRICA         | PG       | notifica analogica RECAPITISTA |
      | 06053 | 2022  | DERUTA              | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA             | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1834  | QUARTU SANT'ELENA   | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 2026  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 2026  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1874  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1809  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1899  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1976  | MONTERADO         | AN       | notifica analogica RECAPITISTA |

      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |
      | 87020 | 2026  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 2026  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1838  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1838  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 2026  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 2026  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 2026  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 2026  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1838  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
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
      | CAP   | COSTO | MUNICIPALITY      | PROVINCE | SUBJECT                        |
      | 05010 | 2676  | COLLELUNGO        | TR       | notifica analogica FSU         |
      | 06031 | 2676  | BEVAGNA           | PG       | notifica analogica RECAPITISTA |
      | 64011 | 931   | ALBA ADRIATICA    | TE       | notifica analogica RECAPITISTA |
      | 00010 | 2524  | CASAPE            | RM       | notifica analogica RECAPITISTA |
      | 70010 | 2459  | ADELFIA           | BA       | notifica analogica RECAPITISTA |
      | 10010 | 2549  | ANDRATE           | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2626  | MONTERADO         | AN       | notifica analogica RECAPITISTA |
      | 06012 | 934   | CERBARA           | PG       | notifica analogica RECAPITISTA |

      | 09121 | 934   | CAGLIARI          | CA       | notifica analogica RECAPITISTA |
      | 87020 | 2676  | ACQUAPPESA        | CS       | notifica analogica RECAPITISTA |
      | 07011 | 2676  | BONO              | SS       | notifica analogica RECAPITISTA |
      | 33012 | 2488  | SAPPADA           | UD       | notifica analogica RECAPITISTA |
      | 06022 | 934   | FOSSATO DI VICO   | PG       | notifica analogica RECAPITISTA |
      | 09050 | 2488  | PULA              | CA       | notifica analogica RECAPITISTA |
      | 67020 | 2676  | ACCIANO           | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 2676  | CAMPOCHIARO       | CB       | notifica analogica RECAPITISTA |

      | 06029 | 2676  | VALFABBRICA       | PG       | notifica analogica RECAPITISTA |
      | 06053 | 2676  | DERUTA            | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1077  | PERUGIA           | PG       | notifica analogica RECAPITISTA |
      | 09045 | 2488  | QUARTU SANT'ELENA | CA       | notifica analogica RECAPITISTA |







Feature: calcolo costo notifica in base hai grammi con notifiche 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1086  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1086  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 962   | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 913   | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 982   | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1038  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 35049 | 982   | SANTA CATERINA D'ESTE   | PD       | notifica analogica RECAPITISTA |
      | 90052 | 913   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 17011 | 1086  | ALBISOLA CAPO           | SV       | notifica analogica RECAPITISTA |
      | 87020 | 1086  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |

      | 21009 | 962   | BARDELLO                | VA       | notifica analogica RECAPITISTA |
      | 14027 | 982   | TONENGO                 | AT       | notifica analogica RECAPITISTA |
      | 07011 | 1086  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 943   | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 943   | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1086  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1086  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1086  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1086  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 943   | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 962   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1086  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1086  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1086  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1086  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1086  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1086  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1086  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1086  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1086  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1086  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1086  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1188  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1188  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1062  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1008  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1084  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1148  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |

      | 11100 | 1079  | AOSTA                   | AO       | notifica analogica RECAPITISTA |
      | 87020 | 1188  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1188  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1031  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1031  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1188  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1188  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1188  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1188  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1031  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1062  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1188  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1188  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1188  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1188  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1188  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1188  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1188  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1188  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1188  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1188  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1188  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1205  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1205  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1079  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1025  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1101  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1165  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 90054 | 1025  | GERACI SICULO           | PA       | notifica analogica RECAPITISTA |
      | 88071 | 1205  | STALETTI                | CZ       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |

      | 38097 | 1205  | TERRE D'ADIGE           | TN       | notifica analogica RECAPITISTA |
      | 87020 | 1205  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1205  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1048  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1048  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1205  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1205  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1205  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1205  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1048  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1079  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1205  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1205  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1205  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1205  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1205  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1205  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1205  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1205  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1205  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1205  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1205  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1209  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1209  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1082  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1029  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1104  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1169  | MONTERADO               | AN       | notifica analogica RECAPITISTA |

      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1209  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1209  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1051  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1051  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1209  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1209  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1209  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1209  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1051  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1082  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1209  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1209  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1209  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1209  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1209  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1209  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1209  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1209  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1209  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1209  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1209  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1239  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1239  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1113  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1059  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1135  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1199  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 90072 | 1059  | ALTOFONTE               | PA       | notifica analogica RECAPITISTA |

      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1239  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1239  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1082  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1082  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1239  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1239  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1239  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1239  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1082  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1113  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1239  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1239  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1239  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1239  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1239  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1239  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1239  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1239  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1239  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1239  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1239  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1329  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1329  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1191  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1133  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1215  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1285  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |

      | 86081 | 1216  | AGNONE                  | IS       | notifica analogica RECAPITISTA |
      | 87020 | 1329  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1329  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1158  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1158  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1329  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1329  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1329  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1329  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1158  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1191  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1329  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1329  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1329  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1329  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1329  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1329  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1329  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1329  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1329  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1329  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1329  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1427  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1427  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1290  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1232  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1314  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1383  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 90082 | 1232  | SANTA CRISTINA GELA     | PA       | notifica analogica RECAPITISTA |

      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1427  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1427  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1257  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1257  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1427  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1427  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1427  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1427  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1257  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1290  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1427  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1427  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1427  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1427  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1427  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1427  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1427  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1427  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1427  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1427  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1427  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1431  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1431  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1293  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1235  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1317  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1387  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |

      | 34129 | 936   | TRIESTE                 | TS       | notifica analogica RECAPITISTA |
      | 87020 | 1431  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1431  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1260  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1260  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1431  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1431  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1431  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1431  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1260  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1293  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1431  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1431  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1431  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1431  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1431  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1431  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1431  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1431  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1431  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1431  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1431  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1495  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1495  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1358  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1300  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1382  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1451  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 94028 | 1300  | VILLAROSA               | EN       | notifica analogica RECAPITISTA |

      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 87020 | 1495  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1495  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1325  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1325  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1495  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1495  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1495  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1495  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1325  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1358  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1495  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1495  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1495  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1495  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1495  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1495  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1495  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1495  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1495  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1495  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1495  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 1612  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 1612  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1460  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1395  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1485  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 1562  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 19126 | 1079  | LA SPEZIA               | SP       | notifica analogica RECAPITISTA |
      | 87020 | 1612  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 1612  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1424  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1424  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 1612  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 1612  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 1612  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 1612  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1424  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1460  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1612  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1612  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1612  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1612  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1612  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1612  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1612  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1612  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1612  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1612  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1612  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 2050  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 2050  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1899  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1833  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1924  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2001  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 95055 | 1833  | SAN PIETRO CLARENZA     | CT       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 87020 | 2050  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 2050  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1862  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1862  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 2050  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 2050  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 2050  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 2050  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1862  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1899  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 2050  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 2050  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 2050  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 2050  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 2050  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 2050  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 2050  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 2050  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 2050  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 2050  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 2050  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 2054  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 2054  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 1902  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 1837  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 1927  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2004  | MONTERADO               | AN       | notifica analogica RECAPITISTA |

      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |
      | 87020 | 2054  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 2054  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 1866  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 1866  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 2054  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 2054  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 2054  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 2054  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 1866  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 1902  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 2054  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 2054  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 2054  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 2054  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 2054  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 2054  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 2054  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 2054  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 2054  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 2054  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 2054  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 05010 | 2730  | COLLELUNGO              | TR       | notifica analogica FSU         |
      | 06031 | 2730  | BEVAGNA                 | PG       | notifica analogica RECAPITISTA |
      | 64011 | 933   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 00010 | 2579  | CASAPE                  | RM       | notifica analogica RECAPITISTA |
      | 70010 | 2513  | ADELFIA                 | BA       | notifica analogica RECAPITISTA |
      | 10010 | 2604  | ANDRATE                 | TO       | notifica analogica RECAPITISTA |
      | 60012 | 2681  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 06012 | 936   | CERBARA                 | PG       | notifica analogica RECAPITISTA |

      | 09121 | 936   | CAGLIARI                | CA       | notifica analogica RECAPITISTA |
      | 87020 | 2730  | ACQUAPPESA              | CS       | notifica analogica RECAPITISTA |
      | 07011 | 2730  | BONO                    | SS       | notifica analogica RECAPITISTA |
      | 33012 | 2542  | SAPPADA                 | UD       | notifica analogica RECAPITISTA |
      | 06022 | 936   | FOSSATO DI VICO         | PG       | notifica analogica RECAPITISTA |
      | 09050 | 2542  | PULA                    | CA       | notifica analogica RECAPITISTA |
      | 67020 | 2730  | ACCIANO                 | AQ       | notifica analogica RECAPITISTA |
      | 86020 | 2730  | CAMPOCHIARO             | CB       | notifica analogica RECAPITISTA |

      | 06029 | 2730  | VALFABBRICA             | PG       | notifica analogica RECAPITISTA |
      | 06053 | 2730  | DERUTA                  | PG       | notifica analogica RECAPITISTA |
      | 06134 | 1079  | PERUGIA                 | PG       | notifica analogica RECAPITISTA |
      | 09045 | 2542  | QUARTU SANT'ELENA       | CA       | notifica analogica RECAPITISTA |

      | 27063 | 2579  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 2730  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 2730  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 2730  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 2730  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 2730  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 2730  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 2730  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 2730  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 2730  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 2730  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 2730  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1079  | SEULO                   | NU       | notifica analogica RECAPITISTA |







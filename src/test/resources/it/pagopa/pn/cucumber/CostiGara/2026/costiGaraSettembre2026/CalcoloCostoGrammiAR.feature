Feature: calcolo costo notifica in base hai grammi con notfiche AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-20GR_1] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | feePolicy             | DELIVERY_MODE        |
      | document              | DOC_4_PG;            |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 541   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 601   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 463   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 395   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 90088 | 463   | SAN CIPIRELLO           | PA       | notifica analogica RECAPITISTA |
      | 88071 | 463   | STALETTI                | CZ       | notifica analogica RECAPITISTA |
      | 85036 | 594   | ROCCANOVA               | PZ       | notifica analogica RECAPITISTA |
      | 21009 | 564   | BARDELLO                | VA       | notifica analogica RECAPITISTA |
      | 64011 | 594   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 86170 | 526   | ISERNIA                 | IS       | notifica analogica RECAPITISTA |
      | 95056 | 463   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 541   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 463   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 541   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 541   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 541   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 564   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 564   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 564   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 564   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 564   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 564   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 564   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 564   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 564   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 564   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 564   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 564   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 564   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-21GR_2] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | feePolicy             | DELIVERY_MODE        |
      | document              | DOC_5_PG;            |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 613   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 663   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 522   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 436   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |

      | 74021 | 504   | CAROSINO                | TA       | notifica analogica RECAPITISTA |
      | 95056 | 522   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 613   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 522   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 613   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 613   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 613   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 626   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 626   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 626   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 626   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 626   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 626   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 626   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 626   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 626   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 626   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 626   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 626   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 626   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-50GR_3] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | feePolicy             | DELIVERY_MODE        |
      | document              | DOC_8_PG;DOC_8_PG;   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 630   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 680   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 539   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 453   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 91030 | 630   | SAN VITO LO CAPO        | TP       | notifica analogica RECAPITISTA |
      | 36049 | 643   | SOVIZZO                 | VI       | notifica analogica RECAPITISTA |
      | 63094 | 680   | BISIGNANO               | AP       | notifica analogica RECAPITISTA |
      | 64011 | 594   | ALBA ADRIATICA          | TE       | notifica analogica RECAPITISTA |
      | 86170 | 526   | ISERNIA                 | IS       | notifica analogica RECAPITISTA |
      | 95056 | 539   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 630   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 539   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 630   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 630   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 630   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 643   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 643   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 643   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 643   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 643   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 643   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 643   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 643   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 643   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 643   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 643   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 643   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 643   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-51GR_4] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                   |
      | senderDenomination    | Comune di palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
      | document              | DOC_8_PG;DOC_8_PG;DOC_1_PG; |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 670   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 722   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 571   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 485   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |

      | 28028 | 684   | PETTENASCO              | NO       | notifica analogica RECAPITISTA |
      | 95056 | 571   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 670   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 571   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 670   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 670   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 670   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 684   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 684   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 684   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 684   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 684   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 684   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 684   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 684   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 684   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 684   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 684   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 684   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 684   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-100GR_5] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                     |
      | senderDenomination    | Comune di palermo                             |
      | physicalCommunication | AR_REGISTERED_LETTER                          |
      | feePolicy             | DELIVERY_MODE                                 |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG; |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 701   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 752   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 602   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 516   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 91032 | 602   | PETROSINO               | TP       | notifica analogica RECAPITISTA |

      | 80146 | 388   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 95056 | 602   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 701   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 602   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 701   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 701   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 701   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 714   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 714   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 714   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 714   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 714   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 714   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 714   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 714   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 714   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 714   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 714   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 714   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 714   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-101GR_6] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                    |
      | senderDenomination    | Comune di palermo                            |
      | physicalCommunication | AR_REGISTERED_LETTER                         |
      | feePolicy             | DELIVERY_MODE                                |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 744   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 799   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 638   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 552   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 90020 | 638   | CASTELLANA SICULA       | PA       | notifica analogica RECAPITISTA |
      | 64100 | 526   | TERAMO                  | TE       | notifica analogica RECAPITISTA |
      | 86081 | 799   | AGNONE                  | IS       | notifica analogica RECAPITISTA |
      | 95056 | 638   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 744   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 638   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 744   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 744   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 744   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 760   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 760   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 760   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 760   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 760   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 760   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 760   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 760   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 760   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 760   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 760   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 760   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 760   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-250GR_7] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                       |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 843   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 897   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 736   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 650   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 92035 | 736   | JOPPOLO GIANCAXIO       | AG       | notifica analogica RECAPITISTA |

      | 87030 | 736   | SAN VINCENZO LA COSTA   | CS       | notifica analogica RECAPITISTA |
      | 95056 | 736   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 843   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 736   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 843   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 843   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 843   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 858   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 858   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 858   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 858   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 858   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 858   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 858   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 858   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 858   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 858   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 858   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 858   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 858   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-251GR_8] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                       |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 879   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 934   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 765   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 679   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 51018 | 557   | PIEVE A NIEVOLE         | PT       | notifica analogica RECAPITISTA |
      | 64100 | 526   | TERAMO                  | TE       | notifica analogica RECAPITISTA |
      | 86081 | 934   | AGNONE                  | IS       | notifica analogica RECAPITISTA |
      | 95056 | 765   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 879   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 765   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 879   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 879   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 879   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 894   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 894   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 894   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 894   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 894   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 894   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 894   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 894   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 894   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 894   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 894   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 894   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 894   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-350GR_9] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                        |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 943   | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 999   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 830   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 744   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |

      | 95058 | 830   | CAMPOROTONDO ETNEO      | CT       | notifica analogica RECAPITISTA |
      | 44026 | 557   | MESOLA                  | FE       | notifica analogica RECAPITISTA |
      | 95056 | 830   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 943   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 830   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 943   | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 943   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 943   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 959   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 959   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 959   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 959   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 959   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 959   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 959   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 959   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 959   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 959   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 959   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 959   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 959   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-351GR_10] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                        |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 1018  | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 1079  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 891   | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 805   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 95056 | 891   | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 1018  | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 891   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 1018  | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 1018  | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 1018  | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 1036  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1036  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1036  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1036  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1036  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1036  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1036  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1036  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1036  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1036  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1036  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1036  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1036  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-1000GR_11] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                                                        |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY              | PROVINCE | SUBJECT                        |
      | 80060 | 1457  | MASSAQUANO                | NA       | notifica analogica FSU         |
      | 60012 | 1518  | MONTERADO                 | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                    | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                      | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1330  | CASAREA                   | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1244  | NAPOLI                    | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                  | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE                | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                      | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                  | LT       | notifica analogica RECAPITISTA |

      | 96026 | 1330  | PORTOPALO DI CAPO PASSERO | SR       | notifica analogica RECAPITISTA |
      | 95056 | 1330  | SANT'AGATA LI BATTIATI    | CT       | notifica analogica RECAPITISTA |
      | 90010 | 1457  | ALTAVILLA MILICIA         | PA       | notifica analogica RECAPITISTA |
      | 88020 | 1330  | CORTALE                   | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 1457  | CAGNANO VARANO            | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                  | SA       | notifica analogica RECAPITISTA |
      | 90052 | 1457  | CERDA                     | PA       | notifica analogica RECAPITISTA |
      | 95059 | 1457  | LICODIA EUBEA             | CT       | notifica analogica RECAPITISTA |

      | 27063 | 1475  | MONTALTO PAVESE           | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1475  | BADESI                    | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1475  | BORTIGIADAS               | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1475  | SANT'ANTONIO DI GALLURA   | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1475  | DECIMOPUTZU               | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1475  | DOMUS DE MARIA            | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1475  | SILIQUA                   | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1475  | VALLERMOSA                | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1475  | VILLASPECIOSA             | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1475  | PIMENTEL                  | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1475  | SAMATZAI                  | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1475  | USSANA                    | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1475  | SEULO                     | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-1001GR_12] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                                                        |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 1546  | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 1613  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1401  | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1316  | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 95056 | 1401  | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 1546  | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 1401  | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 1546  | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 1546  | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 1546  | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 1567  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 1567  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 1567  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 1567  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 1567  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 1567  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 1567  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 1567  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 1567  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 1567  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 1567  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 1567  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 1567  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-2000GR_13] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>                                                                                        |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_300_PG;DOC_300_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_AR      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 80060 | 2222  | MASSAQUANO              | NA       | notifica analogica FSU         |
      | 60012 | 2290  | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60123 | 526   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 70123 | 368   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 80013 | 2078  | CASAREA                 | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1992  | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO                | AV       | notifica analogica RECAPITISTA |
      | 00012 | 557   | ALBUCCIONE              | RM       | notifica analogica RECAPITISTA |
      | 00118 | 465   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 04100 | 493   | FOGLIANO                | LT       | notifica analogica RECAPITISTA |
      | 95056 | 2078  | SANT'AGATA LI BATTIATI  | CT       | notifica analogica RECAPITISTA |
      | 90010 | 2222  | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 2078  | CORTALE                 | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 2222  | CAGNANO VARANO          | FG       | notifica analogica RECAPITISTA |

      | 84022 | 534   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 2222  | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 2222  | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 2243  | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 2243  | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 2243  | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 2243  | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 2243  | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 2243  | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 2243  | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 2243  | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 2243  | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 2243  | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 2243  | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 2243  | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 2243  | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-20GR_14] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | <STATE>    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | COSTO | STATE      |
      | 1026  | MESSICO    |
      | 1026  | SUD AFRICA |

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-21GR_15] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 21gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_5_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1241" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-50GR_16] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 50gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_8_PG;DOC_8_PG;              |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1258" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-51GR_17] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 51gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_8_PG;DOC_8_PG;DOC_1_PG      |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1361" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-100GR_18] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 100gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber               |
      | senderDenomination    | Comune di palermo                             |
      | physicalCommunication | AR_REGISTERED_LETTER                          |
      | feePolicy             | DELIVERY_MODE                                 |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG; |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1391" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-101GR_19] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 101gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber              |
      | senderDenomination    | Comune di palermo                            |
      | physicalCommunication | AR_REGISTERED_LETTER                         |
      | feePolicy             | DELIVERY_MODE                                |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1782" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-250GR_20] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 250gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                 |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1881" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-251GR_21] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 251gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                 |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2070" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-350GR_22] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 350gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                  |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2135" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-351GR_23] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 351gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                  |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2849" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-1000GR_24] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1000gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3288" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-1001GR_25] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1001gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4447" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-2000GR_26] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 2000gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_300_PG;DOC_300_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "5124" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-20GR_27] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "914" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-21GR_28] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 21gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_5_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1123" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-50GR_29] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 50gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_8_PG;DOC_8_PG;              |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1140" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-51GR_30] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 51gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_8_PG;DOC_8_PG;DOC_1_PG      |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1234" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-100GR_31] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 100gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber               |
      | senderDenomination    | Comune di palermo                             |
      | physicalCommunication | AR_REGISTERED_LETTER                          |
      | feePolicy             | DELIVERY_MODE                                 |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG; |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1265" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-101GR_32] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 101gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber              |
      | senderDenomination    | Comune di palermo                            |
      | physicalCommunication | AR_REGISTERED_LETTER                         |
      | feePolicy             | DELIVERY_MODE                                |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1454" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-250GR_33] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 250gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                 |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1552" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-251GR_34] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 251gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                 |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1689" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-350GR_35] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 350gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                  |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1754" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-351GR_36] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 351gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                  |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2197" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-1000GR_37] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1000gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2636" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-1001GR_38] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1001gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3423" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-2000GR_39] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 2000gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_300_PG;DOC_300_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | ALBANIA    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4099" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-20GR_40] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1082" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-21GR_41] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 21gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_5_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1331" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-50GR_42] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 50gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_8_PG;DOC_8_PG;              |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1348" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-51GR_43] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 51gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_8_PG;DOC_8_PG;DOC_1_PG      |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1493" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-100GR_44] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 100gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber               |
      | senderDenomination    | Comune di palermo                             |
      | physicalCommunication | AR_REGISTERED_LETTER                          |
      | feePolicy             | DELIVERY_MODE                                 |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG; |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1524" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-101GR_45] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 101gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber              |
      | senderDenomination    | Comune di palermo                            |
      | physicalCommunication | AR_REGISTERED_LETTER                         |
      | feePolicy             | DELIVERY_MODE                                |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1907" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-250GR_46] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 250gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                 |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2006" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-251GR_47] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 251gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                 |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2522" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-350GR_48] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 350gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                  |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2587" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-351GR_49] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 351gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                  |
      | senderDenomination    | Comune di palermo                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                             |
      | feePolicy             | DELIVERY_MODE                                                    |
      | document              | DOC_50_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3625" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-1000GR_50] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1000gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4064" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-1001GR_51] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1001gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_100_PG;DOC_100_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_7_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "5360" della notifica

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-2000GR_52] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 2000gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                                                  |
      | senderDenomination    | Comune di palermo                                                                                |
      | physicalCommunication | AR_REGISTERED_LETTER                                                                             |
      | feePolicy             | DELIVERY_MODE                                                                                    |
      | document              | DOC_300_PG;DOC_300_PG;DOC_100_PG;DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | AUSTRALIA  |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "6036" della notifica


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-100GR_53] (Settembre) Invio notifica ZONE_1 (test num.2) e verifica calcolo del costo su raccomandata con peso = 100gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber               |
      | senderDenomination    | Comune di palermo                             |
      | physicalCommunication | AR_REGISTERED_LETTER                          |
      | feePolicy             | DELIVERY_MODE                                 |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_4_PG; |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | <state>    |
      | physicalAddress_zip     | ZONE_1     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1265" della notifica
    Examples:
      | state              |
      | ALBANIA            |
      | MACEDONIA DEL NORD |

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_AR-101GR_54] (Settembre) Invio notifica ZONE_2 (test num.2) e verifica calcolo del costo su raccomandata con peso = 101gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber              |
      | senderDenomination    | Comune di palermo                            |
      | physicalCommunication | AR_REGISTERED_LETTER                         |
      | feePolicy             | DELIVERY_MODE                                |
      | document              | DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_5_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1782" della notifica


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_AR-250GR_55] (Settembre) Invio notifica ZONE_3 (test num.2) e verifica calcolo del costo su raccomandata con peso = 250gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber                                 |
      | senderDenomination    | Comune di palermo                                               |
      | physicalCommunication | AR_REGISTERED_LETTER                                            |
      | feePolicy             | DELIVERY_MODE                                                   |
      | document              | DOC_50_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_8_PG;DOC_6_PG |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | <state>    |
      | physicalAddress_zip     | ZONE_3     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2006" della notifica
    Examples:
      | state       |
      | AUSTRALIA   |
      | MIDWAY      |
      | PHOENIX     |
      | SANTA CROCE |
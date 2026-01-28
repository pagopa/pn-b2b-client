Feature: calcolo costo notifica in base hai grammi con notfiche AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-20GR_1] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 540   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 600   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 462   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 394   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 90088 | 462   | SAN CIPIRELLO          | PA       | notifica analogica RECAPITISTA |
      | 88071 | 462   | STALETTI               | CZ       | notifica analogica RECAPITISTA |
      | 85036 | 593   | ROCCANOVA              | PZ       | notifica analogica RECAPITISTA |
      | 21009 | 563   | BARDELLO               | VA       | notifica analogica RECAPITISTA |
      | 64011 | 593   | ALBA ADRIATICA         | TE       | notifica analogica RECAPITISTA |
      | 86170 | 525   | ISERNIA                | IS       | notifica analogica RECAPITISTA |
      | 95056 | 462   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 540   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 462   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 540   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 540   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 540   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-21GR_2] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 611   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 662   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 521   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 434   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |

      | 74021 | 502   | CAROSINO               | TA       | notifica analogica RECAPITISTA |
      | 95056 | 521   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 611   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 521   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 611   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 611   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 611   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-50GR_3] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 628   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 678   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 537   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 451   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 91030 | 628   | SAN VITO LO CAPO       | TP       | notifica analogica RECAPITISTA |
      | 36049 | 641   | SOVIZZO                | VI       | notifica analogica RECAPITISTA |
      | 63094 | 678   | BISIGNANO              | AP       | notifica analogica RECAPITISTA |
      | 64011 | 593   | ALBA ADRIATICA         | TE       | notifica analogica RECAPITISTA |
      | 86170 | 525   | ISERNIA                | IS       | notifica analogica RECAPITISTA |
      | 95056 | 537   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 628   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 537   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 628   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 628   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 628   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-51GR_4] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 668   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 719   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 569   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 483   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |

      | 28028 | 681   | PETTENASCO             | NO       | notifica analogica RECAPITISTA |
      | 95056 | 569   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 668   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 569   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 668   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 668   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 668   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-100GR_5] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 697   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 749   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 598   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 512   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 91032 | 598   | PETROSINO              | TP       | notifica analogica RECAPITISTA |

      | 80146 | 387   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 95056 | 598   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 697   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 598   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 697   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 697   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 697   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-101GR_6] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 740   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 795   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 634   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 548   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 90020 | 634   | CASTELLANA SICULA      | PA       | notifica analogica RECAPITISTA |
      | 64100 | 525   | TERAMO                 | TE       | notifica analogica RECAPITISTA |
      | 86081 | 795   | AGNONE                 | IS       | notifica analogica RECAPITISTA |
      | 95056 | 634   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 740   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 634   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 740   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 740   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 740   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-250GR_7] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 835   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 890   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 729   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 643   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 92035 | 729   | JOPPOLO GIANCAXIO      | AG       | notifica analogica RECAPITISTA |

      | 87030 | 729   | SAN VINCENZO LA COSTA  | CS       | notifica analogica RECAPITISTA |
      | 95056 | 729   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 835   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 729   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 835   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 835   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 835   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-251GR_8] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 871   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 927   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 758   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 672   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 51018 | 556   | PIEVE A NIEVOLE        | PT       | notifica analogica RECAPITISTA |
      | 64100 | 525   | TERAMO                 | TE       | notifica analogica RECAPITISTA |
      | 86081 | 927   | AGNONE                 | IS       | notifica analogica RECAPITISTA |
      | 95056 | 758   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 871   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 758   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 871   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 871   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 871   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-350GR_9] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 933   | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 989   | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 820   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 734   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |

      | 95058 | 820   | CAMPOROTONDO ETNEO     | CT       | notifica analogica RECAPITISTA |
      | 44026 | 556   | MESOLA                 | FE       | notifica analogica RECAPITISTA |
      | 95056 | 820   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 933   | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 820   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 933   | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 933   | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 933   | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-351GR_10] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 1008  | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 1069  | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 881   | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 795   | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 95056 | 881   | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 1008  | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 881   | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 1008  | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 1008  | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 1008  | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-1000GR_11] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
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
      | 80060 | 1429  | MASSAQUANO                | NA       | notifica analogica FSU         |
      | 60012 | 1490  | MONTERADO                 | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                    | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                      | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1302  | CASAREA                   | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1216  | NAPOLI                    | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO                  | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE                | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                      | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO                  | LT       | notifica analogica RECAPITISTA |

      | 96026 | 1302  | PORTOPALO DI CAPO PASSERO | SR       | notifica analogica RECAPITISTA |
      | 95056 | 1302  | SANT'AGATA LI BATTIATI    | CT       | notifica analogica RECAPITISTA |
      | 90010 | 1429  | ALTAVILLA MILICIA         | PA       | notifica analogica RECAPITISTA |
      | 88020 | 1302  | CORTALE                   | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 1429  | CAGNANO VARANO            | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA                  | SA       | notifica analogica RECAPITISTA |
      | 90052 | 1429  | CERDA                     | PA       | notifica analogica RECAPITISTA |
      | 95059 | 1429  | LICODIA EUBEA             | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-1001GR_12] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 1518  | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 1585  | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1374  | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1288  | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 95056 | 1374  | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 1518  | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 1374  | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 1518  | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 1518  | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 1518  | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-2000GR_13] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
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
      | CAP   | COSTO | MUNICIPALITY           | PROVINCE | SUBJECT                        |
      | 80060 | 2168  | MASSAQUANO             | NA       | notifica analogica FSU         |
      | 60012 | 2235  | MONTERADO              | AN       | notifica analogica RECAPITISTA |
      | 60123 | 525   | ANCONA                 | AN       | notifica analogica RECAPITISTA |
      | 70123 | 367   | BARI                   | BA       | notifica analogica RECAPITISTA |
      | 80013 | 2024  | CASAREA                | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1938  | NAPOLI                 | NA       | notifica analogica RECAPITISTA |
      | 83100 | 407   | AVELLINO               | AV       | notifica analogica RECAPITISTA |
      | 00012 | 556   | ALBUCCIONE             | RM       | notifica analogica RECAPITISTA |
      | 00118 | 464   | ROMA                   | RM       | notifica analogica RECAPITISTA |
      | 04100 | 492   | FOGLIANO               | LT       | notifica analogica RECAPITISTA |
      | 95056 | 2024  | SANT'AGATA LI BATTIATI | CT       | notifica analogica RECAPITISTA |
      | 90010 | 2168  | ALTAVILLA MILICIA      | PA       | notifica analogica RECAPITISTA |
      | 88020 | 2024  | CORTALE                | CZ       | notifica analogica RECAPITISTA |
      | 71010 | 2168  | CAGNANO VARANO         | FG       | notifica analogica RECAPITISTA |

      | 84022 | 533   | CAMPAGNA               | SA       | notifica analogica RECAPITISTA |
      | 90052 | 2168  | CERDA                  | PA       | notifica analogica RECAPITISTA |
      | 95059 | 2168  | LICODIA EUBEA          | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-20GR_14] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
      | 1025  | MESSICO    |
      | 1025  | SUD AFRICA |

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-21GR_15] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    And viene verificato il costo = "1240" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-50GR_16] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    And viene verificato il costo = "1256" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-51GR_17] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    And viene verificato il costo = "1358" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-100GR_18] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1388" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-101GR_19] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    And viene verificato il costo = "1778" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-250GR_20] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "1873" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-251GR_21] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    And viene verificato il costo = "2063" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-350GR_22] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    And viene verificato il costo = "2125" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-351GR_23] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    And viene verificato il costo = "2839" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-1000GR_24] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    And viene verificato il costo = "3260" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-1001GR_25] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    And viene verificato il costo = "4419" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-2000GR_26] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    And viene verificato il costo = "5070" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-20GR_27] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    And viene verificato il costo = "912" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-21GR_28] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    And viene verificato il costo = "1122" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-50GR_29] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    And viene verificato il costo = "1138" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-51GR_30] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    And viene verificato il costo = "1232" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-100GR_31] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1261" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-101GR_32] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    And viene verificato il costo = "1450" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-250GR_33] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "1545" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-251GR_34] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    And viene verificato il costo = "1682" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-350GR_35] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    And viene verificato il costo = "1744" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-351GR_36] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    And viene verificato il costo = "2187" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-1000GR_37] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    And viene verificato il costo = "2609" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-1001GR_38] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    And viene verificato il costo = "3395" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-2000GR_39] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    And viene verificato il costo = "4045" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-20GR_40] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    And viene verificato il costo = "1081" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-21GR_41] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    And viene verificato il costo = "1329" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-50GR_42] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    And viene verificato il costo = "1346" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-51GR_43] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    And viene verificato il costo = "1491" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-100GR_44] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1520" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-101GR_45] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    And viene verificato il costo = "1904" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-250GR_46] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "1998" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-251GR_47] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    And viene verificato il costo = "2514" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-350GR_48] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    And viene verificato il costo = "2577" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-351GR_49] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    And viene verificato il costo = "3615" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-1000GR_50] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    And viene verificato il costo = "4036" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-1001GR_51] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    And viene verificato il costo = "5332" della notifica

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-2000GR_52] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    And viene verificato il costo = "5982" della notifica


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-100GR_53] (Gennaio) Invio notifica ZONE_1 (test num.2) e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1261" della notifica
    Examples:
      | state              |
      | ALBANIA            |
      | MACEDONIA DEL NORD |

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_AR-101GR_54] (Gennaio) Invio notifica ZONE_2 (test num.2) e verifica calcolo del costo su raccomandata con peso = 101gr
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
    And viene verificato il costo = "1778" della notifica


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_AR-250GR_55] (Gennaio) Invio notifica ZONE_3 (test num.2) e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "1998" della notifica
    Examples:
      | state       |
      | AUSTRALIA   |
      | MIDWAY      |
      | PHOENIX     |
      | SANTA CROCE |
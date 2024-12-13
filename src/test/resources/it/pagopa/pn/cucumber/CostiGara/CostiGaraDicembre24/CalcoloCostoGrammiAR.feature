Feature: calcolo costo notifica in base hai grammi con notfiche AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-20GR_1] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 546   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 608   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 538   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 467   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 397   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 418   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 546   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 458   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 485   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 90088 | 467   | SAN CIPIRELLO   | PA    | notifica analogica RECAPITISTA |
      | 84022 | 467   | CAMPAGNA        | SA    | notifica analogica RECAPITISTA |
      | 88071 | 467   | STALETTI        | CZ    | notifica analogica RECAPITISTA |
      | 85036 | 608   | ROCCANOVA       | PZ    | notifica analogica RECAPITISTA |
      | 21009 | 546   | BARDELLO        | VA    | notifica analogica RECAPITISTA |
      | 64011 | 608   | ALBA ADRIATICA  | TE    | notifica analogica RECAPITISTA |
      | 86170 | 538   | ISERNIA         | IS    | notifica analogica RECAPITISTA |

  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-21GR_2] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 619   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 671   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 581   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 526   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 438   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 459   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 620   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 508   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 536   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 74021 | 508   | CAROSINO        | TA       | notifica analogica RECAPITISTA |


  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-50GR_3] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 634   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 686   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 596   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 541   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 453   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 474   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 635   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 523   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 551   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 91030 | 541   | SAN VITO LO CAPO  | TP       | notifica analogica RECAPITISTA |
      | 36049 | 635  | SOVIZZO            | VI       | notifica analogica RECAPITISTA |
      | 63094 | 686   | BISIGNANO        | AP       | notifica analogica RECAPITISTA |
      | 64011 | 686   | ALBA ADRIATICA   | TE       | notifica analogica RECAPITISTA |
      | 86170 | 596   | ISERNIA          | IS       | notifica analogica RECAPITISTA |

  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-51GR_4] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 675   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 728   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 638   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 574   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 486   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 507   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 675   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 563   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 591   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 28028 | 675   | PETTENASCO        | NO       | notifica analogica RECAPITISTA |

  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-100GR_5] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 702   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 755   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 665   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 601   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 513   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 534   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 702   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 590   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 618    | FOGLIANO    | LT       | notifica analogica RECAPITISTA |
      | 91032 | 601    | PETROSINO   | TP       | notifica analogica RECAPITISTA |

      | 80146 | 513    | NAPOLI   | NA       | notifica analogica RECAPITISTA |

  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-101GR_6] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 746   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 802   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 713   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 637   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 549   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 570   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 746   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 635   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 663   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 90020 | 637    | CASTELLANA SICULA   | PA       | notifica analogica RECAPITISTA |
      | 64100 | 713    | TERAMO      | TE       | notifica analogica RECAPITISTA |
      | 86081 | 802    | AGNONE      | IS       | notifica analogica RECAPITISTA |


  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-250GR_7] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 833   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 889   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 800   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 724   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 636   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 657   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 833   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 722   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 750   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 92035 | 724   | JOPPOLO GIANCAXIO | AG  | notifica analogica RECAPITISTA |

      | 87030 | 724    | SAN VINCENZO LA COSTA   | CS       | notifica analogica RECAPITISTA |

  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-251GR_8] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 869   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 926   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 842   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 753   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 665   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 690   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 869   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 757   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 790   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 51018 | 869   | PIEVE A NIEVOLE     | PT       | notifica analogica RECAPITISTA |
      | 64100 | 842   | TERAMO      | TE       | notifica analogica RECAPITISTA |
      | 86081 | 926   | AGNONE      | IS       | notifica analogica RECAPITISTA |


  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-350GR_9] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 926   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 983   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 899   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 810   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 722   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 747   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 926   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 814   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 847   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 95058 | 810   | CAMPOROTONDO ETNEO      | CT       | notifica analogica RECAPITISTA |
      | 44026 | 926   | MESOLA     | FE       | notifica analogica RECAPITISTA |

  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-351GR_10] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 1002  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 1065  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 977   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 873   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 785   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 805   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1003  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 891   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 920   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-1000GR_11] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 1389  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 1452  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 1364  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371  | BARI          | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1260  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1172  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1192  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1390  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1278  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1307  | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 96026 | 1260  | PORTOPALO DI CAPO PASSERO | SR  | notifica analogica RECAPITISTA |


  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-1001GR_12] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 1480  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 1549  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 1460  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371  | BARI          | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1332  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1245  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1266  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1480  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1369  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1398  | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-2000GR_13] (Dicembre) Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 2077  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 2146  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 2057  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 371  | BARI          | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1929  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1842  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1863  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 2077  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1966  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1995  | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @costoAnalogicoDicembre24
  Scenario Outline: [CALCOLO-COSTO_AR-20GR_14] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | COSTO | STATE   |
      | 1036  | MESSICO |
      | 1036  | SUD AFRICA |

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-21GR_15] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1255" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-50GR_16] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1270" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-51GR_17] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1375" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-100GR_18] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1402" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-101GR_19] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1802" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-250GR_20] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1889" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-251GR_21] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2081" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-350GR_22] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2138" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-351GR_23] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2869" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-1000GR_24] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3256" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-1001GR_25] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4441" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-2000GR_26] (Dicembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "5038" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-20GR_27] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "921" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-21GR_28] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1135" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-50GR_29] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1150" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-51GR_30] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1246" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-100GR_31] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1273" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-101GR_32] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1465" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-250GR_33] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1552" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-251GR_34] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1692" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-350GR_35] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1749" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-351GR_36] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2202" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-1000GR_37] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2589" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-1001GR_38] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3394" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-2000GR_39] (Dicembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3991" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-20GR_40] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1093" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-21GR_41] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1348" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-50GR_42] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1363" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-51GR_43] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1511" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-100GR_44] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1538" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-101GR_45] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1929" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-250GR_46] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2016" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-251GR_47] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2544" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-350GR_48] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2601" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-351GR_49] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3662" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-1000GR_50] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4049" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-1001GR_51] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "5375" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-2000GR_52] (Dicembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "5972" della notifica




  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-100GR_53] (Dicembre) Invio notifica ZONE_1 (test num.2) e verifica calcolo del costo su raccomandata con peso = 100gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1273" della notifica

  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-101GR_54] (Dicembre) Invio notifica ZONE_2 (test num.2) e verifica calcolo del costo su raccomandata con peso = 101gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1802" della notifica


  @costoAnalogicoDicembre24
  Scenario: [CALCOLO-COSTO_AR-250GR_55] (Dicembre) Invio notifica ZONE_3 (test num.2) e verifica calcolo del costo su raccomandata con peso = 250gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2016" della notifica
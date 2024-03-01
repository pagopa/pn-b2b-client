Feature: calcolo costo notifica in base hai grammi con notfiche AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-20GR_1] Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
      | 60012 | 452   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 409   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 376   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 467   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 397   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 417   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 546   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 457   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 484     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-21GR_2] Invio notifica e verifica calcolo del costo su raccomandata con peso = 21gr
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
      | 60012 | 454   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 395   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 360   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 475   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 387   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 408   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 558   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 447   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 475   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-50GR_3] Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr
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
      | 80060 | 634   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 469   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 410   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 375   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 490   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 402   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 423   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 573   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 462   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 490     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-51GR_4] Invio notifica e verifica calcolo del costo su raccomandata con peso = 51gr
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
      | 80060 | 674   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 500   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 441   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 404   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 522   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 434   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 455   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 613   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 502   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 530     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-100GR_5] Invio notifica e verifica calcolo del costo su raccomandata con peso = 100gr
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
      | 80060 | 701   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 527   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 468   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 431   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 549   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 461   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 482   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 640   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 529   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 557     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-101GR_6] Invio notifica e verifica calcolo del costo su raccomandata con peso = 101gr
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
      | 80060 | 745   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 561   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 503   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 464   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 586   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 498   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 518   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 684   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 573   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 601     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-250GR_7] Invio notifica e verifica calcolo del costo su raccomandata con peso = 250gr
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
      | 80060 | 832   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 648   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 590   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 551   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 673   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 585   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 605   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 771   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 660   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 688     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-251GR_8] Invio notifica e verifica calcolo del costo su raccomandata con peso = 251gr
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
      | 80060 | 869   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 675   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 621   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 577   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 702   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 614   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 638   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 807   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 696   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 728   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-350GR_9] Invio notifica e verifica calcolo del costo su raccomandata con peso = 350gr
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
      | 80060 | 926   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 732   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 678   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 634   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 759   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 671   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 695   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 864   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 753   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 785     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-351GR_10] Invio notifica e verifica calcolo del costo su raccomandata con peso = 351gr
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
      | 80060 | 1002  | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 791   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 734   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 690   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 821   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 733   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 754   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 941   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 830   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 858     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-1000GR_11] Invio notifica e verifica calcolo del costo su raccomandata con peso = 1000gr
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
      | 80060 | 1389  | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 1178  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 1121  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 1077  | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1208  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1120  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1141  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1328  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1217  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1245     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-1001GR_12] Invio notifica e verifica calcolo del costo su raccomandata con peso = 1001gr
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
      | 80060 | 1479  | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 1246  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 1191  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 1142  | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1281  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1193  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1214  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1418  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1307  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1336  | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-2000GR_13] Invio notifica e verifica calcolo del costo su raccomandata con peso = 2000gr
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
      | 80060 | 2076  | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 1843  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 1788  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 1739  | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1878  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1790  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1811  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 2015  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1904  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1933     | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-20GR_14] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1036" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-21GR_15] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    And viene verificato il costo = "1154" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-50GR_16] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    And viene verificato il costo = "1169" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-51GR_17] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    And viene verificato il costo = "1273" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-100GR_18] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1300" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-101GR_19] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    And viene verificato il costo = "1700" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-250GR_20] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "1787" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-251GR_21] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    And viene verificato il costo = "1980" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-350GR_22] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    And viene verificato il costo = "2037" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-351GR_23] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    And viene verificato il costo = "2768" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-1000GR_24] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    And viene verificato il costo = "3155" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-1001GR_25] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    And viene verificato il costo = "4340" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-2000GR_26] Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    And viene verificato il costo = "4937" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-20GR_27] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
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

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-21GR_28] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    And viene verificato il costo = "1034" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-50GR_29] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    And viene verificato il costo = "1049" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-51GR_30] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    And viene verificato il costo = "1144" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-100GR_31] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1171" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-101GR_32] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    And viene verificato il costo = "1364" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-250GR_33] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "1451" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-251GR_34] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    And viene verificato il costo = "1591" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-350GR_35] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    And viene verificato il costo = "1648" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-351GR_36] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    And viene verificato il costo = "2100" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-1000GR_37] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    And viene verificato il costo = "2487" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-1001GR_38] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    And viene verificato il costo = "3292" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-2000GR_39] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    And viene verificato il costo = "3889" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-20GR_40] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
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

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-21GR_41] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 21gr
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
    And viene verificato il costo = "1246" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-50GR_42] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 50gr
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
    And viene verificato il costo = "1261" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-51GR_43] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 51gr
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
    And viene verificato il costo = "1410" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-100GR_44] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1437" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-101GR_45] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 101gr
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
    And viene verificato il costo = "1828" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-250GR_46] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "1915" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-251GR_47] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 251gr
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
    And viene verificato il costo = "2442" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-350GR_48] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 350gr
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
    And viene verificato il costo = "2499" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-351GR_49] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 351gr
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
    And viene verificato il costo = "3561" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-1000GR_50] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1000gr
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
    And viene verificato il costo = "3948" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-1001GR_51] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 1001gr
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
    And viene verificato il costo = "5273" della notifica

  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-2000GR_52] Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso = 2000gr
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
    And viene verificato il costo = "5870" della notifica



  @CostoCartaceoComplete
  Scenario Outline: [CALCOLO-COSTO_AR-50GR-F24_53] Invio notifica e verifica calcolo del costo su raccomandata con peso = 50gr e con un f24
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | feePolicy             | DELIVERY_MODE        |
      | document              | DOC_8_PG;DOC_5_PG;   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@ok_AR                         |
      | physicalAddress_municipality | <MUNICIPALITY>                    |
      | physicalAddress_province     | <PROVINCE>                        |
      | physicalAddress_zip          | <CAP>                             |
      | payment_pagoPaForm           | NOALLEGATO                        |
      | payment_f24                  | PAYMENT_F24_SIMPLIFIED            |
      | title_payment                | F24_SEMPLIFICATO_CLMCST42R12D969Z |
      | apply_cost_f24               | SI                                |
      | payment_multy_number         | 1                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                |
      | 80060 | 634   | MASSAQUANO | NA       | notifica analogica FSU |



  @CostoCartaceoComplete
  Scenario: [CALCOLO-COSTO_AR-50GR-F24_54] Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso = 50gr e con un f24
    Given viene generata una nuova notifica
      | subject               | notifica analogica ZONA 1 |
      | senderDenomination    | Comune di palermo         |
      | physicalCommunication | AR_REGISTERED_LETTER      |
      | feePolicy             | DELIVERY_MODE             |
      | document              | DOC_8_PG;DOC_5_PG;        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                              |
      | physicalAddress_State   | ALBANIA                           |
      | physicalAddress_zip     | ZONE_1                            |
      | physicalAddress_address | Via@ok_RIR                        |
      | payment_pagoPaForm      | NOALLEGATO                        |
      | payment_f24             | PAYMENT_F24_SIMPLIFIED            |
      | title_payment           | F24_SEMPLIFICATO_CLMCST42R12D969Z |
      | apply_cost_f24          | SI                                |
      | payment_multy_number    | 1                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1049" della notifica


  Scenario Outline: [CALCOLO-COSTO_AR-50GR-F24_55] Invio notifica e verifica calcolo del costo su raccomandata con peso = 605gr e con 120 f24
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | feePolicy             | DELIVERY_MODE        |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@ok_AR                         |
      | physicalAddress_municipality | <MUNICIPALITY>                    |
      | physicalAddress_province     | <PROVINCE>                        |
      | physicalAddress_zip          | <CAP>                             |
      | payment_pagoPaForm           | NULL                              |
      | apply_cost_pagopa            | NULL                                |
      | payment_f24                  | PAYMENT_F24_SIMPLIFIED            |
      | title_payment                | F24_SEMPLIFICATO_CLMCST42R12D969Z |
      | apply_cost_f24               | SI                                |
      | payment_multy_number         | 120                               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Examples:
      | CAP   | MUNICIPALITY | PROVINCE | SUBJECT                |
      | 80060 | MASSAQUANO   | NA       | notifica analogica FSU |
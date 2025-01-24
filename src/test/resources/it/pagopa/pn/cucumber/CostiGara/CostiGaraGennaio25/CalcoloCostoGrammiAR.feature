Feature: calcolo costo notifica in base hai grammi con notfiche AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 543   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 604   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 535   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 376   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 465   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 396   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 416   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 543   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 456   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 482   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 90088 | 465   | SAN CIPIRELLO   | PA    | notifica analogica RECAPITISTA |
      | 84022 | 465   | CAMPAGNA        | SA    | notifica analogica RECAPITISTA |
      | 88071 | 465   | STALETTI        | CZ    | notifica analogica RECAPITISTA |
      | 85036 | 604   | ROCCANOVA       | PZ    | notifica analogica RECAPITISTA |
      | 21009 | 543   | BARDELLO        | VA    | notifica analogica RECAPITISTA |
      | 64011 | 604   | ALBA ADRIATICA  | TE    | notifica analogica RECAPITISTA |
      | 86170 | 535   | ISERNIA         | IS    | notifica analogica RECAPITISTA |

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 615   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 666   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 577   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 413   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 524   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 437   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 458   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 616   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 506   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 534   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 74021 | 506   | CAROSINO        | TA       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 632   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 683   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 594   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 429   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 540   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 453   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 474   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 632   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 522   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 550   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 91030 | 540   | SAN VITO LO CAPO  | TP       | notifica analogica RECAPITISTA |
      | 36049 | 632  | SOVIZZO            | VI       | notifica analogica RECAPITISTA |
      | 63094 | 683   | BISIGNANO        | AP       | notifica analogica RECAPITISTA |
      | 64011 | 683   | ALBA ADRIATICA   | TE       | notifica analogica RECAPITISTA |
      | 86170 | 594   | ISERNIA          | IS       | notifica analogica RECAPITISTA |

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 672   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 724   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 636   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 458   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 573   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 486   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 506   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 672   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 562   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 590   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 28028 | 672   | PETTENASCO        | NO       | notifica analogica RECAPITISTA |

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 701   | MASSAQUANO | NA       | notifica analogica FSU         |
      | 60012 | 754   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 666   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 488   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 602   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 515   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 536   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 701   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 592   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 619    | FOGLIANO    | LT       | notifica analogica RECAPITISTA |
      | 91032 | 602    | PETROSINO   | TP       | notifica analogica RECAPITISTA |

      | 80146 | 515    | NAPOLI   | NA       | notifica analogica RECAPITISTA |

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 745   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 800   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 712   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 520   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 638   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 551   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 572   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 745   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 636   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 663   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 90020 | 638    | CASTELLANA SICULA   | PA       | notifica analogica RECAPITISTA |
      | 64100 | 712    | TERAMO      | TE       | notifica analogica RECAPITISTA |
      | 86081 | 800    | AGNONE      | IS       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 840   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 895   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 807   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 615   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 733   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 646   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 667   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 840   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 730   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 758   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 92035 | 733   | JOPPOLO GIANCAXIO | AG  | notifica analogica RECAPITISTA |

      | 87030 | 733    | SAN VINCENZO LA COSTA   | CS       | notifica analogica RECAPITISTA |

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 876   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 932   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 850   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 641   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 762   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 675   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 699   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 876   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 766   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 798   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |
      | 51018 | 876   | PIEVE A NIEVOLE     | PT       | notifica analogica RECAPITISTA |
      | 64100 | 850   | TERAMO      | TE       | notifica analogica RECAPITISTA |
      | 86081 | 932   | AGNONE      | IS       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 938   | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 995   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 912   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 703   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 824   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 737   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 761   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 938   | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 828   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 860   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 95058 | 824   | CAMPOROTONDO ETNEO      | CT       | notifica analogica RECAPITISTA |
      | 44026 | 938   | MESOLA     | FE       | notifica analogica RECAPITISTA |

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 1013  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 1075  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 988   | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 758   | BARI         | BA       | notifica analogica RECAPITISTA |
      | 80013 | 886   | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 799   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 819   | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1014  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 904   | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 932   | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 1435  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 1497  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 1410  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 1180  | BARI          | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1307  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1220  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1241  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1435  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1325  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1353  | FOGLIANO     | LT       | notifica analogica RECAPITISTA |

      | 96026 | 1307  | PORTOPALO DI CAPO PASSERO | SR  | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 1524  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 1592  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 1505  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 1245  | BARI          | BA       | notifica analogica RECAPITISTA |
      | 80013 | 1379  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1293  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1313  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 1525  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 1415  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 1444  | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 80060 | 2174  | MASSAQUANO   | NA       | notifica analogica FSU         |
      | 60012 | 2242  | MONTERADO    | AN       | notifica analogica RECAPITISTA |
      | 60123 | 2155  | ANCONA       | AN       | notifica analogica RECAPITISTA |
      | 70123 | 1895  | BARI          | BA       | notifica analogica RECAPITISTA |
      | 80013 | 2029  | CASAREA      | NA       | notifica analogica RECAPITISTA |
      | 80123 | 1943  | NAPOLI       | NA       | notifica analogica RECAPITISTA |
      | 83100 | 1963  | AVELLINO     | AV       | notifica analogica RECAPITISTA |
      | 00012 | 2175  | ALBUCCIONE   | RM       | notifica analogica RECAPITISTA |
      | 00118 | 2065  | ROMA         | RM       | notifica analogica RECAPITISTA |
      | 04100 | 2094  | FOGLIANO     | LT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | COSTO | STATE   |
      | 1026  | MESSICO |
      | 1026  | SUD AFRICA |

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1242" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1259" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1362" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1391" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1786" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1881" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2071" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2133" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2854" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3275" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4443" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "5094" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "912" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1124" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1141" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1235" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1264" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1454" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1549" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1687" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1749" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2196" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2617" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3411" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4061" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1082" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1333" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1350" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1496" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1526" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1911" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2006" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2527" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "2589" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "3635" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "4057" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "5364" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "6014" della notifica




  @costoAnalogicoGennaio25
  Scenario: [CALCOLO-COSTO_AR-100GR_53] (Gennaio) Invio notifica ZONE_1 (test num.2) e verifica calcolo del costo su raccomandata con peso = 100gr
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
    And viene verificato il costo = "1264" della notifica

  @costoAnalogicoGennaio25
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1786" della notifica


  @costoAnalogicoGennaio25
  Scenario: [CALCOLO-COSTO_AR-250GR_55] (Gennaio) Invio notifica ZONE_3 (test num.2) e verifica calcolo del costo su raccomandata con peso = 250gr
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
    And viene verificato il costo = "2006" della notifica
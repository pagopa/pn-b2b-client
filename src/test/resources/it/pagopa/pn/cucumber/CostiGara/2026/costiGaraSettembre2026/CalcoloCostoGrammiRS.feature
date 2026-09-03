Feature: calcolo costo notifica in base hai grammi con notifiche RS

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_RS-20GR_1] (Settembre) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject            | <SUBJECT>         |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
      | document           | DOC_4_PG;         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NOALLEGATO     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY            | PROVINCE | SUBJECT                        |
      | 60010 | 397   | BARBARA                 | AN       | notifica analogica FSU         |
      | 04100 | 354   | LE FERRIERE             | LT       | notifica analogica RECAPITISTA |
      | 00123 | 325   | ROMA                    | RM       | notifica analogica RECAPITISTA |
      | 00018 | 418   | CRETONE                 | RM       | notifica analogica RECAPITISTA |
      | 70124 | 272   | BARI                    | BA       | notifica analogica RECAPITISTA |
      | 60012 | 341   | MONTERADO               | AN       | notifica analogica RECAPITISTA |
      | 60126 | 292   | ANCONA                  | AN       | notifica analogica RECAPITISTA |
      | 80022 | 341   | ARZANO                  | NA       | notifica analogica RECAPITISTA |
      | 84124 | 292   | SALERNO                 | SA       | notifica analogica RECAPITISTA |
      | 80129 | 272   | NAPOLI                  | NA       | notifica analogica RECAPITISTA |
      | 27062 | 418   | CAMPOSPINOSO ALBAREDO   | PV       | notifica analogica RECAPITISTA |
      | 92038 | 341   | MONTEVAGO               | AG       | notifica analogica RECAPITISTA |
      | 92044 | 341   | PALMA DI MONTECHIARO    | AG       | notifica analogica RECAPITISTA |

      | 14027 | 418   | TONENGO                 | AT       | notifica analogica RECAPITISTA |
      | 90010 | 397   | ALTAVILLA MILICIA       | PA       | notifica analogica RECAPITISTA |
      | 88020 | 341   | CORTALE                 | CZ       | notifica analogica RECAPITISTA |

      | 84022 | 397   | CAMPAGNA                | SA       | notifica analogica RECAPITISTA |
      | 90052 | 397   | CERDA                   | PA       | notifica analogica RECAPITISTA |
      | 95059 | 397   | LICODIA EUBEA           | CT       | notifica analogica RECAPITISTA |

      | 27063 | 418   | MONTALTO PAVESE         | PV       | notifica analogica RECAPITISTA |
      | 07053 | 418   | BADESI                  | OT       | notifica analogica RECAPITISTA |
      | 07054 | 418   | BORTIGIADAS             | OT       | notifica analogica RECAPITISTA |
      | 07055 | 418   | SANT'ANTONIO DI GALLURA | OT       | notifica analogica RECAPITISTA |
      | 09001 | 418   | DECIMOPUTZU             | CA       | notifica analogica RECAPITISTA |
      | 09002 | 418   | DOMUS DE MARIA          | CA       | notifica analogica RECAPITISTA |
      | 09003 | 418   | SILIQUA                 | CA       | notifica analogica RECAPITISTA |
      | 09004 | 418   | VALLERMOSA              | CA       | notifica analogica RECAPITISTA |
      | 09005 | 418   | VILLASPECIOSA           | CA       | notifica analogica RECAPITISTA |
      | 09006 | 418   | PIMENTEL                | CA       | notifica analogica RECAPITISTA |
      | 09007 | 418   | SAMATZAI                | CA       | notifica analogica RECAPITISTA |
      | 09008 | 418   | USSANA                  | CA       | notifica analogica RECAPITISTA |
      | 08014 | 418   | SEULO                   | NU       | notifica analogica RECAPITISTA |


  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_RS-20GR_2] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
      | document           | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_State   | <state>      |
      | physicalAddress_zip     | ZONE_1       |
      | physicalAddress_address | Via@ok_RIS   |
      | payment_pagoPaForm      | NOALLEGATO   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "724" della notifica
    Examples:
      | state              |
      | ALBANIA            |
      | MACEDONIA DEL NORD |

  @costoAnalogicoSettembre26
  Scenario: [CALCOLO-COSTO_RS-20GR_3] (Settembre) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
      | document           | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_State   | MESSICO      |
      | physicalAddress_zip     | ZONE_2       |
      | physicalAddress_address | Via@ok_RIS   |
      | payment_pagoPaForm      | NOALLEGATO   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "837" della notifica

  @costoAnalogicoSettembre26
  Scenario Outline: [CALCOLO-COSTO_RS-20GR_4] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
      | document           | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_State   | <state>      |
      | physicalAddress_zip     | ZONE_3       |
      | physicalAddress_address | Via@ok_RIS   |
      | payment_pagoPaForm      | NOALLEGATO   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "893" della notifica
    Examples:
      | state       |
      | AUSTRALIA   |
      | MIDWAY      |
      | PHOENIX     |
      | SANTA CROCE |
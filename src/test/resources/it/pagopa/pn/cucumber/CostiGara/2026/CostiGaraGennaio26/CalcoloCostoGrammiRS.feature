Feature: calcolo costo notifica in base hai grammi con notifiche RS

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_RS-20GR_1] (Gennaio) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
      | CAP   | COSTO | MUNICIPALITY          | PROVINCE | SUBJECT                        |
      | 60010 | 396   | BARBARA               | AN       | notifica analogica FSU         |
      | 04100 | 353   | LE FERRIERE           | LT       | notifica analogica RECAPITISTA |
      | 00123 | 324   | ROMA                  | RM       | notifica analogica RECAPITISTA |
      | 00018 | 417   | CRETONE               | RM       | notifica analogica RECAPITISTA |
      | 70124 | 271   | BARI                  | BA       | notifica analogica RECAPITISTA |
      | 60012 | 340   | MONTERADO             | AN       | notifica analogica RECAPITISTA |
      | 60126 | 291   | ANCONA                | AN       | notifica analogica RECAPITISTA |
      | 80022 | 340   | ARZANO                | NA       | notifica analogica RECAPITISTA |
      | 84124 | 291   | SALERNO               | SA       | notifica analogica RECAPITISTA |
      | 80129 | 271   | NAPOLI                | NA       | notifica analogica RECAPITISTA |
      | 27062 | 417   | CAMPOSPINOSO ALBAREDO | PV       | notifica analogica RECAPITISTA |
      | 92038 | 340   | MONTEVAGO             | AG       | notifica analogica RECAPITISTA |
      | 92044 | 340   | PALMA DI MONTECHIARO  | AG       | notifica analogica RECAPITISTA |

      | 14027 | 417   | TONENGO               | AT       | notifica analogica RECAPITISTA |
      | 90010 | 396   | ALTAVILLA MILICIA     | PA       | notifica analogica RECAPITISTA |
      | 88020 | 340   | CORTALE               | CZ       | notifica analogica RECAPITISTA |

      | 84022 | 396   | CAMPAGNA              | SA       | notifica analogica RECAPITISTA |
      | 90052 | 396   | CERDA                 | PA       | notifica analogica RECAPITISTA |
      | 95059 | 396   | LICODIA EUBEA         | CT       | notifica analogica RECAPITISTA |


  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_RS-20GR_2] (Gennaio) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    And viene verificato il costo = "723" della notifica
    Examples:
      | state              |
      | ALBANIA            |
      | MACEDONIA DEL NORD |

  @costoAnalogicoGennaio26
  Scenario: [CALCOLO-COSTO_RS-20GR_3] (Gennaio) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    And viene verificato il costo = "836" della notifica

  @costoAnalogicoGennaio26
  Scenario Outline: [CALCOLO-COSTO_RS-20GR_4] (Gennaio) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    And viene verificato il costo = "892" della notifica
    Examples:
      | state       |
      | AUSTRALIA   |
      | MIDWAY      |
      | PHOENIX     |
      | SANTA CROCE |
Feature: calcolo costo notifica in base hai grammi con notifiche RS

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoAprile25
  Scenario Outline: [CALCOLO-COSTO_RS-20GR_1] (Aprile) Invio notifica e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY          | PROVINCE | SUBJECT                        |
      | 60010 | 398   | BARBARA               | AN       | notifica analogica FSU         |
      | 04100 | 355   | LE FERRIERE           | LT       | notifica analogica RECAPITISTA |
      | 00123 | 326   | ROMA                  | RM       | notifica analogica RECAPITISTA |
      | 00018 | 420   | CRETONE               | RM       | notifica analogica RECAPITISTA |
      | 70124 | 273   | BARI                  | BA       | notifica analogica RECAPITISTA |
      | 60012 | 342   | MONTERADO             | AN       | notifica analogica RECAPITISTA |
      | 60126 | 293   | ANCONA                | AN       | notifica analogica RECAPITISTA |
      | 80022 | 342   | ARZANO                | NA       | notifica analogica RECAPITISTA |
      | 84124 | 293   | SALERNO               | SA       | notifica analogica RECAPITISTA |
      | 80129 | 273   | NAPOLI                | NA       | notifica analogica RECAPITISTA |
      | 27062 | 420   | CAMPOSPINOSO ALBAREDO | PV       | notifica analogica RECAPITISTA |
      | 92038 | 342   | MONTEVAGO             | AG       | notifica analogica RECAPITISTA |
      | 92044 | 342   | PALMA DI MONTECHIARO  | AG       | notifica analogica RECAPITISTA |

      | 14027 | 420   | TONENGO               | AT       | notifica analogica RECAPITISTA |

  @costoAnalogicoAprile25
  Scenario: [CALCOLO-COSTO_RS-20GR_2] (Aprile) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
      | document           | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_State   | ALBANIA      |
      | physicalAddress_zip     | ZONE_1       |
      | physicalAddress_address | Via@ok_RIS   |
      | payment_pagoPaForm      | NOALLEGATO   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "729" della notifica

  @costoAnalogicoAprile25
  Scenario: [CALCOLO-COSTO_RS-20GR_3] (Aprile) Invio notifica ZONE_2 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "842" della notifica

  @costoAnalogicoAprile25
  Scenario: [CALCOLO-COSTO_RS-20GR_4] (Aprile) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
      | document           | DOC_4_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_State   | AUSTRALIA    |
      | physicalAddress_zip     | ZONE_3       |
      | physicalAddress_address | Via@ok_RIS   |
      | payment_pagoPaForm      | NOALLEGATO   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "898" della notifica
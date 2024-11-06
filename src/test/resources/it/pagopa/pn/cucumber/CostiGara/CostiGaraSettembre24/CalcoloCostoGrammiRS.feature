Feature: calcolo costo notifica in base hai grammi con notifiche RS

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @costoAnalogicoSettembre24
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE | SUBJECT                        |
      | 60010 | 402   | OSTRA        | AN       | notifica analogica FSU         |
#      | 04100 | 341   | LE FERRIERE  | LT       | notifica analogica RECAPITISTA |
#      | 00123 | 314   | ROMA         | RM       | notifica analogica RECAPITISTA |
#      | 00018 | 402   | CRETONE      | RM       | notifica analogica RECAPITISTA |
#      | 70124 | 274   | BARI         | BA       | notifica analogica RECAPITISTA |
#      | 60012 | 344   | MONTERADO    | AN       | notifica analogica RECAPITISTA |
#      | 60126 | 295   | ANCONA       | AN       | notifica analogica RECAPITISTA |
#      | 80022 | 344   | ARZANO       | NA       | notifica analogica RECAPITISTA |
#      | 84124 | 295   | SALERNO      | SA       | notifica analogica RECAPITISTA |
#      | 80129 | 274   | NAPOLI       | NA       | notifica analogica RECAPITISTA |
#      | 27062 | 402   | CAMPOSPINOSO ALBAREDO   | PV       | notifica analogica RECAPITISTA |
#      | 92038 | 344   | MONTEVAGO               | AG       | notifica analogica RECAPITISTA |
#      | 92044 | 344   | PALMA DI MONTECHIARO    | AG       | notifica analogica RECAPITISTA |
#
#      | 14027 | 402   | TONENGO              | AT       | notifica analogica RECAPITISTA |

  @costoAnalogicoSettembre24
  Scenario: [CALCOLO-COSTO_RS-20GR_2] (Settembre) Invio notifica ZONE_1 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    And viene verificato il costo = "737" della notifica

  @costoAnalogicoSettembre24
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "852" della notifica

  @costoAnalogicoSettembre24
  Scenario: [CALCOLO-COSTO_RS-20GR_4] (Settembre) Invio notifica ZONE_3 e verifica calcolo del costo su raccomandata con peso <= 20gr
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
    And viene verificato il costo = "909" della notifica





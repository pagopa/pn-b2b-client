Feature: Aggiunta lingua aggiuntiva notifiche

  @additionalLanguage
  Scenario: [BILINGUISMO-1-OK] Viene create una notifica utilizzando la lingua di default
    Given viene generata una nuova notifica V24
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin V24 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V24"

  @additionalLanguage
  Scenario Outline: [BILINGUISMO-2-OK] Viene create una notifica utilizzando una sola lingua ammissibile (DE, SL, FR)
    Given viene generata una nuova notifica V24
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
      | additionalLanguages   | <LANGUAGE>                  |
    And destinatario Mario Gherkin V24 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V24"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |

  @additionalLanguage
  Scenario: [BILINGUISMO-3-KO] Viene create una notifica passando diverse lingue ammissibili
    Given viene generata una nuova notifica V24
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
      | additionalLanguages   | DE,FR                       |
    And destinatario Mario Gherkin V24 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica ha sollevato un errore "400"

  @additionalLanguage
  Scenario: [BILINGUISMO-4-KO] Viene create una notifica passando come lingua un valore non valido
    Given viene generata una nuova notifica V24
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
      | additionalLanguages   | POLACCO                     |
    And destinatario Mario Gherkin V24 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica ha sollevato un errore "400"

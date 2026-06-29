Feature: Aggiunta lingua aggiuntiva notifiche

  @additionalLanguage
  Scenario: [BILINGUISMO-1-OK] Viene create una notifica utilizzando la lingua di default
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

  @additionalLanguage
  Scenario Outline: [BILINGUISMO-2-OK] Viene create una notifica utilizzando una sola lingua ammissibile (DE, SL, FR)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
      | additionalLanguages   | <LANGUAGE>                  |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |

  @additionalLanguage
  Scenario: [BILINGUISMO-3-KO] Viene create una notifica passando diverse lingue ammissibili
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
      | additionalLanguages   | DE,FR                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica ha sollevato un errore "400"

  @additionalLanguage
  Scenario: [BILINGUISMO-4-KO] Viene create una notifica passando come lingua un valore non valido
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
      | additionalLanguages   | POLACCO                     |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica ha sollevato un errore "400"












     #1.  PF - Email - Radd - Analogico
  #@dataprepVarStatiche
  Scenario Outline: [1]
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | additionalLanguages   | <LANGUAGE>                  |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Ada              |
      | taxId                   | LVLDAA85T50G702B |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |


  #2.  PF - PEC - noRadd - Digitale
  @dataprepVarStatiche
  Scenario Outline: [2]
    And viene generata una nuova notifica
      | subject             | invio notifica con cucumber |
      | additionalLanguages | <LANGUAGE>                  |
    And destinatario
      | digitalDomicile_address             | destinatario@certificatanoprod.notifichedigitali.it |
      | physicalAddress_municipality        | Settimo Milanese                                    |
      | physicalAddress_zip                 | 20019                                               |
      | physicalAddress_province            | MI                                                  |
      | physicalAddress_State               | ITALIA                                              |
      | physicalAddress_municipalityDetails | Settimo Milanese                                    |
      | denomination                        | Ada                                                 |
      | taxId                               | LVLDAA85T50G702B                                    |
      #| digitalDomicile         | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |


  #3. PG - Email - Radd - Digitale
  @dataprepVarStatiche
  Scenario Outline: [3]
    And viene generata una nuova notifica
      | subject             | invio notifica con cucumber |
      | additionalLanguages | <LANGUAGE>                  |
    And destinatario
      | denomination  | MarcoPorcioCatoneSpqr |
      | taxId         | 12825810299           |
      | recipientType | PG                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |


  # 4. PG - Email - NoRadd - Analogico
  #@dataprepVarStatiche
  Scenario Outline: [4]
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | additionalLanguages   | <LANGUAGE>                  |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination                        | MarcoPorcioCatoneSpqr |
      | taxId                               | 12825810299           |
      | recipientType                       | PG                    |
      | digitalDomicile                     | NULL                  |
      | physicalAddress_address             | Via@ok_AR             |
      #| physicalAddress_address             | Via Repubblica   |
      | physicalAddress_municipality        | Settimo Milanese      |
      | physicalAddress_zip                 | 20019                 |
      | physicalAddress_province            | MI                    |
      | physicalAddress_State               | ITALIA                |
      | physicalAddress_municipalityDetails | Settimo Milanese      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |


  # 5. PG - PEC - Radd - Digitale
  @dataprepVarStatiche
  Scenario Outline: [5]
    And viene generata una nuova notifica
      | subject             | invio notifica con cucumber |
      | additionalLanguages | <LANGUAGE>                  |
    And destinatario
      | denomination            | MarcoPorcioCatoneSpqr                               |
      | taxId                   | 12825810299                                         |
      | recipientType           | PG                                                  |
      | digitalDomicile_address | destinatario@certificatanoprod.notifichedigitali.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |


  #6.  PF - eMAIL - Radd - Digitale
  @dataprepVarStatiche
  Scenario Outline: [6]
    And viene generata una nuova notifica
      | subject             | invio notifica con cucumber |
      | additionalLanguages | <LANGUAGE>                  |
    And destinatario
      | denomination            | Ada                                                 |
      | taxId                   | LVLDAA85T50G702B                                    |
      | digitalDomicile         | NULL                                                |
      | digitalDomicile_address | destinatario@certificatanoprod.notifichedigitali.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |




    # ITALIANI

    #1.  PF - Email - Radd - Analogico
  #@dataprepVarStatiche
  Scenario: [1I]
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Ada              |
      | taxId                   | LVLDAA85T50G702B |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  #2.  PF - PEC - noRadd - Digitale
  @dataprepVarStatiche
  Scenario: [2I]
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | digitalDomicile_address             | destinatario@certificatanoprod.notifichedigitali.it |
      | physicalAddress_municipality        | Settimo Milanese                                    |
      | physicalAddress_zip                 | 20019                                               |
      | physicalAddress_province            | MI                                                  |
      | physicalAddress_State               | ITALIA                                              |
      | physicalAddress_municipalityDetails | Settimo Milanese                                    |
      | denomination                        | Ada                                                 |
      | taxId                               | LVLDAA85T50G702B                                    |
      #| digitalDomicile         | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


      #3. PG - Email - Radd - Digitale
  @dataprepVarStatiche
  Scenario: [3I]
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination  | MarcoPorcioCatoneSpqr |
      | taxId         | 12825810299           |
      | recipientType | PG                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


      # 4. PG - Email - NoRadd - Analogico
  #@dataprepVarStatiche
  Scenario: [4I]
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      #| physicalCommunication | AR_REGISTERED_LETTER        |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario
      | denomination                        | MarcoPorcioCatoneSpqr |
      | taxId                               | 12825810299           |
      | digitalDomicile                     | NULL                  |
      #| physicalAddress_address             | Via@ok_AR             |
      #| physicalAddress_address             | Via Repubblica   |
      | physicalAddress_address             | Via@ok_890            |
      | physicalAddress_municipality        | Settimo Milanese      |
      | physicalAddress_zip                 | 20019                 |
      | physicalAddress_province            | MI                    |
      | physicalAddress_State               | ITALIA                |
      | physicalAddress_municipalityDetails | Settimo Milanese      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


      # 5. PG - PEC - Radd - Digitale
  @dataprepVarStatiche
  Scenario: [5I]
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination            | MarcoPorcioCatoneSpqr                               |
      | taxId                   | 12825810299                                         |
      | recipientType           | PG                                                  |
      | digitalDomicile_address | destinatario@certificatanoprod.notifichedigitali.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


  #6.  PF - eMAIL - Radd - Digitale
  @dataprepVarStatiche
  Scenario: [6I]
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | taxId                   | LVLDAA85T50G702B                                    |
      | digitalDomicile         | NULL                                                |
      | digitalDomicile_address | destinatario@certificatanoprod.notifichedigitali.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


          #7. PG - Email- Pec- NoRadd - Digitale
  @dataprepVarStatiche
  Scenario: [7I]
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination                        | MarcoPorcioCatoneSpqr                               |
      | taxId                               | 12825810299                                         |
      | recipientType                       | PG                                                  |
      | digitalDomicile_address             | destinatario@certificatanoprod.notifichedigitali.it |
      | physicalAddress_municipality        | Settimo Milanese                                    |
      | physicalAddress_zip                 | 20019                                               |
      | physicalAddress_province            | MI                                                  |
      | physicalAddress_State               | ITALIA                                              |
      | physicalAddress_municipalityDetails | Settimo Milanese                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


  @dataprepVarStatiche
  Scenario Outline: [7]
    And viene generata una nuova notifica
      | subject             | invio notifica con cucumber |
      | additionalLanguages | <LANGUAGE>                  |
    And destinatario
      | denomination                        | MarcoPorcioCatoneSpqr                               |
      | taxId                               | 12825810299                                         |
      | recipientType                       | PG                                                  |
      | digitalDomicile_address             | destinatario@certificatanoprod.notifichedigitali.it |
      | physicalAddress_municipality        | Settimo Milanese                                    |
      | physicalAddress_zip                 | 20019                                               |
      | physicalAddress_province            | MI                                                  |
      | physicalAddress_State               | ITALIA                                              |
      | physicalAddress_municipalityDetails | Settimo Milanese                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Examples:
      | LANGUAGE |
      | DE       |
      | FR       |
      | SL       |


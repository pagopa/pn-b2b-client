Feature: arricchimento della timeline con eventi intermedi (preesiti) di postalizzazione

  @preesitiDisabledFlag
  Scenario: [PREESITI_POSTALIZZAZIONE_1] Verifica assenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode RECAG008A all’interno della timeline B2B sia della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG008A |

  @preesitiDisabledFlag
  Scenario: [PREESITI_POSTALIZZAZIONE_2] Verifica assenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode RECAG012A all’interno della timeline B2B sia della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-lte10_890       |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-Giacenza-lte10_890   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG012A |

  @preesitiDisabledFlag
  Scenario: [PREESITI_POSTALIZZAZIONE_3] Verifica assenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode CON018 all’interno della timeline B2B sia della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | ok_AR                       |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@ok_AR                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è visibile
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON018 |


  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_4] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG008A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    #And la notifica viene recuperata dal sistema tramite codice IUN
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG008A |

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_5] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG012A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-lte10_890     |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile
    #And la notifica viene recuperata dal sistema tramite codice IUN
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG012A |

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_6] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG012 all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-lte10_890     |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@OK-WO-011B            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" non è visibile
    #And la notifica viene recuperata dal sistema tramite codice IUN
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG012 |

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_7] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode CON018 all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-lte10_890     |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@ok_AR                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è visibile
    #And la notifica viene recuperata dal sistema tramite codice IUN
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON018 |

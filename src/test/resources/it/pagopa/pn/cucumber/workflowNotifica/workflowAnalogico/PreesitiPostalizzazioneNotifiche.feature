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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And vengono letti i dettagli della notifica lato web dal destinatario
    #Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile


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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile
    And vengono letti i dettagli della notifica lato web dal destinatario
    #Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile


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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è visibile
    And vengono letti i dettagli della notifica lato web dal destinatario
    #Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è visibile

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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" è visibile
    And vengono letti i dettagli della notifica lato web dal destinatario
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile

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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" è visibile
    And vengono letti i dettagli della notifica lato web dal destinatario
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile

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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012" non è visibile
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" è visibile
    And vengono letti i dettagli della notifica lato web dal destinatario
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012" non è visibile
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" non è visibile

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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" è visibile
    And vengono letti i dettagli della notifica lato web dal destinatario
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è visibile

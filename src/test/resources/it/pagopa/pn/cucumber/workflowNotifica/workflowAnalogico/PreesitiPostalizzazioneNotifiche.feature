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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    And lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è presente
    Then lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è presente

  @preesitiDisabledFlag
  Scenario: [PREESITI_POSTALIZZAZIONE_2] Verifica assenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode RECAG012A all’interno della timeline B2B sia della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-lte10_890     |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    And lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è presente
    Then lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è presente

  @preesitiDisabledFlag
  Scenario: [PREESITI_POSTALIZZAZIONE_3] Verifica assenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode CON018 all’interno della timeline B2B sia della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | ok_AR            |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | via@ok_AR        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    And lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è presente
    Then lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è presente

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" è visibile

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_6] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG012 all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | OK-WO-011B       |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | via@OK-WO-011B   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" è visibile
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è presente
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" è visibile
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è presente

  @precondition @cleanWebhook @preesitiDisabledFlag @webhook3
  Scenario: [PREESITI_POSTALIZZAZIONE_WEBHOOK_1] Verifica assenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode RECAG008A all’interno della timeline stream webhook.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di verona                |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V23" per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SCHEDULE_REFINEMENT" con la versione "V23"
    And vengono letti gli eventi dello stream versione "V23"
    And viene verificato che gli eventi dello stream non contengono l'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A"

  @precondition @cleanWebhook @preesitiEnabledTags @webhook3
  Scenario: [PREESITI_POSTALIZZAZIONE_WEBHOOK_2] Verifica presenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode RECAG012A all’interno della timeline stream webhook.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di verona                |
    And destinatario
      | denomination            | OK-Giacenza-lte10_890     |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@OK-Giacenza-lte10_890 |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V23" per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" con la versione "V23"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SCHEDULE_REFINEMENT" con la versione "V23"
    And vengono letti gli eventi dello stream versione "V23"
    And viene verificato che gli eventi dello stream contengono l'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A"

  @precondition @preesitiEnabledTags @webhook3
  Scenario: [PREESITI_POSTALIZZAZIONE_WEBHOOK_3] Verifica presenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode RECAG012A all’interno della timeline stream webhook.
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di verona                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | OK-WO-011B       |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | via@OK-WO-011B   |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V23" per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" con la versione "V23"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SCHEDULE_REFINEMENT" con la versione "V23"
    And vengono letti gli eventi dello stream versione "V23"
    And viene verificato che gli eventi dello stream contengono l'elemento di timeline "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012"
    And viene verificato che gli eventi dello stream non contengono l'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012"

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_8] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRN003A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | Via@OK-Giacenza_AR      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A" è visibile

  @preesitiEnabledTags
  Scenario Outline: [PREESITI_POSTALIZZAZIONE_9] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRI003A/RECAG003A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | <physicalAddress>       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    Examples:
      | physicalAddress | deliveryDetailCode |
      | Via@OK_RIR      | RECRI003A          |
      | Via@fail_890    | RECAG003A          |

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_10] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG005A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890   |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005A" è visibile

  @preesitiEnabledTags
  Scenario Outline: [PREESITI_POSTALIZZAZIONE_11.A] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRN002A/RECRS002A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | <physicalCommunication>         |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | <physicalAddress>       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    Examples:
      | physicalAddress             | deliveryDetailCode | physicalCommunication |
      | Via@OK-PersonaAbilitata_890 | RECAG002A          | REGISTERED_LETTER_890 |
      | Via@FAIL_AR                 | RECRN002A          | AR_REGISTERED_LETTER  |

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_11.B] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRS002A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002A" è visibile

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_12] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG006A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890           |
      | taxId                   | CLMCST42R12D969Z                  |
      | digitalDomicile         | NULL                              |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006A" è visibile

  @preesitiEnabledTags
  Scenario Outline: [PREESITI_POSTALIZZAZIONE_13] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRN001A/RECRN005A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | <physicalCommunication>         |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | <physicalAddress>       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    Examples:
      | physicalAddress              | deliveryDetailCode | physicalCommunication |
      | Via@OK_AR                    | RECRN001A          | AR_REGISTERED_LETTER  |
      | Via@FAIL-CompiutaGiacenza_AR | RECRN005A          | AR_REGISTERED_LETTER  |
      | Via@OK-WO_890                | RECAG001A          | REGISTERED_LETTER_890 |

  @preesitiEnabledTags
  Scenario Outline: [PREESITI_POSTALIZZAZIONE_14] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRN004A/RECAG007A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | <physicalCommunication>         |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | <physicalAddress>       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    Examples:
      | physicalAddress             | deliveryDetailCode | physicalCommunication |
      | Via@FAIL-Giacenza_AR        | RECRN004A          | AR_REGISTERED_LETTER  |
      | Via@FAIL-Giacenza-lte10_890 | RECAG007A          | REGISTERED_LETTER_890 |

  @preesitiEnabledTags
  Scenario Outline: [PREESITI_POSTALIZZAZIONE_15] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRN002D/RECAG003D all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | <physicalCommunication>         |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | <physicalAddress>       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    Examples:
      | physicalAddress               | deliveryDetailCode | physicalCommunication |
      | Via@FAIL-Irreperibile_AR_SLOW | RECRN002D          | AR_REGISTERED_LETTER  |
      | Via@FAIL-Irreperibile_890     | RECAG003D          | REGISTERED_LETTER_890 |

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_16] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG008A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | Via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" è visibile

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_17.A] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRI004A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_RIR |
      | digitalDomicile         | NULL         |
      | physicalAddress_State   | MESSICO      |
      | physicalAddress_zip     | ZONE_2       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Cucumber"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004A" è visibile

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_17.B] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRSI004A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL         |
      | physicalAddress_State   | ALBANIA      |
      | physicalAddress_zip     | ZONE_1       |
      | physicalAddress_address | Via@fail_RIS |
      | payment_pagoPaForm      | NOALLEGATO   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRSI004A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRSI004A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRSI004A" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRSI004A" è visibile

  @preesitiEnabledTags
  Scenario Outline: [PREESITI_POSTALIZZAZIONE_18] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECRS002D/RECRS004A/RECRS005A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | <physicalAddress>       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" non è visibile
    And lato mittente da api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>" è visibile
    Examples:
      | physicalAddress                        | deliveryDetailCode |
      | Via@FAIL_RS_IRREPERIBILE_ASSOLUTO      | RECRS002D          |
      | Via@FAIL_RS_MANCATA_CONSEGNA_PGIACENZA | RECRS004A          |
      | Via@OK_RS_COMPIUTA_GIACENZA            | RECRS005A          |



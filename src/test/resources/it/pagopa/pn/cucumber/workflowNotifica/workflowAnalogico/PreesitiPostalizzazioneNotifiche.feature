Feature: arricchimento della timeline con eventi intermedi (preesiti) di postalizzazione

  @preesitiDisabledFlag
  Scenario: [PREESITI_POSTALIZZAZIONE_1] Verifica assenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode sia all’interno della timeline B2B sia della timeline web
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
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS002A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS002D" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002D" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG002A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003D" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS004A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS005A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN004A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRSI004A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004A" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012" non è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" non è visibile
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS002A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS002D |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN001A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN002A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN002D |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG001A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG002A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG003A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG003D |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS005A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN003A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN005A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG005A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG006A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG007A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG008A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRSI004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRI003A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRI004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON018 |

  @preesitiEnabledTags
  Scenario: [PREESITI_POSTALIZZAZIONE_2] Verifica presenza evento SEND_ANALOG_PROGRESS con i nuovi DeliveryDetailCode sia all’interno della timeline B2B sia della timeline web
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
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS002A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS002D" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002D" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG002A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003D" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS004A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS005A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN004A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRSI004A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004A" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012" è visibile
    Then lato utente l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018" è visibile
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS002A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS002D |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN001A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN002A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN002D |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG001A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG002A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG003A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG003D |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRS005A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN003A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN005A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG005A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG006A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG007A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG008A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRSI004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRI003A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRI004A |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON018 |



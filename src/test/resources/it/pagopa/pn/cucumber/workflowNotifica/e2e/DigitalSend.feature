Feature: Digital send e2e

  @e2e @ignore
  Scenario: [B2B_DIGITAL_SEND_6] Invio ad indirizzo speciale successo al primo tentativo
    Given viene generata una nuova notifica
        | subject | invio notifica con cucumber |
        | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
        | digitalDomicile_address | testpagopa1@pnpagopa.postecert.local |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
        | details | NOT_NULL |
        | details_digitalAddress | {"address": "testpagopa1@pnpagopa.postecert.local", "type": "PEC"} |
        | details_recIndex | 0 |
        | details_digitalAddressSource | SPECIAL |
        | details_retryNumber | 0 |
        | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
        | details | NOT_NULL |
        | details_responseStatus | OK |
        | details_sendingReceipts | [{"id": null, "system": null}] |
        | details_digitalAddress | {"address": "testpagopa1@pnpagopa.postecert.local", "type": "PEC"} |
        | details_recIndex | 0 |
        | details_digitalAddressSource | SPECIAL |
        | details_retryNumber | 0 |
        | details_sentAttemptMade | 0 |
    # Then viene verificato che l'elemento di timeline "GET_ADDRESS" esista
    #    | details | NOT_NULL |
    #    | details_digitalAddress | {"address": "testpagopa1@pnpagopa.postecert.local", "type": "PEC"} |
    #    | details_recIndex | 0 |
    #    | details_digitalAddressSource | SPECIAL |
    #    | details_retryNumber | 0 |
    #    | details_sentAttemptMade | 0 |

  @e2e @ignore
  Scenario: [B2B_DIGITAL_SEND_7] Invio ad indirizzo speciale fallimento al primo tentativo e successo al secondo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@OK-pecFirstFailSecondSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO"
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista e che abbia details
      | digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | recIndex | 0 |
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And viene verificato che l'elemento di timeline "DELIVERED" esista e che abbia details
      | digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | recIndex | 0 |

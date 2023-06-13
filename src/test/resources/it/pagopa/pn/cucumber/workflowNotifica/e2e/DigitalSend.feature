Feature: Digital send e2e

  @e2e
  Scenario: [B2B_DIGITAL_SEND_1] Invio ad indirizzo di piattaforma successo al primo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    #And viene effettuato il pre-caricamento di un documento
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato
    And viene letta la timeline fino all'elemento "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    #And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"


  @e2e @ignore
  Scenario: [B2B_DIGITAL_SEND_2] Invio ad indirizzo speciale fallimento al primo tentativo e fallimento al secondo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@FAIL-pecFirstKOSecondKO.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "test@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "test@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "examtest@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_6] Invio ad indirizzo speciale successo al primo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | testpagopa1@pnpagopa.postecert.local |
    #And viene effettuato il pre-caricamento di un documento
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato
    And viene letta la timeline fino all'elemento "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
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
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "testpagopa1@pnpagopa.postecert.local", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    #And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @e2e
  Scenario: [B2B_DIGITAL_SEND_7] Invio ad indirizzo speciale fallimento al primo tentativo e successo al secondo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@OK-pecFirstFailSecondSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_9] Invio ad indirizzo generale successo al primo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Louis Armstrong |
      | taxId | RMSLSO31M04Z404R |
      | digitalDomicile | NULL |
    #And viene effettuato il pre-caricamento di un documento
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato
    And viene letta la timeline fino all'elemento "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    #And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @e2e
  Scenario: [B2B_DIGITAL_SEND_10] Invio ad indirizzo generale fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Sara Bianchi |
      | taxId | SHRSWP58T71D544X |
      | digitalDomicile | NULL |
    #And viene effettuato il pre-caricamento di un documento
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato
    And viene letta la timeline fino all'elemento "SEND_DIGITAL_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene letta la timeline fino all'elemento "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    #And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @e2e
  Scenario: [B2B_DIGITAL_SEND_11] Invio ad indirizzo generale fallimento al primo tentativo, successo al ritentativo e al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Mario Rossi |
      | taxId | PGVCKH47H05A521N |
      | digitalDomicile | NULL |
    #And viene effettuato il pre-caricamento di un documento
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato
    And viene letta la timeline fino all'elemento "SEND_DIGITAL_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene letta la timeline fino all'elemento "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    #And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @e2e
  Scenario: [B2B_DIGITAL_SEND_12] Invio ad indirizzo generale fallimento al primo tentativo e al ritentativo, successo al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Mario Gialli |
      | taxId | PTRVTL34D21F890A |
      | digitalDomicile | NULL |
    #And viene effettuato il pre-caricamento di un documento
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato
    And viene letta la timeline fino all'elemento "SEND_DIGITAL_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene letta la timeline fino all'elemento "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    #And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @e2e
    # da scrivere
  Scenario: [B2B_DIGITAL_SEND_13] Invio ad indirizzo generale fallimento al primo tentativo, al ritentativo e al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Mario Gialli |
      | taxId | PTRVTL34D21F890A |
      | digitalDomicile | NULL |
    #And viene effettuato il pre-caricamento di un documento
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato
    And viene letta la timeline fino all'elemento "SEND_DIGITAL_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    And viene letta la timeline fino all'elemento "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 1 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    #And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"
    #And viene verificato che l'elemento di timeline "DELIVERED" esista e che abbia details
     # | digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
     # | recIndex | 0 |





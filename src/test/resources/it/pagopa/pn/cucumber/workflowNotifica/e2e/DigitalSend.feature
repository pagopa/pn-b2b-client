Feature: Digital send e2e

  @e2e
  Scenario: [B2B_DIGITAL_SEND_1] Invio ad indirizzo di piattaforma successo al primo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_2] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@OK-pecFirstFailSecondSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "example@FAIL-pecFirstKO.it"
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_3] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e al secondo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@OK-pecFirstFailSecondSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "example@OK-pecSuccess.it"
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_4] Invio ad indirizzo di piattaforma fallimento al primo tentativo e al ritentativo, successo al secondo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@FAIL-pecFirstKOSecondKO.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "example@OK-pecSuccess.it"
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_5] Invio ad indirizzo di piattaforma fallimento al primo tentativo, al ritentativo e al secondo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@FAIL-pecFirstKOSecondKO.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "example@FAIL-pecFirstKO.it"
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost | 133 |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "PREPARE_SIMPLE_REGISTERED_LETTER" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_6] Invio ad indirizzo speciale successo al primo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | testpagopa1@pnpagopa.postecert.local |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "testpagopa1@pnpagopa.postecert.local", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "testpagopa1@pnpagopa.postecert.local", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "testpagopa1@pnpagopa.postecert.local", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

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
  Scenario: [B2B_DIGITAL_SEND_8] Invio ad indirizzo speciale fallimento al primo tentativo e fallimento al secondo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
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
  Scenario: [B2B_DIGITAL_SEND_9] Invio ad indirizzo generale successo al primo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Louis Armstrong |
      | taxId | RMSLSO31M04Z404R |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_10] Invio ad indirizzo generale fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Sara Bianchi |
      | taxId | SHRSWP58T71D544X |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_11] Invio ad indirizzo generale fallimento al primo tentativo, successo al ritentativo e al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Mario Rossi |
      | taxId | PGVCKH47H05A521N |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_12] Invio ad indirizzo generale fallimento al primo tentativo e al ritentativo, successo al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Mario Gialli |
      | taxId | PTRVTL34D21F890A |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [B2B_DIGITAL_SEND_13] Invio ad indirizzo generale fallimento al primo tentativo, al ritentativo e al secondo tentativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Mario Gialli |
      | taxId | VHGRBT95E07L215U |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    # si ritenta l'invio
    And si attende che si ritenti l'invio dopo l'evento "SEND_DIGITAL_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | isFirstSendRetry | true |
    # secondo invio
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | false |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
      | details_isAvailable | true |
      | isFirstSendRetry | true |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost | 133 |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "PREPARE_SIMPLE_REGISTERED_LETTER" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |





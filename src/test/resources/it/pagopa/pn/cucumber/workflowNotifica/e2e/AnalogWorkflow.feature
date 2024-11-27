Feature: Workflow analogico

  @e2e
  Scenario: [E2E-WF-ANALOG-1] Invio notifica con percorso analogico 890 con verifica elementi di timeline SEND_ANALOG_PROGRESS con legalFactId di category ANALOG_DELIVERY
  e documentType 23L, SEND_ANALOG_FEEDBACK e ANALOG_SUCCESS_WORKFLOW
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci|
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                           |
      | details                 | NOT_NULL                                                                                                                                                                                       |
      | details_recIndex        | 0                                                                                                                                                                                              |
      | details_sentAttemptMade | 0                                                                                                                                                                                              |
      | details_physicalAddress | {"address": "VIA@OK_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @giacenza890Complex @con020 @con020success
    # TODO c'è scritto OK-GiacenzaDelegato-lte10_890 nello scenario ma si manda un @OK-PersonaAbilitata_890 nell'address, è giusto?
  Scenario: [E2E-WF-ANALOG-2] Invio notifica con percorso analogico. Successo giacenza delegato 890 (OK-GiacenzaDelegato-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-PersonaAbilitata_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline            | true                                                                                                                                                                                                            |
      | details                 | NOT_NULL                                                                                                                                                                                                        |
      | details_recIndex        | 0                                                                                                                                                                                                               |
      | details_sentAttemptMade | 0                                                                                                                                                                                                               |
      | details_physicalAddress | {"address": "VIA@OK-PERSONAABILITATA_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON020 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG002B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG002C |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-PERSONAABILITATA_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus | OK |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @con020 @con020success
  Scenario: [E2E-WF-ANALOG-3] Invio notifica con percorso analogico 890. Successo al secondo tentativo invio 890.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Retry_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline            | true                                                                                                                                                                                                 |
      | details                 | NOT_NULL                                                                                                                                                                                             |
      | details_recIndex        | 0                                                                                                                                                                                                    |
      | details_sentAttemptMade | 0                                                                                                                                                                                                    |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 2
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON020 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG004 |
      | details_deliveryFailureCause | F01 F02 F03 F04 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | OK |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-4] Invio notifica con percorso analogico. Successo invio RS (OK_RS)
    Given viene generata una nuova notifica
      | subject | notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" esista
      | loadTimeline            | true                                                                                                                                                         |
      | details                 | NOT_NULL                                                                                                                                                     |
      | details_recIndex        | 0                                                                                                                                                            |
      | details_physicalAddress | {"address": "VIA@OK_RS", "municipality": "COSENZA", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost      | 194                                                                                                                                                          |
    And viene verificato che l'elemento di timeline "DIGITAL_DELIVERY_CREATION_REQUEST" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "DIGITAL_FAILURE_WORKFLOW" esista
      | details                 | NOT_NULL                           |
      | details_recIndex        | 0                                  |
      | details_sentAttemptMade | 0                                  |
      | legalFactsIds           | [{"category": "DIGITAL_DELIVERY"}] |
   # And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate più 3"m" per il destinatario 0 rispetto ell'evento in timeline "DIGITAL_DELIVERY_CREATION_REQUEST"
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex             | 0                                  |
      | details_digitalAddressSource | SPECIAL                            |
      | details_sentAttemptMade | 0                                  |
    And viene verificato che l'elemento di timeline "PREPARE_SIMPLE_REGISTERED_LETTER" esista
      | details                 | NOT_NULL                                                                                                                                                                                |
      | details_recIndex        | 0                                                                                                                                                                                       |
      | details_physicalAddress | {"address": "VIA@OK_RS", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |


  @e2e
  Scenario: [E2E-WF-ANALOG-5] Invio notifica con percorso analogico. Successo invio RS (OK_RS) in cui la notifica viene visualizzata prima
  dell’evento SEND_SIMPLE_REGISTERED_LETTER
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
    And viene verificato che il numero di elementi di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" della notifica sia di 0

  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-6] Invio notifica con percorso analogico. Successo al secondo tentativo invio RS (OK-Retry_RS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@OK-Retry_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" esista
      | loadTimeline            | true                                                                                                                                                                                         |
      | details                 | NOT_NULL                                                                                                                                                                                     |
      | details_recIndex        | 0                                                                                                                                                                                            |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_RS", "municipality": "MILANO", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost      | 133                                                                                                                                                                                          |
    And viene verificato che l'elemento di timeline "DIGITAL_FAILURE_WORKFLOW" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "PREPARE_SIMPLE_REGISTERED_LETTER" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_RS", "municipality": "MILANO", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_sentAttemptMade | 0 |
      | details_recIndex | 0 |
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details | NOT_NULL |
      | details_sentAttemptMade | 1 |
      | details_recIndex | 0 |
    And viene verificato che il numero di elementi di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" sia di 2
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |

  @e2e @giacenza890Complex @con020 @con020success
  Scenario: [E2E-WF-ANALOG-7] Invio notifica con percorso analogico. Successo giacenza lte 890 (OK-Giacenza-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline            | true                                                                                                                                                                                                          |
      | details                 | NOT_NULL                                                                                                                                                                                                      |
      | details_recIndex        | 0                                                                                                                                                                                                             |
      | details_sentAttemptMade | 0                                                                                                                                                                                                             |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-LTE10_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON020 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @ignore @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-8] Invio notifica con percorso analogico. Successo giacenza gt 890 (OK-Giacenza-gt10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-GT10_890", "municipality": "MILANO", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @ignore @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-8_1] Invio notifica con percorso analogico. Successo giacenza gt 890 (OK-Giacenza-gt10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-GT10_890", "municipality": "MILANO", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-9] Invio notifica con percorso analogico. Successo giacenza 890 gt 23L(OK-Giacenza-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                             |
      | details                 | NOT_NULL                                                                                                                                                                                                         |
      | details_recIndex        | 0                                                                                                                                                                                                                |
      | details_sentAttemptMade | 0                                                                                                                                                                                                                |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-GT10-23L_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-10] Invio notifica con percorso analogico. Successo giacenza 890 gt 23L(OK-GiacenzaDelegato-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006C" e verifica data delay più 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006C |


  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-11] Partenza workflow cartaceo se viene inviato un messaggio di cortesia
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_DOMICILE" esista
      | loadTimeline | true  |
      | poolingTime  | 360000 |
      | numCheck     | 16     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SCHEDULE_ANALOG_WORKFLOW" esista
      | NULL | NULL |
    And controlla che il timestamp di "SEND_ANALOG_DOMICILE" sia dopo quello di invio e di attesa di lettura del messaggio di cortesia
      | NULL | NULL |

  @e2e  @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-12] Invio notifica con percorso analogico. Successo giacenza delegato 890 (OK-GiacenzaDelegato-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                                   |
      | details                 | NOT_NULL                                                                                                                                                                                                               |
      | details_recIndex        | 0                                                                                                                                                                                                                      |
      | details_sentAttemptMade | 0                                                                                                                                                                                                                      |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZADELEGATO-LTE10_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-13] Invio notifica con percorso analogico. Successo giacenza delegato gt 23L 890 (OK-GiacenzaDelegato-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                                     |
      | details                 | NOT_NULL                                                                                                                                                                                                                 |
      | details_recIndex        | 0                                                                                                                                                                                                                        |
      | details_sentAttemptMade | 0                                                                                                                                                                                                                        |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZADELEGATO-GT10-23L_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e @ignore @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-14] Invio notifica con percorso analogico. Fallimento giacenza gt 890 (FAIL-Giacenza-gt10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007C |

  @e2e @ignore @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-15] Invio notifica con percorso analogico. Fallimento giacenza gt 23L 890 (FAIL-Giacenza-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007C |

  @e2e @ignore @giacenza890Complex
  Scenario: [E2E-WF-ANALOG-16] Invio notifica con percorso analogico. Compiuta giacenza 890 (OK-CompiutaGiacenza_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-COMPIUTA-GIACENZA_890", "municipality": "MILANO", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG008B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG008C |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-17] Invio notifica con percorso analogico. Successo giacenza ar (OK-Giacenza_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                   |
      | pollingTime             | 30000                                                                                                                                                                                                  |
      | numCheck                | 30                                                                                                                                                                                                     |
      | details                 | NOT_NULL                                                                                                                                                                                               |
      | details_recIndex        | 0                                                                                                                                                                                                      |
      | details_sentAttemptMade | 0                                                                                                                                                                                                      |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003B |
      | details_attachments | [{"documentType": "AR"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-18] Successo invio raccomandata semplice
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                          |
      | details                 | NOT_NULL                                                                                                                                                                                      |
      | details_recIndex        | 0                                                                                                                                                                                             |
      | details_sentAttemptMade | 0                                                                                                                                                                                             |
      | details_physicalAddress | {"address": "VIA@OK_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |

  @e2e
  Scenario: [E2E-WF-ANALOG-19] Invio notifica con percorso analogico. Fallimento giacenza lte 890 (FAIL-Giacenza-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                            |
      | pollingTime             | 30000                                                                                                                                                                                                           |
      | numCheck                | 30                                                                                                                                                                                                              |
      | details                 | NOT_NULL                                                                                                                                                                                                        |
      | details_recIndex        | 0                                                                                                                                                                                                               |
      | details_sentAttemptMade | 0                                                                                                                                                                                                               |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA-LTE10_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-20] Invio notifica con percorso analogico. Fallimento giacenza AR (FAIL-Giacenza_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                      |
      | pollingTime             | 30000                                                                                                                                                                                                     |
      | numCheck                | 30                                                                                                                                                                                                        |
      | details                 | NOT_NULL                                                                                                                                                                                                  |
      | details_recIndex        | 0                                                                                                                                                                                                         |
      | details_sentAttemptMade | 0                                                                                                                                                                                                         |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN004B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN004C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-21] Fallimento invio raccomandata semplice
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                            |
      | details                 | NOT_NULL                                                                                                                                                                                        |
      | details_recIndex        | 0                                                                                                                                                                                               |
      | details_sentAttemptMade | 0                                                                                                                                                                                               |
      | details_physicalAddress | {"address": "VIA@FAIL_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN002B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN002C |

  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-22] Invio notifica con percorso analogico. Successo giacenza gt 10 AR (OK-Giacenza-gt10_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                         |
      | pollingTime             | 30000                                                                                                                                                                                                        |
      | numCheck                | 30                                                                                                                                                                                                           |
      | details                 | NOT_NULL                                                                                                                                                                                                     |
      | details_recIndex        | 0                                                                                                                                                                                                            |
      | details_sentAttemptMade | 0                                                                                                                                                                                                            |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-GT10_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-23] Invio notifica con percorso analogico. Fallimento giacenza gt 10 AR (FAIL-Giacenza-gt10_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                           |
      | pollingTime             | 30000                                                                                                                                                                                                          |
      | numCheck                | 30                                                                                                                                                                                                             |
      | details                 | NOT_NULL                                                                                                                                                                                                       |
      | details_recIndex        | 0                                                                                                                                                                                                              |
      | details_sentAttemptMade | 0                                                                                                                                                                                                              |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA-GT10_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN004B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNRN012 |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-24] Invio notifica con percorso analogico. Fallimento compiuta giacenza AR (FAIL-CompiutaGiacenza_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-CompiutaGiacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                              |
      | pollingTime             | 30000                                                                                                                                                                                                             |
      | numCheck                | 30                                                                                                                                                                                                                |
      | details                 | NOT_NULL                                                                                                                                                                                                          |
      | details_recIndex        | 0                                                                                                                                                                                                                 |
      | details_sentAttemptMade | 0                                                                                                                                                                                                                 |
      | details_physicalAddress | {"address": "VIA@FAIL-COMPIUTAGIACENZA_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN005B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNRN012 |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-25] Invio notifica con percorso analogico. Successo seconda raccomandata AR (OK-Retry_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Retry_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 2
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 2
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON020 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN006 |
      | details_deliveryFailureCause | F01 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-26] Invio notifica con percorso analogico. Successo non rendicontabile AR (OK-NonRendicontabile_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-NonRendicontabile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                      |
      | pollingTime             | 40000                                                                                                                                                                                                     |
      | numCheck                | 30                                                                                                                                                                                                        |
      | details                 | NOT_NULL                                                                                                                                                                                                  |
      | details_recIndex        | 0                                                                                                                                                                                                         |
      | details_sentAttemptMade | 0                                                                                                                                                                                                         |
      | details_physicalAddress | {"address": "VIA@OK-NONRENDICONTABILE_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 2
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN013 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-27] Invio notifica con percorso analogico. Successo causa forza maggiore AR (OK-CausaForzaMaggiore_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-CausaForzaMaggiore_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                             |
      | pollingTime             | 40000                                                                                                                                                                                                            |
      | numCheck                | 30                                                                                                                                                                                                               |
      | details                 | NOT_NULL                                                                                                                                                                                                         |
      | details_recIndex        | 0                                                                                                                                                                                                                |
      | details_sentAttemptMade | 0                                                                                                                                                                                                                |
      | details_physicalAddress | {"address": "VIA@OK-CAUSAFORZAMAGGIORE_AR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN015 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-28] Invio notifica con percorso analogico. Fallimento 890 (FAIL_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                             |
      | pollingTime             | 30000                                                                                                                                                                                            |
      | numCheck                | 30                                                                                                                                                                                               |
      | details                 | NOT_NULL                                                                                                                                                                                         |
      | details_recIndex        | 0                                                                                                                                                                                                |
      | details_sentAttemptMade | 0                                                                                                                                                                                                |
      | details_physicalAddress | {"address": "VIA@FAIL_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003C |
      | details_deliveryFailureCause | M02 M05 M06 M07 M08 M09 |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e @ignore @con020 @con020success
  Scenario: [E2E-WF-ANALOG-29] Invio notifica con percorso analogico. Fallimento irreperibile 890 (FAIL-Irreperibile_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON020 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
      | details_physicalAddress | {"address": "VIA@FAIL-IRREPERIBILE_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | KO |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-30] Invio notifica con percorso analogico. Fallimento primo tentativo e successo secondo tentativo 890 (FAIL-Discovery_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                       |
      | pollingTime             | 30000                                                                                                                                                                      |
      | numCheck                | 30                                                                                                                                                                         |
      | details                 | NOT_NULL                                                                                                                                                                   |
      | details_recIndex        | 0                                                                                                                                                                          |
      | details_sentAttemptMade | 1                                                                                                                                                                          |
      | details_physicalAddress | {"address": "via@sequence.5s-CON080.5s-RECAG001A.5s-RECAG001B[DOC:23L].5s-RECAG001C", "municipality": "Milano", "province": "MI", "zip": "20121", "foreignState": "Italia"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Indagine Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG001C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-31] Invio notifica con percorso analogico. Fallimento primo tentativo e secondo tentativo 890 (FAIL-DiscoveryIrreperibile_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Indagine Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-32] Invio notifica con percorso analogico. Successo non rendicontabile 890 (OK-NonRendicontabile_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-NonRendicontabile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline            | true                                                                                                                                                                                                       |
      | pollingTime             | 30000                                                                                                                                                                                                      |
      | numCheck                | 30                                                                                                                                                                                                         |
      | details                 | NOT_NULL                                                                                                                                                                                                   |
      | details_recIndex        | 0                                                                                                                                                                                                          |
      | details_sentAttemptMade | 0                                                                                                                                                                                                          |
      | details_physicalAddress | {"address": "VIA@OK-NONRENDICONTABILE_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 2
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG013 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-33] Invio notifica con percorso analogico. Successo causa forza maggiore 890 (OK-CausaForzaMaggiore_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-CausaForzaMaggiore_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                                              |
      | pollingTime             | 30000                                                                                                                                                                                                             |
      | numCheck                | 30                                                                                                                                                                                                                |
      | details                 | NOT_NULL                                                                                                                                                                                                          |
      | details_recIndex        | 0                                                                                                                                                                                                                 |
      | details_sentAttemptMade | 0                                                                                                                                                                                                                 |
      | details_physicalAddress | {"address": "VIA@OK-CAUSAFORZAMAGGIORE_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG015 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-34] Invio notifica con percorso analogico. Successo RIS (OK_RIS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" esista

      | loadTimeline            | true                                                                                                                                                                                           |
      | details                 | NOT_NULL                                                                                                                                                                                       |
      | details_recIndex        | 0                                                                                                                                                                                              |
      | details_sentAttemptMade | 0                                                                                                                                                                                              |
      | details_physicalAddress | {"address": "VIA@OK_RIS", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost      | 133                                                                                                                                                                                            |

    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080  |

  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-35] Invio notifica con percorso digitale. Fallimento RIS (FAIL_RIS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | email@fail.it |
      | physicalAddress_address | Via@FAIL_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" esista

      | loadTimeline            | true                                                                                                                                                                                             |
      | details                 | NOT_NULL                                                                                                                                                                                         |
      | details_recIndex        | 0                                                                                                                                                                                                |
      | details_sentAttemptMade | 0                                                                                                                                                                                                |
      | details_physicalAddress | {"address": "VIA@FAIL_RIS", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost      | 133                                                                                                                                                                                              |

    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRSI004B |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRSI004C |

  @e2e
  Scenario: [E2E-WF-ANALOG-36] Invio notifica con percorso analogico. Successo RIR (OK_RIR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci|
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

      | loadTimeline            | true                                                                                                                                                                                            |
      | details                 | NOT_NULL                                                                                                                                                                                        |
      | details_recIndex        | 0                                                                                                                                                                                               |
      | details_sentAttemptMade | 0                                                                                                                                                                                               |
      | details_physicalAddress | {"address": "VIA@OK_RIR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |

    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI001 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI002 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI003C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-37] Invio notifica con percorso analogico. Fallimento RIR (FAIL_RIR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline            | true                                                                                                                                                                                       |
      | details                 | NOT_NULL                                                                                                                                                                                   |
      | details_recIndex        | 0                                                                                                                                                                                          |
      | details_sentAttemptMade | 0                                                                                                                                                                                          |
      | details_physicalAddress | {"address": "VIA@FAIL_RIR", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI001 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI002 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI004B |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI004C |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-38] Invio notifica con percorso analogico. Fallimento RS (FAIL_RS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRS002B |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080  |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRS002C |
      | details_deliveryFailureCause | M07 |

  @e2e @ignore
  Scenario: [E2E-WF-ANALOG-39] Invio notifica con percorso analogico. Fallimento consolidatore AR (FAIL_Consolidatore-AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_Consolidatore-AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "ANALOG_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
Feature: test per il recupero indirizzo al primo tentativo vas

  #VAS Introdotto con la Versione di NotificaV25 (FullSentNotificationV27, WebhookV28)
  #PA abilitata per il vas SI -> Comune_Multi AKA Comune di Palermo
  #PA abilitata per il vas NO -> Comune_1 AKA Comune di Milano
  #PG censita per il vas SI -> 01113570442
  #PG censita per il vas NO -> 15376371009
  #PF censita per il vas SI -> Mario Cucumber AKA Ettore Fieramosca
  #PF censita per il vas NO -> Leonardo da Vinci

# ************************************************ Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine - CONSEGNA andata a buon fine.

  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO (OK -> DWXA-QELZ-WLJK-202505-T-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 1-6-43-48
  Scenario: [RICERCA_INDIRIZZO_MONO_PF_OK_3-5-15-24-43-48] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimelime       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                               |
      | details_registry        | ANPR                                                                   |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "Via Umbria 5L", "municipality": "PADOVA", "zip": "35127"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline            | true                                                                   |
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus  | OK                                                                     |
    Then viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline                  | true                   |
      | details                       | NOT_NULL               |
      | details_numberOfRecipients    | 1                      |
      | parametriCalcoloCostoNotifica | recipients:1,ko:0,ok:1 |
          #| details_notificationCost   | 100      |
  #TODO PUBLIC_REGISTRY_VALIDATION_CALL: contenga lista utenze
  #todo elementi presenti una sola volta.


  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO (OK -> KLQJ-RHLT-UEGD-202505-Q-1)
  @ricercaIndirizzoVas
  Scenario: [4-5] Invio notifica 890 monodestinatario verso PG con campo address vuoto e recupero indirizzo da RI - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimelime       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                                      |
      | details_registry        | REGISTRO_IMPRESE                                                              |
      | details_recIndex        | 0                                                                             |
      | details_physicalAddress | {"address": "Roma Via del Campo 101", "municipality": "Roma", "zip": "00121"} |

  #PA ABILITATA, PF CENSITA + PG CENSITA, CLIENT ABILITATO (OK -> WDNQ-XAYK-HJLU-202505-U-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 2-7-53-57
  Scenario: [53-57] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto e recupero indirizzo dai registri nazionali - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimelime       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0, 1]   |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_registry        | ANPR                                                                   |
      | details_physicalAddress | {"address": "Via Umbria 5L", "municipality": "PADOVA", "zip": "35127"} |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                                      |
      | details_recIndex        | 1                                                                             |
      | details_registry        | REGISTRO_IMPRESE                                                              |
      | details_physicalAddress | {"address": "Roma Via del Campo 101", "municipality": "Roma", "zip": "00121"} |
      #TODO: i costi delle notifiche andate in ACCEPTED credo si verifichino nel REFINEMENT
    Then viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline                  | true                   |
      | details                       | NOT_NULL               |
      | details_recIndex              | 1                      |
      #| legalFactsIds    | [{"category": "RECIPIENT_ACCESS"}] |
      #| details_numberOfRecipients    | 2                      |
      #| details_notificationCost   | 200      |
      | parametriCalcoloCostoNotifica | recipients:2,ko:0,ok:2 |

# *************************************************** Indirizzi NON recuperati dai registri (recipient non abilitati) - CREAZIONE notifica NON andata a buon fine

  #PA ABILITATA, PF NON CENSITA, CLIENT ABILITATO (OK -> TGUX-QNJY-HJLP-202505-E-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 44
  Scenario: [6-44] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun indirizzo trovato da ANPR notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                       |
      | details                       | NOT_NULL                                                                                   |
      | details_refusalReasons        | [{"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"}] |
#     | details_notificationCost | 100                                                                                      |
      | parametriCalcoloCostoNotifica | recipients:1,ko:0,ok:1                                                                     |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #PA ABILITATA, PG NON CENSITA, CLIENT ABILITATO -> (OK HXGM-QLMY-EDNP-202505-H-1)
  @ricercaIndirizzoVas
  Scenario: [7] Invio notifica AR monodestinatario verso PG con campo address vuoto e nessun indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Non Censito |
      | recipientType   | PG             |
      | taxId           | 15376371009    |
      | digitalDomicile | NULL           |
      | physicalAddress | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                                                                       |
      | details                | NOT_NULL                                                                                   |
      | details_refusalReasons | [{"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"}] |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #PA ABILITATA, PF NON CENSITA + PG NON CENSITA, CLIENT ABILITATO (OK -> UENK-VKXT-ZUPN-202505-A-1)
  @ricercaIndirizzoVas
  Scenario: [8] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto entrambi NON censiti notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Non Censito |
      | recipientType   | PG             |
      | taxId           | 15376371009    |
      | digitalDomicile | NULL           |
      | physicalAddress | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                                                                                                                                                                 |
      | details                | NOT_NULL                                                                                                                                                                             |
      | details_refusalReasons | [{"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"}, {"detail": "Address not found for recipient index: 1", "errorCode": "ADDRESS_NOT_FOUND"}] |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0,1]    |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 1        |

  #PA ABILITATA, PF NON CENSITA + PG CENSITA, CLIENT ABILITATO (OK -> WZQT-JLQA-RPKQ-202505-L-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 3-54
  Scenario: [10M-54] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                       |
      | details                       | NOT_NULL                                                                                   |
      | details_refusalReasons        | [{"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_numberOfRecipients    | 2                                                                                          |
      #      | details_notificationCost   | xxx Uniform cost                     |
      | parametriCalcoloCostoNotifica | recipients:2,ko:0,ok:2                                                                     |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0,1]    |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

# ************************** Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine - CONSEGNA andata a buon fine (CONDIZIONI PARTICOLARI)

  #PA ABILITATA, PF COMPILATA A MANO + PG CENSITA, CLIENT ABILITATO (OK -> XGRJ-JKTE-EJKX-202505-Z-1)
  @ricercaIndirizzoVas
  Scenario: [9M] Invio notifica AR multidestinatario verso PF compilata e PG con campo address vuoto e indirizzo trovato da RI notifica accettata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [1]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                                      |
      | details_recIndex        | 1                                                                             |
      | details_registry        | REGISTRO_IMPRESE                                                              |
      | details_physicalAddress | {"address": "Roma Via del Campo 101", "municipality": "Roma", "zip": "00121"} |

  #TODO: serve sequence KO primo tentativo (ma nella request viene passata come physicalAddress, quindi non so se questo vanifica il test)
  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO, SEQUENCE KO AL PRIMO TENTATIVO
  #@ricercaIndirizzoVas
  Scenario: [12] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR -  Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details_registry        | ANPR                                                                   |
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "Via Umbria 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus  | OK                                                                     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                | NOT_NULL |
      | details_responseStatus | KO       |
      | details_recIndex       | 0        |
      #TODO      | details_physicalAddress | {"address": "Via Umbria 5L", "municipality": "PADOVA", "zip": "35127"} |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" non esista
      | details                 | NOT_NULL |
      | details_sentAttemptMade | 1        |

  #TODO NOTA by MATTEO: Questo scenario si può tranquillamente integrare con il primo scenario del file feature, la struttura è praticamente identica
  #TODO: serve sequence KO primo tentativo (ma nella request viene passata come physicalAddress, quindi non so se questo vanifica il test)
  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO, SEQUENCE OK AL PRIMO TENTATIVO
  #@ricercaIndirizzoVas #serve sequence ok primo tentativo
  Scenario: [11] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details_registry        | ANPR                                                                   |
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "Via Umbria 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus  | OK                                                                     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL  |
      | details_responseStatus     | OK        |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG001C |
      #TODO      | details_physicalAddress    | {"address": "Via Umbria 5L", "municipality": "PADOVA", "zip": "35127"} |

# ************************************************** Abilitazione PA / FeatureFlag / WI-VAS-1.3 + WI-VAS-1.4 + client WI-VAS-1.5 ********************

  #PA ABILITATA, PG CENSITA, CLIENT NON ABILITATO
  @ricercaIndirizzoVas
  Scenario: [16-17] Creazione notifica PA abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA NON ABILITATA, PG CENSITA, CLIENT NON ABILITATO
  @ricercaIndirizzoVas
  Scenario: [23_19] Creazione notifica PA NON abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA NON ABILITATA, PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas
  Scenario: [18-17] Creazione notifica PA NON abilitata - Feature flag Attivo - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [19] Creazione notifica PA abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  #TODO MATTEO (SECONDO ME E' UN PO INUTILE QUESTO SCENARIO, a meno che nel refusal reason non ci aspettiamo un messaggio diverso rispetto allo scenario 23_19)
  #PA ABILITATA, PG CENSITA, CLIENT NON ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [20] Creazione notifica PA abilitata - Feature flag Spento - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  #TODO MATTEO (SECONDO ME E' UN PO INUTILE QUESTO SCENARIO, a meno che nel refusal reason non ci aspettiamo un messaggio diverso rispetto allo scenario 23_19)
  #PA NON ABILITATA, PG CENSITA, CLIENT ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [21] Creazione notifica PA non abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  #TODO MATTEO (SECONDO ME E' UN PO INUTILE QUESTO SCENARIO, a meno che nel refusal reason non ci aspettiamo un messaggio diverso rispetto allo scenario 23_19)
  #PA NON ABILITATA, PG CENSITA, CLIENT NON ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [22] Creazione notifica PA non abilitata - Feature flag Spento - Client non aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO IN INVIO, CLIENT DISABILITATO IN LETTURA
  @ricercaIndirizzoVas
  Scenario: [23] Creazione notifica PA abilitata - Feature flag Attivo - Client Non aggiornato in lettura
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    Then recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL"
    Then recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE"

# **************************************************** Stream *************************************************************

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO, STREAM PIU' RECENTE
  @cleanWebhook @webhook1
  Scenario: [RICERCA_INDIRIZZI_VAS_STREAM_NEW] Invio notifica e controllo che stream con eventType vuoto e versione da V26 contenga elemento PUBLIC_REGISTRY_VALIDATION_CALL
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "più recente"
    And si crea il nuovo stream per il "Comune_Multi" con versione "più recente"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" con la versione "più recente"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" con la versione "più recente"

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO, STREAM PRECEDENTE ALLA 28 (CHE HA STATO INTRODOTTO IL VAS)
  @cleanWebhook @webhook1
  Scenario: [RICERCA_INDIRIZZI_VAS_STREAM_OLD] Invio notifica e controllo che stream con eventType vuoto e versione da V26 contenga elemento PUBLIC_REGISTRY_VALIDATION_CALL
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V27"
    Then si controlla che tra gli elementi dello stream con versione "V27" ritornati non ci sia l'elemento "PUBLIC_REGISTRY_VALIDATION_CALL"
    And si controlla che tra gli elementi dello stream con versione "V27" ritornati non ci sia l'elemento "PUBLIC_REGISTRY_VALIDATION_RESPONSE"

#********************************************** VERIFICA COSTI UNIFORM *****************************************************

#Flag : technicalRefusalCostMode
#Param: technicalRefusalCost

  #PA ABILITATA, PF NON CENSITO + PG CENSITO + PG CON ERRORE 429 (too many request), CLIENT ABILITATO (OK -> UQTQ-YJQD-DAQX-202505-Z-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 4-55
  Scenario: [6-55] Invio notifica AR multidestinatario verso PF/PG tutti con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                                                                                                                        |
      | details                       | NOT_NULL                                                                                                                                                                                                    |
      | details_numberOfRecipients    | 3                                                                                                                                                                                                           |
      | details_refusalReasons        | [{"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"}, {"detail": "Address search for recipient index: 2, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | parametriCalcoloCostoNotifica | recipients:3,ko:1,ok:2                                                                                                                                                                                      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline       | false    |
      | details            | NOT_NULL |
      | details_recIndexes | [0,1,2]  |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 1        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 2        |

  #PA ABILITATA, PG CON ERRORE 429 (too many request), CLIENT ABILITATO (OK -> XNWA-LJMY-PRAQ-202505-Z-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 46
  Scenario: [46] Invio notifica AR monodestinatario verso PG con campo address vuoto notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                              |
      | details                       | NOT_NULL                                                                                                          |
      | details_refusalReasons        | [{"detail": "Address search for recipient index: 0, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}] |
#TODO      | details_notificationCost | xxx Uniform cost |
      | parametriCalcoloCostoNotifica | recipients:1,ko:1,ok:0                                                                                            |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #PA ABILITATA, PG CON ERRORE 500 (server down), CLIENT ABILITATO (OK -> XRLA-KATH-TVAY-202505-Q-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 47
  Scenario: [47] Invio notifica AR monodestinatario verso PF con campo address vuoto notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF Errore 500    |
      | taxId           | SRFBRD80A01E256Z |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                              |
      | details                       | NOT_NULL                                                                                                          |
      | details_refusalReasons        | [{"detail": "Address search for recipient index: 0, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | parametriCalcoloCostoNotifica | recipients:1,ko:1,ok:0                                                                                            |
          #TODO    | details_notificationCost | xxx Uniform cost |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #PA ABILITATA, PF CON ERRORE 500 (server down) + PF CON ERRORE 429 (too many request), CLIENT ABILITATO (OK -> KPUD-HVLQ-KHJQ-202505-A-1)
  @ricercaIndirizzoVas @technicalRefusalCost #costi 5-56
  Scenario: [56] Invio notifica AR multidestinatario verso PF con campo address vuoto (error 429 e error 500) - notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF Errore 500    |
      | taxId           | SRFBRD80A01E256Z |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                                                                                                                                               |
      | details                       | NOT_NULL                                                                                                                                                                                                                           |
      | details_numberOfRecipients    | 2                                                                                                                                                                                                                                  |
      | details_refusalReasons        | [{"detail": "Address search for recipient index: 0, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}, {"detail": "Address search for recipient index: 1, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | parametriCalcoloCostoNotifica | recipients:2,ko:2,ok:0                                                                                                                                                                                                             |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0,1]    |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 1        |

#********************************************** VERIFICA COSTI RECIPIENT_BASED *****************************************************

  @ricercaIndirizzoVas @technicalRefusalCost #costi 8-58
  Scenario: [58] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                 |
      | details                       | NOT_NULL                             |
      | details_refusalReasons        | [{"errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_numberOfRecipients    | 2                                    |
      | parametriCalcoloCostoNotifica | recipients:2,ko:0,ok:2               |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0,1]    |

  #PA ABILITATA, PF NON CENSITO + PG CENSITO + PF CON ERRORE 429 (too many request), CLIENT ABILITATO (OK -> KTRU-GYXA-EAXZ-202505-L-1)
  #@ricercaIndirizzoVas @technicalRefusalCost #costi 9-59 coperto da [6-55]
  Scenario: [59] Invio notifica AR multidestinatario verso PF/PG tutti con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                       |
      | details                       | NOT_NULL                                                                                                   |
      | details_refusalReasons        | [{"recIndex": 0, "errorCode": "ADDRESS_NOT_FOUND"}, {"recIndex": 2, "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | details_numberOfRecipients    | 3                                                                                                          |
      | parametriCalcoloCostoNotifica | recipients:3,ko:1,ok:2                                                                                     |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0,1,2]  |

  #PA ABILITATA, PF CON ERRORE 429 (too many request) + PF CON ERRORE 500 (server down), CLIENT ABILITATO (OK -> VZLT-GPVD-LVXD-202505-N-1)
  #@ricercaIndirizzoVas @technicalRefusalCost #costi 10-60 #coperto dal 56
  Scenario: [60] Invio notifica AR multidestinatario verso PF tutti con campo address vuoto notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF ERRORE 500    |
      | taxId           | SRFBRD80A01E256Z |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                           |
      | details                       | NOT_NULL                                                                                                       |
      | details_refusalReasons        | [{"recIndex": 0, "errorCode": "ADDRESS_SEARCH_FAILED"}, {"recIndex": 1, "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | details_numberOfRecipients    | 2                                                                                                              |
      | parametriCalcoloCostoNotifica | recipients:2,ko:2,ok:0                                                                                         |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0,1]    |

  #PA ABILITATA, PF NON CENSITA, CLIENT ABILITATO (OK -> RGQK-WXHW-JALK-202505-N-1)
  #@ricercaIndirizzoVas @technicalRefusalCost #costi 49 #coperto da 6-44
  Scenario: [49] Invio notifica AR monodestinatario verso PF con campo address vuoto nessun indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                       |
      | details                       | NOT_NULL                                                                                   |
      | details_refusalReasons        | [{"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"}] |
      | parametriCalcoloCostoNotifica | recipients:1,ko:0,ok:1                                                                     |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |

  #PA ABILITATA, PF CON ERRORE 429 (too many request), CLIENT ABILITATO (OK -> ZMXP-WHKH-TUQL-202505-V-1)
  #@ricercaIndirizzoVas @technicalRefusalCost #costi 50 #coperto dal 46
  Scenario: [50] Invio notifica AR monodestinatario verso PF e nessun indirizzo trovato da RI - notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                              |
      | details                       | NOT_NULL                                                                                                          |
      | details_refusalReasons        | [{"detail": "Address search for recipient index: 0, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | parametriCalcoloCostoNotifica | recipients:1,ko:1,ok:0                                                                                            |
  #TODO    | details_notificationCost | xxx  cost |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |

  #PA ABILITATO, PF CON ERRORE 400 incorporato, CLIENT ABILITATO (OK -> )
  @ricercaIndirizzoVas @technicalRefusalCost #costi 51
  Scenario: [51] Invio notifica AR monodestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF Errore 400    |
      | taxId           | MTTBNN14A01A001N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                              |
      | details                       | NOT_NULL                                                                                                          |
      | details_refusalReasons        | [{"detail": "Address search for recipient index: 0, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | parametriCalcoloCostoNotifica | recipients:1,ko:1,ok:0                                                                                            |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |

  #PA ABILITATO, PF CON ERRORE 500 (server down), CLIENT ABILITATO (OK -> VQHK-NJRL-DLTX-202505-T-1)
  #@ricercaIndirizzoVas @technicalRefusalCost #costi 52 #coperto da 47
  Scenario: [52] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF Errore 500    |
      | taxId           | SRFBRD80A01E256Z |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                                                              |
      | details                       | NOT_NULL                                                                                                          |
      | details_numberOfRecipients    | 1                                                                                                                 |
      | details_refusalReasons        | [{"detail": "Address search for recipient index: 0, encountered an error", "errorCode": "ADDRESS_SEARCH_FAILED"}] |
      | parametriCalcoloCostoNotifica | recipients:1,ko:1,ok:0                                                                                            |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |

# ************************************************* VERIFICHE LATO DESTINATARIO ***************************************

  # ricezione notifiche (tullio -> VLJX-MLGN-LRGQ-202505-P-1)
  @useB2B @ricercaIndirizzoVas
  Scenario: [26]
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    #Given imposto lo iun di SharedSteps a "UWAE-ZLAG-PNDE-202505-N-1" e la pa a Comune_Multi
    And lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL"
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE"


  Scenario: [TEST_LAZY_COSTI] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun trovato da RI notifica rifiutata - Vas attivo
    Given imposto lo iun di SharedSteps a "UENK-VKXT-ZUPN-202505-A-1" e la pa a Comune_Multi
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline                  | true                                                                     |
      | details                       | NOT_NULL                                                                 |
      | details_numberOfRecipients    | 1                                                                        |
      #2 ADDRESS NOT FOUND, MODALITA' DEFAULT, 2 recipients, 2 ADDRESS_NOT_FOUND (contano come OK), mi aspetto 100 (costoBaseNotifica -> pn.technical_refusal_cost_mode.uniform)
      | details_refusalReasons        | [{"errorCode": "ADDRESS_NOT_FOUND"}, {"errorCode": "ADDRESS_NOT_FOUND"}] |
      | parametriCalcoloCostoNotifica | recipients:2,ko:0,ok:2                                                   |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline | false |

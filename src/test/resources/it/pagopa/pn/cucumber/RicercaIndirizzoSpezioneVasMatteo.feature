Feature: test per il recupero indirizzo al primo tentativo vas

  #VAS Introdotto con la Versione di NotificaV25 (FullSentNotificationV27, WebhookV28)
  #PA abilitata per il vas SI -> Comune_Multi AKA Comune di Palermo
  #PA abilitata per il vas NO -> Comune_1 AKA Comune di Milano
  #PG censita per il vas SI -> 01113570442
  #PG censita per il vas NO -> 15376371009
  #PF censita per il vas SI -> Mario Cucumber AKA Ettore Fieramosca
  #PF censita per il vas NO -> Leonardo da Vinci

# ************************************************ Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine - CONSEGNA andata a buon fine.

  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas @technicalRefusalCostUniform @technicalRefusalCostRecipient #costi 1-6-43-48
  Scenario: [3-15-24-43-48] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | registry                | ANPR                                                                   |
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus  | OK                                                                     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                               |
      | details_recIndex           | 0                                                                      |
      | details_deliveryDetailCode | RECAG001C                                                              |
      | details_physicalAddress    | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus     | OK                                                                     |
      #TODO: i costi delle notifiche andate in ACCEPTED credo si verifichino nel REFINEMENT
#    Then viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
#      | loadTimeline               | true     |
#      | details                    | NOT_NULL |
#      | details_numberOfRecipients | 1        |
#      | details_notificationCost   | 100      |

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas
  Scenario: [4] Invio notifica 890 monodestinatario verso PG con campo address vuoto e recupero indirizzo da RI Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | registry         | REGISTRO_IMPRESE |
      | details          | NOT_NULL         |
      | details_recIndex | 0                |

  #PA ABILITATA, PF CENSITA + PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 2-7-53-57
  Scenario: [53-57] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto e recupero indirizzo dai registri nazionali Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | registry                | ANPR                                                                   |
      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details          | NOT_NULL         |
      | details_recIndex | 1                |
      | registry         | REGISTRO_IMPRESE |
      #TODO???| details_physicalAddress | {"address": "xxx", "municipality": "ROMA", "municipalityDetails": "ROMA", "at": "Presso", "addressDetails": "", "province": "RM", "zip": "00121", "foreignState": "ITALIA"} |
      #TODO: i costi delle notifiche andate in ACCEPTED credo si verifichino nel REFINEMENT
#    Then viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
#      | loadTimeline               | true     |
#      | details                    | NOT_NULL |
#      | details_numberOfRecipients | 2        |
#      | details_notificationCost   | 200      |

# *************************************************** Indirizzi NON recuperati dai registri (recipient non abilitati) - CREAZIONE notifica NON andata a buon fine

  #PA ABILITATA, PF NON CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 44
  Scenario: [6-44] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun indirizzo trovato da ANPR notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true                                                                                     |
      | details                  | NOT_NULL                                                                                 |
      | details_refusalReasons   | {"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"} |
      | details_notificationCost | 100                                                                                      |

  #PA ABILITATA, PG NON CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas
  Scenario: [7] Invio notifica AR monodestinatario verso PG con campo address vuoto e nessun indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Non Censito |
      | taxId           | 15376371009    |
      | digitalDomicile | NULL           |
      | physicalAddress | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                                                                     |
      | details                | NOT_NULL                                                                                 |
      | details_refusalReasons | {"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"} |

  #PA ABILITATA, PF NON CENSITA + PG NON CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas
  Scenario: [8] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto entrambi NON censiti Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Non Censito |
      | taxId           | 15376371009    |
      | digitalDomicile | NULL           |
      | physicalAddress | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline           | true                                                                                     |
      | details                | NOT_NULL                                                                                 |
      | details_refusalReasons | {"detail": "Address not found for recipient index: 1", "errorCode": "ADDRESS_NOT_FOUND"} |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                                                                     |
      | details                | NOT_NULL                                                                                 |
      | details_refusalReasons | {"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"} |

  #PA ABILITATA, PF NON CENSITA + PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 3-54
  Scenario: [10M-54] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline               | true                                 |
      | details                    | NOT_NULL                             |
      | details_refusalReasons     | [{"errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_numberOfRecipients | 2                                    |
      #TODO      | details_notificationCost   | xxx Uniform cost                     |

# ************************** Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine - CONSEGNA andata a buon fine (CONDIZIONI PARTICOLARI)

  #PA ABILITATA, PF COMPILATA A MANO + PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas
  Scenario: [9M] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e uno compilato e indirizzo trovato da RI notifica accettata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details          | NOT_NULL |
      | details_recIndex | 1        |
      #TODO: verificare corretti elementi per PF 0 PS ATTUALMENTE SONO 2 PG

  #TODO: serve sequence KO primo tentativo (ma nella request viene passata come physicalAddress, quindi non so se questo vanifica il test)
  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO, SEQUENCE KO AL PRIMO TENTATIVO
  @ricercaIndirizzoVas
  Scenario: [12] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details_registry        | ANPR                                                                   |
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus  | OK                                                                     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                | NOT_NULL |
      | details_responseStatus | KO       |
      | details_recIndex       | 0        |
      #TODO      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
      #TODO: aggiungi step che verifichi nessun attempt 2 dell' elemento?

  #TODO NOTA by MATTEO: Questo scenario si può tranquillamente integrare con il primo scenario del file feature, la struttura è praticamente identica
  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO, SEQUENCE OK AL PRIMO TENTATIVO
  @ricercaIndirizzoVas #serve sequence ok primo tentativo
  Scenario: [11] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | registry                | ANPR                                                                   |
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus  | OK                                                                     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL  |
      | details_responseStatus     | OK        |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG001C |
      #TODO      | details_physicalAddress    | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |

# ************************************************** Abilitazione PA / FeatureFlag / WI-VAS-1.3 + WI-VAS-1.4 + client WI-VAS-1.5 ********************

  #PA ABILITATA, PG CENSITA, CLIENT NON ABILITATO
  @ricercaIndirizzoVas
  Scenario: [16-17] Creazione notifica PA abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    #TODO non bisogno fare nessun controllo? Solo inviarla e aspettare che vada in REFUSED ?

  #PA NON ABILITATA, PG CENSITA, CLIENT NON ABILITATO
  @ricercaIndirizzoVas
  Scenario: [23_19] Creazione notifica PA NON abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                   |
      | details                | NOT_NULL               |
      | details_refusalReasons | [{"errorCode": "xxx"}] |

  #PA NON ABILITATA, PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas
  Scenario: [18-17] Creazione notifica PA NON abilitata - Feature flag Attivo - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "REFUSED"

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [23_19] Creazione notifica PA abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"

  #TODO MATTEO (SECONDO ME E' UN PO INUTILE QUESTO SCENARIO, a meno che nel refusal reason non ci aspettiamo un messaggio diverso rispetto allo scenario 23_19)
  #PA ABILITATA, PG CENSITA, CLIENT NON ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [20] Creazione notifica PA abilitata - Feature flag Spento - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"

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
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "REFUSED"

  #TODO MATTEO (SECONDO ME E' UN PO INUTILE QUESTO SCENARIO, a meno che nel refusal reason non ci aspettiamo un messaggio diverso rispetto allo scenario 23_19)
  #PA NON ABILITATA, PG CENSITA, CLIENT NON ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [22] Creazione notifica PA non abilitata - Feature flag Spento - Client non aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "REFUSED"

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO IN INVIO, CLIENT DISABILITATO IN LETTURA
  @ricercaIndirizzoVas
  Scenario: [23] Creazione notifica PA abilitata - Feature flag Attivo - Client Non aggiornato in lettura
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    Then recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL"
    Then recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE"

# ****************************************************   Stream *************************************************************

  #TODO ??? DECEDUTO ??? PERCHE' ???
  @cleanWebhook @webhook1
  Scenario: [] Invio notifica e controllo che stream con eventType vuoto e versione da V26 contenga elemento ANALOG_WORKFLOW_RECIPIENT_DECEASED
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione ""
    And si crea il nuovo stream per il "Comune_Multi" con versione ""
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "" con la versione ""

  #TODO ??? CHE SI VUOLE FARE CON QUESTO TEST ???
  @cleanWebhook @webhook1
  Scenario: []
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V28"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V28"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "" con la versione "V28"

# *************************************************************** flag costi ***********************************************************************

#Flag : technicalRefusalCostMode
#Param: technicalRefusalCost

  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 4-55
  Scenario: [55] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
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
      | loadTimeline               | true             |
      | details                    | NOT_NULL         |
      | details_numberOfRecipients | 3                |
      | details_notificationCost   | xxx Uniform cost |

  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 46
  Scenario: [46] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
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
      | loadTimeline             | true             |
      | details                  | NOT_NULL         |
      | details_notificationCost | xxx Uniform cost |

  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 47
  Scenario: [47] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
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
      | loadTimeline             | true             |
      | details                  | NOT_NULL         |
      | details_notificationCost | xxx Uniform cost |


  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 5-56
  Scenario: [56] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG / PF ERRORE 500 |
      | taxId           | SRFBRD80A01E256Z   |
      | digitalDomicile | NULL               |
      | physicalAddress | NULL               |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline               | true             |
      | details                    | NOT_NULL         |
      | details_numberOfRecipients | 2                |
      | details_notificationCost   | xxx Uniform cost |

#********************************************** VERIFICA COSTI RECIPIENT **********************************

  @ricercaIndirizzoVas @technicalRefusalCostRecipient #costi 8-58
  Scenario: [58] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline               | true                                 |
      | details                    | NOT_NULL                             |
      | details_refusalReasons     | [{"errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_numberOfRecipients | 2                                    |
      | details_notificationCost   | xxx Tot                              |

  @ricercaIndirizzoVas @technicalRefusalCostRecipient #costi 9-59
  Scenario: [59] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario
      | denomination    | PG Censito  |
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
      | loadTimeline               | true     |
      | details                    | NOT_NULL |
      | details_numberOfRecipients | 3        |
      | details_notificationCost   | xxx Tot  |

  @ricercaIndirizzoVas @technicalRefusalCostRecipient #costi 10-60
  Scenario: [60] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG / PF ERRORE 500 |
      | taxId           | SRFBRD80A01E256Z   |
      | digitalDomicile | NULL               |
      | physicalAddress | NULL               |
    And destinatario
      | denomination    | PF Errore 429    |
      | taxId           | GKRLGS31H68E907N |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_numberOfRecipients | 2         |
      | details_notificationCost   | xxx  cost |

  @ricercaIndirizzoVas @technicalRefusalCostRecipient #costi 49
  Scenario: [49] Invio notifica AR monodestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true              |
      | details                  | NOT_NULL          |
      | details_notificationCost | CALCOLATO:UNIFORM |

  @ricercaIndirizzoVas @technicalRefusalCostRecipient #costi 50
  Scenario: [50] Invio notifica AR monodestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
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
      | loadTimeline             | true      |
      | details                  | NOT_NULL  |
      | details_notificationCost | xxx  cost |

  @ricercaIndirizzoVas @technicalRefusalCostRecipient #costi 51
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
      | loadTimeline             | true      |
      | details                  | NOT_NULL  |
      | details_notificationCost | xxx  cost |

  @ricercaIndirizzoVas @technicalRefusalCostRecipient #costi 52
  Scenario: [52] Invio notifica AR monodestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
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
      | loadTimeline             | true      |
      | details                  | NOT_NULL  |
      | details_notificationCost | xxx  cost |

# ************************************************* VERIFICHE LATO DESTINATARIO ***************************************

  # ricezione notifiche
  @useB2B
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
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    And lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL"


# **************************************** ricezione notifiche ******************************************
  @useB2B
  Scenario: [26]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
  #TODO che si vuole fare qua? come fa a essere visibile e non visibile insieme ?
    And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE" è visibile
    And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL" è visibile
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL" non è visibile
    And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non è visibile



















  # Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine -CONSEGNA andata a buon fine.
  @ricercaIndirizzoVas @technicalRefusalCostUniform @technicalRefusalCostRecipient #costi 1-6-43-48
  Scenario: [LAZY_ACCEPTED] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR Vas attivo
    Given imposto lo iun di SharedSteps a "UWAE-ZLAG-PNDE-202505-N-1" e la pa a Comune_Multi
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | registry                | ANPR                                                                   |
      | details                 | NOT_NULL                                                               |
      | details_recIndex        | 0                                                                      |
      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
      | details_responseStatus  | OK                                                                     |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
#    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
#      | registry                | ANPR                                                                   |
#      | details                 | NOT_NULL                                                               |
#      | details_recIndex        | 0                                                                      |
#      | details_physicalAddress | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |
#      | details_responseStatus  | OK                                                                     |
#    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
#      | loadTimeline               | true      |
#      | details                    | NOT_NULL  |
#      | details_responseStatus     | OK        |
#      | details_recIndex           | 0         |
#      | details_deliveryDetailCode | RECAG001C |
      #TODO: nota by Matteo. Ci possiamo risparmiare questo controllo (i fail sull'indirizzo sono facilissimi, basta un campo diverso, maiuscolo anziché minuscolo e addio)
#      | details_physicalAddress    | {"address": "VIA UMBRIA 5L", "municipality": "PADOVA", "zip": "35127"} |


  #PA ABILITATA, PF NON CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas @technicalRefusalCostUniform #costi 44
  Scenario: [LAZY_REFUSED] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun indirizzo trovato da ANPR notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And imposto lo iun di SharedSteps a "TGUX-QNJY-HJLP-202505-E-1" e la pa a Comune_Multi
#    And viene generata una nuova notifica
#      | subject            | invio notifica con cucumber |
#      | senderDenomination | Comune di Palermo           |
#    And destinatario Leonardo da Vinci e:
#      | digitalDomicile | NULL |
#      | physicalAddress | NULL |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL_REFUSED"
#    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE_REFUSED" non esista
#      | details          | NOT_NULL |
#      | details_recIndex | 0        |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true                                                                                       |
      | details                  | NOT_NULL                                                                                   |
      | details_refusalReasons   | [{"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_notificationCost | 100                                                                                        |
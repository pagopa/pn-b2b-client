Feature: test per il recupero indirizzo al primo tentativo vas

  #VAS Introdotto con la Versione di NotificaV25 (FullSentNotificationV27, WebhookV28)
  #PA abilitata per il vas SI -> Comune_Multi AKA Comune di Palermo
  #PA abilitata per il vas NO -> Comune_1 AKA Comune di Milano
  #PG censita per il vas SI -> 01113570442
  #PG censita per il vas NO -> 15376371009
  #PF censita per il vas SI -> Mario Cucumber AKA Ettore Fieramosca
  #PF censita per il vas NO -> Leonardo da Vinci

# *Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine - CONSEGNA andata a buon fine.

  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas  #rif srs 3-5-15-24-43-48-11
  Scenario: [RICERCA_INDIRIZZO_MONO_PF_OK] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR - Vas attivo
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
      | details_recIndex              | 0                      |


  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas #rif srs 4-5
  Scenario: [RICERCA_INDIRIZZO_MONO_PG_OK] Invio notifica 890 monodestinatario verso PG con campo address vuoto e recupero indirizzo da RI - Vas attivo
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

  #PA ABILITATA, PF CENSITA + PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas  #rif srs 53-57
  Scenario: [RICERCA_INDIRIZZO_MULTI_PF_PG_OK] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto e recupero indirizzo dai registri nazionali - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
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
    Then viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline                  | true                   |
      | details                       | NOT_NULL               |
      | details_recIndex              | 1                      |
      | parametriCalcoloCostoNotifica | recipients:2,ko:0,ok:2 |

# Indirizzi NON recuperati dai registri (recipient non abilitati) - CREAZIONE notifica NON andata a buon fine

  #PA ABILITATA, PF NON CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas  #rif srs 6-44
  Scenario: [RICERCA_INDIRIZZO_MONO_PF_KO] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun indirizzo trovato da ANPR notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
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
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #PA ABILITATA, PG NON CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas #rif srs 7
  Scenario: [RICERCA_INDIRIZZO_MONO_PG_KO] Invio notifica AR monodestinatario verso PG con campo address vuoto e nessun indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
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

  #PA ABILITATA, PF NON CENSITA + PG NON CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas #rif srs 8
  Scenario: [RICERCA_INDIRIZZO_MULTI_PF_PG_KO] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto entrambi NON censiti notifica rifiutata - Vas attivo
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

  #PA ABILITATA, PF NON CENSITA + PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas  #rif srs 10-54
  Scenario: [RICERCA_INDIRIZZO_MULTI_PF_PG_KO_2] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
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
      | parametriCalcoloCostoNotifica | recipients:2,ko:0,ok:2                                                                     |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0,1]    |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

# Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine - CONSEGNA andata a buon fine (CONDIZIONI PARTICOLARI)

  #PA ABILITATA, PF COMPILATA A MANO + PG CENSITA, CLIENT ABILITATO
  @ricercaIndirizzoVas #rif srs 9
  Scenario: [RICERCA_INDIRIZZO_MULTI_PF_COMP_PG_OK] Invio notifica AR multidestinatario verso PF compilata e PG con campo address vuoto e indirizzo trovato da RI notifica accettata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
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


  #PA ABILITATA, PF CENSITA, CLIENT ABILITATO, SEQUENCE KO AL PRIMO TENTATIVO
  @ricercaIndirizzoVas #rif srs 12
  Scenario: [RICERCA_INDIRIZZO_SECONDO_TENTATIVO] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR -  Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF 2 tentativi   |
      | recipientType   | PF               |
      | taxId           | CNCGPP80A01H501J |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimelime       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And viene verificato che l'elemento di timeline "PREPARE_ANALOG_DOMICILE_FAILURE" esista
      | loadTimelime         | true     |
      | details              | NOT_NULL |
      | details_failureCause | D02      |
      | details_recIndex     | 0        |



#  Abilitazione PA / FeatureFlag / WI-VAS-1.3 + WI-VAS-1.4 + client WI-VAS-1.5

  #PA ABILITATA, PG CENSITA, CLIENT NON ABILITATO
  @ricercaIndirizzoVas #rif srs 16-17
  Scenario: [RICERCA_INDIRIZZO_MONO_API_NON_AGGIORN_KO] Creazione notifica PA abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA NON ABILITATA, PG CENSITA, CLIENT NON ABILITATO
  @ricercaIndirizzoVas #rif srs 23-19
  Scenario: [RICERCA_INDIRIZZO_MONO_API_NON_AGGIORN_KO_2] Creazione notifica PA NON abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
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
  @ricercaIndirizzoVas #rif srs 18-17
  Scenario: [RICERCA_INDIRIZZO_MONO_PA_NON_ABILIT_KO] Creazione notifica PA NON abilitata - Feature flag Attivo - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Milano            |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled #rif srs 19
  Scenario: [RICERCA_INDIRIZZO_MONO_FLAG_OFF] Creazione notifica PA abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
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
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA ABILITATA, PG CENSITA, CLIENT NON ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled #rif srs 20
  Scenario: [RICERCA_INDIRIZZO_MONO_FLAG_OFF_2] Creazione notifica PA abilitata - Feature flag Spento - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA NON ABILITATA, PG CENSITA, CLIENT ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled #rif srs 21
  Scenario: [RICERCA_INDIRIZZO_MONO_FLAG_OFF_3] Creazione notifica PA non abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Milano            |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  #PA NON ABILITATA, PG CENSITA, CLIENT NON ABILITATO, FEATURE FLAG DISATTIVATO
  @ricercaIndirizzoVas @physicalAddressLookupDisabled #rif srs 22
  Scenario: [RICERCA_INDIRIZZO_MONO_FLAG_OFF_4] Creazione notifica PA non abilitata - Feature flag Spento - Client non aggiornato e notifica rifiutata
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
  @ricercaIndirizzoVas #rif srs 23
  Scenario: [RICERCA_INDIRIZZO_MONO_API_NON_AGGIORN_DESTINAT] Creazione notifica PA abilitata - Feature flag Attivo - Client Non aggiornato in lettura
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

# STREAM

  #PA ABILITATA, PG CENSITA, CLIENT ABILITATO, STREAM PIU' RECENTE
  @ricercaIndirizzoVas@cleanWebhook @webhook1
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
  @ricercaIndirizzoVas@cleanWebhook @webhook1
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

# VERIFICA COSTI

#Flag : technicalRefusalCostMode
#Param: technicalRefusalCost

  #PA ABILITATA, PF NON CENSITO + PG CENSITO + PG CON ERRORE 429 (too many request), CLIENT ABILITATO
  @ricercaIndirizzoVas  #rif srs 6-55
  Scenario: [RICERCA_INDIRIZZO_MULTI_COSTI] Invio notifica AR multidestinatario verso PF/PG tutti con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
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

  #PA ABILITATA, PG CON ERRORE 429 (too many request), CLIENT ABILITATO
  @ricercaIndirizzoVas  #rif srs 46
  Scenario: [RICERCA_INDIRIZZO_MULTI_COSTI_2] Invio notifica AR monodestinatario verso PG con campo address vuoto notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
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
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #PA ABILITATA, PG CON ERRORE 500 (server down), CLIENT ABILITATO
  @ricercaIndirizzoVas #rif srs 47
  Scenario: [RICERCA_INDIRIZZO_MULTI_COSTI_3] Invio notifica AR monodestinatario verso PF con campo address vuoto notifica rifiutata - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
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
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #PA ABILITATA, PF CON ERRORE 500 (server down) + PF CON ERRORE 429 (too many request), CLIENT ABILITATO
  @ricercaIndirizzoVas #rif srs 56
  Scenario: [RICERCA_INDIRIZZO_MULTI_COSTI_4] Invio notifica AR multidestinatario verso PF con campo address vuoto (error 429 e error 500) - notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
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

#VERIFICA COSTI RECIPIENT_BASED

  @ricercaIndirizzoVas #rif srs 58
  Scenario: [RICERCA_INDIRIZZO_MULTI_COSTI_5] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata - Vas attivo
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

  #PA ABILITATA, PF NON CENSITO + PG CENSITO + PF CON ERRORE 429 (too many request), CLIENT ABILITATO
  #@ricercaIndirizzoVas  #coperto da [6-55]
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

  #PA ABILITATA, PF CON ERRORE 429 (too many request) + PF CON ERRORE 500 (server down), CLIENT ABILITATO
  #@ricercaIndirizzoVas  #coperto dal 56
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

  #PA ABILITATA, PF NON CENSITA, CLIENT ABILITATO
  #@ricercaIndirizzoVas  #costi 49 #coperto da 6-44
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
  #@ricercaIndirizzoVas  #costi 50 #coperto dal 46
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
      And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |

  #PA ABILITATO, PF CON ERRORE 400 incorporato, CLIENT ABILITATO (OK -> )
  @ricercaIndirizzoVas  #rif srs 51
  Scenario: [RICERCA_INDIRIZZO_MULTI_COSTI_6] Invio notifica AR monodestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
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
  #@ricercaIndirizzoVas  #costi 52 #coperto da 47
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

#VERIFICHE LATO DESTINATARIO

  # ricezione notifiche
  @ricercaIndirizzoVas
  Scenario: [RICERCA_INDIRIZZO_MONO_LATO_DESTINATARIO]
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
    And lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL"
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE"


# TEST POST-SPERIMENTAZIONE

  #@ricercaIndirizzoVas  #rif srs 28 # test post-sperimentazione
  Scenario: [RICERCA_INDIRIZZO_MONO_PF_FINE_SPERIMENT_OK] Invio notifica con vas post-sperimentazione anche per PA non abilitate - Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
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
      | details_recIndex              | 0                      |



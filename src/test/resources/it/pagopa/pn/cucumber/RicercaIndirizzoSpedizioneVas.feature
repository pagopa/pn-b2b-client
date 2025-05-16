Feature: test per il recupero indirizzo al primo tentativo vas

  # Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine -CONSEGNA andata a buon fine.

  @ricercaIndirizzoVas @technicalRefusualCostUniform @technicalRefusalCostRecipient #costi 1-6-43-48
  Scenario: [3-15-24-43-48] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR Vas attivo
    #Given il test è effettuabile con API versione "V25" o superiore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    #And esiste l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE" abbia notificationCost uguale a "null" per l'utente 0
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      #| xxx lista utenze | xxx |
      | loadTimeline | true |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | loadTimeline            | true                                                                                                                                                                      |
      | registry                | ANPR                                                                                                                                                                      |
      | details                 | NOT_NULL                                                                                                                                                                  |
      | details_recIndex        | 0                                                                                                                                                                         |
      | details_physicalAddress | {"address": "xxx", "municipality": "PADOVA", "municipalityDetails": "", "at": "Presso", "addressDetails": "", "province": "PD", "zip": "35127", "foreignState": "ITALIA"} |
      | details_responseStatus  | OK                                                                                                                                                                        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                  |
      | details_recIndex           | 0                                                                                                                                                                         |
      | details_deliveryDetailCode | XXX                                                                                                                                                                       |
      | details_physicalAddress    | {"address": "xxx", "municipality": "PADOVA", "municipalityDetails": "", "at": "Presso", "addressDetails": "", "province": "PD", "zip": "35127", "foreignState": "ITALIA"} |
      | details_responseStatus     | ok                                                                                                                                                                        |
    #costi 1-6
    Then viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
      | loadTimeline               | true     |
      | details                    | NOT_NULL |
      | details_numberOfRecipients | 1        |
      | details_notificationCost   | 100      |


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
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | xxx lista utenze | xxx |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | registry         | RI       |
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  @ricercaIndirizzoVas @technicalRefusualCostUniform #costi 2-7-53-57
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
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | xxx lista utenze | xxx |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                                                                                                                                  |
      | details_recIndex        | 0                                                                                                                                                                         |
      | registry                | ANPR                                                                                                                                                                      |
      | details_physicalAddress | {"address": "xxx", "municipality": "PADOVA", "municipalityDetails": "", "at": "Presso", "addressDetails": "", "province": "PD", "zip": "35127", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL                                                                                                                                                                    |
      | details_recIndex        | 1                                                                                                                                                                           |
      | registry                | REGISTRO_IMPRESE                                                                                                                                                            |
      | details_physicalAddress | {"address": "xxx", "municipality": "ROMA", "municipalityDetails": "ROMA", "at": "Presso", "addressDetails": "", "province": "RM", "zip": "00121", "foreignState": "ITALIA"} |
    #costi 2-7
    Then viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
      | loadTimeline               | true     |
      | details                    | NOT_NULL |
      | details_numberOfRecipients | 2        |
      | details_notificationCost   | 200      |

  @ricercaIndirizzoVas @technicalRefusualCostUniform #costi 44
  Scenario: [6-44] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun indirizzo trovato da ANPR notifica rifiutata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PF Non Censito   |
      | taxId           | DVNLRD52D15M059P |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | xxx lista utenze | xxx |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true                                                                                     |
      | details                  | NOT_NULL                                                                                 |
      #| details_refusalReasons | [{"errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_refusalReasons   | {"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"} |
      | details_notificationCost | 100                                                                                      |


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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                                                                     |
      | details                | NOT_NULL                                                                                 |
      #| details_refusalReasons | [{"errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_refusalReasons | {"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"} |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | xxx lista utenze | xxx |

 #*** ADD _8. entrambi non censiti
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    Then viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline           | true                                                                                     |
      | details                | NOT_NULL                                                                                 |
      | details_refusalReasons | {"detail": "Address not found for recipient index: 1", "errorCode": "ADDRESS_NOT_FOUND"} |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                                                                     |
      | details                | NOT_NULL                                                                                 |
      | details_refusalReasons | {"detail": "Address not found for recipient index: 0", "errorCode": "ADDRESS_NOT_FOUND"} |



  # Call una volta/lista utenze
  #Response non esista



  @ricercaIndirizzoVas @technicalRefusualCostUniform #costi 3-54
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
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline | true     |
      | details      | NOT_NULL |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | details | NOT_NULL |
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline               | true                                 |
      | details                    | NOT_NULL                             |
      | details_refusalReasons     | [{"errorCode": "ADDRESS_NOT_FOUND"}] |
      | details_numberOfRecipients | 2                                    |
      | details_notificationCost   | xxx Uniform cost                     |


# Inserimento MANUALE + VAS.

  @ricercaIndirizzoVas
  Scenario: [9M] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e uno compilato e indirizzo trovato da RI notifica accettata Vas attivo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PagoPA S.p.A       |
      | taxId                               | 15376371009        |
      | digitalDomicile                     | NULL               |
      | recipientType                       | PG                 |
      | physicalAddress_address             | Piazza Colonna 370 |
      | physicalAddress_municipality        | Roma               |
      | physicalAddress_municipalityDetails | NULL               |
      | at                                  | NULL               |
      | physicalAddress_addressDetails      | NULL               |
      | physicalAddress_province            | RM                 |
      | physicalAddress_State               | ITALIA             |
      | physicalAddress_zip                 | 00187              |
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
      #   Verificare corretti elementi per PF 0 PS ATTUALMENTE SONO 2 PG






# **** tentativi

  @ricercaIndirizzoVas #serve sequence KO primo tentativo
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
    #TODO VAS: una volta che capiamo cosa cercare rimuovere lo step di sopra (per ora ci limitiamo a cercarlo, senza dataTest)
#    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
#      | xxx lista utenze | xxx |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | loadTimeline            | true                                                                                                                                                                      |
      | details_registry        | ANPR                                                                                                                                                                      |
      | details                 | NOT_NULL                                                                                                                                                                  |
      | details_recIndex        | 0                                                                                                                                                                         |
      | details_physicalAddress | {"address": "xxx", "municipality": "PADOVA", "municipalityDetails": "", "at": "Presso", "addressDetails": "", "province": "PD", "zip": "35127", "foreignState": "ITALIA"} |
      | details_responseStatus  | OK                                                                                                                                                                        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                 | NOT_NULL                                                                                                                                                                  |
      | details_recIndex        | 0                                                                                                                                                                         |
      | details_physicalAddress | {"address": "xxx", "municipality": "PADOVA", "municipalityDetails": "", "at": "Presso", "addressDetails": "", "province": "PD", "zip": "35127", "foreignState": "ITALIA"} |
      | details_responseStatus  | ko                                                                                                                                                                        |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
      #aggiungi step che verifichi nessu attempt 2 dell' elemento?





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
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | xxx lista utenze | xxx |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | loadTimeline            | true                                                                                                                                                                      |
      | registry                | ANPR                                                                                                                                                                      |
      | details                 | NOT_NULL                                                                                                                                                                  |
      | details_recIndex        | 0                                                                                                                                                                         |
      | details_physicalAddress | {"address": "xxx", "municipality": "PADOVA", "municipalityDetails": "", "at": "Presso", "addressDetails": "", "province": "PD", "zip": "35127", "foreignState": "ITALIA"} |
      | details_responseStatus  | OK                                                                                                                                                                        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                  |
      | details_recIndex           | 0                                                                                                                                                                         |
      | details_deliveryDetailCode | XXX                                                                                                                                                                       |
      | details_physicalAddress    | {"address": "xxx", "municipality": "PADOVA", "municipalityDetails": "", "at": "Presso", "addressDetails": "", "province": "PD", "zip": "35127", "foreignState": "ITALIA"} |
      | details_responseStatus     | ok                                                                                                                                                                        |


  #Abilitazione PA / FeatureFlag / WI-VAS-1.3 + WI-VAS-1.4 + client WI-VAS-1.5 ********************


  @ricercaIndirizzoVas
  Scenario: [16-17] Crezione notifica PA abilitata - Feature flag Attivo - Client versione precedente e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    #When la notifica viene inviata tramite api b2b con la versione "V24" dal "AB" e si attende che lo stato diventi "REFUSED"
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"

# client non aggiornato



  @ricercaIndirizzoVas
  Scenario: [23_19] Crezione notifica PA NON abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
   # When la notifica viene inviata tramite api b2b con la versione "V24" dal "NON AB" e si attende che lo stato diventi "REFUSED"
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                   |
      | details                | NOT_NULL               |
      | details_refusalReasons | [{"errorCode": "xxx"}] |
#Client non aggiornato



  @ricercaIndirizzoVas
  Scenario: [18-17] Crezione notifica PA NON abilitata - Feature flag Attivo - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "NON AB" e si attende che lo stato diventi "REFUSED"




  # *************** Feature flag OFF


  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [23_19] Crezione notifica PA abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"


  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [20] Crezione notifica PA abilitata - Feature flag Spento - Client versione precedente e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"


  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [21] Crezione notifica PA non abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "NON AB" e si attende che lo stato diventi "REFUSED"


  @ricercaIndirizzoVas @physicalAddressLookupDisabled
  Scenario: [22] Crezione notifica PA non abilitata - Feature flag Spento - Client non aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito  |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    #When la notifica viene inviata tramite api b2b con la versione "V24" dal "NON AB" e si attende che lo stato diventi "REFUSED"
    When la notifica viene inviata tramite api b2b dal "NO AB" e si attende che lo stato diventi "REFUSED"




  # ***** Client non aggiornato in lettura

  @ricercaIndirizzoVas
  Scenario: [23] Crezione notifica PA abilitata - Feature flag Attivo - Client Non aggiornato in lettura
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
    Then recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    Then recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL"





#******************************************   Stream



  @cleanWebhook @webhook1
  Scenario: [] Invio notifica e controllo che stream con eventType vuoto e versione da V26 contenga elemento ANALOG_WORKFLOW_RECIPIENT_DECEASED
#    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V26"
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
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "" con la versione "v28"



  # ******************** flag costi

#Flag : technicalRefusalCostMode
#Param: technicalRefusalCost


  @ricercaIndirizzoVas @technicalRefusualCostUniform #costi 4-55
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline               | true             |
      | details                    | NOT_NULL         |
      | details_numberOfRecipients | 3                |
      | details_notificationCost   | xxx Uniform cost |

  @ricercaIndirizzoVas @technicalRefusualCostUniform #costi 46
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true             |
      | details                  | NOT_NULL         |
      | details_notificationCost | xxx Uniform cost |

  @ricercaIndirizzoVas @technicalRefusualCostUniform #costi 47
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true             |
      | details                  | NOT_NULL         |
      | details_notificationCost | xxx Uniform cost |


  @ricercaIndirizzoVas @technicalRefusualCostUniform #costi 5-56
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline               | true             |
      | details                    | NOT_NULL         |
      | details_numberOfRecipients | 2                |
      | details_notificationCost   | xxx Uniform cost |




#*********  Costi RECIPIENT **********************************


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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true             |
      | details                  | NOT_NULL         |
      | details_notificationCost | xxx Uniform cost |

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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
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
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline             | true      |
      | details                  | NOT_NULL  |
      | details_notificationCost | xxx  cost |


  # ********** lato destinatario



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
    Then lato destinatario la notifica può essere correttamente recuperata da "ETTORE_FIERAMOSCA" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    And lato destinatario la notifica può essere correttamente recuperata da "ETTORE_FIERAMOSCA" e verifica presenza dell'evento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"

# ricezione notifiche
  @useB2B
  Scenario: [26]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then la notifica può essere correttamente recuperata da "ETTORE_FIERAMOSCA"
    And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE" è visibile
    And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL" è visibile
    #Then la notifica può essere correttamente recuperata da "ETTORE_FIERAMOSCA" V25
    #And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE" non è visibile
    #And lato api l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL" non è visibile
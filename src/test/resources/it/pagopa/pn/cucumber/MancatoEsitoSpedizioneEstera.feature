Feature: Mancato esito spedizione analogica este@mancatoEsitoSpedizioneEstera



  #SEND_ANALOG_TIMEOUT_CREATION_REQUEST(diagnostico)
  #SEND_ANALOG_TIMEOUT(con allegato)

  #ANALOG_FAILURE_WORKFLOW_TIMEOUT
  #DELIVERY_TIMEOUT(stato)

  #deliveryDetailsCode RIR DEMAT : RECRI004B - RECRI003B


  #                      *** MONODESTINATARIO ***


  @mancatoEsitoSpedizioneEstera #rif 6
  Scenario: [DELIVERY_TIMEOUT_6] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, nessun esito secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX no esito no esito                       |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_State   | MESSICO                                      |
      | physicalAddress_zip     | ZONE_2                                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    And viene verificato che l'elemento di timeline "PREPARE_ANALOG_DOMICILE" esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene controllato che l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" non esiste
    And viene verificato che l'elemento di timeline "PREPARE_ANALOG_DOMICILE" esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 1        |
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 1                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXXX"}]    |
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    And verifica che in timeline non siano presenti i DeliveryDetailCode di demat "RECRI004B" "RECRI003B"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    #And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"

  @mancatoEsitoSpedizioneEstera #rif 3
  Scenario: [DELIVERY_TIMEOUT_3] Notifica monodestinatario, spedizione RIR esito positivo primo tentativo e stato delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | Via@ok_RIR |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      | details_responseStatus  | OK       |
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @mancatoEsitoSpedizioneEstera #rif 4
  Scenario: [DELIVERY_TIMEOUT_4] Notifica monodestinatario, con esito ko primo tentativo, recupero indirizzo internazionale, secondo tentativo con esito ok
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale ok |
      | recipientType           | PG                                     |
      | taxId                   | xxx                                    |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR               |
      | digitalDomicile         | NULL                                   |
      | physicalAddress_State   | MESSICO                                |
      | physicalAddress_zip     | ZONE_2                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      | details_responseStatus  | KO       |
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    # verifica se ko o ok

  @mancatoEsitoSpedizioneEstera #rif 5
  Scenario: [DELIVERY_TIMEOUT_5] Notifica monodestinatario, con esito ko primo tentativo, recupero indirizzo internazionale, secondo tentativo esito ko
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale irreperibile |
      | recipientType           | PG                                               |
      | taxId                   | xxx                                              |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR                         |
      | digitalDomicile         | NULL                                             |
      | physicalAddress_State   | MESSICO                                          |
      | physicalAddress_zip     | ZONE_2                                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_deliveryDetailCode | RECRI003B |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 1         |
    And vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"


  @mancatoEsitoSpedizioneEstera #rif 7
  Scenario: [DELIVERY_TIMEOUT_7] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, esito positivo secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale ok |
      | recipientType           | PG                                     |
      | taxId                   | xxx                                    |
      | physicalAddress_address | @XXX no esito - ok                     |
      | digitalDomicile         | NULL                                   |
      | physicalAddress_State   | MESSICO                                |
      | physicalAddress_zip     | ZONE_2                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
    And viene controllato che l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" non esiste

  @mancatoEsitoSpedizioneEstera #rif 8
  Scenario: [DELIVERY_TIMEOUT_8] Notifica monodestinatario, nessun esito primo tentativo, recupero indirizzo internazionale, secondo tentativo esito ko
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale irreperibile |
      | recipientType           | PG                                               |
      | taxId                   | xxx                                              |
      | physicalAddress_address | Via@ no esito - registro                         |
      | digitalDomicile         | NULL                                             |
      | physicalAddress_State   | MESSICO                                          |
      | physicalAddress_zip     | ZONE_2                                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"
   # Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene controllato che l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" non esiste


  @mancatoEsitoSpedizioneEstera #rif 9
  Scenario: [DELIVERY_TIMEOUT_9] Notifica monodestinatario, nessun esito primo tentativo, secondo tentativo indirizo non trovato
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo non trovato |
      | recipientType           | PG                               |
      | taxId                   | xxx                              |
      | physicalAddress_address | Via@ no esito - registro         |
      | digitalDomicile         | NULL                             |
      | physicalAddress_State   | MESSICO                          |
      | physicalAddress_zip     | ZONE_2                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And verifica che in timeline non siano presenti i DeliveryDetailCode di demat "RECRI004B" "RECRI003B"
    And viene verificato che l'elemento di timeline "PREPARE_ANALOG_DOMICILE" non esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 1        |
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    And vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"


  @mancatoEsitoSpedizioneEstera #rif 10
  Scenario: [DELIVERY_TIMEOUT_10] Notifica monodestinatario, nessun esito primo tentativo, secondo tentativo indirizo identico al primo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo identico al primo |
      | recipientType           | PG                                     |
      | taxId                   | xxx                                    |
      | physicalAddress_address | Via@ no esito - registro               |
      | digitalDomicile         | NULL                                   |
      | physicalAddress_State   | MESSICO                                |
      | physicalAddress_zip     | ZONE_2                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And verifica che in timeline non siano presenti i DeliveryDetailCode di demat "RECRI004B" "RECRI003B"
    And viene verificato che l'elemento di timeline "PREPARE_ANALOG_DOMICILE" non esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 1        |
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    And vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"

  @mancatoEsitoSpedizioneEstera #rif 11
  Scenario: [DELIVERY_TIMEOUT_11] Notifica monodestinatario, nessun esito primo tentativo RIR, secondo tentativo indirizo nazionale ok
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo nazionale ok |
      | recipientType           | PG                                |
      | taxId                   | xxx                               |
      | physicalAddress_address | Via@ no esito - registro          |
      | digitalDomicile         | NULL                              |
      | physicalAddress_State   | MESSICO                           |
      | physicalAddress_zip     | ZONE_2                            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "xxx" per l'utente 0
    And viene controllato che l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" non esiste

  @mancatoEsitoSpedizioneEstera #rif 12
  Scenario: [DELIVERY_TIMEOUT_12] Notifica monodestinatario, nessun esito primo tentativo, secondo tentativo indirizo nazionale ko
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo nazionale irreperibile |
      | recipientType           | PG                                          |
      | taxId                   | xxx                                         |
      | physicalAddress_address | Via@ no esito - registro                    |
      | digitalDomicile         | NULL                                        |
      | physicalAddress_State   | MESSICO                                     |
      | physicalAddress_zip     | ZONE_2                                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "xxx" per l'utente 0
    And viene controllato che l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" non esiste
    And vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"


  @mancatoEsitoSpedizioneEstera #rif 13
  Scenario: [DELIVERY_TIMEOUT_13] Notifica monodestinatario, esito deceduto primo tentativo RIR
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG                |
      | recipientType           | PG                |
      | taxId                   | xxx               |
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO           |
      | physicalAddress_zip     | ZONE_2            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" con notificationCost uguale a "NotNull" per l'utente 0
    And viene controllato che l'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" non esiste


    #            ***LATO DESTINATARIO***

  @mancatoEsitoSpedizioneEstera #rif 15
  Scenario: [DELIVERY_TIMEOUT_DESTINATARIO] Notifica monodestinatario, RIR senza esito e visualizzazione nuovi elementi di timeout lato destinatario
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "SEND_ANALOG_TIMEOUT"
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "ANALOG_FAILURE_WORKFLOW_TIMEOUT"


  #           ***VERSIONE PRECEDENTE API***

  @mancatoEsitoSpedizioneEstera #rif 16
  Scenario: [DELIVERY_TIMEOUT_API_PRECEDENTI] Notifica monodestinatario, RIR senza esito e nuovi elementi di timeout non presenti con versione V25
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "XXX"
    Then recuperando la fullSentNotification con la versione b2b "V25" non è presente l'elemento di timeline "SEND_ANALOG_TIMEOUT_CREATION_REQUEST"
    Then recuperando la fullSentNotification con la versione b2b "V25" non è presente l'elemento di timeline "SEND_ANALOG_TIMEOUT"
    Then recuperando la fullSentNotification con la versione b2b "V25" non è presente l'elemento di timeline "ANALOG_FAILURE_WORKFLOW_TIMEOUT"

  @mancatoEsitoSpedizioneEstera #rif 17
  Scenario: [DELIVERY_TIMEOUT_API_PRECEDENTI] Notifica monodestinatario, RIR senza esito e nuovi elementi di timeout non presenti con versione V26
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "XXX"
    Then recuperando la fullSentNotification con la versione b2b "V26" non è presente l'elemento di timeline "SEND_ANALOG_TIMEOUT_CREATION_REQUEST"
    Then recuperando la fullSentNotification con la versione b2b "V26" non è presente l'elemento di timeline "SEND_ANALOG_TIMEOUT"
    Then recuperando la fullSentNotification con la versione b2b "V26" non è presente l'elemento di timeline "ANALOG_FAILURE_WORKFLOW_TIMEOUT"




  #            *********MONODESTINATARIO DA ITALIA*******

  @mancatoEsitoSpedizioneEstera #rif 18
  Scenario: [DELIVERY_TIMEOUT_18] Notifica monodestinatario, nazionale nessun esito , secondo tentativo RIR esito ok
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale ok |
      | recipientType           | PG                                     |
      | taxId                   | xxx                                    |
      | physicalAddress_address | @XXX                                   |
      | digitalDomicile         | ITA                                    |
      | physicalAddress_State   | ITA                                    |
      | physicalAddress_zip     | ITA                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "xxx" per l'utente 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" non esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |


  @mancatoEsitoSpedizioneEstera #rif 19
  Scenario: [DELIVERY_TIMEOUT_19] Notifica monodestinatario, nazionale nessun esito , secondo tentativo RIR esito ko
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale ko |
      | recipientType           | PG                                     |
      | taxId                   | xxx                                    |
      | physicalAddress_address | @XXX                                   |
      | digitalDomicile         | ITA                                    |
      | physicalAddress_State   | ITA                                    |
      | physicalAddress_zip     | ITA                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "xxx" per l'utente 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" non esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |



  @mancatoEsitoSpedizioneEstera #rif 20
  Scenario: [DELIVERY_TIMEOUT_20] Notifica monodestinatario, nazionale nessun esito , secondo tentativo RIR nessun esito
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX                                         |
      | digitalDomicile         | ITA                                          |
      | physicalAddress_State   | ITA                                          |
      | physicalAddress_zip     | ITA                                          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" con productType uguale a "RIR" per l'utente 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 1                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0



  #                          *** VISUALIZZAZIONE ***

  #*serve un nostro utente censito con Nessun esito primo tentativo e nessun esito secondo (RIR)

  @mancatoEsitoSpedizioneEstera #rif 21
  Scenario: [DELIVERY_TIMEOUT_21] Notifica monodestinatario, primo tentativo con visualizzazione, nessun secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_VIEWED" con notificationCost uguale a "NotNull" per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "null" per l'utente 0


  @mancatoEsitoSpedizioneEstera #rif 22
  Scenario: [DELIVERY_TIMEOUT_22] Notifica monodestinatario, primo tentativo con visualizzazione, nessun secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "null" per l'utente 0
    And esiste l'elemento di timeline della notifica "NOTIFICATION_VIEWED" con notificationCost uguale a "NotNull" per l'utente 0
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"


  @mancatoEsitoSpedizioneEstera #rif 23
  Scenario: [DELIVERY_TIMEOUT_23] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, nessun esito secondo tentativo, visualizzazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And "Mario Cucumber" legge la notifica
    And esiste l'elemento di timeline della notifica "NOTIFICATION_VIEWED" con notificationCost uguale a "null" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"




  #                     ***CANCELLAZIONE***

  @mancatoEsitoSpedizioneEstera #rif 24
  Scenario: [DELIVERY_TIMEOUT_24] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, nessun esito secondo tentativo, cancellazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" con notificationCost uguale a "null" per l'utente 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |


  @mancatoEsitoSpedizioneEstera #rif 25
  Scenario: [DELIVERY_TIMEOUT_25] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale e cancellazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" con notificationCost uguale a "NotNull" per l'utente 0
    #continua?


  @mancatoEsitoSpedizioneEstera #rif 26
  Scenario: [DELIVERY_TIMEOUT_25] Notifica monodestinatario, nessuno esito primo tentativo, nessun esito RIR secondo tentativo e successiva cancellazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    When la notifica può essere annullata dal sistema tramite codice IUN
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" con notificationCost uguale a "null" per l'utente 0
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"

    #                        ***MULTIDESTINATARIO***


  @mancatoEsitoSpedizioneEstera #rif 27
  Scenario: [DELIVERY_TIMEOUT_27] Notifica multidestinatario, entrambi timeout , uno visualizza
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX                                         |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_State   | MESSICO                                      |
      | physicalAddress_zip     | ZONE_2                                       |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 1                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 1                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 1                         |
      | details_sentAttemptMade | 1                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 1
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_VIEWED" con notificationCost uguale a "null" per l'utente 0


  @mancatoEsitoSpedizioneEstera #rif 28
  Scenario: [DELIVERY_TIMEOUT_28] Notifica multidestinatario, entrambi timeout , notifica annullata
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX                                         |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_State   | MESSICO                                      |
      | physicalAddress_zip     | ZONE_2                                       |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 1                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 1                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 1                         |
      | details_sentAttemptMade | 1                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 1
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" con notificationCost uguale a "null" per l'utente 0
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" con notificationCost uguale a "null" per l'utente 1

  @mancatoEsitoSpedizioneEstera #rif 29
  Scenario: [DELIVERY_TIMEOUT_29] Notifica multidestinatario, timeout + irrerepiribile e stato atteso irreperibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX                                         |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_State   | MESSICO                                      |
      | physicalAddress_zip     | ZONE_2                                       |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 0                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 1                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |
    Then vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"

  @mancatoEsitoSpedizioneEstera #rif 30
  Scenario: [DELIVERY_TIMEOUT_30] Notifica multidestinatario, entrambi timeout
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX                                         |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_State   | MESSICO                                      |
      | physicalAddress_zip     | ZONE_2                                       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"


  @mancatoEsitoSpedizioneEstera #rif 31
  Scenario: [DELIVERY_TIMEOUT_31] Notifica multidestinatario, timeout + deceduto e stato atteso delivery_timeout
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX                                         |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_State   | MESSICO                                      |
      | physicalAddress_zip     | ZONE_2                                       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" con notificationCost uguale a "NotNull" per l'utente 1
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"


  @mancatoEsitoSpedizioneEstera #rif 32
  Scenario: [DELIVERY_TIMEOUT_32] Notifica multidestinatario, timeout + delivered e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale no esito |
      | recipientType           | PG                                           |
      | taxId                   | xxx                                          |
      | physicalAddress_address | @XXX                                         |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_State   | MESSICO                                      |
      | physicalAddress_zip     | ZONE_2                                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    #da vedere le tempistiche

  @mancatoEsitoSpedizioneEstera #rif 33
  Scenario: [DELIVERY_TIMEOUT_33] Notifica multidestinatario, timeout + irreperibile +  deceduto e stato atteso irreperibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" con notificationCost uguale a "NotNull" per l'utente 2
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 2                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |

  @mancatoEsitoSpedizioneEstera #rif 34
  Scenario: [DELIVERY_TIMEOUT_34] Notifica multidestinatario, timeout + delivered + deceduto e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" con notificationCost uguale a "NotNull" per l'utente 2
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" esista
      | details                 | NOT_NULL                  |
      | details_recIndex        | 0                         |
      | details_sentAttemptMade | 2                         |
      | details_attachments     | [{"documentType": "XXX"}] |
      | legalFactsIds           | [{"category": "XXX"}]     |


#**************************************************************
# ************** Test Extra alla progettazione ***************
#**************************************************************


  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_14e] Notifica multodestinatario, nessun esito secondo tentativo, irreperibile, delivered e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_15e] Notifica multodestinatario, nessun esito secondo tentativo, irreperibile, delivered, deceduto e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"



    #                    ***MULTIDESTINATARIO CON VISUALIZZAZIONE***

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_11Ve] Notifica multodestinatario, nessun esito secondo tentativo, delivered e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_12Ve] Notifica multodestinatario, nessun esito secondo tentativo, irrerepiribile e stato atteso irreperibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_13Ve] Notifica multodestinatario, nessun esito secondo tentativo, deceduto e stato atteso delivery_timeout
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_16Ve] Notifica multodestinatario, nessun esito secondo tentativo, irreperibile, delivered, deceduto e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX    |
      | digitalDomicile         | NULL    |
      | physicalAddress_State   | MESSICO |
      | physicalAddress_zip     | ZONE_2  |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"

    #                        ***SCENARI TENTATIVI E REGISTRI***

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_MULTID_1e] Notifica multidestinatario: Tentativo 1 esito ok e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | PG Tentativo 1 esito ok |
      | recipientType           | PG                      |
      | taxId                   | xxx                     |
      | physicalAddress_address | @XXX                    |
      | digitalDomicile         | NULL                    |
      | physicalAddress_State   | MESSICO                 |
      | physicalAddress_zip     | ZONE_2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_MULTID_2e] Notifica multidestinatario: Tentativo 2 Nessun indirizzo e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | PG Censito Nessun indirizzo |
      | recipientType           | PG                          |
      | taxId                   | xxx                         |
      | physicalAddress_address | @XXX                        |
      | digitalDomicile         | NULL                        |
      | physicalAddress_State   | MESSICO                     |
      | physicalAddress_zip     | ZONE_2                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_MULTID_3e] Notifica multidestinatario: Tentativo 2 Indirizzo identico al primo e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | PG Censito indirizzo identico al primo |
      | recipientType           | PG                                     |
      | taxId                   | xxx                                    |
      | physicalAddress_address | @XXX                                   |
      | digitalDomicile         | NULL                                   |
      | physicalAddress_State   | MESSICO                                |
      | physicalAddress_zip     | ZONE_2                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_MULTID_4e] Notifica multidestinatario: Tentativo 2 esito ok e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | PG Tentativo 2 esito ok |
      | recipientType           | PG                      |
      | taxId                   | xxx                     |
      | physicalAddress_address | @XXX                    |
      | digitalDomicile         | NULL                    |
      | physicalAddress_State   | MESSICO                 |
      | physicalAddress_zip     | ZONE_2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEsteraExtra
  Scenario: [DELIVERY_TIMEOUT_MULTID_5e] Notifica multidestinatario: Tentativo 2 esito ok, DELIVERY_TIMEOUT, Tentativo 2 Nessun indirizzo, Tentativo 1 esito ok
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito indirizzo internazionale |
      | recipientType           | PG                                  |
      | taxId                   | xxx                                 |
      | physicalAddress_address | @XXX                                |
      | digitalDomicile         | NULL                                |
      | physicalAddress_State   | MESSICO                             |
      | physicalAddress_zip     | ZONE_2                              |
    And destinatario
      | denomination            | PG Tentativo 2 esito ok |
      | recipientType           | PG                      |
      | taxId                   | xxx                     |
      | physicalAddress_address | @XXX                    |
      | digitalDomicile         | NULL                    |
      | physicalAddress_State   | MESSICO                 |
      | physicalAddress_zip     | ZONE_2                  |
    And destinatario
      | denomination            | PG Nessun indirizzo |
      | recipientType           | PG                  |
      | taxId                   | xxx                 |
      | physicalAddress_address | @XXX                |
      | digitalDomicile         | NULL                |
      | physicalAddress_State   | MESSICO             |
      | physicalAddress_zip     | ZONE_2              |
    And destinatario
      | denomination            | PG Tentativo 1 esito ok |
      | recipientType           | PG                      |
      | taxId                   | xxx                     |
      | physicalAddress_address | @XXX                    |
      | digitalDomicile         | NULL                    |
      | physicalAddress_State   | MESSICO                 |
      | physicalAddress_zip     | ZONE_2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"
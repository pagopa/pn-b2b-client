Feature: Mancato esito spedizione analogica este@mancatoEsitoSpedizioneEstera



  #SEND_ANALOG_TIMEOUT_CREATION_REQUEST(diagnostico)
  #SEND_ANALOG_TIMEOUT

  #ANALOG_FAILURE_WORKFLOW_TIMEOUT
  #DELIVERY_TIMEOUT(stato)

  #deliveryDetailsCode RIR DEMAT : RECRSI004B - RECRI004B - RECRI003B


#And viene effettuato un controllo sulla durata della retention di

  #                      *** MONODESTINATARIO ***

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_1] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, nessun esito secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT_CREATION_REQUEST" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT_CREATION_REQUEST" al tentativo "ATTEMPT_1"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"

#SEND_ANALOG_TIMEOUT -> controlla attestato



  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_2] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, secondo tentativo con esito ok
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                 | NOT_NULL|
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_3] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo nazionale
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo nazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                 | NOT_NULL|
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_4] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo identico al primo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo identico  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino allo stato della notifica "xxx"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
      | details_sentAttemptMade | 1        |


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_5] Notifica monodestinatario, nessuno esito primo tentativo, recupero di nessun indirizzo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo non trovato  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino allo stato della notifica "xxx"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_TIMEOUT" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
      | details_sentAttemptMade | 1        |




  #                          *** VISUALIZZAZIONE ***

  #*serve un nostro utente censito

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_6] Notifica monodestinatario, visualizzazione, nessuno esito primo tentativo, nessun secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    #And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "null" per l'utente 0
    #Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    #Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_7] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale,visualizzazione, nessun esito secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "null" per l'utente 0
    And esiste l'elemento di timeline della notifica "NOTIFICATION_VIEWED" con notificationCost uguale a "NotNull" per l'utente 0
    #Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    #Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_8] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, nessun esito secondo tentativo, visualizzazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_1"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    #Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    #Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    #stato viewed?



  #                     ***CANCELLAZIONE***

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_9] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, nessun esito secondo tentativo, cancellazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    And esiste l'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW_TIMEOUT" con notificationCost uguale a "NotNull" per l'utente 0
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" con notificationCost uguale a "null" per l'utente 0


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_10] Notifica monodestinatario, nessuno esito primo tentativo, recupero indirizzo internazionale, cancellazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_TIMEOUT" al tentativo "ATTEMPT_0"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" con notificationCost uguale a "NotNull" per l'utente 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |


    #                        ***MULTIDESTINATARIO***

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_11] Notifica multodestinatario, nessun esito secondo tentativo, delivered e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_12] Notifica multodestinatario, nessun esito secondo tentativo, irrerepiribile e stato atteso irreperibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_13] Notifica multodestinatario, nessun esito secondo tentativo, deceduto e stato atteso delivery_timeout
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_14] Notifica multodestinatario, nessun esito secondo tentativo, irreperibile, delivered e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
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

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_15] Notifica multodestinatario, nessun esito secondo tentativo, irreperibile, delivered, deceduto e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
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

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_16] Notifica multodestinatario, nessun esito secondo tentativo, irreperibile, delivered, deceduto e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"



    #            ***LATO DESTINATARIO***

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_DESTINATARIO]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"
    And lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "SEND_ANALOG_TIMEOUT_CREATION_REQUEST"
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "SEND_ANALOG_TIMEOUT"
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline "ANALOG_FAILURE_WORKFLOW_TIMEOUT"


  #           ***VERSIONE PRECEDENTE API***

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_API_PRECEDENTI]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "XXX"
    Then recuperando la fullSentNotification con la versione b2b "V25" non è presente l'elemento di timeline "SEND_ANALOG_TIMEOUT_CREATION_REQUEST"
    Then recuperando la fullSentNotification con la versione b2b "V25" non è presente l'elemento di timeline "SEND_ANALOG_TIMEOUT"
    Then recuperando la fullSentNotification con la versione b2b "V25" non è presente l'elemento di timeline "ANALOG_FAILURE_WORKFLOW_TIMEOUT"


    #                    ***MULTIDESTINATARIO CON VISUALIZZAZIONE***


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_11V] Notifica multodestinatario, nessun esito secondo tentativo, delivered e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_12V] Notifica multodestinatario, nessun esito secondo tentativo, irrerepiribile e stato atteso irreperibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_13V] Notifica multodestinatario, nessun esito secondo tentativo, deceduto e stato atteso delivery_timeout
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_16V] Notifica multodestinatario, nessun esito secondo tentativo, irreperibile, delivered, deceduto e stato atteso delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERY_TIMEOUT"




    #                        ***SCENARI TENTATIVI E REGISTRI***


  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_MULTID_1] Notifica multidestinatario: Tentativo 1 esito ok e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination    | PG Tentativo 1 esito ok  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_MULTID_2] Notifica multidestinatario: Tentativo 2 Nessun indirizzo e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination    | PG Censito Nessun indirizzo  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_MULTID_3] Notifica multidestinatario: Tentativo 2 Indirizzo identico al primo e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination    | PG Censito indirizzo identico al primo  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_MULTID_4] Notifica multidestinatario: Tentativo 2 esito ok e DELIVERY_TIMEOUT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination    | PG Tentativo 2 esito ok  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"

  @mancatoEsitoSpedizioneEstera
  Scenario: [DELIVERY_TIMEOUT_MULTID_5] Notifica multidestinatario: Tentativo 2 esito ok, DELIVERY_TIMEOUT, Tentativo 2 Nessun indirizzo, Tentativo 1 esito ok
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER      |
    And destinatario
      | denomination    | PG Censito indirizzo internazionale  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination    | PG Tentativo 2 esito ok  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination    | PG Nessun indirizzo  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    And destinatario
      | denomination    | PG Tentativo 1 esito ok  |
      | recipientType   | PG          |
      | taxId           | xxx |
      | physicalAddress_address | @XXX |
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    Then vengono letti gli eventi fino allo stato della notifica "xxx"
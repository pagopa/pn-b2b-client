Feature: verifica consume stream

  @webhookV30Informal @precondition @cleanWebhook @webhook3
  Scenario: [STREAM_CONSUME_CONTAINS_ONLY_LEGAL] Creando uno stream per una PA, e inviando con questa 1 notifica legale e 1 bonaria, lo stream con communicationType = LEGAL contiene solo elementi relativi alla notifica a valore legale
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo waitForAccepted a "true"
    And allo stream versione "V30" si setta il campo communicationType a "LEGAL"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V30"
    #invio notifica bonaria
    And l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                 |
      | messageId       | ${NEW-IT}               |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    #invio notifica legale
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When si invoca l'api Webhook versione "V30" per ottenere gli elementi di timeline di tale notifica
    Then lo stream "V30" contiene solo elementi relativi a notifiche LEGAL
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV30Informal @precondition @cleanWebhook @webhook3
  Scenario: [STREAM_CONSUME_CONTAINS_ONLY_INFORMAL] Creando uno stream per una PA, e inviando con questa 1 notifica legale e 1 bonaria, lo stream con communicationType = INFORMAL contiene solo elementi relativi alla notifica bonaria
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo waitForAccepted a "true"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V30"
    #invio notifica legale
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #invio notifica bonaria
    And l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                 |
      | messageId       | ${NEW-IT}               |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    When si invoca l'api Webhook versione "V30" per ottenere gli elementi di timeline di tale notifica
    Then lo stream "V30" contiene solo elementi relativi a notifiche INFORMAL
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV30Informal @precondition @cleanWebhook @webhook3
  Scenario: [STREAM_INFORMAL_CONSUME_1] Consume di uno stream con communicationType = INFORMAL con notifica andata in COMPLETED_REACHED (flusso digitale)
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo waitForAccepted a "true"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V30"
    And l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                 |
      | messageId       | ${NEW-IT}               |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    When si invoca l'api Webhook versione "V30" per ottenere gli elementi di timeline di tale notifica
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "ACCEPTED" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V30"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_MESSAGE" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_MESSAGE_PROGRESS" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_MESSAGE_FEEDBACK" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "DELIVERED" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "WORKFLOW_DONE_REACHED" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "COMPLETED_REACHED" con la versione "V30"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV30Informal @precondition @cleanWebhook @webhook3
  Scenario: [STREAM_INFORMAL_CONSUME_2] Consume di uno stream con communicationType = INFORMAL con notifica andata in COMPLETED_UNREACHED
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo waitForAccepted a "true"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V30"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                        |
      | messageId       | ${NEW-IT}                      |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M005  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "WORKFLOW_ENDED_UNREACHED" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "COMPLETED_UNREACHED" con la versione "V30"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV30Informal @precondition @cleanWebhook @webhook3
  Scenario: [STREAM_INFORMAL_CONSUME_3] Consume di uno stream con communicationType = INFORMAL con notifica andata in COMPLETED_REACHED (flusso analogico)
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo waitForAccepted a "true"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V30"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | MessaMora                  |
      | messageId                | ${NEW-IT}                  |
      | subject                  | Test workflow              |
      | recipientType            | PG                         |
      | taxId                    | 20517490320                |
      | denomination             | Acme spa                   |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | Via@OK_RIS                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "COVERPAGE_CREATION_REQUEST" della notifica bonaria
    And si attende che venga prodotto l'elemento "PREPARE_ANALOG_DELIVERY" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "COVERPAGE_CREATION_REQUEST" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PREPARE_ANALOG_DELIVERY" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_MESSAGE_PROGRESS" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_MESSAGE_FEEDBACK" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "COMPLETED_REACHED" con la versione "V30"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV30Informal @precondition @cleanWebhook @webhook3
  Scenario: [STREAM_INFORMAL_CONSUME_4] Consume di uno stream con communicationType = INFORMAL con notifica visualizzata
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo waitForAccepted a "true"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V30"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                        |
      | messageId       | ${NEW-IT}                      |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And il destinatario Ettore Fieramosca legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "PROCESSING" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "INFORMAL_NOTIFICATION_VIEWED" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "COMPLETED_REACHED" con la versione "V30"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV30Informal @precondition @cleanWebhook @webhook3
  Scenario: [STREAM_INFORMAL_CONSUME_5] Consume di uno stream con communicationType = INFORMAL con notifica undeliverable e SEND_DIGITAL_MESSAGE_SKIP
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo waitForAccepted a "true"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V30"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder          |
      | messageId       | ${NEW-IT}         |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
      | phone_number    | NULL              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | SMS |
    And si attende che la notifica bonaria passi in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_MESSAGE_SKIP" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" con la versione "V30"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "UNDELIVERABLE" con la versione "V30"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
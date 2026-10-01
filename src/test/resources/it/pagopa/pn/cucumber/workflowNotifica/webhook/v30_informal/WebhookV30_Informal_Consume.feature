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
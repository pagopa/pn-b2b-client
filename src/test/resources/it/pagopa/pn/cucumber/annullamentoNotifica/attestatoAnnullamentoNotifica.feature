Feature: produzione del documento di annullamento notifica

  @attestatoAnnullamentoNotificaTODO
  Scenario: [ATTESTATO_ANNULLAMENTO_1]
    Given viene generata una nuova notifica con la versione più recente
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune_Multi                    |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then viene verificata la presenza dell'elemento di timeline "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST" introdotto con la versione 25 in tutte le versioni
#    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST"
    #And è presente il documento che ne attesta l'annullamento

  @attestatoAnnullamentoNotificaTODO
  Scenario: [ATTESTATO_ANNULLAMENTO_2]
    Given viene generata una nuova notifica con la versione più recente
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune_Multi                    |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    Then viene verificata la presenza dell'elemento di timeline "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST" introdotto con la versione 25 in tutte le versioni

  @attestatoAnnullamentoNotifica @precondition @webhook2
#  @cleanWebhookTODO MATTEO rimettere post test stefano
    #NOTA: il nuovo elemento di Timeline NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST è nascosto lato API, ed consultabile a DB unicamente in modo manuale
  Scenario: [ATTESTATO_ANNULLAMENTO_WEBHOOK]
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V10"
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V23"
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V24"
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V25"
    And viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune_Multi                    |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-testV10" con eventType "TIMELINE" con versione "V10"
    And si predispone 1 nuovo stream denominato "stream-testV23" con eventType "TIMELINE" con versione "V23"
    And si predispone 1 nuovo stream denominato "stream-testV24" con eventType "TIMELINE" con versione "V24"
    And si predispone 1 nuovo stream denominato "stream-testV24" con eventType "TIMELINE" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V24"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V25"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then si invoca l'api Webhook versione "V10" per ottenere gli elementi di timeline di tale notifica
    Then si invoca l'api Webhook versione "V23" per ottenere gli elementi di timeline di tale notifica
    Then si invoca l'api Webhook versione "V24" per ottenere gli elementi di timeline di tale notifica
    Then si invoca l'api Webhook versione "V25" per ottenere gli elementi di timeline di tale notifica
#    Then tra gli elementi di timeline restituiti dal Webhook 10 viene verificata la presenza dell'elemento "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST", introdotto con la versione 25
#    Then tra gli elementi di timeline restituiti dal Webhook 23 viene verificata la presenza dell'elemento "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST", introdotto con la versione 25
#    Then tra gli elementi di timeline restituiti dal Webhook 24 viene verificata la presenza dell'elemento "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST", introdotto con la versione 25
#    Then tra gli elementi di timeline restituiti dal Webhook 25 viene verificata la presenza dell'elemento "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST", introdotto con la versione 25


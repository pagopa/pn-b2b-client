Feature: produzione del documento di annullamento notifica

  @attestatoAnnullamentoNotifica
  Scenario: [ATTESTATO_ANNULLAMENTO_LEGAL_FACT] Verifica della presenza del nuovo legalFact avente categoria NOTIFICATION_CANCELLED in seguito all'annullamento di una notifica
    Given viene generata una nuova notifica con la versione più recente
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune_Multi                    |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then tra gli elementi di timeline con categoria "NOTIFICATION_CANCELLED" è presente un legalFact con categoria "NOTIFICATION_CANCELLED"
    And l'utente "Mario Cucumber" recupera i legalFacts richiamando l'api versione 20 e tra questi "COMPARE" il legalFact con categoria "NOTIFICATION_CANCELLED"

  @attestatoAnnullamentoNotifica
  Scenario: [ATTESTATO_ANNULLAMENTO_VERSIONE_PRECEDENTE] Recuperando la timeline con versioni anteriori alla V25, in seguito all'annullamento di una notifica non devono esserci elementi aventi legalFacts con categoria "NOTIFICATION_CANCELLED"
    Given viene generata una nuova notifica con la versione più recente
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune_Multi                    |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    When si invoca l'api B2B versione "V23" per ottenere gli elementi di timeline di tale notifica
    Then tra gli elementi di timeline versione "V23" di categoria "NOTIFICATION_CANCELLED" nessuno contiene un legalFact con categoria "NOTIFICATION_CANCELLED"

  @attestatoAnnullamentoNotifica @precondition @webhook2
  #@cleanWebhook
    # NOTA: il nuovo elemento di Timeline NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST è nascosto lato API.
      # In fase di test manuale, commentare l'annotation @cleanWebhook per consentire al tester di avere il tempo materiale di controllare a DB la versione degli stream
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
    And si predispone 1 nuovo stream denominato "stream-testV25" con eventType "TIMELINE" con versione "V25"
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


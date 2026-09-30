Feature: Gestione puntuale per singolo CF degli esiti delle richieste in batch verso INIPEC

  # COSA VERIFICHIAMO
  # Una notifica verso una PG priva di domicilio digitale fa interrogare INIPEC per recuperarne
  # la PEC. L'esito per ciascun CF viene tracciato su DynamoDB `pn-batchRequests` tramite i campi
  # `sendStatus` e `retry`. Gli scenari controllano che, a fronte di esiti INIPEC noti (pilotati da
  # CF "magici" del mock), il record finisca nello stato atteso. Mappatura esito -> stato su DB:
  #
  #   "risulta INVIATA al primo tentativo"        -> sendStatus = SENT,     retry = 0
  #   "risulta RIMESSA in retry"                  -> sendStatus = NOT_SENT,  retry > 0
  #   "non risulta alcuna richiesta INIPEC"       -> nessun record (né SENT né NOT_SENT)
  #

  @inipecGestionePuntuale @inipecParallel
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_02.1] Notifica verso due PG con esiti errore temporaneo e domicilio trovato rispettivamente nel recupero del domicilio digitale presso INIPEC nuova versione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 00845483379 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    # 17492068394 -> esito INIPEC atteso: statoImpresa=ER
    And destinatario
      | taxId           | 17492068394 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta INIPEC per il cf "00845483379" risulta INVIATA al primo tentativo
    Then la richiesta INIPEC per il cf "17492068394" risulta RIMESSA in retry


  @inipecGestionePuntuale @inipecParallel
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_01] Notifica verso due PG con esiti posizione non trovata e posizione senza pec associata rispettivamente nel recupero del domicilio digitale presso INIPEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    # 39274018561 -> esito INIPEC atteso: "statoImpresa=ND
    And destinatario
      | taxId           | 39274018561 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    # 83016259471 -> esito INIPEC atteso: statoImpresa=NF
    And destinatario
      | taxId           | 83016259471 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta INIPEC per il cf "39274018561" risulta INVIATA al primo tentativo
    Then la richiesta INIPEC per il cf "83016259471" risulta INVIATA al primo tentativo


  @inipecGestionePuntuale @inipecIsolato
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_02.2] Notifica verso due PG con esiti errore temporaneo e domicilio trovato rispettivamente nel recupero del domicilio digitale presso INIPEC vecchia versione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 29527800386 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    # 10433218194 -> esito INIPEC atteso: se nel batch è contenuto questo CF numerico tutto il batch andrà in errore e non ci sarà la gestione puntuale per singolo codice fiscale
    And destinatario
      | taxId           | 10433218194 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then per i cf "29527800386" e "10433218194" non risulta alcuna richiesta INIPEC

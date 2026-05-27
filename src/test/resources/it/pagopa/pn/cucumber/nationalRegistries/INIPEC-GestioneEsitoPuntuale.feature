Feature: Gestione puntuale per singolo CF degli esiti delle richieste in batch verso INIPEC

  @inipecGestionePuntuale
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_01.1] Notifica verso due PG con esiti errore temporaneo e domicilio trovato rispettivamente nel recupero del domicilio digitale presso INIPEC nuova versione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 29527800386 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And destinatario
      | taxId           | 17492068394 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NATIONAL_REGISTRY_CALL"
    Then viene verificato che la richiesta per il cf "29527800386" non risulti in retry
    And si attende per 10 minuti
    Then viene verificato che la richiesta per il cf "17492068394" risulti inviata in DLQ e con retry>0


  @inipecGestionePuntuale
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_01.2] Notifica verso due PG con esiti errore temporaneo e domicilio trovato rispettivamente nel recupero del domicilio digitale presso INIPEC vecchia versione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 29527800386 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And destinatario
      | taxId           | 10433218194 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NATIONAL_REGISTRY_CALL"
    And si attende per 10 minuti
    Then viene verificato che la richiesta per il cf "29527800386" risulti inviata in DLQ e con retry>0
    Then viene verificato che la richiesta per il cf "10433218194" risulti inviata in DLQ e con retry>0


  @inipecGestionePuntuale
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_01.3] Notifica verso due PG con esiti posizione non trovata e posizione senza pec associata rispettivamente nel recupero del domicilio digitale presso INIPEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 39274018561 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And destinatario
      | taxId           | 83016259471 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NATIONAL_REGISTRY_CALL"
    Then viene verificato che la richiesta per il cf "39274018561" non risulti in retry
    Then viene verificato che la richiesta per il cf "83016259471" non risulti in retry
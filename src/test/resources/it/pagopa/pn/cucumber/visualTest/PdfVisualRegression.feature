Feature: Visual regression dei documenti PDF prodotti da SEND

  # Ogni documento prodotto da SEND viene confrontato, testo dei campi dinamici e resa visiva, con il suo golden master,
  # generato dal Template Engine con gli stessi dati della notifica di riferimento.
  # Limiti: l'attestazione di disponibilità dell'esito cartaceo non è prodotta da nessun workflow (solo da uno script
  # operativo): per questo documento esiste solo il golden master.
  # Blocchi attuali:
  # - [VRT-06] in TEST nessun disservizio risolto ha un'attestazione prodotta dopo il golden master: quelle esistenti
  #   seguono il template precedente (footer diverso). Lo scenario crea un disservizio e non è stato eseguito.
  # - [VRT-07] l'AAR standard non viene più prodotto (vedi lo scenario commentato).

  @visualTest @work-item:QA-18240
  Scenario: [VRT-01] L'attestazione di presa in carico corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "SENDER_ACK"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And si recupera il documento "SENDER_ACK" della notifica
    Then il documento è conforme al golden master
    And per il destinatario 1 del documento si verificano i seguenti campi
      | campo                            | valore        |
      | Nome e Cognome / Ragione Sociale | Mario Gherkin |

  @visualTest @work-item:QA-18240
  Scenario: [VRT-02] L'attestazione di consegna digitale corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "PEC_DELIVERY"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And si recupera il documento "PEC_DELIVERY" della notifica
    Then il documento è conforme al golden master

  @visualTest @work-item:QA-18240
  Scenario: [VRT-03] L'attestazione di avvenuto accesso corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "NOTIFICATION_VIEWED"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And si recupera il documento "NOTIFICATION_VIEWED" della notifica
    Then il documento è conforme al golden master

  @visualTest @work-item:QA-18240
  Scenario: [VRT-04] Il deposito dell'avviso per irreperibilità corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "ANALOG_FAILURE"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And si recupera il documento "ANALOG_FAILURE" della notifica
    Then il documento è conforme al golden master

  @visualTest @work-item:QA-18240
  Scenario: [VRT-05] La dichiarazione di annullamento corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "NOTIFICATION_CANCELLED"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And si recupera il documento "NOTIFICATION_CANCELLED" della notifica
    Then il documento è conforme al golden master

  # [VRT-06] Preparato, non eseguito: apre e chiude un disservizio con l'API interna di pn-downtime-logs (KO poi OK),
  # scrivendo nello storico dei disservizi e nello stato della piattaforma dell'ambiente: va eseguito solo dopo
  # accordo col team. L'alternativa che non scrive, leggere l'ultimo disservizio risolto ("dell'ultimo disservizio
  # risolto"), non è praticabile finché in TEST tutte le attestazioni esistenti seguono il template precedente.
  @visualTest @work-item:QA-18240 @ignore
  Scenario: [VRT-06] L'attestazione di malfunzionamento corrisponde al golden master
    Given viene aperto e chiuso un disservizio della funzionalità "NOTIFICATION_CREATE"
    When si recupera il documento "MALFUNCTION" del disservizio creato
    Then il documento è conforme al golden master

  # [VRT-07] Disattivato: l'AAR standard (AAR_NOTIFICATION) non viene più prodotto. In pn-delivery-push-workflow la
  # configurazione `pn-send-mode` ha dal 2024-06-26 una voce fissa su AAR_NOTIFICATION_RADD_ALT e l'ECS di TEST non la
  # sovrascrive: la scelta per CAP (radd-experimentation-zip-*) vale solo con la voce dinamica <RADD_TEMPLATE_DEFINITION>.
  # Verificato il 2026-11-18 sul repository e sull'ambiente TEST. Template, fixture e golden di NOTIFICATION_AAR restano
  # nel repo: se la voce dinamica tornasse in uso, basta un CAP fuori dalle liste di sperimentazione.
  #
  # @visualTest @work-item:QA-18240
  # Scenario: [VRT-07] L'avviso di avvenuta ricezione corrisponde al golden master
  #   Given viene generata la notifica di riferimento del documento "NOTIFICATION_AAR"
  #   When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
  #   And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
  #   And si recupera il documento "NOTIFICATION_AAR" della notifica
  #   Then il documento è conforme al golden master

  @visualTest @work-item:QA-18240
  Scenario: [VRT-08] L'avviso di avvenuta ricezione con ritiro RADD corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "NOTIFICATION_AAR_RADD_ALT"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And si recupera il documento "NOTIFICATION_AAR_RADD_ALT" della notifica
    Then il documento è conforme al golden master

  @visualTest @work-item:QA-18240
  Scenario: [VRT-09] L'attestazione di timeout del workflow analogico corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si recupera il documento "ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT" della notifica
    Then il documento è conforme al golden master

  # [VRT-10] La chiave del file è sulla timeline B2B (COVERPAGE_CREATION_REQUEST); il download passa dall'API privata
  # di SafeStorage (pn.safeStorage.base-url, in locale tunnel su localhost:8888). Oggetto e testo della copertina
  # vengono dal messaggio creato da ${NEW-IT} (RISCUOTI_IT, con avviso di pagamento): la fixture del golden master
  # riporta gli stessi valori.
  @visualTest @work-item:QA-18240
  Scenario: [VRT-10] La copertina della comunicazione bonaria cartacea corrisponde al golden master
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | MessaMora                  |
      | messageId                | ${NEW-IT}                  |
      | subject                  | Test workflow              |
      | recipientType            | PF                         |
      | taxId                    | FRMTTR76M06B715E           |
      | denomination             | Ettore Fieramosca          |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | Via@OK_RS                  |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "COVERPAGE_CREATION_REQUEST" della notifica bonaria
    And si recupera il documento "INFORMAL_ANALOG_COMMUNICATION" della notifica bonaria
    Then il documento è conforme al golden master

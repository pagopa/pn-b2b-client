Feature: Visual regression dei documenti PDF prodotti da SEND

  # Ogni documento prodotto da SEND viene confrontato, testo dei campi dinamici e resa visiva, con il suo golden master,
  # generato dal Template Engine con gli stessi dati della notifica di riferimento.
  # Limiti: l'attestazione di disponibilità dell'esito cartaceo e la comunicazione bonaria cartacea non sono recuperabili
  # tramite API: per questi documenti esiste solo il golden master.
  # Blocchi attuali:
  # - [VRT-06] in TEST nessun disservizio risolto ha un'attestazione prodotta dopo il golden master: quelle esistenti
  #   seguono il template precedente (footer diverso).
  # - [VRT-07] l'AAR standard: in TEST le notifiche dei destinatari di test ricevono l'AAR RADD alternativo.

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

  @visualTest @work-item:QA-18240 @ignore
  Scenario: [VRT-06] L'attestazione di malfunzionamento corrisponde al golden master
    When si recupera il documento "MALFUNCTION" dell'ultimo disservizio risolto
    Then il documento è conforme al golden master

  @visualTest @work-item:QA-18240 @ignore
  Scenario: [VRT-07] L'avviso di avvenuta ricezione corrisponde al golden master
    Given viene generata la notifica di riferimento del documento "NOTIFICATION_AAR"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And si recupera il documento "NOTIFICATION_AAR" della notifica
    Then il documento è conforme al golden master

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

Feature: Notifica visualizzata

  @e2e
  Scenario: [E2E-NOTIFICATION-VIEWED-1] Visualizzazione da parte del destinatario della notifica
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Ettore Fieramosca |
      | taxId        | FRMTTR76M06B715E  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene letta la timeline fino all'elemento "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    And viene letta la timeline fino all'elemento "NOTIFICATION_VIEWED"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "NOTIFICATION_VIEWED"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-NOTIFICATION-VIEWED-2] Visualizzazione da parte del delegato della notifica
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Ettore Fieramosca |
      | taxId        | FRMTTR76M06B715E  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene letta la timeline fino all'elemento "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And viene letta la timeline fino all'elemento "NOTIFICATION_VIEWED"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
      | details_delegateInfo | {"taxId": "CLMCST42R12D969Z", "denomination": "Cristoforo Colombo", "delegateType": "PF"} |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "NOTIFICATION_VIEWED"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-NOTIFICATION-VIEWED-4] A valle della visualizzazione della notifica non deve essere generato un nuovo elemento di timeline NOTIFICATION VIEWED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Ettore Fieramosca |
      | taxId        | FRMTTR76M06B715E  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene letta la timeline fino all'elemento "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    And viene letta la timeline fino all'elemento "NOTIFICATION_VIEWED"
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
    # seconda lettura
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    And verifico che l'atto opponibile a terzi di "NOTIFICATION_VIEWED" sia lo stesso
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
    And viene verificato che il numero di elementi di timeline "NOTIFICATION_VIEWED" della notifica sia di 1
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "NOTIFICATION_VIEWED"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-INHIBITION-3] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    La notifica viene letta subito dopo la generazione dell'evento di timeline SCHEDULE_REFINEMENT. Questa lettura non deve generare
    un evento di timeline REFINEMENT.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "REFINEMENT"

  @e2e
  Scenario: [E2E-WF-INHIBITION-4] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  La notifica viene letta subito dopo la generazione dell'evento di timeline DIGITAL_FAILURE_WORKFLOW. Questa lettura non deve generare
  un evento di timeline PREPARE_SIMPLE_REGISTERED_LETTER e SEND_SIMPLE_REGISTERED_LETTER.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "PREPARE_SIMPLE_REGISTERED_LETTER"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

  @e2e
  Scenario: [E2E-WF-INHIBITION-5] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  La notifica viene letta subito dopo essere stata accettata. Questa lettura non deve generare un evento di timeline SEND_ANALOG_DOMICILE.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_ANALOG_DOMICILE"


  Scenario: [E2E-WF-INHIBITION-6] Invio notifica con percorso analogico. Notifica visualizzata tra un tentativo e l'altro
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene letta la timeline fino all'elemento "SEND_ANALOG_DOMICILE"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    Then la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    Then viene letta la timeline fino all'elemento "SEND_ANALOG_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    Then viene verificato che il numero di elementi di timeline "SEND_ANALOG_DOMICILE" della notifica sia di 1
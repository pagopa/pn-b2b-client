Feature: Notifica visualizzata

  @e2e
  Scenario: [E2E-NOTIFICATION-VIEWED-1] Visualizzazione da parte del destinatario della notifica
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Ettore Fieramosca |
      | taxId        | FRMTTR76M06B715E  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
      | loadTimeline | true |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline | true |
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
    Then viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
      | loadTimeline | true |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline | true |
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
    Then viene verificato che l'elemento di timeline "REQUEST_ACCEPTED" esista
      | loadTimeline | true |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And la notifica può essere correttamente recuperata da "Ettore Fieramosca"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline | true |
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
  Scenario: [E2E-NOTIFICATION-VIEWED-5] A valle della visualizzazione della notifica, il destinatario non deve essere nella tabella pn-paper-notification-failed
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Dino Sauro |
      | taxId        | DSRDNI00A01A225I  |
      | digitalDomicile | NULL |
      | physicalAddress_address | @FAIL-DiscoveryIrreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "COMPLETELY_UNREACHABLE" esista
      | loadTimeline | true |
      | pollingTime | 40000 |
      | numCheck    | 20     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che il destinatario "DSRDNI00A01A225I" di tipo "PF" sia nella tabella pn-paper-notification-failed
    And la notifica può essere correttamente recuperata da "Dino Sauro"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
    And viene verificato che il destinatario "DSRDNI00A01A225I" di tipo "PF" non sia nella tabella pn-paper-notification-failed

  @e2e
  Scenario: [E2E-WF-INHIBITION-2] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | pollingTime | 4000 |
      | numCheck    | 8    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_digitalAddressSource | SPECIAL |
    And la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
    And viene verificato che il numero di elementi di timeline "SCHEDULE_REFINEMENT" della notifica sia di 0

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
    Then viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 7     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |

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
    Then viene verificato che l'elemento di timeline "DIGITAL_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    And viene verificato che l'elemento di timeline "PREPARE_SIMPLE_REGISTERED_LETTER" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-INHIBITION-5] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  La notifica viene letta subito dopo essere stata accettata. Questa lettura non deve generare un evento di timeline SEND_ANALOG_DOMICILE.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
    And destinatario
      | denomination | Ettore Fieramosca |
      | taxId        | FRMTTR76M06B715E  |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Ettore Fieramosca"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_DOMICILE" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e
  Scenario: [E2E-WF-INHIBITION-6] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  La notifica viene letta subito dopo la generazione dell'evento di timeline ANALOG_FAILURE_WORKFLOW. Questa lettura non deve generare
  un evento di timeline REFINEMENT.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
    And destinatario
      | denomination | Dino Sauro |
      | taxId        | DSRDNI00A01A225I  |
      | digitalDomicile | NULL |
      | physicalAddress_address | @FAIL-DiscoveryIrreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | loadTimeline | true |
      | pollingTime | 40000 |
      | numCheck    | 16     |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "ANALOG_FAILURE_WORKFLOW" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    Then la notifica può essere correttamente recuperata da "Dino Sauro"
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-WF-INHIBITION-7] Invio notifica con percorso analogico. Notifica visualizzata tra un tentativo e l'altro
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
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_DOMICILE" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    Then la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    Then viene verificato che il numero di elementi di timeline "SEND_ANALOG_DOMICILE" della notifica sia di 1
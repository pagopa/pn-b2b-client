Feature: esposizione timestamp tecnici per gli SLA

  Scenario Outline: [TIMESTAMP_TECNICI_SLA_B2B] Controllo che i nuovi campi del TimelineElement siano presenti chiamando la versione v24 dell'API e assenti chiamando la v23
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<timeline_element>"
    When si invoca l'api B2B versione "<version>" per ottenere gli elementi di timeline di tale notifica
    Then gli elementi di timeline restituiti da B2B contengono i campi attesi in accordo alla versione "<version>"
    Examples:
      | timeline_element | version |
      | REQUEST_ACCEPTED | V24     |
      | REQUEST_ACCEPTED | V23     |
      #| NOTIFICATION_VIEWED                     | v24    |
      #| SEND_ANALOG_FEEDBACK                    | v24    |
      #| ANALOG_FAILURE_WORKFLOW                 | v24    |
      #| COMPLETELY_UNREACHABLE                  | v24    |
      #| REFINEMENT                              | v24    |
      #| SEND_ANALOG_PROGRESS                    | v24    |
      #| SCHEDULE_REFINEMENT                     | v24    |
      #| ANALOG_SUCCESS_WORKFLOW                 | v24    |
      #| COMPLETELY_UNREACHABLE_CREATION_REQUEST | v24    |
      #| NOTIFICATION_VIEWED                     | v23    |
      #| SEND_DIGITAL_FEEDBACK                   | v23    |
      #| SEND_ANALOG_FEEDBACK                    | v23    |
      #| ANALOG_FAILURE_WORKFLOW                 | v23    |
      #| COMPLETELY_UNREACHABLE                  | v23    |
      #| REFINEMENT                              | v23    |
      #| SEND_ANALOG_PROGRESS                    | v23    |
      #| SCHEDULE_REFINEMENT                     | v23    |
      #| ANALOG_SUCCESS_WORKFLOW                 | v23    |
      #| COMPLETELY_UNREACHABLE_CREATION_REQUEST | v23    |

  @webhook2 @cleanWebhook
  Scenario Outline: [TIMESTAMP_TECNICI_SLA_WEBHOOK] Controllo che i nuovi campi del TimelineElement siano presenti chiamando la versione v24 dell'API e assenti chiamando la v23
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "<version>"
    And viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "<version>"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "<version>"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<timeline_element>"
    When si invoca l'api Webhook versione "<version>" per ottenere gli elementi di timeline di tale notifica
    Then gli elementi di timeline restituiti dal Webhook contengono i campi attesi in accordo alla versione "<version>"
    Examples:
      | timeline_element | version |
      | REQUEST_ACCEPTED | V24     |
      | REQUEST_ACCEPTED | V23     |

  @webhook2 @cleanWebhook
  Scenario Outline: [TIMESTAMP_TECNICI_SLA_READ_STREAM_OK] Leggendo gli elementi di uno stream invocando un api con versione uguale a quella con cui è stato creato lo stream, la chiamata va a buon fine
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "<version>"
    And viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "<version>"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "<version>"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi dello stream versione "<version>"
    Then la chiamata restituisce correttamente lo stream di elementi timeline versione "<version>"
    Examples:
      | version |
      | V24     |
      | V23     |

  @webhook2 @cleanWebhook
  Scenario Outline: [TIMESTAMP_TECNICI_SLA_READ_STREAM_KO] Leggendo gli elementi di uno stream invocando un api con versione diversa da quella con cui è stato creato lo stream, si ottiene errore
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "<versionCreate>"
    And vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "<versionRead>"
    And viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "<versionCreate>"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con versione "<versionCreate>"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi dello stream versione "<versionRead>"
    Then la chiamata restituisce un errore <errorCode> riportante la dicitura "TODO"
    Examples:
      | versionCreate | versionRead | errorCode |
      | V24           | V23         | 400       |
      | V23           | V24         | 403       |
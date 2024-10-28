Feature: esposizione timestamp tecnici per gli SLA

  @timestampTecnici
  Scenario Outline: [TIMESTAMP_TECNICI_SLA_B2B_ANALOG] Controllo che i nuovi campi del TimelineElement siano presenti chiamando la versione v24 dell'API e assenti chiamando la v23
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<timeline_element>"
    When si invoca l'api B2B versione "<version>" per ottenere gli elementi di timeline di tale notifica
    Then gli elementi di timeline restituiti da B2B contengono i campi attesi in accordo alla versione "<version>"
    Examples:
      | timeline_element        | version |
      | ANALOG_SUCCESS_WORKFLOW | V24     |
      | ANALOG_SUCCESS_WORKFLOW | V23     |

  @timestampTecnici
  Scenario Outline: [TIMESTAMP_TECNICI_SLA_B2B_ANALOG_FAIL] Controllo che i nuovi campi del TimelineElement siano presenti chiamando la versione v24 dell'API e assenti chiamando la v23
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When si invoca l'api B2B versione "<version>" per ottenere gli elementi di timeline di tale notifica
    Then gli elementi di timeline restituiti da B2B contengono i campi attesi in accordo alla versione "<version>"
    Examples:
      | version |
      | V24     |
      | V23     |

  @timestampTecnici
  Scenario Outline: [TIMESTAMP_TECNICI_SLA_B2B_DIGITAL] Controllo che i nuovi campi del TimelineElement siano presenti chiamando la versione v24 dell'API e assenti chiamando la v23
    Given viene generata una nuova notifica
      | subject            | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo              |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<timeline_element>"
    When si invoca l'api B2B versione "<version>" per ottenere gli elementi di timeline di tale notifica
    Then gli elementi di timeline restituiti da B2B contengono i campi attesi in accordo alla versione "<version>"
    Examples:
      | timeline_element | version |
      | REQUEST_ACCEPTED | V24     |
      | REQUEST_ACCEPTED | V23     |

  @timestampTecnici @precondition @webhook2 @cleanWebhook
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

  @timestampTecnici @precondition @webhook2 @cleanWebhook
  Scenario: [TIMESTAMP_TECNICI_SLA_WEBHOOK_2] Controllo che in presenza di due stream (uno v23 e uno v24) richiamando la get con versione v23 i nuovi campi del TimelineElement non siano presenti
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V24"
    And vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V23"
    And viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test24" con eventType "TIMELINE" con versione "V24"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V24"
    And si predispone 1 nuovo stream denominato "stream-test23" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    When si invoca l'api Webhook versione "V23" per ottenere gli elementi di timeline di tale notifica
    Then gli elementi di timeline restituiti dal Webhook contengono i campi attesi in accordo alla versione "V23"

  @timestampTecnici @precondition @webhook2 @cleanWebhook
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

  @timestampTecnici @precondition @webhook2 @cleanWebhook
  Scenario Outline: [TIMESTAMP_TECNICI_SLA_READ_STREAM_KO] Leggendo gli elementi di uno stream invocando un api con versione diversa da quella con cui è stato creato lo stream, si ottiene errore
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V24"
    And vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V23"
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
    Then la chiamata restituisce un errore 403 riportante la dicitura "GENERIC_ERROR"
    Examples:
      | versionCreate | versionRead |
      | V24           | V23         |
      | V23           | V24         |
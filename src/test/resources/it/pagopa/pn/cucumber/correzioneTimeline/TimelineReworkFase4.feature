#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3124920415/SRS+Correzione+timeline+fase+4?atlOrigin=eyJpIjoiMTlhZWRjYTUwZTQ3NGY2ZTg2YzBjNmZhZmUxY2M0NjIiLCJwIjoiYyJ9
#PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3176693889/PST+-+Correzione+Timeline+-+FASE+4
Feature: Test relativi al SRS di correzione timeline fase 4

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_1] Tentativo di effettuare una correzione puntuale di ANALOG_FAILURE_WORKFLOW senza che sia presente un Altro elemento con categoria ANALOG_FAILURE_WORKFLOW o ANALOG_SUCCESS_WORKFLOW in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Galileo Galilei e:
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | ANALOG_FAILURE_WORKFLOW |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_2] Tentativo di effettuare una correzione puntuale inserendo nella request il timelineElementId di un elemento la cui category non è invalidabile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | ANALOG_SUCCESS_WORKFLOW |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_3] Tentativo di effettuare una correzione puntuale inserendo nella request i timelineElementId di due elemento le cui category non sono invalidabili
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | ANALOG_SUCCESS_WORKFLOW |
      | element2 | REFINEMENT              |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_4] Tentativo di effettuare una correzione puntuale inserendo nella request più timelineElementId di cui uno la cui category non è invalidabile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | PREPARE_ANALOG_DOMICILE |
      | element2 | REFINEMENT              |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_5] Tentativo di effettuare una correzione puntuale inserendo nella request un timelineElementId di un elemento la cui category è invalidabile, ma non risulta presente nella timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | id1 | SEND_ANALOG_PROGRESS.IUN_ZUKD-GRUX-NMYZ-202607-N-1.RECINDEX_0.ATTEMPT_0.IDX_1 |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_6] Tentativo di effettuare una correzione puntuale inserendo nella request IUN null
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | iun      | EMPTY_STRING         |
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_7] Tentativo di effettuare una correzione puntuale inserendo nella request IUN formalmente invalido
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | iun      | INVALID-IUN          |
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_8] Tentativo di effettuare una correzione puntuale inserendo nella request IUN inesistente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | iun      | TEST-INEX-ISTE-123456-Z-1 |
      | element1 | SEND_ANALOG_PROGRESS      |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 404

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_9] Tentativo di effettuare una correzione puntuale inserendo nella request recIndex null
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | recIndex | EMPTY_STRING         |
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_10] Tentativo di effettuare una correzione puntuale inserendo nella request recIndex inesistente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | recIndex | RECINDEX_2           |
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_11] Tentativo di effettuare una correzione puntuale mentre è in atto un'altro rework
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And viene invocata una richiesta di restart per la notifica appena creata
    And si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 409

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_12] Tentativo di effettuare una correzione puntuale senza che la notifica sia in effective date
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_13] Tentativo di effettuare una correzione puntuale di PREPARE_ANALOG_DOMICILE con notifica andata in KO all'attempt0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Galileo Galilei e:
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | PREPARE_ANALOG_DOMICILE;ATTEMPT_1 |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_14] Tentativo di effettuare una correzione puntuale di PREPARE_ANALOG_DOMICILE_FAILURE con notifica andata in KO all'attempt0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Galileo Galilei e:
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | PREPARE_ANALOG_DOMICILE_FAILURE |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_15] Tentativo di effettuare una correzione puntuale di COMPLETELY_UNREACHABLE con notifica andata in KO all'attempt0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Galileo Galilei e:
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | COMPLETELY_UNREACHABLE |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_16] Tentativo di effettuare una correzione puntuale di COMPLETELY_UNREACHABLE_CREATION_REQUEST con notifica andata in KO all'attempt0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Galileo Galilei e:
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | COMPLETELY_UNREACHABLE_CREATION_REQUEST |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_17] Tentativo di effettuare una correzione puntuale inserendo nella request timelineElementId relativi a un destinatario diverso da quello del recIndex
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | recIndex | RECINDEX_1                      |
      | element1 | SEND_ANALOG_PROGRESS;RECINDEX_0 |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_18] Tentativo di effettuare una correzione puntuale inserendo nella request SEND_ANALOG_PROGRESS + un'altro elemento
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | PREPARE_ANALOG_DOMICILE |
      | element2 | SEND_ANALOG_PROGRESS    |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_19] Tentativo di effettuare una correzione puntuale di un elemento già corretto in precedenza
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "DONE" entro 300 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    When viene ripetuta la richiesta di invalidazione precedente
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_20] Tentativo di effettuare una correzione puntuale passando una lista di timelineElementIds vuota
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Galileo Galilei e:
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | recIndex | RECINDEX_0 |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_KO_21] Tentativo di effettuare una correzione puntuale degli elementi di visualizzazione dopo averla visualizzata entro 120 giorni
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Galileo Galilei e:
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And "Galileo Galilei" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED_CREATION_REQUEST"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | NOTIFICATION_VIEWED                  |
      | element2 | NOTIFICATION_VIEWED_CREATION_REQUEST |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_INVALIDATION_CHECK_FULL_SENT_NOTIFICATION] Correzione puntuale di un elemento di timeline e verifica della presenza (o meno) del NOTIFICATION_TIMELINE_REWORKED nelle fullSentNotification con le varie versioni
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"
    And recuperando la fullSentNotification con la versione b2b "V23" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"
    And recuperando la fullSentNotification con la versione b2b "V2" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"

  @timelineReworkF4 @cleanWebhook @precondition @webhookV29
  Scenario: [TR4_INVALIDATION_CHECK_WEBHOOK] Correzione puntuale di un elemento di timeline e verifica della presenza (o meno) del NOTIFICATION_TIMELINE_REWORKED negli stream con le varie versioni
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And si predispone 1 nuovo stream denominato "stream-testLast" con eventType "TIMELINE" con versione "più recente"
    And si predispone 1 nuovo stream denominato "stream-testV28" con eventType "TIMELINE" con versione "V28"
    And si predispone 1 nuovo stream denominato "stream-testV25" con eventType "TIMELINE" con versione "V25"
    And si predispone 1 nuovo stream denominato "stream-testV23" con eventType "TIMELINE" con versione "V23"
    And si predispone 1 nuovo stream denominato "stream-testV10" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "più recente"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V28"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V25"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "più recente"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V28"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V25"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si invoca l'api Webhook versione "più recente" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V28" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V25" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V23" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V10" per ottenere gli elementi di timeline di tale notifica
    Then la category "NOTIFICATION_TIMELINE_REWORKED" è presente in almeno un elemento di timeline restituito dalla consumeStream con versione "più recente"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V28"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V25"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V23"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V10"

  @timelineReworkF4 @visualizzazioneNotifica
  Scenario: [VISUALIZZAZIONE_POST_120_GG] In caso di notifica visualizzata dopo più di 120 giorni, la visualizzazione non deve produrre gli elementi di timeline di visualizzazione, nè la relativa attestazione opponibile
    Given "Comune_Multi" recupera lato web PA una notifica monodestinatario in stato "EFFECTIVE_DATE" inviata tra 200 e 120 giorni fa con destinatario Mario Gherkin senza allegati disponibili
    When "Mario Gherkin" legge la notifica ricevuta
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED_CREATION_REQUEST" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  @timelineReworkF4 @visualizzazioneNotifica
  Scenario: [VISUALIZZAZIONE_POST_120_GG_DECEDUTO] In caso di notifica visualizzata dopo più di 120 giorni, la visualizzazione non deve produrre gli elementi di timeline di visualizzazione, nè la relativa attestazione opponibile
    Given "Comune_Multi" recupera lato web PA una notifica monodestinatario in stato "RETURNED_TO_SENDER" inviata tra 200 e 120 giorni fa con destinatario Mario Gherkin senza allegati disponibili
    When "Mario Gherkin" legge la notifica ricevuta
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED_CREATION_REQUEST" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  @timelineReworkF4 @visualizzazioneNotifica @visualizzazioneNotificaFeatureFlagOff
  Scenario: [VISUALIZZAZIONE_ENTRO_120_GG_ATTESTAZIONE_OPPONIBILE] In caso di notifica perfezionata visualizzata entro 120 giorni, la visualizzazione produce gli elementi di timeline di visualizzazione e la relativa attestazione opponibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @timelineReworkF4 @visualizzazioneNotifica @visualizzazioneNotificaFeatureFlagOff
  Scenario: [VISUALIZZAZIONE_ENTRO_120_GG_ATTESTAZIONE_OPPONIBILE_DECEDUTO] In caso di notifica in stato deceduto visualizzata entro 120 giorni, la visualizzazione produce gli elementi di timeline di visualizzazione e la relativa attestazione opponibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    When "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @visualizzazioneNotificaFeatureFlagOff
  Scenario: [VISUALIZZAZIONE_POST_120_GG_FF_OFF] In caso di notifica visualizzata dopo più di 120 giorni, la visualizzazione non deve produrre gli elementi di timeline di visualizzazione, nè la relativa attestazione opponibile
    Given "Comune_Multi" recupera lato web PA una notifica monodestinatario in stato "EFFECTIVE_DATE" inviata tra 200 e 120 giorni fa con destinatario Mario Gherkin senza allegati disponibili
    When "Mario Gherkin" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED_CREATION_REQUEST"
    And "Mario Gherkin" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @visualizzazioneNotificaFeatureFlagOff
  Scenario: [VISUALIZZAZIONE_POST_120_GG_DECEDUTO_FF_OFF] In caso di notifica visualizzata dopo più di 120 giorni, la visualizzazione non deve produrre gli elementi di timeline di visualizzazione, nè la relativa attestazione opponibile
    Given "Comune_Multi" recupera lato web PA una notifica monodestinatario in stato "RETURNED_TO_SENDER" inviata tra 200 e 120 giorni fa con destinatario Mario Gherkin senza allegati disponibili
    When "Mario Gherkin" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED_CREATION_REQUEST"
    And "Mario Gherkin" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

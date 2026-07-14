#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3124920415/SRS+Correzione+timeline+fase+4?atlOrigin=eyJpIjoiMTlhZWRjYTUwZTQ3NGY2ZTg2YzBjNmZhZmUxY2M0NjIiLCJwIjoiYyJ9
#PST:
Feature: Test relativi al SRS di correzione timeline fase 4

  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_1] Tentativo di effettuare una correzione puntuale senza che la notifica sia andata in EFFECTIVE_DATE
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
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_2] Tentativo di effettuare una correzione puntuale inserendo nella request il timelineElementId di un elemento la cui category non è invalidabile
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
      | element1 | REFINEMENT |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_3] Tentativo di effettuare una correzione puntuale inserendo nella request i timelineElementId di due elemento le cui category non sono invalidabili
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
      | element1 | REFINEMENT          |
      | element2 | SCHEDULE_REFINEMENT |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_4] Tentativo di effettuare una correzione puntuale inserendo nella request più timelineElementId di cui uno la cui category non è invalidabile
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
      | element2 | REFINEMENT           |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  #TODO: capire come fare, singolo elemento invalidabile, ma non presente nella timeline
  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_5] Tentativo di effettuare una correzione puntuale inserendo nella request un timelineElementId di un elemento la cui category è invalidabile, ma non risulta presente nella timeline
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
      | element1 | REFINEMENT |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi

  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_6] Tentativo di effettuare una correzione puntuale inserendo nella request IUN null
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
  Scenario: [TR4_REMOVE_KO_7] Tentativo di effettuare una correzione puntuale inserendo nella request IUN formalmente invalido
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
  Scenario: [TR4_REMOVE_KO_8] Tentativo di effettuare una correzione puntuale inserendo nella request IUN inesistente
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
  Scenario: [TR4_REMOVE_KO_9] Tentativo di effettuare una correzione puntuale inserendo nella request recIndex null
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

  #TODO: capire come fare, recIndex inesistente
  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_10] Tentativo di effettuare una correzione puntuale inserendo nella request recIndex inesistente
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
  Scenario: [TR4_REMOVE_KO_11] Tentativo di effettuare una correzione puntuale mentre è in atto un'altro rework
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
    And si verifica che la richiesta di restart effettuata sia in stato "IN_PROGRESS" entro 300 secondi controllando ogni 5 secondi
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 409

  @timelineReworkF4
  Scenario: [TR4_REMOVE_KO_12] Tentativo di effettuare una correzione puntuale senza che la notifica sia in effective date
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri
      | element1 | SEND_ANALOG_PROGRESS |
    Then si verifica che la richiesta di remove effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di remove effettuata sia in stato "ERROR" entro 300 secondi controllando ogni 5 secondi
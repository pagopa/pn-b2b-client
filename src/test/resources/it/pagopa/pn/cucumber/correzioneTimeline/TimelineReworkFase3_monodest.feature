Feature: Correzione timeline fase 3
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  @timelineReworkF3 @checkRestart #6.1, 6.9
  Scenario Outline: [TR3_RESTART_MONODEST_ATTEMPT0_OK_RESTART_ATTEMPT0_KO] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in OK, il restart va in KO all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in OK, andrà in KO al nuovo attempt 0
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
#      | payment_pagoPaForm      | NOALLEGATO |
#      | apply_cost_pagopa       | SI         |
#      | payment_multy_number    | 1          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" al tentativo "REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart #6.2, 6.10
  Scenario Outline: [TR3_RESTART_MONODEST_ATTEMPT0_OK_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in OK, il restart va in OK all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart #6.3, 6.11
  Scenario Outline: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_OK_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in KO e attempt 1 in OK, il restart va in OK all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in OK, andrà in ok al nuovo attempt 0
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart #6.4, 6.12
  Scenario Outline: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_KO_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 e 1 in KO, il restart va in OK all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in KO, andrà in OK al nuovo attempt 0
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart #6.5
  Scenario Outline: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_KO_RESTART_ATTEMPT0_OK_2] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 e 1 in KO, il restart va in OK all'attempt 1
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in KO, andrà in OK al nuovo attempt 1
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart #6.6
  Scenario Outline: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_OK_RESTART_ATTEMPT1_KO] Viene effettuata un'operazione di restart dell'attempt 1 per una notifica mono-destinatario con attempt 0 in KO e 1 in OK, il restart va in KO all'attempt 1
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in OK, andrà in KO al nuovo attempt 1
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | digitalDomicile         | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart #6.7
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_OK_RESTART_ATTEMPT1_DECEASED] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in KO e 1 in OK, il restart va in returned to sender all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in OK, andrà in returned to sender al nuovo attempt 0
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | digitalDomicile         | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart #6.8
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_DECESED_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in KO e 1 in OK, il restart va in returned to sender all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in DECEDUTO, andrà in OK al nuovo attempt 0
      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
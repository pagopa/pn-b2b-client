Feature: avanzamento b2b notifica analogico difgitale

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


   #Scenario di Test Fix Simone
 # 1	monodestinatario PF -> insuccesso e verifica nuovo WF (con controllo date perfezionamento per decorrenza termini)
  @workflowDigitale
  Scenario: [B2B_TIMELINE_FIX_7179_1] Notifica mono destinatario con workflow digitale fallito - Destinatario PF
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST"
    And verifica generazione Atto opponibile senza la messa a disposizione in "DIGITAL_DELIVERY_CREATION_REQUEST"
    #In ambiente di produzione l'incremento è di 15"d" (15 giorni Failure e 7 giorni per Success) al momento 3"m" (3 minuti Failure e 1 minuto per Success)
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "SCHEDULE_REFINEMENT_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "DIGITAL_FAILURE_WORKFLOW"
    And la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"
 # 2	monodestinatario PG -> insuccesso e verifica nuovo WF  (con controllo date perfezionamento per decorrenza termini)
  #TODO Repererire una PG per cui fallisce l'invio digitale

  @dev @mockNR
  Scenario: [B2B_TIMELINE_FIX_7179_2] Notifica mono destinatario con workflow digitale fallito - Destinatario PG
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    #And destinatario Cucumber Society
    And destinatario Gherkin Analogic e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST"
    And verifica generazione Atto opponibile senza la messa a disposizione in "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"

      # 3	monodestinatario PF -> successo e verifica nuovo WF (con controllo date perfezionamento per decorrenza termini)
  @workflowDigitale
  Scenario: [B2B_TIMELINE_FIX_7179_3] Notifica mono destinatario con workflow digitale completato con successo - Destinatario PF
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    And verifica generazione Atto opponibile senza la messa a disposizione in "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "SCHEDULE_REFINEMENT_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    #And vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

 # 4	monodestinatario PG -> successo e verifica nuovo WF  (con controllo date perfezionamento per decorrenza termini)
  @workflowDigitale
  Scenario: [B2B_TIMELINE_FIX_7179_4] Notifica mono destinatario con workflow digitale completato con successo - Destinatario PG
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    And verifica generazione Atto opponibile senza la messa a disposizione in "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "SCHEDULE_REFINEMENT_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    #And vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"


 # 5 monodestinatario PF -> insuccesso e verifica nuovo WF (con controllo date perfezionamento per decorrenza termini) --> caso unreachable
  @dev
  Scenario: [B2B_TIMELINE_FIX_7179_5] Notifica analogica mono destinatario con destinatario irreperibile - Destinatario PF
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate più 0 per il destinatario 0
    And vengono letti gli eventi fino allo stato della notifica "COMPLETELY_UNREACHABLE"
    And la PA richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"

     # 6	multidestinatario -> insuccesso e verifica nuovo WF (con controllo date perfezionamento per decorrenza termini) --> caso completed per tutti i destinatari
  @workflowDigitale
  Scenario: [B2B_TIMELINE_FIX_7179_6] Notifica multi destinatario con workflow digitale fallito per tutti i destinatari
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test1@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    Then esiste l'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    And esiste l'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 1 rispetto ell'evento in timeline "DIGITAL_DELIVERY_CREATION_REQUEST"
    And esiste l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And esiste l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 1
    And esiste l'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" per l'utente 0
    And esiste l'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" per l'utente 1

           # 7	multidestinatario -> insuccesso e verifica nuovo WF (con controllo date perfezionamento per decorrenza termini) --> caso completed per 1 dei due destinatari e verifica stato complessivo notifica
  @workflowDigitale
  Scenario: [B2B_TIMELINE_FIX_7179_7] Notifica multi destinatario con workflow digitale fallito per un destinatario
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 1 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi e verifico che l'utente 1 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" per l'utente 0
    #Ritardare Il Perfezionamento per lo stato DELIVERED
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"




 # 8	multidestinatario -> insuccesso e verifica nuovo WF (con controllo date perfezionamento per decorrenza termini) --> caso completely unreachable
  @dev
  Scenario: [B2B_TIMELINE_FIX_7179_8] Notifica multi destinatario con workflow analogico con destinatari irreperibili
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi e verifico che l'utente 1 non abbia associato un evento "COMPLETELY_UNREACHABLE"
    #TODO Controllare non arriva lo stato COMPLETELY_UNREACHABLE arriva   ACCEPTED-DELIVERING-EFFECTIVE_DATE
    #Ritardare Il Perfezionamento per lo stato DELIVERED
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_FIX_7179_8_1] Notifica multi destinatario con workflow analogico con un destinatario irreperibile
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Cucumber Society
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "DIGITAL_DELIVERY_CREATION_REQUEST"
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "SCHEDULE_REFINEMENT_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 1
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_FIX_7179_8_2] Notifica multi destinatario con workflow analogico con un destinatario irreperibile
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Cucumber Society
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING" per il destinatario 0 e presente l'evento "DIGITAL_DELIVERY_CREATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING" per il destinatario 0 e presente l'evento "SCHEDULE_REFINEMENT_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING" per il destinatario 0 e presente l'evento "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 1
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"


 # 9	multidestinatario -> successo e verifica nuovo WF (con controllo date perfezionamento per decorrenza termini) --> caso completed per tutti i destinatari
  @workflowDigitale @mockPec
  Scenario: [B2B_TIMELINE_FIX_7179_9] Notifica multi destinatario PG PF con workflow digitale completato con successo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Gherkin Analogic e:
      | digitalDomicile_address | test@OK-pecSuccess.it |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test1@OK-pecSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 1
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 1 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1

  @workflowDigitale @mockPec
  Scenario: [B2B_TIMELINE_FIX_7179_10] Notifica multi destinatario PF PF con workflow digitale completato con successo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@OK-pecSuccess.it |
    And destinatario
      | taxId                   | GLLGLL64B15G702I    |
      | digitalDomicile_address | test1@OK-pecSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 1
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 1 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1



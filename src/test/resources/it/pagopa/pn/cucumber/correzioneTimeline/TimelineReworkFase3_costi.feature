Feature: Correzione timeline fase 3 costi
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  @timelineReworkF3 @checkCostiRestart
  Scenario Outline: [TR3_11_1_2]
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@ok_890              |
      | physicalAddress_municipality | LAGO PATRIA             |
      | physicalAddress_zip          | 80014                   |
      | physicalAddress_province     | NA                      |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_async_sada0 |
      | apply_cost_pagopa            | SI                      |
      | payment_multy_number         | <payments>              |
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 0 per il suo pagamento alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    And viene verificato che l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    Examples:
      | payments |
      | 1        |
      | 2        |

    #11.3 ??? attemptId = ATTEMPT_0 in KO per destinatario deceduto

    #11.4 FLAT_RATE, SYNC, N combinazioni -> dopo restart il baseCost non cambia

    #11.5 FLAT_RATE, ASYNC, N combinazioni -> dopo restart il baseCost non cambia

    #11.6 DELIVERY_MODE, SYNC, N combinazioni -> dopo restart il baseCost non cambia, costi supplementari si

    #11.7 DELIVERY_MODE, ASYNC, applyCost TRUE N combinazioni -> dopo restart
    # il baseCost cambia
    # firstAnalogCost immutato
    #


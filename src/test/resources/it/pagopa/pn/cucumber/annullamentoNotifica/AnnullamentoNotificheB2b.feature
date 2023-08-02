Feature: annullamento notifiche b2b

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_1] PA mittente: Annullamento notifica in stato “depositata”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica


  Scenario: [B2B-PA-ANNULLAMENTO_2] PA mittente: annullamento notifica in stato “invio in corso”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica

  Scenario: [B2B-PA-ANNULLAMENTO_3] PA mittente: annullamento notifica in stato “consegnata”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica

  Scenario: [B2B-PA-ANNULLAMENTO_4] PA mittente: annullamento notifica in stato “perfezionata per decorrenza termini”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica

  Scenario: B2B-PA-ANNULLAMENTO_5] PA mittente: annullamento notifica in stato “irreperibile totale”
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica


  Scenario: B2B-PA-ANNULLAMENTO_6] PA mittente: annullamento notifica in stato “avvenuto accesso”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica

    #Da Verificare...............
  #Scenario: [B2B-PA-ANNULLAMENTO_7] PA mittente: annullamento notifica in fase di validazione [TA]

  Scenario: [B2B-PA-ANNULLAMENTO_8] PA mittente: annullamento notifica con pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica

        #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_9] PA mittente: notifica con pagamento in stato “Annullata” - presenza box di pagamento

  Scenario: [B2B-PA-ANNULLAMENTO_10] PA mittente: dettaglio notifica annullata - download allegati (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente

  Scenario: [B2B-PA-ANNULLAMENTO_11] PA mittente: dettaglio notifica annullata - download bollettini di pagamento (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato il costo = "100" della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


  Scenario: [B2B-PA-ANNULLAMENTO_12] PA mittente: dettaglio notifica annullata - download allegati (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
    And download attestazione opponibile AAR
    Then il download si conclude correttamente

  Scenario: [B2B-PA-ANNULLAMENTO_13] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
    And download attestazione opponibile AAR
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

        #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_14] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline

          #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_15] AuditLog: verifica presenza evento post annullamento notifica

  #LATO DESTINATARIO-----------------------------------

  Scenario: [B2B-PA-ANNULLAMENTO_16] Destinatario: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
       #Specificare il Destinatario
    When viene richiesto il download del documento "NOTIFICA"
    Then l'operazione ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-ANNULLAMENTO_17] Destinatario: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato il costo = "100" della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
    #Specificare il Destinatario
    When viene richiesto il download del documento "PAGOPA"
    Then l'operazione ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-ANNULLAMENTO_18] Destinatario: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
    And download attestazione opponibile AAR
    Then l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-ANNULLAMENTO_19] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And si verifica il corretto annullamento della notifica
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "400"

            #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_20] Destinatario: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)

              #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_21] Destinatario: dettaglio notifica annullata - verifica presenza elemento di timeline


                #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_22] Annullamento notifica con pagamento: verifica cancellazione IUV da tabella pn-NotificationsCost

                  #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_23]
  #PA mittente: notifica con pagamento in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]

                    #Da Verificare...............Solo Manuale
  #Scenario:  [B2B-PA-ANNULLAMENTO_24]
  #PA mittente: visualizzazione dettaglio notifica annullata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

                      #Da Verificare...............Solo Manuale
  #Scenario:  [B2B-PA-ANNULLAMENTO_25]
  #Destinatario: visualizzazione dettaglio notifica annullata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

                        #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_26]
  #PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato


  #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_27]
  #PA mittente: annullamento notifica durante invio sms di cortesia

    #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_28]
  #PA mittente: annullamento notifica durante invio mail di cortesia

      #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_29]
  #PA mittente: annullamento notifica durante invio pec

        #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_30]
  #PA mittente: annullamento notifica durante pagamento da parte del destinatario

          #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_31]
  #Accesso alla tabella pn-TimelinesForInvoicing, popolata da pn-progression-sensor, per verifica fatturazione dei costi contestualmente all’annullamento di una notifica analogica

          #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_32]
  #Accesso alla tabella pn-TimelinesForInvoicing, popolata da pn-progression-sensor, per verifica fatturazione dei costi contestualmente all’annullamento di una notifica digitale
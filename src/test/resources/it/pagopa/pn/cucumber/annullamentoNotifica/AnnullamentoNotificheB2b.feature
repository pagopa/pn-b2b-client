Feature: annullamento notifiche b2b


  #ANNULLAMENTO LATO PA----------------------------------->>
  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_1] PA mittente: Annullamento notifica in stato “depositata”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
   # And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
   # When la notifica può essere annullata dal sistema tramite codice IUN
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_2] PA mittente: annullamento notifica in stato “invio in corso”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_3] PA mittente: annullamento notifica in stato “consegnata”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_4] PA mittente: annullamento notifica in stato “perfezionata per decorrenza termini”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_5] PA mittente: annullamento notifica in stato “irreperibile totale”
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di MILANO |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | MNTMRA03M71C615V |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: B2B-PA-ANNULLAMENTO_6] PA mittente: annullamento notifica in stato “avvenuto accesso”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento @ignore #Modificare il Test in Validation la notifica non può essere annullata bisogna vericare un errore.....
  Scenario: [B2B-PA-ANNULLAMENTO_7] PA mittente: annullamento notifica in fase di validazione [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "IN_VALIDATION"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_8] PA mittente: annullamento notifica con dati pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato il costo = "100" della notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_8_1] PA mittente: annullamento notifica con pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_8_2] PA mittente: annullamento notifica con dati pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And viene verificato il costo = "100" della notifica con un errore "404"


  @Annullamento @ignore
  Scenario:  [B2B-PA-ANNULLAMENTO_9] PA mittente: notifica con pagamento in stato “Annullata” - presenza box di pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_10] PA mittente: dettaglio notifica annullata - download allegati (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_11] PA mittente: dettaglio notifica annullata - download bollettini di pagamento (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato il costo = "100" della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_12] PA mittente: dettaglio notifica annullata - download AAR (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then download attestazione opponibile AAR

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_13] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And download attestazione opponibile AAR
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_13_1] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_13_2] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_13_3] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_13_4] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"

  @Annullamento @workflowAnalogico @ignore
  Scenario: [B2B-PA-ANNULLAMENTO_13_5] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS"

  @Annullamento @workflowAnalogico
  Scenario: [B2B-PA-ANNULLAMENTO_13_6] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenari positivi)
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_13_7] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And  vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

    #SENDER_ACK - RECIPIENT_ACCESS - PEC_RECEIPT - DIGITAL_DELIVERY - DIGITAL_DELIVERY_FAILURE - SEND_ANALOG_PROGRESS - COMPLETELY_UNREACHABLE

  @Annullamento
  Scenario:  [B2B-PA-ANNULLAMENTO_14] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"

  @Annullamento
  Scenario:  [B2B-PA-ANNULLAMENTO_14_1] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLATION_REQUEST
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"

  @Annullamento
  Scenario Outline: [B2B-PA-ANNULLAMENTO_15] AuditLog: verifica presenza evento post annullamento notifica
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log              |
      | AUD_NT_CANCELLED       |

  #ANNULLAMENTO LATO DESTINATARIO----------------------------------->>

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_16] Destinatario PF: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When il documento notificato non può essere correttamente recuperato da "Mario Cucumber"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_16_1] Destinatario  PF: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    When il documento notificato non può essere correttamente recuperato da "Mario Cucumber"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_16_2] Destinatario  PF: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When il documento notificato non può essere correttamente recuperato da "Mario Cucumber"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_17] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_17_1] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_17_2] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_18] Destinatario PF: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When download attestazione opponibile AAR da parte "Mario Cucumber"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_18_1] Destinatario PF: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    When download attestazione opponibile AAR da parte "Mario Cucumber"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_18_2] Destinatario PF: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" e successivamente annullata
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When download attestazione opponibile AAR da parte "Mario Cucumber"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK" con errore "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19_1] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK" con errore "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19_2] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED" e successivamente annullata
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then "Mario Cucumber" richiede il download dell'attestazione opponibile "SENDER_ACK" con errore "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19_3] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK" con errore "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19_4] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "PEC_RECEIPT" con errore "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19_5] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY" con errore "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19_6] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE" con errore "404"

  @Annullamento @workflowAnalogico @ignore
  Scenario: [B2B-PF-ANNULLAMENTO_19_7] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | MNTMRA03M71C615V |
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@fail_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento  @workflowAnalogico  @ignore
  Scenario: [B2B-PF-ANNULLAMENTO_19_8] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | MNTMRA03M71C615V |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_19_9] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED" e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK" con errore "404"

    #SENDER_ACK - RECIPIENT_ACCESS - PEC_RECEIPT - DIGITAL_DELIVERY - DIGITAL_DELIVERY_FAILURE - SEND_ANALOG_PROGRESS - COMPLETELY_UNREACHABLE

  @Annullamento
  Scenario:  [B2B-PF-ANNULLAMENTO_20] Destinatario PF: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"

  @Annullamento
  Scenario:  [B2B-PF-ANNULLAMENTO_20_1] Destinatario PF: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"

  @Annullamento
  Scenario:  [B2B-PF-ANNULLAMENTO_20_2] Destinatario PF: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @Annullamento
  Scenario:  [B2B-PF-ANNULLAMENTO_21] Destinatario PF: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"

  @Annullamento             #Da Verificare...............
  Scenario:  [B2B-PA-ANNULLAMENTO_22] Annullamento notifica con pagamento: verifica cancellazione IUV da tabella pn-NotificationsCost
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #When la notifica con pagamento può essere annullata dal sistema tramite codice IUV
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    #Then si verifica la coretta cancellazione da tabella pn-NotificationsCost

  @Annullamento #Da Verificare...............
  Scenario:  [B2B-PA-ANNULLAMENTO_23] PA mittente: notifica con pagamento in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #And si verifica la coretta cancellazione da tabella pn-NotificationsCost
    When viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    Then la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED


  @Annullamento @ignore #Conflitto 409 Conflict
  Scenario:  [B2B-PA-ANNULLAMENTO_23_1] PA mittente: notifica con pagamento non in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    Then la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED


                    #Da Verificare...............Solo Manuale
  #Scenario:  [B2B-PA-ANNULLAMENTO_24]
  #PA mittente: visualizzazione dettaglio notifica annullata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

                      #Da Verificare...............Solo Manuale
  #Scenario:  [B2B-PA-ANNULLAMENTO_25]
  #Destinatario: visualizzazione dettaglio notifica annullata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

  @Annullamento #DOPO L'AANUULLAMENTO NON E POSSIBILE VERIFICARE LATO TA L'AVVENUTA RICEZIONE DEL SMS
  Scenario:  [B2B-PA-ANNULLAMENTO_27] PA mittente: annullamento notifica durante invio sms di cortesia
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Louis Armstrong |
      | taxId | RMSLSO31M04Z404R |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "+393214210000", "type": "SMS"} |
      | details_recIndex | 0 |
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #Da verificare la corretta consegna del messaggio di cortesia

  @Annullamento @ignore
  Scenario:  [B2B-PA-ANNULLAMENTO_27_1] PA mittente: annullamento notifica inibizione invio sms di cortesia
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Louis Armstrong |
      | taxId | RMSLSO31M04Z404R |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
   # Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "+393214210000", "type": "SMS"} |
      | details_recIndex | 0 |

  @Annullamento  #DOPO L'AANUULLAMENTO NON E POSSIBILE VERIFICARE LATO TA L'AVVENUTA RICEZIONE DEL email
  Scenario:  [B2B-PA-ANNULLAMENTO_28] PA mittente: annullamento notifica durante invio mail di cortesia
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
   # And si verifica la corretta acquisizione della notifica
        #Valutare lo step
    #And vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_COURTESY_MESSAGE"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex | 0 |
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #Da verificare la corretta consegna del messaggio di cortesia

  @Annullamento @ignore
  Scenario:  [B2B-PA-ANNULLAMENTO_28_1] PA mittente: annullamento notifica inibizione invio mail di cortesia
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #Valutare lo step
    #And vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_COURTESY_MESSAGE"
    Then viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" non esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex | 0 |

  @Annullamento   #Da Verificare...............OK OPPURE UN kO CHE NON SIA DOVUTO ALL'ANNULLAMENTO  DOPO L'ANNULLAMENTO DOVREBBE ESSERE INIBITO
  Scenario:  [B2B-PA-ANNULLAMENTO_29] #PA mittente: annullamento notifica durante invio pec
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK"

  @Annullamento @ignore
  Scenario:  [B2B-PA-ANNULLAMENTO_29_1] #PA mittente: annullamento notifica inibizione invio pec
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
        #Valutare lo step
    #And vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_DIGITAL_PROGRESS"
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @Annullamento     #Da Verificare...............
  Scenario:  [B2B-PA-ANNULLAMENTO_30] PA mittente: annullamento notifica durante pagamento da parte del destinatario
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
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then si attende il corretto pagamento della notifica

          #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_31]
  #Accesso alla tabella pn-TimelinesForInvoicing, popolata da pn-progression-sensor, per verifica fatturazione dei costi contestualmente all’annullamento di una notifica analogica

          #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_32]
  #Accesso alla tabella pn-TimelinesForInvoicing, popolata da pn-progression-sensor, per verifica fatturazione dei costi contestualmente all’annullamento di una notifica digitale

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_31] PA mittente: Annullamento notifica in stato “CANCELLED”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la notifica non può essere annullata dal sistema tramite codice IUN più volte



  @Annullamento @ignore
  Scenario: [B2B-PF-MULTI-ANNULLAMENTO_1] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"
    And "Mario Gherkin" tenta il recupero dell'allegato "PAGOPA"
    And il download ha prodotto un errore con status code "404"


  @Annullamento @webhook1
  Scenario: [B2B-STREAM_TIMELINE_24] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given vengono cancellati tutti gli stream presenti del "Comune_1"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream V2 denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOTIFICATION_CANCELLATION_REQUEST"

  @Annullamento @webhook1
  Scenario: [B2B-STREAM_TIMELINE_25] Invio notifica digitale ed attesa stato CANCELLED stream v2_scenario positivo
    Given vengono cancellati tutti gli stream presenti del "Comune_1"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream V2 denominato "stream-test" con eventType "STATUS"
    And si crea il nuovo stream per il "Comune_1"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "CANCELLED"


  @Annullamento
  Scenario:  [B2B-PA-ANNULLAMENTO_26] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And viene verificato che nell'elemento di timeline della notifica "NOTIFICATION_CANCELLED" sia presente il campo notRefinedRecipientIndex

  @Annullamento
  Scenario: [B2B-PA-ANNULLAMENTO_27] PA mittente: Annullamento notifica e inibizione invio SEND_SIMPLE_REGISTERED_LETTER
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
   # And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
   # When la notifica può essere annullata dal sistema tramite codice IUN
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" non esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |


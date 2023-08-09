Feature: annullamento notifiche b2b

  #ANNULLAMENTO LATO PA----------------------------------->>
  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_1] PA mittente: Annullamento notifica in stato “depositata”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_2] PA mittente: annullamento notifica in stato “invio in corso”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_3] PA mittente: annullamento notifica in stato “consegnata”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_4] PA mittente: annullamento notifica in stato “perfezionata per decorrenza termini”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then si verifica il corretto annullamento della notifica
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @B2Btest
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
    And vengono letti gli eventi fino allo stato della notifica "COMPLETELY_UNREACHABLE"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @B2Btest
  Scenario: B2B-PA-ANNULLAMENTO_6] PA mittente: annullamento notifica in stato “avvenuto accesso”
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @B2Btest
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

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_8] PA mittente: annullamento notifica con pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @B2Btest
  Scenario:  [B2B-PA-ANNULLAMENTO_9] PA mittente: notifica con pagamento in stato “Annullata” - presenza box di pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    #Rispondere in maniera sincrona al chiamante (204 richiesta accettata)
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #Inserisce in timeline il nuovo elemento NOTIFICATION_CANCELLATION_REQUEST
    # (che non porta lo stato della notifica in CANCELLED) indica l'inizio delle operazione di annullamento della notifica.
    # Da questo momento in poi verranno bloccate tutte le operazioni, dunque il workflow per la notifica in questione.


  @B2Btest
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

  @B2Btest
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

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_12] PA mittente: dettaglio notifica annullata - download AAR (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And download attestazione opponibile AAR
    Then il download si conclude correttamente

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And download attestazione opponibile AAR
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13_1] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13_2] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13_3] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13_4] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13_5] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13_6] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"

  @B2Btest
  Scenario: [B2B-PA-ANNULLAMENTO_13_7] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

    #SENDER_ACK - RECIPIENT_ACCESS - PEC_RECEIPT - DIGITAL_DELIVERY - DIGITAL_DELIVERY_FAILURE - SEND_ANALOG_PROGRESS - COMPLETELY_UNREACHABLE

  @B2Btest
  Scenario:  [B2B-PA-ANNULLAMENTO_14] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"

  @B2Btest
  Scenario:  [B2B-PA-ANNULLAMENTO_14_1] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLATION_REQUEST
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"

          #Da Verificare...............
  @B2Btest
  Scenario Outline: [B2B-PA-ANNULLAMENTO_15] AuditLog: verifica presenza evento post annullamento notifica
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then viene verificato che esiste un audit log "<audit-log>" in "5y"
    Examples:
      | audit-log              |
      | AUD_NT_CANCELLED       |


  #ANNULLAMENTO LATO DESTINATARIO----------------------------------->>

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_16] Destinatario: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #When viene richiesto il download del documento "NOTIFICA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Mario Cucumber" tenta il recupero dell'allegato "NOTIFICA"
    Then il download ha prodotto un errore con status code "404"

  Scenario: [B2B-PF-ANNULLAMENTO_16_1] Destinatario: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    #When viene richiesto il download del documento "NOTIFICA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Mario Cucumber" tenta il recupero dell'allegato "NOTIFICA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_16_2] Destinatario: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #When viene richiesto il download del documento "NOTIFICA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Mario Cucumber" tenta il recupero dell'allegato "NOTIFICA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_17] Destinatario: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #When viene richiesto il download del documento "PAGOPA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_17_1] Destinatario: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    #When viene richiesto il download del documento "PAGOPA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_17_2] Destinatario: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #When viene richiesto il download del documento "PAGOPA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_18] Destinatario: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And download attestazione opponibile AAR
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_18_1] Destinatario: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And download attestazione opponibile AAR
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_18_2] Destinatario: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And download attestazione opponibile AAR
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_1] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_2] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_3] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_4] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "PEC_RECEIPT"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_5] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_6] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_7] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_8] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PF-ANNULLAMENTO_19_9] Destinatario: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

    #SENDER_ACK - RECIPIENT_ACCESS - PEC_RECEIPT - DIGITAL_DELIVERY - DIGITAL_DELIVERY_FAILURE - SEND_ANALOG_PROGRESS - COMPLETELY_UNREACHABLE

  @B2Btest
  Scenario:  [B2B-PF-ANNULLAMENTO_20] Destinatario: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"

  @B2Btest
  Scenario:  [B2B-PF-ANNULLAMENTO_20_1] Destinatario: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"

  @B2Btest
  Scenario:  [B2B-PF-ANNULLAMENTO_20_2] Destinatario: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"


  @B2Btest
  Scenario:  [B2B-PF-ANNULLAMENTO_21] Destinatario: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"


  @B2Btest             #Da Verificare...............
  Scenario:  [B2B-PA-ANNULLAMENTO_22] Annullamento notifica con pagamento: verifica cancellazione IUV da tabella pn-NotificationsCost
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    #TODO manca la funzione.......................
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #When la notifica con pagamento può essere annullata dal sistema tramite codice IUV
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    #Then si verifica la coretta cancellazione da tabella pn-NotificationsCost

  @B2Btest #Da Verificare...............
  Scenario:  [B2B-PA-ANNULLAMENTO_23] PA mittente: notifica con pagamento in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #And la notifica con pagamento può essere annullata dal sistema tramite codice IUV
    #And si verifica la coretta cancellazione da tabella pn-NotificationsCost
    And viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della notifica

                    #Da Verificare...............Solo Manuale
  #Scenario:  [B2B-PA-ANNULLAMENTO_24]
  #PA mittente: visualizzazione dettaglio notifica annullata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

                      #Da Verificare...............Solo Manuale
  #Scenario:  [B2B-PA-ANNULLAMENTO_25]
  #Destinatario: visualizzazione dettaglio notifica annullata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

  @B2Btest                     #Da Verificare...............
  Scenario:  [B2B-PF-ANNULLAMENTO_26] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then la notifica non può essere correttamente recuperata da "Mario Cucumber"
    And  la notifica non può essere correttamente recuperata da "Mario Gherkin" con delega

  @B2Btest                     #Da Verificare...............
  Scenario:  [B2B-PF-ANNULLAMENTO_26_1] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la notifica non può essere correttamente recuperata da "Mario Cucumber"
    And  la notifica non può essere correttamente recuperata da "Mario Gherkin" con delega

  @B2Btest
  Scenario:  [B2B-PF-ANNULLAMENTO_26_2] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then la notifica può essere correttamente letta da "Mario Gherkin"
    And si tenta la lettura della notifica da parte del delegato "Mario Gherkin" che produce un errore con status code "404"

  @B2Btest
  Scenario:  [B2B-PF-ANNULLAMENTO_26_3] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la notifica può essere correttamente letta da "Mario Gherkin"
    And si tenta la lettura della notifica da parte del delegato "Mario Gherkin" che produce un errore con status code "404"

  @B2Btest #Da Verificare...............OK OPPURE UN kO CHE NON SIA DOVUTO ALL'ANNULLAMENTO DOPO L'ANNULLAMENTO DOVREBBE ESSERE INIBITO
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

  @B2Btest
  Scenario:  [B2B-PA-ANNULLAMENTO_27_1] PA mittente: annullamento notifica inibizione invio sms di cortesia
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Louis Armstrong |
      | taxId | RMSLSO31M04Z404R |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
   # Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" non esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "+393214210000", "type": "SMS"} |
      | details_recIndex | 0 |

  @B2Btest  #Da Verificare...............OK OPPURE UN kO CHE NON SIA DOVUTO ALL'ANNULLAMENTO  DOPO L'ANNULLAMENTO DOVREBBE ESSERE INIBITO
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
    And si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex | 0 |
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #Da verificare la corretta consegna del messaggio di cortesia

  @B2Btest
  Scenario:  [B2B-PA-ANNULLAMENTO_28_1] PA mittente: annullamento notifica inibizione invio mail di cortesia
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" non esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex | 0 |

  @B2Btest    #Da Verificare...............OK OPPURE UN kO CHE NON SIA DOVUTO ALL'ANNULLAMENTO  DOPO L'ANNULLAMENTO DOVREBBE ESSERE INIBITO
  Scenario:  [B2B-PA-ANNULLAMENTO_29] #PA mittente: annullamento notifica durante invio pec
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK"

  @B2Btest
  Scenario:  [B2B-PA-ANNULLAMENTO_29_1] #PA mittente: annullamento notifica inibizione invio pec
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_PROGRESS" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @B2Btest     #Da Verificare...............
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
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then si attende il corretto pagamento della notifica

          #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_31]
  #Accesso alla tabella pn-TimelinesForInvoicing, popolata da pn-progression-sensor, per verifica fatturazione dei costi contestualmente all’annullamento di una notifica analogica

          #Da Verificare...............
  #Scenario:  [B2B-PA-ANNULLAMENTO_32]
  #Accesso alla tabella pn-TimelinesForInvoicing, popolata da pn-progression-sensor, per verifica fatturazione dei costi contestualmente all’annullamento di una notifica digitale
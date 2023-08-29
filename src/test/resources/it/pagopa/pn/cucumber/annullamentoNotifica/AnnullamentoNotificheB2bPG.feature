Feature: annullamento notifiche b2b


  #ANNULLAMENTO LATO DESTINATARIO PG----------------------------------->>

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_1] Destinatario PG: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #When viene richiesto il download del documento "NOTIFICA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Gherkin spa" tenta il recupero dell'allegato "NOTIFICA"
    Then il download ha prodotto un errore con status code "404"

  Scenario: [B2B-PG-ANNULLAMENTO_2] Destinatario  PG: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    #When viene richiesto il download del documento "NOTIFICA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Gherkin spa" tenta il recupero dell'allegato "NOTIFICA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_3] Destinatario  PG: dettaglio notifica annullata - download allegati (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #When viene richiesto il download del documento "NOTIFICA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Gherkin spa" tenta il recupero dell'allegato "NOTIFICA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_4] Destinatario  PG: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    #When viene richiesto il download del documento "PAGOPA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Gherkin spa" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_5] Destinatario  PG: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    #When viene richiesto il download del documento "PAGOPA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Gherkin spa" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_6] Destinatario  PG: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #When viene richiesto il download del documento "PAGOPA"
    #Then l'operazione ha prodotto un errore con status code "400"
    When "Gherkin spa" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_7] Destinatario  PG: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And download attestazione opponibile AAR
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_8] Destinatario  PG: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And download attestazione opponibile AAR
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_9] Destinatario  PG: dettaglio notifica annullata - download AAR (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And download attestazione opponibile AAR
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_10] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_11] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_12] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_13] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_14] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "PEC_RECEIPT"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_15] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_16] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_17] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_18] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"
    Then l'operazione ha prodotto un errore con status code "404"

  @B2Btest
  Scenario: [B2B-PG-ANNULLAMENTO_19] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Gherkin spa" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'operazione ha prodotto un errore con status code "404"

    #SENDER_ACK - RECIPIENT_ACCESS - PEC_RECEIPT - DIGITAL_DELIVERY - DIGITAL_DELIVERY_FAILURE - SEND_ANALOG_PROGRESS - COMPLETELY_UNREACHABLE

  @B2Btest
  Scenario:  [B2B-PG-ANNULLAMENTO_20] Destinatario  PG: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"

  @B2Btest
  Scenario:  [B2B-PG-ANNULLAMENTO_21] Destinatario  PG: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"

  @B2Btest
  Scenario:  [B2B-PG-ANNULLAMENTO_22] Destinatario  PG: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata dal "Comune_1"
    And si verifica la corretta acquisizione della richiesta di invio notifica
    When la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"


  @B2Btest
  Scenario:  [B2B-PG-ANNULLAMENTO_23] Destinatario  PG: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"


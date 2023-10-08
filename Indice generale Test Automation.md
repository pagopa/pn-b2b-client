# Questo elenco riporta i Test Automatici che sono attualmente implementati
## Table of Contents

1. [Annullamento_Notifica](#annullamento-notifica)
2. [Invio Notifica](#invio-notifica)
   1. [Invio](#invio)
   2. [Download](#download)
   3. [Validation](#validation)
3. [Service_Desk](#service-desk)
4. [Visualizzazione notifica](#visualizzazione-notifica)
   1. [Deleghe](#deleghe)
   2. [Destinatario persona fisica](#destinatario-persona-fisica)
5. [Workflow notifica](#workflow-notifica)
   1. [B2B](#b2b)
   2. [Download](#notifica)
   3. [Webhook](#webhook)
6. [Allegati](#allegati)
7. [Api Key Manager](#api-key-manager)
8. [Downtime logs](#downtime-logs)
9. [User Attributes](#user-attributes)
10. [Test di integrazione della pubblica amministrazione](#test-di-integrazione-della-pubblica-amministrazione)

## Annullamento_Notifica

### Annullamento Notifiche B2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_1] PA mittente: Annullamento notifica in stato “depositata”</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. Si recupera la notifica tramite IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_2] PA mittente: annullamento notifica in stato “invio in corso”</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERING`
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_3] PA mittente: annullamento notifica in stato “consegnata”</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERED`
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_4] PA mittente: annullamento notifica in stato “perfezionata per decorrenza termini”</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `EFFECTIVE_DATE`
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_5] PA mittente: annullamento notifica in stato “irreperibile totale”</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_6] PA mittente: annullamento notifica in stato “avvenuto accesso”</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERED`
4. `Mario Gherkin` legge la notifica ricevuta
5. vengono letti gli eventi fino allo stato della notifica `VIEWED`
6. la notifica può essere annullata dal sistema tramite codice IUN
7. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
8. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_7] PA mittente: annullamento notifica in fase di validazione [TA]</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `IN_VALIDATION`
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_8] PA mittente: annullamento notifica con dati pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. viene verificato il costo = `100` della notifica
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_8_1] PA mittente: annullamento notifica con pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. l'avviso pagopa viene pagato correttamente
4. si attende il corretto pagamento della notifica
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_8_2] PA mittente: annullamento notifica con dati pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. viene verificato il costo = `100` della notifica con un errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_9] PA mittente: notifica con pagamento in stato “Annullata” - presenza box di pagamento</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_10] PA mittente: dettaglio notifica annullata - download allegati (scenari positivi)</summary>

**Descrizione**


1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. viene richiesto il download del documento `NOTIFICA`
7. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_11] PA mittente: dettaglio notifica annullata - download bollettini di pagamento (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. viene verificato il costo = `100` della notifica
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
7. viene richiesto il download del documento `PAGOPA`
8. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_12] PA mittente: dettaglio notifica annullata - download AAR (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. download attestazione opponibile AAR

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. download attestazione opponibile AAR
7. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
8. la PA richiede il download dell'attestazione opponibile `SENDER_ACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13_1] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. `Mario Gherkin` legge la notifica ricevuta
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED` e successivamente annullata
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
7. la PA richiede il download dell'attestazione opponibile `RECIPIENT_ACCESS`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13_2] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. la PA richiede il download dell'attestazione opponibile `PEC_RECEIPT`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13_3] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. la PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13_4] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. la PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13_5] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenari positivi)</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. la PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13_6] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. la PA richiede il download dell'attestazione opponibile `COMPLETELY_UNREACHABLE`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_13_7] PA mittente: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenari positivi)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica "`REQUEST_ACCEPTED`"
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
7. la PA richiede il download dell'attestazione opponibile `SENDER_ACK`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_14] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. vengono letti gli eventi fino all'elemento di timeline della notifica "`NOTIFICATION_CANCELLED`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_14_1] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLATION_REQUEST
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`



[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_15] AuditLog: verifica presenza evento post annullamento notifica</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
6. viene verificato che esiste un audit log "`AUD_NT_CANCELLED`" in "`10y`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_16] Destinatario PF: dettaglio notifica annullata - download allegati (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. il documento notificato non può essere correttamente recuperato da "`Mario Cucumber`"
5. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_16_1] Destinatario  PF: dettaglio notifica annullata - download allegati (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
4. il documento notificato non può essere correttamente recuperato da "`Mario Cucumber`"
5. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_16_2] Destinatario  PF: dettaglio notifica annullata - download allegati (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. il documento notificato non può essere correttamente recuperato da "`Mario Cucumber`"
6. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_17] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
5. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_17_1] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
4. "`Mario Cucumber`" tenta il recupero dell'allegato "`PAGOPA`"
5. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_17_2] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
4. "`Mario Cucumber`" tenta il recupero dell'allegato "`PAGOPA`"
5. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_18] Destinatario PF: dettaglio notifica annullata - download AAR (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. il documento notificato non può essere correttamente recuperato da "`Mario Cucumber`"
5. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_18_1] Destinatario PF: dettaglio notifica annullata - download AAR (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
4. il documento notificato non può essere correttamente recuperato da "`Mario Cucumber`"
5. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_18_2] Destinatario PF: dettaglio notifica annullata - download AAR (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` 
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION` e successivamente annullata
4. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
5. il documento notificato non può essere correttamente recuperato da "`Mario Cucumber`"
6. il download ha prodotto un errore con status code "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica "`NOTIFICATION_CANCELLATION_REQUEST`"
5. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`SENDER_ACK`" con errore "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_1] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica "`NOTIFICATION_CANCELLED`"
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
6. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`SENDER_ACK`" con errore "`404`"



[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_2] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED` e successivamente annullata
4. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
5. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`SENDER_ACK`" con errore "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_3] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
6. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`SENDER_ACK`" con errore "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_4] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
6. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`PEC_RECEIPT`" con errore "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_5] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
6. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`DIGITAL_DELIVERY`" con errore "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_6] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
6. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`DIGITAL_DELIVERY_FAILURE`" con errore "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_7] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenario negativo)
</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_8] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenario negativo)
</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_19_9] Destinatario PF: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
6. "`Mario Gherkin`" richiede il download dell'attestazione opponibile "`SENDER_ACK`" con errore "`404`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_20] Destinatario PF: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_20_1] Destinatario PF: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
 </summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_20_2] Destinatario PF: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `CANCELLED`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_21] Destinatario PF: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_22] Annullamento notifica con pagamento: verifica cancellazione IUV da tabella pn-NotificationsCost
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_23] PA mittente: notifica con pagamento in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Gherkin spa
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
5. viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
6. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_23_1] PA mittente: notifica con pagamento non in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]
</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario Gherkin spa
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_27] PA mittente: annullamento notifica durante invio sms di cortesia
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Louis Armstrong
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. i verifica la corretta acquisizione della notifica
4. viene verificato che l'elemento di timeline  `SEND_COURTESY_MESSAGE` esista
5. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_27_1] PA mittente: annullamento notifica inibizione invio sms di cortesia
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Louis Armstrong
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST` 
4. viene verificato che l'elemento di timeline  `SEND_COURTESY_MESSAGE` non esista

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_28] PA mittente: annullamento notifica durante invio mail di cortesia
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Galileo Galilei
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. i verifica la corretta acquisizione della notifica
4. viene verificato che l'elemento di timeline  `SEND_COURTESY_MESSAGE` esista
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
7. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_28_1] PA mittente: annullamento notifica inibizione invio mail di cortesia
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Galileo Galilei
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. viene verificato che l'elemento di timeline  `SEND_COURTESY_MESSAGE` non esista


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_29] #PA mittente: annullamento notifica durante invio pec</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS` e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
6. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK`  con responseStatus "`OK`"


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_29_1] #PA mittente: annullamento notifica inibizione invio pec</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`  e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. viene verificato che l'elemento di timeline `NOTIFICATION_CANCELLATION_REQUEST` non esista


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_30] PA mittente: annullamento notifica durante pagamento da parte del destinatario</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. l'avviso pagopa viene pagato correttamente
4. la notifica può essere annullata dal sistema tramite codice IUN
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
6. si attende il corretto pagamento della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_31] PA mittente: Annullamento notifica in stato “CANCELLED”</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
5. la notifica non può essere annullata dal sistema tramite codice IUN più volte


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-MULTI-ANNULLAMENTO_1] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Cucumber e Mario Gherkin
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN dal comune `Comune_Multi`
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. `Mario Cucumber` tenta il recupero dell'allegato `PAGOPA`
6. il download ha prodotto un errore con status code`404`
7. `Mario Gherkin` tenta il recupero dell'allegato `PAGOPA`
8. il download ha prodotto un errore con status code`404`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_24] Invio notifica digitale ed attesa Timeline NOTIFICATION_CANCELLATION_REQUEST stream v2_scenario positivo
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. si predispone 1 nuovo stream V2 denominato `stream-test` con eventType `TIMELINE`
3. si crea il nuovo stream per il `Comune_1`
4. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOTIFICATION_CANCELLATION_REQUEST`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_24_1]Invio notifica digitale ed attesa Timeline NOTIFICATION_CANCELLED stream v2_scenario positivo
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. si predispone 1 nuovo stream V2 denominato `stream-test` con eventType `TIMELINE`
3. si crea il nuovo stream per il `Comune_1`
4. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOTIFICATION_CANCELLED`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_25] Invio notifica digitale ed attesa stato CANCELLED stream v2_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. si predispone 1 nuovo stream V2 denominato `stream-test` con eventType `TIMELINE`
3. si crea il nuovo stream per il `Comune_1`
4. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `CANCELLED`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_24_2] Invio notifica digitale ed attesa di un eventi di Timeline stream v2  con controllo EventId incrementale e senza duplicati scenario positivo
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. si predispone 1 nuovo stream V2 denominato `stream-test` con eventType `TIMELINE`
3. si crea il nuovo stream per il `Comune_1`
4. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOTIFICATION_CANCELLED`
7. viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati
8. viene generata una nuova notifica  con destinatario Cucumber Analogic
9. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
10. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `DIGITAL_FAILURE_WORKFLOW`
11. viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati




[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_24_3] Invio notifica digitale ed attesa di un eventi di Timeline stream v2  con controllo EventId incrementale e senza duplicati scenario positivo
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. si predispone 1 nuovo stream V2 denominato `stream-test` con eventType `TIMELINE`
3. si crea il nuovo stream per il `Comune_1`
4. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOTIFICATION_CANCELLED`
7. viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati
8. viene generata una nuova notifica  con destinatario Cucumber Analogic
9. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
10. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `DIGITAL_FAILURE_WORKFLOW`
11. viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati
12. viene generata una nuova notifica  con destinatario Mario Gherkin
13. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
14. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_DOMICILE`
15. viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_24_4] Invio notifica digitale ed attesa di un eventi di Timeline stream v1  con controllo EventId incrementale e senza duplicati scenario positivo
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. si predispone 1 nuovo stream V2 denominato `stream-test` con eventType `TIMELINE`
3. si crea il nuovo stream per il `Comune_1`
4. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. la notifica può essere annullata dal sistema tramite codice IUN
6. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_DOMICILE`
7. viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati
8. viene generata una nuova notifica  con destinatario Cucumber Analogic
9. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
10. vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `DIGITAL_FAILURE_WORKFLOW`
11. viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_32] PA mittente: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. vengono letti gli eventi fino allo stato della notifica "`CANCELLED`"
5. vengono letti gli eventi fino all'elemento di timeline della notifica`NOTIFICATION_CANCELLED`
6. viene verificato che nell'elemento di timeline della notifica`NOTIFICATION_CANCELLED` sia presente il campo notRefinedRecipientIndex



[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_33] PA mittente: Annullamento notifica e inibizione invio SEND_SIMPLE_REGISTERED_LETTER
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. viene verificato che nell'elemento di timeline della notifica`SEND_SIMPLE_REGISTERED_LETTER` non esista



[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_34] PA mittente: Annullamento notifica e inibizione invio PREPARE_SIMPLE_REGISTERED_LETTER
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. viene verificato che nell'elemento di timeline della notifica`PREPARE_SIMPLE_REGISTERED_LETTER` non esista


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_35] PA mittente: annullamento notifica e inibizione invio SEND_DIGITAL_PROGRESS</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. viene verificato che nell'elemento di timeline della notifica`SEND_DIGITAL_PROGRESS` non esista


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_36] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. viene verificato che nell'elemento di timeline della notifica`ANALOG_SUCCESS_WORKFLOW` non esista


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_37] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. viene verificato che nell'elemento di timeline della notifica`ANALOG_SUCCESS_WORKFLOW` non esista


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_38] PA mittente: Annullamento notifica in stato “depositata” da parte di una PA diversa da quella che ha inviato la notifica scenario negativo
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica non può essere annullata dal sistema tramite codice IUN dal comune `Comune_Multi`
4. l'operazione di annullamento ha prodotto un errore con status code `404`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_39] Generazione con gruppo e invio notifica con gruppo e cancellazione notifica con gruppo diverso ApiKey_scenario netagivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario  Mario Cucumber
2. viene settato il gruppo della notifica con quello dell'apikey
3. viene settato il taxId della notifica con quello dell'apikey
4. la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
5. si verifica la corretta acquisizione della notifica
6. viene modificato lo stato dell'apiKey in `BLOCK`
7. l'apiKey viene cancellata
8. Viene creata una nuova apiKey per il comune `Comune_1` con gruppo differente del invio notifica
9. iene impostata l'apikey appena generata
10. la notifica non può essere annullata dal sistema tramite codice IUN
11. l'operazione di annullamento ha prodotto un errore con status code `404`
12. viene modificato lo stato dell'apiKey in `BLOCK`
13. l'apiKey viene cancellata


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_40] Invio notifica con api b2b e tentativo recupero del documento di una notifica annullata tramite AppIO_scenario negativo
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Mario Gherkin
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. il documento notificato può essere recuperata tramite AppIO da `Mario Gherkin`
4. il tentativo di recupero con appIO ha prodotto un errore con status code `404`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature)

</details>

### Annullamento Notifiche B2b Deleghe

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_26] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
</summary>

**Descrizione**

1. Mario Gherkin  rifiuta se presente la delega ricevuta Mario Cucumber
2. Mario Gherkin viene delegato da Mario Cucumber
3. Mario Gherkin accetta la delega Mario Cucumber
4. Viene creata una nuova notifica con destinatario Mario Cucumber
5. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
7. la notifica può essere correttamente recuperata da `Mario Cucumber`
8. la notifica può essere correttamente letta da `Mario Gherkin` con delega


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bDeleghe.feature)

</details>

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_26_1] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
</summary>

**Descrizione**

1. Mario Gherkin  rifiuta se presente la delega ricevuta Mario Cucumber
2. Mario Gherkin viene delegato da Mario Cucumber
3. Mario Gherkin accetta la delega Mario Cucumber
4. Viene creata una nuova notifica con destinatario Mario Cucumber
5. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
7. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
8. la notifica può essere correttamente recuperata da `Mario Cucumber`
9. la notifica può essere correttamente letta da `Mario Gherkin` con delega


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_26_2] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
</summary>

**Descrizione**

1. Mario Gherkin  rifiuta se presente la delega ricevuta Mario Cucumber
2. Mario Gherkin viene delegato da Mario Cucumber
3. Mario Gherkin accetta la delega Mario Cucumber
4. Viene creata una nuova notifica con destinatario Mario Cucumber
5. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
7. la notifica può essere correttamente recuperata da `Mario Cucumber`
8. la notifica può essere correttamente letta da `Mario Gherkin` con delega


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-ANNULLAMENTO_26_3] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
</summary>

**Descrizione**

1. Mario Gherkin  rifiuta se presente la delega ricevuta Mario Cucumber
2. Mario Gherkin viene delegato da Mario Cucumber
3. Mario Gherkin accetta la delega Mario Cucumber
4. Viene creata una nuova notifica con destinatario Mario Cucumber
5. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
7. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
8. la notifica può essere correttamente recuperata da `Mario Cucumber`
9. la notifica può essere correttamente letta da `Mario Gherkin` con delega


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_32] Invio notifica digitale mono destinatario e recupero documento notificato_scenario negativo
</summary>

**Descrizione**

1. Mario Gherkin  rifiuta se presente la delega ricevuta Mario Cucumber
2. Mario Gherkin viene delegato da Mario Cucumber
3. Mario Gherkin accetta la delega Mario Cucumber
4. Viene creata una nuova notifica con destinatario Mario Cucumber
5. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
7. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
8. il documento notificato non può essere correttamente recuperato da `Mario Gherkin` con delega restituendo un errore `404`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-ANNULLAMENTO_33] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario negativo
</summary>

**Descrizione**

1. Mario Gherkin  rifiuta se presente la delega ricevuta Mario Cucumber
2. Mario Gherkin viene delegato da Mario Cucumber
3. Mario Gherkin accetta la delega Mario Cucumber
4. Viene creata una nuova notifica con destinatario Mario Cucumber
5. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
6. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
7. il documento notificato non può essere correttamente recuperato da `Mario Gherkin` con delega restituendo un errore `404`



[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bDeleghe.feature)

</details>


### Annullamento Notifiche B2b PG


<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_1] Destinatario PG: dettaglio notifica annullata - download allegati (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. il documento notificato non può essere correttamente recuperato da `GherkinSrl`
5. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary> [B2B-PG-ANNULLAMENTO_2] Destinatario  PG: dettaglio notifica annullata - download allegati (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
4. il documento notificato non può essere correttamente recuperato da `GherkinSrl`
5. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_3] Destinatario  PG: dettaglio notifica annullata - download allegati (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
4. il documento notificato non può essere correttamente recuperato da `GherkinSrl`
5. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_4] Destinatario  PG: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. `GherkinSrl` tenta il recupero dell'allegato `PAGOPA`
5. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_5] Destinatario  PG: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
4. `GherkinSrl` tenta il recupero dell'allegato `PAGOPA`
5. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_6] Destinatario  PG: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED` e successivamente annullata
3. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
4. `GherkinSrl` tenta il recupero dell'allegato `PAGOPA`
5. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_7] Destinatario  PG: dettaglio notifica annullata - download AAR (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. download attestazione opponibile AAR da parte `GherkinSrl`
6. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_8] Destinatario  PG: dettaglio notifica annullata - download AAR (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
5. download attestazione opponibile AAR da parte `GherkinSrl`
6. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_9] Destinatario  PG: dettaglio notifica annullata - download AAR (scenario negativo)</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`  e successivamente annullata
4. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
5. download attestazione opponibile AAR da parte `GherkinSrl`
6. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_10] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. `GherkinSrl` richiede il download dell'attestazione opponibile `SENDER_ACK` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_11] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `SENDER_ACK` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_12] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`  e successivamente annullata
4. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
5. `GherkinSrl` richiede il download dell'attestazione opponibile `SENDER_ACK` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_13] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi RECIPIENT_ACCESS (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `SENDER_ACK` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_14] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi PEC_RECEIPT (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `PEC_RECEIPT` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_15] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_16] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi DIGITAL_DELIVERY_FAILURE (scenario negativo)
</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY_FAILURE` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_17] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SEND_ANALOG_PROGRESS (scenario negativo)
</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_18] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi COMPLETELY_UNREACHABLE (scenario negativo)
</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `COMPLETELY_UNREACHABLE` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_19] Destinatario  PG: dettaglio notifica annullata - download atti opponibili a terzi SENDER_ACK (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`  e successivamente annullata
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
5. vengono letti gli eventi fino allo stato della notifica `CANCELLED`
6. `GherkinSrl` richiede il download dell'attestazione opponibile `SENDER_ACK` con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_20] Destinatario  PG: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_21] Destinatario  PG: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_22] Destinatario  PG: notifica con pagamento in stato “Annullata” - box di pagamento (scenario negativo)
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. la notifica può essere annullata dal sistema tramite codice IUN
4. vengono letti gli eventi fino allo stato della notifica `CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-ANNULLAMENTO_23] Destinatario  PG: dettaglio notifica annullata - verifica presenza elemento di timeline NOTIFICATION_CANCELLED
</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario GherkinSrl
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`  e successivamente annullata
3. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLATION_REQUEST`
4. vengono letti gli eventi fino allo stato della notifica `CANCELLED` 
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_CANCELLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature)

</details>



## Invio Notifica

### Invio

#### Persona fisica

##### Invio notifiche b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Si recupera la notifica tramite IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_2] Invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber ma diverso idempotenceToken
5. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
6. Se ne verifica la corretta acquisizione

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_3] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
5. La notifica viene inviata dal `Comune_1`
6. L'operazione va in errore con codice 409

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_4] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale codice fiscale del creditore ma diverso codice avviso
5. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
6. Se ne verifica la corretta acquisizione

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_5] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
5. la notifica viene inviata dal `Comune_1`
6. L'operazione va in errore con codice 409

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_9] invio notifiche digitali mono destinatario senza physicalAddress (p.fisica)_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con indirizzo fisico nullo
2. Viene inviata tramite api b2b dal `Comune_1`
3. L'operazione va in errore con codice `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Si tenta il recupero della notifica tramite IUN errato
5. L'operazione va in errore con codice `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_11] Invio notifica digitale mono destinatario Flat_rate_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con feePolicy `FLAT_RATE` e avviso PagoPA
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con feePolicy `DELIVERY_MODE` e avviso PagoPA
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_17] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario senza taxonomyCode
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_18] Invio notifica digitale mono destinatario con taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con taxonomyCode
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_19] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con creditorTaxId, ma senza avviso PagoPA
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_20] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata dal `Comune_Multi`
3. Si verifica la corretta acquisizione della richiesta di invio notifica, controllando la presenza del requestId e dello stato della richiesta


[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_21] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario senza pagamento
2. Viene inviata dal `Comune_Multi`
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_22] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario senza pagamento, ma con il campo amount valorizzato
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Si recupera la notifica tramite IUN
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN
6. l'importo della notifica è `2550`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_24] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con il campo `physicalCommunication` valorizzato con `REGISTERED_LETTER_890`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_25] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con il campo `physicalCommunication` valorizzato con `AR_REGISTERED_LETTER`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_26] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata dal `Comune_1`
3. Viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_27] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata dal `Comune_1`
3. Viene verificato lo stato di accettazione con requestId

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_28] Invio notifica digitale mono destinatario e controllo paProtocolNumber con diverse pa_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber, ma sender `Comune_2`
5. Si attende che la notifica passi in stato `ACCEPTED`
6. Se ne verifica la corretta acquisizione

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_29] Invio notifica digitale mono destinatario e controllo paProtocolNumber con uguale pa_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber e sender dal `Comune_1`
5. L'operazione va in errore con codice `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_30] invio notifiche digitali e controllo paProtocolNumber e idempotenceToken con diversa pa_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken, ma sender `Comune_2`
5. Si attende che la notifica passi in stato `ACCEPTED`
6. Se ne verifica la corretta acquisizione

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND-31] Invio notifica senza indirizzo fisico scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con indirizzo fisico nullo
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con codice `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND-33] Invio notifica senza indirizzo fisico scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con indirizzo fisico nullo
2. Viene inviata dal `Comune_Multi`
3. L'operazione va in errore con codice `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_34] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage  scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_Multi` senza preload allegati e si aspetta che lo stato passi in `REFUSED`
3. Si verifica che la notifica non viene accettata causa mancanza allegato

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_35] Invio notifica mono destinatario con taxId non valido scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite dal `Comune_Multi`
3. L'operazione va in errore con codice `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_36] Invio notifica mono destinatario con max numero allegati scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario `Mario Cucumber`
2. aggiungo `16` numero allegati
3. Viene inviata tramite dal `Comune_Multi`
4. L'operazione va in errore con codice `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_37] Invio notifica  mono destinatario con allegato Injection scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b injection preload allegato dal `Comune_Multi` e si attende che lo stato diventi REFUSED
3. si verifica che la notifica non viene accettata causa `FILE_PDF_INVALID_ERROR`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_38] Invio notifica  mono destinatario con allegato OverSize scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b oversize preload allegato dal `Comune_Multi` e si attende che lo stato diventi REFUSED

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_39] Invio notifica  mono destinatario con allegato Max Num Allegati scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b over 15 preload allegato dal `Comune_Multi` e si attende che lo stato diventi REFUSED
3. si verifica che la notifica non viene accettata causa `INVALID_PARAMETER_MAX_ATTACHMENT`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_39] Invio notifica  mono destinatario con allegato Max Num Allegati scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b over 15 preload allegato dal `Comune_Multi` e si attende che lo stato diventi REFUSED
3. si verifica che la notifica non viene accettata causa `INVALID_PARAMETER_MAX_ATTACHMENT`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_40] Invio notifica digitale mono destinatario con noticeCode ripetuto prima notifica rifiutata</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b senza preload allegato dal `Comune_Multi` e si attende che lo stato diventi REFUSED
3. viene generata una nuova notifica valida con uguale codice fiscale del creditore e uguale codice avviso
4. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN


[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>


##### Invio notifiche b2b con altre PA, multi-destinatario e senza pagamento

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_1] Invio notifica digitale_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_2` e si aspetta che lo stato passi in `ACCEPTED`
3. Si tenta il recupero tramite codice IUN dalla PA `Comune_1`
4. L'operazione va in errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_2] Invio notifica digitale senza pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_3] Invio notifica multi destinatario senza pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_4] Invio notifica multi destinatario con pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_5] Invio notifica multi destinatario PA non abilitata_scenario negativa</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica multi destinatario
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_6] Invio notifica multi destinatario uguale codice avviso_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario dove il secondo destinatario ha lo stesso codice avviso
2. Viene inviata dal `Comune_Multi`
3. L'operazione va in errore con stato `500`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_7] Invio notifica multi destinatario destinatario duplicato_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica multi destinatario con due destinatari uguali
2. Viene inviata dal `Comune_Multi`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_7] Invio notifica multi destinatario destinatario duplicato_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica multi destinatario con 15 destinatari
2. Viene inviata dal `Comune_Multi`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPFMultiPA.feature)

</details>

##### Invio notifiche e2e web PA

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB_PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN web PA_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheE2eWebPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB_PA-SEND_2] Invio notifica digitale senza pagamento e recupero tramite codice IUN web PA_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheE2eWebPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB_PA-SEND_3] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ACCEPTED`
4. La notifica può essere correttamente recuperata dal sistema tramite stato `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheE2eWebPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB_PA-SEND_4] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca:
   - **startDate**: 01/01/2022
   - **endDate**: 01/10/2030
   - **iunMatch**: ACTUAL
   - **subjectRegExp**: cucumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheE2eWebPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB_PA-SEND_5] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca senza filtri

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheE2eWebPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB_PA-SEND_6] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca:
   - **subjectRegExp**: cucumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheE2eWebPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB_PA-SEND_7] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca:
   - **startDate**: 01/01/2022
   - **subjectRegExp**: cucumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheE2eWebPA.feature)

</details>

#### Persona giuridica

##### Invio notifiche b2b per la persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_1] Invio notifica digitale mono destinatario persona giuridica lettura tramite codice IUN (p.giuridica con P.Iva)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_2] Invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
6. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
7. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_3] invio notifiche digitali mono destinatario (p.giuridica con P.Iva)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e idempotenceToken `AME2E3626070001.1`
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.1`
6. Quando la notifica viene inviata tramite api b2b (NON aspetta che passi in stato `ACCEPTED`
7. L'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_4] invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e creditorTaxId
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
6. La notifica viene inviata tramite api b2b dal `Comune_1` aspetta che la notifica passi in stato `ACCEPTED`
7. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_5] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e creditorTaxId
2. La notifica viene inviata tramite api b2b
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
6. Quando la notifica viene inviata dal `Comune_1`
7. L'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_9] invio notifiche digitali mono destinatario senza physicalAddress (p.giuridica)_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e physical address `NULL`
2. Quando la notifica viene inviata dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Quando si tenta il recupero della notifica dal sistema tramite codice IUN
5. L'operazione ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_11] Invio notifica digitale mono destinatario Flat_rate_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e con PagoPA payment form fee policy `FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e con PagoPA payment form fee policy `DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_15] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) senza taxonomy code
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_16] Invio notifica digitale mono destinatario con taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con taxonomy code
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_17] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con creditorTaxId e senza PagoPA payment form
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_18] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con creditorTaxId
2. Quando la notifica viene inviata dal `Comune_Multi`
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_19] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e senza pagamento
2. Quando la notifica viene inviata dal `Comune_Multi`
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_20] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con importo(amount) 2550
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN
5. L'importo della notifica è `2550`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_21] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con physicalCommunication `REGISTERED_LETTER_890`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_22] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con physicalCommunication `AR_REGISTERED_LETTER`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_23] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con idempotenceToken `AME2E3626070001.3`
2. La notifica viene inviata dal `Comune_1`
3. Viene verificato lo stato di accettazione con requestID

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_24] Invio notifica digitale mono destinatario e controllo paProtocolNumber con diverse pa_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber
5. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_25] Invio notifica digitale mono destinatario e controllo paProtocolNumber con uguale pa_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber
5. Quando la notifica viene inviata dal `Comune_1`
6. L'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_26] invio notifiche digitali e controllo paProtocolNumber e idempotenceToken con diversa pa_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con idempotenceToken `AME2E3626070001.3`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.1`
5. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_27] Invio notifica mono destinatario con documenti pre-caricati non trovati su safestorage  scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con recipientType PG e taxId `CCRMCT06A03A433H`
2. La notifica viene inviata tramite api b2b senza preload allegato dal `Comune_Multi` e si attende che lo stato diventi `REFUSED`
3. Si verifica che la notifica non viene accettata causa `ALLEGATO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_28] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e idempotenceToken `AME2E3626070001.3`
2. Quando la notifica viene inviata dal `Comune_1`
3. Viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_29] Invio notifica  mono destinatario con Piva errata</summary>

**Descrizione**

1. Viene generata una nuova notifica con recipientType PG e taxId `CCRMCT06A03A433H`
2. La notifica viene inviata dal `Comune_Multi`
3. l'invio della notifica ha sollevato un errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_30] Invio notifica  mono destinatario con Piva errata</summary>

**Descrizione**

1. Viene generata una nuova notifica con recipientType PG e taxId `1266681029H`
2. La notifica viene inviata dal `Comune_Multi`
3. l'invio della notifica ha sollevato un errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_31] Invio notifica  mono destinatario con Piva corretta</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi ACCEPTED
3. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>

##### Invio notifiche b2b per la persona giuridica con codice fiscale (società semplice)

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_1] Invio notifica digitale mono destinatario persona giuridica lettura tramite codice IUN (p.giuridica con CF)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario codice fiscale(Cucumber Society)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_2] Invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
5. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_3] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
5. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_4] invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e creditorTaxId
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
5. La notifica viene inviata tramite api b2b dal `Comune_1` aspetta che la notifica passi in stato `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_5] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e creditorTaxId
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
5. La notifica viene inviata tramite api b2b dal `Comune_1`
6. L'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_9] invio notifiche digitali mono destinatario senza physicalAddress (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e physical address `NULL`
2. La notifica viene inviata dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Si tenta il recupero della notifica tramite IUN errato
5. L'operazione va in errore con codice `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_11] Invio notifica digitale mono destinatario Flat_rate_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e con PagoPA payment form fee policy `FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e con PagoPA payment form fee policy `DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_15] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e senza taxonomy code
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_16] Invio notifica digitale mono destinatario con taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con taxonomyCode
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_17] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con creditorTaxId e senza PagoPA payment form
2. La notifica viene inviata tramite api b2b dal `Comune_1` e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_18] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con creditorTaxId
2. La notifica viene inviata dal `Comune_Multi`
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_19] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e senza pagamento
2. La notifica viene inviata dal `Comune_Multi`
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_20] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con importo(amount) `2550`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN
5. L'importo della notifica è `2550`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_21] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con physicalCommunication `REGISTERED_LETTER_890`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_22] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con physicalCommunication `AR_REGISTERED_LETTER`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_23] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con idempotenceToken `AME2E3626070001.3`
2. La notifica viene inviata dal `Comune_1`
3. Viene verificato lo stato di accettazione con requestID

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_24] Invio notifica digitale mono destinatario e controllo paProtocolNumber con diverse pa_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber
5. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_25] Invio notifica digitale mono destinatario e controllo paProtocolNumber con uguale pa_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber
5. La notifica viene inviata dal `Comune_1`
6. L'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_26] invio notifiche digitali e controllo paProtocolNumber e idempotenceToken con diversa pa_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con idempotenceToken `AME2E3626070001.3`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.1`
5. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_27] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con idempotenceToken `AME2E3626070001.3`
2. La notifica viene inviata dal `Comune_1`
3. Viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>


##### Invio notifiche b2b con altre PA, multi-destinatario e senza pagamento per persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_01] Invio notifica digitale_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
3. Si tenta il recupero dal sistema tramite codice IUN dalla PA `Comune_1`
4. L'operazione ha generato un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_02] Invio notifica digitale senza pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) con payment `NULL`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_03] Invio notifica multi destinatario senza pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e `Mario Cucumber` entrambi con payment `NULL`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_04] Invio notifica multi destinatario con pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e `Mario Cucumber` con pagamento
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_05] Invio notifica multi destinatario PA non abilitata_scenario negativa</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e `Mario Cucumber` con senderDenomination `Comune di milano`
2. La notifica viene inviata dal `Comune_1`
3. Then l'invio ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_06] Invio notifica multi destinatario uguale codice avviso_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva)
2. E `Mario Cucumber` con uguale codice avviso del destinatario numero 1
3. La notifica viene inviata dal `Comune_Multi`
4. Then l'invio ha prodotto un errore con status code `500`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_07] Invio notifica multi destinatario senza pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e Cucumber srl(P.Iva) entrambi con payment `NULL`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_08] Invio notifica multi destinatario con pagamento_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e Cucumber srl(P.Iva) entrambi con payment
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_09] Invio notifica multi destinatario PA non abilitata_scenario negativa</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e Cucumber srl(P.Iva) con senderDenomination `Comune di milano`
2. La notifica viene inviata dal `Comune_1`
3. L'invio ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>

### Download

#### Persona fisica

##### Download da persona fisica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_1] download documento notificato_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `NOTIFICA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_2] download documento pagopa_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario `Mario Cucumber` con payment_f24flatRate `SI`
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `PAGOPA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_3] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica con feePolicy `DELIVERY_MODE`
2. Con destinatario `Mario Cucumber` con payment_f24standard `SI`
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `PAGOPA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>

#### Persona giuridica

##### Download da persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_1] download documento notificato_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario Gherkin spa(CF)
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `NOTIFICA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_2] download documento pagopa_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario Gherkin spa(CF) con payment_f24flatRate `SI`
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `PAGOPA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_3] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica con feePolicy `DELIVERY_MODE`
2. Con destinatario Gherkin spa(CF) con payment_f24standard `SI`
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `PAGOPA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_4] download documento notificato_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario Cucumber Society(P.IVA)
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `NOTIFICA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_5] download documento pagopa_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario Cucumber Society(P.IVA) con payment_f24flatRate `SI`
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `PAGOPA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_6] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario Cucumber Society(P.IVA) con payment_f24standard `SI`
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene richiesto il download del documento `PAGOPA`
7. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>

### Validation

#### Persona fisica

##### Validazione campi invio notifiche b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario `Mario Cucumber` con physicalAddress_municipality settato con caretteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_1_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber` con physicalAddress_municipality settato con caretteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_2] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario `Mario Cucumber` con physicalAddress_municipalityDetails settato con caretteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_2_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber` con physicalAddress_municipalityDetails settato con caretteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_3] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

1. Con destinatario avente denominazione con caretteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_3_LITE] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

1. Con destinatario avente denominazione con caretteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_4] invio notifica con oggetto contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

1. Con destinatario avente subject con caretteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_5] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario positivo</summary>

**Descrizione**

1. Con destinatario avente tax_id errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_6] invio notifiche digitali mono destinatario con parametri creditorTaxId errati_scenario negativo</summary>

**Descrizione**

1. Con destinatario avente creditorTaxId errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_7] invio notifiche digitali mono destinatario con parametri senderTaxId errati_scenario negativo</summary>

**Descrizione**

1. Viene generato una notifica avente senderTaxId errato con destinatario `Mario Cucumber`
2. La notifica viene inviata tramite api b2b
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_8] invio notifiche digitali mono destinatario con parametri subject errati_scenario negativo</summary>

**Descrizione**

1. Con destinatario avente subject errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_9] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario `Mario Cucumber` con physicalAddress_State settato con caratteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_9_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber` con physicalAddress_State settato con caratteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario `Mario Cucumber` con physicalAddress_address settato con caratteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_10_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber` con physicalAddress_address settato con caratteri speciali
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_11] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario negativo</summary>

**Descrizione**

1. Con destinatario avente tax_id errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_12] invio notifiche digitali mono destinatario con parametri denomination errati_scenario negativo</summary>

**Descrizione**

1. Con destinatario avente denomination errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_13] invio notifiche digitali mono destinatario con parametri senderDenomination errati_scenario negativo</summary>

**Descrizione**

1. Con destinatario avente senderDenomination errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_14] invio notifiche digitali mono destinatario con parametri abstract errati_scenario negativo</summary>

**Descrizione**

1. Con destinatario avente abstract errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_15] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo</summary>

**Descrizione**

1. Viene generata una notifica con destinatario `Mario Cucumber`
2. Viene configurato noticeCodeAlternative uguale a noticeCode
3. La notifica viene inviata dal `Comune_1`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_16] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. Viene generata una notifica con destinatario `Mario Cucumber`
2. Viene configurato noticeCodeAlternative diversi a noticeCode
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_17] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo</summary>

**Descrizione**

1. Viene generata una notifica con destinatario `Mario Cucumber`
2. Viene configurato noticeCodeAlternative uguale a noticeCode
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_18] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. Viene generata una notifica con destinatario `Mario Cucumber`
2. Viene configurato noticeCodeAlternative diversi a noticeCode
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_19] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Gherkin` avente physicalAddress_zip corretto
2. La notifica viene inviata tramite api b2b dal `Comune_Multi`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_20] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo</summary>

**Descrizione**

1. Con destinatario `Mario Gherkin` avente physicalAddress_zip non corretto
2. La notifica viene inviata dal `Comune_Multi`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_21] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Gherkin` avente physicalAddress_zip corretto
2. La notifica viene inviata tramite api b2b dal `Comune_Multi`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_22] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo</summary>

**Descrizione**

1. Con destinatario `Mario Gherkin` avente physicalAddress_zip non corretto
2. La notifica viene inviata dal `Comune_Multi`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_23] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario `Mario Gherkin` avente physicalAddress_zip non corretto
2. La notifica viene inviata dal `Comune_Multi`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_24] invio notifiche digitali mono destinatario con provincia non presente e Stato Italia scenario negativo</summary>

**Descrizione**

1. Con destinatario avente physicalAddress_State con ITALIA e physicalAddress_province a NULL
2. La notifica viene inviata dal `Comune_Multi`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_25] invio notifiche digitali mono destinatario con provincia non presente e Stato Estero scenario positivo</summary>

**Descrizione**

1. Con destinatario avente physicalAddress_State con FRANCIA e physicalAddress_province non presente
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. Si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_26] invio notifiche digitali mono destinatario con provincia non presente e Stato Estero scenario positivo</summary>

**Descrizione**

1. Con destinatario avente physicalAddress_State con ITALIA e physicalAddress_province con MI
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. Si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_27] invio notifiche digitali mono destinatario con provincia presente e Stato estero scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario avente physicalAddress_State con FRANCIA e physicalAddress_province con MI
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. Si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_28] invio notifiche digitali mono destinatario con provincia presente e Stato estero scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario avente physicalAddress_State con FRANCIA e physicalAddress_province non correto
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. Si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_29] invio notifiche digitali mono destinatario con provincia errata e Stato estero scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario avente physicalAddress_State con ITALIA e physicalAddress_province non correto
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. Si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_30] invio notifiche digitali mono destinatario con provincia non presente e Stato non presente scenario negativo</summary>

**Descrizione**

1. Con destinatario avente physicalAddress_State non presente e physicalAddress_province non presente
2. La notifica viene inviata dal `Comune_Multi`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_31] invio notifiche digitali mono destinatario con provincia non presente e Stato Italia scenario negativo</summary>

**Descrizione**

1. Con destinatario avente physicalAddress_State con ITALIA e physicalAddress_province non presente
2. La notifica viene inviata dal `Comune_Multi`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_32] invio notifiche digitali mono destinatario con provincia  presente e Stato non presente scenario negativo</summary>

**Descrizione**

1. Con destinatario avente physicalAddress_State non presente e physicalAddress_province con MI
2. La notifica viene inviata dal `Comune_Multi`
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_33] Invio notifica digitale con mono destinatario con denomination corretta e recupero tramite codice IUN (p.fisica)_scenario positivo </summary>
    
**Descrizione**

1. viene generata una nuova notifica
2. a destinatario con denomination corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_34] Invio notifica digitale con mono destinatario con denomination errata scenario negativo </summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatario con denomination non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_35] Invio notifica digitale mono destinatario con physicalAddress_address e physicalAddress_addressDetails  corretto (p.fisica)_scenario positivo </summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_address e physicalAddress_addressDetails corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_36] Invio notifica digitale mono destinatario con physicalAddress_municipality corretto (p.fisica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_municipality corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_37] Invio notifica digitale mono destinatario con physicalAddress_municipalityDetails corretto (p.fisica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_municipalityDetails corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_38] Invio notifica digitale mono destinatario con physicalAddress_State corretto (p.fisica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_State corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_39] Invio notifica digitale mono destinatario con physicalAddress_zip corretto (p.fisica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_zip corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_40] Invio notifica digitale mono destinatario con physicalAddress_province corretto (p.fisica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_province corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_41] Invio notifica digitale mono destinatario con physicalAddress_address errato (p.fisica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_address non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_42] Invio notifica digitale mono destinatario con physicalAddress_addressDetails errato (p.fisica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_addressDetails non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_43] Invio notifica digitale mono destinatario con physicalAddress_municipality errato (p.fisica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_municipality non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_44] Invio notifica digitale mono destinatario con physicalAddress_municipalityDetails errato (p.fisica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_municipalityDetails non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_45] Invio notifica digitale mono destinatario con physicalAddress_State errato (p.fisica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_State non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_46] Invio notifica digitale mono destinatario con physicalAddress_zip errato (p.fisica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_zip non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_47] Invio notifica digitale mono destinatario con physicalAddress_province errato (p.fisica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con physicalAddress_address non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_48] invio notifiche digitali mono destinatario con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province corretti scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con tripletta municipality, cap e province valido
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_49] invio notifiche digitali mono destinatario con  con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province errati scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. destinatario Mario Gherkin con tripletta municipality, cap e province non valido
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi REFUSED

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>

#### Persona giuridica

##### Validazione campi invio notifiche b2b con persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_2] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_3] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_4] invio notifica con oggetto contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_5] invio notifiche digitali mono destinatario con errati tax_id errati_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con codice fiscale errato
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_6] invio notifiche digitali mono destinatario con parametri tax_id corretti_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con partita iva errata
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_7] invio notifiche digitali mono destinatario con parametri creditorTaxId errati_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con creditorTaxId errato
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_8] invio notifiche digitali mono destinatario con parametri senderTaxId errati_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con senderTaxId errato
2. La notifica viene generata tramite api b2b
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_9] invio notifiche digitali mono destinatario con parametri subject errati_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con subject errato
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_11] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_12] invio notifiche digitali mono destinatario con parametri denomination errati_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con denomination errata
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_13] invio notifiche digitali mono destinatario con parametri senderDenomination errati_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con senderDenomination errata
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_14] invio notifiche digitali mono destinatario con parametri abstract errati_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con abstract errato
2. Viene inviata dal `Comune_1`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG-15] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene configurato noticeCodeAlternative uguale a noticeCode
3. Viene inviata dal `Comune_1`
4. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_16] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene configurato noticeCodeAlternative diverso da noticeCode
3. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG-17] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene configurato noticeCodeAlternative uguale a noticeCode
3. Viene inviata dal `Comune_Multi`
4. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_18] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene configurato noticeCodeAlternative diverso da noticeCode
3. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_19] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con zipCode corretto
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_PG_20] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con zipCode errato
2. Viene inviata dal `Comune_Multi`
3. L'operazione va in errore con stato `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pg/InvioNotificheB2bPGValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_1] Invio notifica digitale con multi destinatario corretto e recupero tramite codice IUN scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con denomination corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_2] Invio notifica digitale con multi destinatario errati scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con denomination non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_3] Invio notifica digitale mono destinatario con physicalAddress_address e physicalAddress_addressDetails corretto scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_address e physicalAddress_addressDetails corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_4] Invio notifica digitale mono destinatario con physicalAddress_municipality corretto scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_municipality corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_5] Invio notifica digitale mono destinatario con physicalAddress_municipalityDetails corretto scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_municipalityDetails corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_6] Invio notifica digitale mono destinatario con physicalAddress_state corretto scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_State corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_7] Invio notifica digitale mono destinatario con physicalAddress_zip corretto scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_zip corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_8] Invio notifica digitale mono destinatario con physicalAddress_province corretto scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_province corretto
3. la notifica viene inviata dal `Comune_1`
4. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_9] Invio notifica digitale multi destinatario con physicalAddress_address errato scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_address non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_10] Invio notifica digitale multi destinatario con physicalAddress_addressDetails errato scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_addressDetails non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_11] Invio notifica digitale multi destinatario con physicalAddress_municipality errato scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_municipality non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_12] Invio notifica digitale multi destinatario con physicalAddress_municipalityDetails errato scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_municipalityDetails non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_13] Invio notifica digitale multi destinatario con physicalAddress_State errato scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_State non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_14] Invio notifica digitale multi destinatario con physicalAddress_zip errato scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_zip non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_15] Invio notifica digitale multi destinatario con physicalAddress_province errato scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con physicalAddress_province non valido
3. la notifica viene inviata dal `Comune_1`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_16] invio notifiche digitali multi destinatario con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province corretti scenario positivo
</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con tripletta municipality, cap e province valido
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
   <summary>[B2B-PA-SEND_VALID_PF_PG_17] invio notifiche digitali multi destinatario con  con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province errati scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica
2. a destinatari con tripletta municipality, cap e province non valido
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi REFUSED

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>


## Service Desk

### Api Service Desk


<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_UNREACHABLE_4] Invocazione del servizio UNREACHABLE per CF senza notifiche in stato IRR TOT</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per un il CF `CPNTMS85T15H703W`
2. viene invocato il servizio UNREACHABLE
3. la risposta del servizio UNREACHABLE è `0`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_UNREACHABLE_5] Invocazione del servizio UNREACHABLE per CF con sole notifiche in stato IRR TOT</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per un il CF `MNTMRA03M71C615V`
2. viene invocato il servizio UNREACHABLE
3. la risposta del servizio UNREACHABLE è `1`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_UNREACHABLE_6] Invocazione del servizio UNREACHABLE per CF più notifiche non consegnate sia per IRR TOT che per altre motivazioni
</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per un il CF `FRMTTR76M06B715E`
2. viene invocato il servizio UNREACHABLE
3. la risposta del servizio UNREACHABLE è `1`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_UNREACHABLE_7] Invocazione del servizio UNREACHABLE per CF con una sola notifica in stato IRR TOT</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per un il CF `FRMTTR76M06B715E`
2. viene invocato il servizio UNREACHABLE
3. la risposta del servizio UNREACHABLE è `1`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_UNREACHABLE_8] Invocazione del servizio UNREACHABLE per CF con sole notifiche presenti in stato IRR TOT con ultima tentativo di consegna >120g</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per un il CF `TMTMRC66A01H703L`
2. viene invocato il servizio UNREACHABLE
3. la risposta del servizio UNREACHABLE è `1`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_UNREACHABLE_9] Invocazione del servizio UNREACHABLE per CF vuoto</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE con cf vuoto
2. viene invocato il servizio UNREACHABLE con errore
3. il servizio risponde con errore `500`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_UNREACHABLE_10] Invocazione del servizio UNREACHABLE per CF non formalmente corretto</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per un il CF `CPNTMS85T15H703WCPNTMS85T15H703W`
2. viene invocato il servizio UNREACHABLE con errore
3. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_14] Invocazione del servizio CREATE_OPERATION per CF vuoto</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con cf vuoto
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_15] Invocazione del servizio CREATE_OPERATION per CF che non ha notifiche da consegnare per irr tot
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `CPNTMS85T15H703W`
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_16] Invocazione del servizio CREATE_OPERATION per CF errato</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `CPNTMS85T15H703WCPNTMS85T15H703W`
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_17]</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo con campo indirizzo vuoto
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `TMTSFS80A01H703K`
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_18] Invocazione del servizio CREATE_OPERATION con indirizzo errato</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `TMTSFS80A01H703K`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il CF `TMTSFS80A01H703K`
10. viene invocato il servizio SEARCH con delay
11. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_19] Invocazione del servizio CREATE_OPERATION con ticket id vuoto</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con CF `TMTSFS80A01H703K` `ticketid_vuoto` e operation ticket id `1233443322`
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_20] Invocazione del servizio CREATE_OPERATION con ticket id non formalmente corretto
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con CF `TMTSFS80A01H703K` `ticketid_errato` e operation ticket id `1233443322`
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_21] Invocazione del servizio CREATE_OPERATION con operation ticket id non formalmente corretto
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con CF `TMTSFS80A01H703K` e ticket id `1233443322` e `ticketoperationid_errato`
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_22] Invocazione del servizio CREATE_OPERATION con coppia ticket id ed operation ticket id già usati in precedenza
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con CF `TMTSFS80A01H703K` e ticket id `AUTYV7JIYJ40WXC` e e operation ticket id `AUT6DBGNT0`
3. viene invocato il servizio CREATE_OPERATION con errore
4. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_DESK_CREATE_OPERATION_23] Invocazione del servizio CREATE_OPERATION inserimento richiesta corretta con creazione operation id
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `FRMTTR76M06B715E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_24] Invocazione del servizio UPLOAD VIDEO con operation id non esistente</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
2. viene invocato il servizio UPLOAD VIDEO con `abcedred` con errore
3. il servizio risponde con errore `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_25] Invocazione del servizio UPLOAD VIDEO con operation id vuoto</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
2. viene invocato il servizio UPLOAD VIDEO con operationid vuoto
3. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_26] Invocazione del servizio UPLOAD VIDEO con sha256 vuoto</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `FRMTTR76M06B715E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 vuoto
6. viene invocato il servizio UPLOAD VIDEO con errore
7. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_27] Invocazione del servizio UPLOAD VIDEO con preloadidx vuoto</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `FRMTTR76M06B715E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con preloadIdx vuoto
6. viene invocato il servizio UPLOAD VIDEO con errore
7. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_28] Invocazione del servizio UPLOAD VIDEO con sha256 errato</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `FRMTTR76M06B715E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 vuoto
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage con errore
9. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_29] Invocazione del servizio UPLOAD VIDEO con preloadidx errato</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `TMTSFS80A01H703K`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con preloadIdx vuoto
6. viene invocato il servizio UPLOAD VIDEO con errore
7. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_30] Invocazione del servizio UPLOAD VIDEO con esito positivo</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `TMTSFS80A01H703K`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. ila risposta del servizio UPLOAD VIDEO risponde con esito positivo

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_32] Invocazione del servizio UPLOAD VIDEO con formato non consentito</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `XXXCCC87B12H702E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. il video viene caricato su SafeStorage

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_33] Invocazione del servizio UPLOAD VIDEO con url scaduta</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con CF `MNTMRA03M71C615V`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage con url scaduta
9. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_34] Invocazione del servizio SEARCH con CF vuoto</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio SEARCH per il `CF_vuoto`
2. viene invocato il servizio SEARCH con errore
3. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_35] Invocazione del servizio SEARCH con CF non formalmente corretto</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio SEARCH per il `CF_errato`
2. viene invocato il servizio SEARCH con errore
3. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_36] Invocazione del servizio SEARCH con CF corretto</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio SEARCH per il `FRMTTR76M06B715E`
2. viene invocato il servizio SEARCH
3. Il servizio SEARCH risponde con esito positivo

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_37] Invocazione del servizio SEARCH con CF senza notifiche in stato IRR TOT</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio SEARCH per il `CPNTMS85T15H703W`
2. viene invocato il servizio SEARCH
3. Il servizio SEARCH risponde con lista vuota

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_38] Invocazione del servizio SEARCH con CF con una sola notifica reinviata per irreperibilità totale</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTSFS80A01H703K`
2. viene invocato il servizio SEARCH
3. Il servizio SEARCH risponde con esito positivo

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_39] Invocazione del servizio SEARCH con CF con sole notifiche reinviate per irreperibilità con ultimo tentativo di consegna >120g
</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio SEARCH per il `FRMTTR76M06B715E`
2. viene invocato il servizio SEARCH
3. Il servizio SEARCH risponde con esito positivo

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[E2E_40] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK con notifiche in irr tot con ultimo tentativo >120 gg
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTMRC66A01H703L`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTMRC66A01H703L`
10. viene invocato il servizio SEARCH con delay
11. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `OK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_41] Invocazione del servizio SEARCH con CF con sole notifiche reinviate per irreperibilità totale- notifica multidestinatario
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTYRU80A07H703E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTMRC66A01H703L`
10. viene invocato il servizio SEARCH con delay
11. Il servizio SEARCH risponde con esito positivo per lo `QAQN-LJXG-YTNA-202309-Q-1` e lo stato della consegna è `OK`
12. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTRCC80A01A509O`
13. viene invocato il servizio CREATE_OPERATION
14. la risposta del servizio CREATE_OPERATION risponde con esito positivo
15. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
16. viene invocato il servizio UPLOAD VIDEO
17. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
18. il video viene caricato su SafeStorage
19. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTRCC80A01A509O`
20. viene invocato il servizio SEARCH con delay
21. Il servizio SEARCH risponde con esito positivo per lo `QAQN-LJXG-YTNA-202309-Q-1` e lo stato della consegna è `OK`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_42] Inserimento di una nuova richista di reinvio pratiche con stato operation id in CREATED</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `FRMTTR76M06B715E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. viene creata una nuova richiesta per invocare il servizio SEARCH per il `FRMTTR76M06B715E`
9. viene invocato il servizio SEARCH
10. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `CREATING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_SEARCH_43] Inserimento di una nuova richista di reinvio pratiche con stato operation id in CREATED</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTSFS80A01H703K`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTSFS80A01H703K`
10. viene invocato il servizio SEARCH
11. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `CREATING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[E2E_46] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `FRMTTR76M06B715E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il `FRMTTR76M06B715E`
10. viene invocato il servizio SEARCH
11. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `OK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[E2E_47] Invocazione del servizio CREATE_OPERATION con nuovo tantativo consegna non recapitata(KO)</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTSFS80A01H703K`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTSFS80A01H703K`
10. viene invocato il servizio SEARCH
11. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[E2E_48] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK per uno dei multidestinatari
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTCRL80A01F205A`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTCRL80A01F205A`
10. viene invocato il servizio SEARCH
11. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `OK`
12. viene creata una nuova richiesta per invocare il servizio SEARCH per il `CLMCST42R12D969Z`
13. viene invocato il servizio SEARCH
14. Il servizio SEARCH risponde con lista vuota

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[E2E_50] CF per il quale una consegna per irreperibilità totale è andata  KO e si reinserisce nuova richiesta</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTVCN80A01H501P`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTVCN80A01H501P`
10. viene invocato il servizio SEARCH
11. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `KO`
12. viene comunicato il nuovo indirizzo
13. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTVCN80A01H501P`
14. viene invocato il servizio CREATE_OPERATION
15. la risposta del servizio CREATE_OPERATION risponde con esito positivo
16. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
17. viene invocato il servizio UPLOAD VIDEO
18. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
19. il video viene caricato su SafeStorage
20. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTVCN80A01H501P`
21. viene invocato il servizio SEARCH con delay
22. Il servizio SEARCH risponde con esito positivo e lo stato della consegna è `OK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_51] Inserimento di una nuova richista di reinvio pratiche con stato caricamento video su SafeStorage e verifica retention
</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTSFS80A01H703K`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
6. viene invocato il servizio UPLOAD VIDEO
7. la risposta del servizio UPLOAD VIDEO risponde con esito positivo
8. il video viene caricato su SafeStorage
9. viene effettuato un controllo sulla durata della retention

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-SERVICE_PREUPLOAD_VIDEO_54] Invocazione del servizio UPLOAD VIDEO con ContentType vuoto</summary>

**Descrizione**

1. viene comunicato il nuovo indirizzo
2. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `FRMTTR76M06B715E`
3. viene invocato il servizio CREATE_OPERATION
4. la risposta del servizio CREATE_OPERATION risponde con esito positivo
5. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con ContentType vuoto
6. viene invocato il servizio UPLOAD VIDEO con errore
7. il servizio risponde con errore `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-AUTH_55] Connessione al client senza API KEY</summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il `TMTSFS80A01H703K` senza API Key
2. viene invocato il servizio UNREACHABLE con errore senza API Key
3. il servizio risponde con errore `401` senza API Key
4. viene comunicato il nuovo indirizzo
5. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTSFS80A01H703K` senza API Key
6. viene invocato il servizio CREATE_OPERATION senza API Key con errore
7. il servizio risponde con errore `401` senza API Key
8. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO senza API Key
9. viene invocato il servizio UPLOAD VIDEO senza API Key con `TMTSFS80A01H703K` con errore
10. il servizio risponde con errore `401` senza API Key
11. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTSFS80A01H703K` senza API Key
12. viene invocato il servizio SEARCH senza API Key con errore
13. il servizio risponde con errore `401` senza API Key

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary></summary>

**Descrizione**

1. viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il `TMTSFS80A01H703K` con API Key errata
2.viene invocato il servizio UNREACHABLE con errore con API Key errata
3. il servizio risponde con errore `401` con API Key errata
4. viene comunicato il nuovo indirizzo
5. viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con `TMTSFS80A01H703K` con API Key errata
6. viene invocato il servizio CREATE_OPERATION con API Key errata con errore
7. il servizio risponde con errore `401` con API Key errata
8. viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con API Key errata
9. viene invocato il servizio UPLOAD VIDEO con API Key errata con `TMTSFS80A01H703K` con errore
10. il servizio risponde con errore `401` con API Key errata
11. viene creata una nuova richiesta per invocare il servizio SEARCH per il `TMTSFS80A01H703K` con API Key errata
12. viene invocato il servizio SEARCH senza API Key con errore
13. il servizio risponde con errore `401` con API Key errata

[Feature link](src/test/resources/it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature)

</details>

## Visualizzazione notifica

### Deleghe

#### Persona fisica

##### Ricezione notifiche destinate al delegante

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Gherkin` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Il documento notificato può essere correttamente recuperato da `Mario Gherkin`con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `Mario Gherkin` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `F24` può essere correttamente recuperato da `Mario Gherkin` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `F24` può essere correttamente recuperato da `Mario Gherkin` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. `Mario Cucumber` revoca la delega a `Mario Gherkin`
6. La lettura della notifica da parte di `Mario Gherkin` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` rifiuta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La lettura della notifica da parte di `Mario Gherkin` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_8] Delega a se stesso _scenario negativo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Gherkin`
2. L'operazione va in errore con stato `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_9] delega duplicata_scenario negativo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. `Mario Gherkin` viene delegato da `Mario Cucumber`
4. L'operazione va in errore con stato `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Gherkin` con delega
6. La notifica può essere correttamente letta da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_11] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Cucumber`
6. La notifica può essere correttamente letta da `Mario Gherkin` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Gherkin` con delega
6. L'elemento di timeline della lettura riporta i dati di `Mario Gherkin`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_13] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Cucumber`
6. L'elemento di timeline della lettura non riporta i dati di `Mario Gherkin`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MULTI-MANDATE_14] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber` e `Mario Gherkin`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Cucumber`
6. La notifica può essere correttamente letta da `Mario Gherkin` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MULTI-MANDATE_15] Invio notifica digitale a destinatario non reperibile</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario non reperebile
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MULTI-MANDATE_16] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber` e `Mario Gherkin`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Download attestazione opponibile AAR
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
7. La PA richiede il download dell'attestazione opponibile `SENDER_ACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>

#### Persona giuridica

##### Ricezione notifiche destinate al delegante

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `CucumberSpa` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Il documento notificato può essere correttamente recuperato da `CucumberSpa` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `CucumberSpa` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `F24` può essere correttamente recuperato da `CucumberSpa` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `F24` può essere correttamente recuperato da `CucumberSpa` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. `GherkinSrl` revoca la delega a `CucumberSpa`
6. La lettura della notifica da parte del delegato `CucumberSpa` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` rifiuta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La lettura della notifica da parte del delegato `CucumberSpa` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_8] Delega a se stesso _scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. `GherkinSrl` viene delegato da `GherkinSrl`
2. L'operazione va in errore con stato `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_9] delega duplicata_scenario negativo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. `CucumberSpa` viene delegato da `GherkinSrl`
4. L'operazione va in errore con stato `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `CucumberSpa` con delega
6. La notifica può essere correttamente letta da `GherkinSrl`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_11] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl`
6. La notifica può essere correttamente letta da `CucumberSpa` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `CucumberSpa` con delega
6. L'elemento di timeline della lettura riporta i dati di `CucumberSpa`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_13] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl`
6. L'elemento di timeline della lettura non riporta i dati del delegato

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatari `GherkinSrl`e `CucumberSpa`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl`
6. La notifica può essere correttamente letta da `CucumberSpa` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>


#### Persona fisica e giuridica

##### Ricezione notifiche destinate al delegante

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Il documento notificato può essere correttamente recuperato da `GherkinSrl` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `GherkinSrl` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `F24` può essere correttamente recuperato da `GherkinSrl` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `F24` può essere correttamente recuperato da `GherkinSrl` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. `Mario Cucumber` revoca la delega a `GherkinSrl`
6. La lettura della notifica da parte del delegato `GherkinSrl` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` rifiuta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La lettura della notifica da parte del delegato `GherkinSrl` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_8] delega duplicata_scenario negativo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. `GherkinSrl` viene delegato da `Mario Cucumber`
4. L'operazione va in errore con stato `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_9] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl` con delega
6. La notifica può essere correttamente letta da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Cucumber`
6. La notifica può essere correttamente letta da `GherkinSrl` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_11] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl` con delega
6. L'elemento di timeline della lettura riporta i dati di `GherkinSrl`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Cucumber`
6. L'elemento di timeline della lettura non riporta i dati del delegato

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatari `Mario Cucumber`e `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Cucumber`
6. La notifica può essere correttamente letta da `GherkinSrl` con delega

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>

### Destinatario persona fisica

#### Recupero notifiche tramite api AppIO b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_1] Invio notifica con api b2b e recupero tramite AppIO</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_2] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO
6. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
7. La notifica viene inviata tramite api b2b dal `Comune_1`
8. Lo stato diventa `ACCEPTED`
9. La notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)


</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_3] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO
6. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
7. La notifica viene inviata tramite api b2b dal `Comune_1`
8. Lo stato diventa `ACCEPTED`
9. La notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_4] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario Mario Cucumber
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_5] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. `Mario Cucumber` tenta il recupero della notifica tramite AppIO
6. Il tentativo di recupero con appIO ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>

#### Recupero notifiche tramite api AppIO b2b multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_6] Invio notifica con api b2b e recupero tramite AppIO</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario `Mario Cucumber` e `Mario Gherkin`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_7] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO</summary>

**Descrizione**

1. CViene generata la notifica
2. Con destinatario `Mario Cucumber` e `Mario Gherkin`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO
6. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.2`
7. La notifica viene inviata tramite api b2b dal `Comune_Multi`
8. Lo stato diventa `ACCEPTED`
9. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_8] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO</summary>

**Descrizione**

1. Viene generata la notifica
2.
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO
6. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
7. La notifica viene inviata tramite api b2b dal `Comune_Multi`
8. Lo stato diventa `ACCEPTED`
9. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_9] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata la notifica
2. Con destinatario `Mario Cucumber` e `Mario Gherkin`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. `Mario Cucumber` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_10] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata la notifica
2. Con destinatario `Mario Cucumber` e `Mario Gherkin`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. `Mario Gherkin` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_11] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario `Mario Cucumber` e `Mario Gherkin`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. `Mario Cucumber` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_12] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. Viene generata la notifica
2. Con destinatario `Mario Cucumber` e `Mario Gherkin`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. `Mario Gherkin` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>

#### Ricezione notifiche api web con invio tramite api B2B

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Il documento notificato può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. L'allegato `PAGOPA` può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. L'allegato `F24` può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate NULL e payment_f24standard SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. L'allegato `F24` può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_6] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate NULL e payment_f24standard NULL
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. La persona fisica tenta il recupero dell'allegato `F24`
4. Il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_7] Invio notifica digitale altro destinatario e recupero tramite codice IUN API WEB_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Cucumber`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero della notifica
4. Il recupero ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_8] Invio notifica digitale altro destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`
4. Il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_9] Invio notifica digitale altro destinatario e recupero allegato F24_FLAT_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`
4. Il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_10] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `PAGOPA`
4. Il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_11] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_12] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` con `subjectRegExp cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_13] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` con `subjectRegExp cucumber` e `startDate 01/01/2022`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_14] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` con `subjectRegExp cucumber`, `startDate 01/01/2022`,
   `endDate 01/10/2030` e `iunMatch ACTUAL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>

#### Ricezione notifiche api web con invio tramite api B2B multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_1] Invio notifica digitale multi destinatario e recupero tramite codice IUN API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_2] Invio notifica digitale multi destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. Il documento notificato può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_3] Invio notifica digitale multi destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. L'allegato `PAGOPA` può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_4] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. L'allegato `F24` può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_5] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI e payment_f24standard SI
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. L'allegato `F24` può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_6] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e`Mario Cucumber`con payment_pagoPaForm SI, payment_f24standard NULL e payment_f24flatRate NULL
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`
4. `Mario Cucumber` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_7] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI, payment_f24standard NULL e payment_f24flatRate NULL
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`
4. `Mario Cucumber` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_8] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. `Mario Gherkin` con payment_pagoPaForm NULL, payment_f24flatRate SI e payment_f24standard NULL
3. `Mario Cucumber` con payment_pagoPaForm SI, payment_f24flatRate NULL e payment_f24standard SI
4. `Mario Gherkin` tenta il recupero dell'allegato `PAGOPA`, ma il download ha prodotto un errore con status code `404`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_9] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. `Mario Gherkin` con payment_pagoPaForm SI, payment_f24flatRate NULL e payment_f24standard NULL
3. `Mario Cucumber` con payment_pagoPaForm SI, payment_f24flatRate SI e payment_f24standard NULL
4. `Mario Gherkin` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`
5. L'allegato `F24` può essere correttamente recuperato da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_10] Invio notifica digitale multi destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. `Mario Gherkin` con payment_pagoPaForm NULL, payment_f24flatRate SI e payment_f24standard NULL
3. `Mario Cucumber` con payment_pagoPaForm SI, payment_f24flatRate SI e payment_f24standard NULL
4. `Mario Gherkin` tenta il recupero dell'allegato `PAGOPA`, ma il download ha prodotto un errore con status code `404`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_11] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_12] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_13] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber` e `startDate 01/01/2023`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_14] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber`,
   `startDate 01/01/2023`, `endDate 01/10/2030` e `iunMatch ACTUAL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_15] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. La notifica NON può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber`,
   `startDate 01/01/2030` e `endDate 01/10/2033`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWebMulti.feature)

</details>

## Workflow notifica

### B2B

#### Persona fisica

##### Avanzamento notifiche b2b persona fisica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERING`
4. `Mario Cucumber` legge la notifica ricevuta
5. Si verifica che la notifica abbia lo stato `VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_10] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`
4. `Mario Gherkin` legge la notifica ricevuta
5. Si verifica che la notifica abbia lo stato `VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_11] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`
4. `Mario Gherkin` legge la notifica ricevuta
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_12] Invio notifica digitale ed attesa elemento di timeline PREPARE_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `PREPARE_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_13] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_14] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_15] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_18] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campo deliveryDetailCode positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `OK` sia presente il campo `deliveryDetailCode`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `OK` sia presente il campo `deliveryDetailCode` e `deliveryFailureCause`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_DIGITAL_DOMICILE` è presente il campo Digital Address di piattaforma
5. `#PLOMRC01P30L736Y` ha un indirizzo digitale configurato non valido su NR serve un CF con indirizzo digitale in piattaforma

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `PREPARE_DIGITAL_DOMICILE`
4. Viene verificato che nell'elemento di timeline della notifica `PREPARE_DIGITAL_DOMICILE` sia presente il campo Digital Address

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SCHEDULE_DIGITAL_WORKFLOW`
4. Viene verificato che nell'elemento di timeline della notifica `SCHEDULE_DIGITAL_WORKFLOW` sia presente il campo Digital Address

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>

##### Avanzamento notifiche b2b multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_1] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED` dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_2] Invio notifica multi destinatario_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` legge la notifica ricevuta
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_3] Invio notifica multi destinatario_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Cucumber` legge la notifica ricevuta
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_4] Invio notifica multi destinatario SCHEDULE_ANALOG_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SCHEDULE_ANALOG_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_5] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_6] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_7] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_8] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_9] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_11] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica multi destinatario
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>

##### Avanzamento notifiche b2b persona fisica pagamento

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. Viene generata una nuova notifica con feePolicy `DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` legge la notifica ricevuta
4. Vengono verificati costo = `100` e data di perfezionamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_2] Invio notifica e verifica amount</summary>

**Descrizione**

1. Viene generata una nuova notifica con feePolicy `DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` legge la notifica ricevuta
4. Viene verificato il costo = `100` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_3] Invio notifica FLAT e verifica amount</summary>

**Descrizione**

1. Viene generata una nuova notifica con feePolicy `FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` legge la notifica ricevuta
4. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_4] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. Viene generata una nuova notifica con feePolicy `DELIVERY_MODE`, `payment_pagoPaForm`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_5] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con feePolicy `DELIVERY_MODE`, `payment_pagoPaForm` e `payment_f24standard`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Il modello f24 viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_6] Invio notifica e verifica amount</summary>

**Descrizione**

1. Viene generata una nuova notifica con feePolicy `DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` legge la notifica ricevuta
4. Viene verificato il costo = `100` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_7] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con feePolicy `DELIVERY_MODE`, `payment_pagoPaForm` e creditorTaxId `77777777777`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature)

</details>

##### Avanzamento notifiche b2b con workflow cartaceo

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>


**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_RS`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok-Retry_RS`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_RS`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RIS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_RIS`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RIS_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_RIS`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER` e physicalAddress `Via@ok_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_RIR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_4] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_AR_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER` e physicalAddress `Via@fail_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_5] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_890_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_6] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_RIR_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_RIR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_7] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_fail_890_NR_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_8] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_fail_AR_NR_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_9] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail-Discovery_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_10] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail-Discovery_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_11] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER`, physicalAddress `Via@ok_AR`, denomination `Giovanna D'Arco` e taxId `DRCGNN12A46A326K`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_12] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalAddress `Via@ok_890`, denomination `Test 890 ok` e taxId `PRTCAE90A01D086M`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_13] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER`, physicalAddress `Via@fail_AR`, denomination `Test AR Fail` e taxId `MNDLCU98T68C933T`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_14] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica, physicalAddress `Via@fail_890`, denomination `Test 890 Fail` e taxId `PRVMNL80A01F205M`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_15] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_16] Attesa elemento di timeline SEND_ANALOG_FEEDBACK e verifica campo SEND_ANALOG_FEEDBACK positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalAddress `Via@fail_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK`
4. Viene verificato il campo sendRequestId dell' evento di timeline `SEND_ANALOG_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_17] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `PREPARE_ANALOG_DOMICILE`
4. Viene verificato che nell'elemento di timeline della notifica `PREPARE_ANALOG_DOMICILE` siano configurati i campi `municipalityDetails` e `foreignState`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_18] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_ANALOG_DOMICILE` siano configurati i campi `municipalityDetails` e `foreignState`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_19] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER` e physicalAddress `Via@ok_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_ANALOG_DOMICILE` sia valorizzato il campo `serviceLevel` con `AR_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_20] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_ANALOG_DOMICILE` sia valorizzato il campo `serviceLevel` con `REGISTERED_LETTER_890`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_21] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER` e physicalAddress `Via@ok_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `PREPARE_ANALOG_DOMICILE`
4. Viene verificato che nell'elemento di timeline della notifica `PREPARE_ANALOG_DOMICILE` sia valorizzato il campo `serviceLevel` con `AR_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_22] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `PREPARE_ANALOG_DOMICILE`
4. Viene verificato che nell'elemento di timeline della notifica `PREPARE_ANALOG_DOMICILE` sia valorizzato il campo `serviceLevel` con `REGISTERED_LETTER_890`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_23] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER` e physicalAddress `Via@ok_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` sia valorizzato il campo `serviceLevel` con `AR_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_24] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@ok_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` sia valorizzato il campo `serviceLevel` con `REGISTERED_LETTER_890`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_25] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica, taxId `MNDLCU98T68C933T`, physicalCommunication `AR_REGISTERED_LETTER` e physicalAddress `Via@fail_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`
4. `MNDLCU98T68C933T` CF non valido per eseguire il test sul nuovo DEV2

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_26] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890 negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica, taxId `PRVMNL80A01F205M` e physicalAddress `Via@fail_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`
4. `PRVMNL80A01F205M` ha un indirizzo PEC

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_27] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER` e physicalAddress `Via@fail_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_28] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_29] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_RIR_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail_RIR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_30] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_NR negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `AR_REGISTERED_LETTER`, taxId `MNTMRA03M71C615V` e physicalAddress `Via NationalRegistries @fail_AR 5`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_31] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_NR negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, taxId `MNZLSN99E05F205J` e physicalAddress `Via NationalRegistries @fail_890 5`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_32] Invio notifica digitale senza allegato ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo numero pagine AAR</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via minzoni`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE` verifica numero pagine AAR 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_33] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECAG011A positivo PN-5783</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e digitalDomicile_address `test@fail.it` e physicalAddress `Via@ok_RS`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG011A`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_34] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECAG005C positivo PN-6093</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `via@OK-Giacenza-lte10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECAG005C`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_35] Attesa elemento di timeline SEND_ANALOG_DOMICILE_scenario positivo PN-5283 Presente</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via@fail-Discovery_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE` e verifica indirizzo secondo tentativo `ATTEMPT_1`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_36] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-Giacenza-gt10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_37] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-GiacenzaDelegato-gt10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_38] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@FAIL-Giacenza-gt10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_39] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-CompiutaGiacenza_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_40] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**


1. Viene generata una nuova notifica con destinatario persona fisica e taxID `CLMCST42R12D969Z` e physicalAddress `via@OK-Giacenza-gt10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG005C` e verifica data delay più `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_41] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**


1. Viene generata una nuova notifica con destinatario persona fisica e taxID `CLMCST42R12D969Z` e physicalAddress `via@OK-GiacenzaDelegato-gt10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG006C` e verifica data delay più `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_42] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**


1. Viene generata una nuova notifica con destinatario persona fisica e taxID `CLMCST42R12D969Z` e physicalAddress `via@FAIL-Giacenza-gt10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG007C` e verifica data delay più `0`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_43] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**


1. Viene generata una nuova notifica con destinatario persona fisica e taxID `CLMCST42R12D969Z` e physicalAddress `via@OK-CompiutaGiacenza_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG008C` e verifica data delay più `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_44] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-Giacenza-gt10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_45] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 AR momentaneamente non rendicontabile positivo PN-6079</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-CausaForzaMaggiore_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN015`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN001B`  e verifica tipo DOC `AR`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN001C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_46] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 AR momentaneamente non rendicontabile positivo PN-6079</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-NonRendicontabile_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080` tentativo `ATTEMPT_0.IDX_1`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN013`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080` tentativo `ATTEMPT_0.IDX_3`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN001B` e verifica tipo DOC `AR`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN001C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_47] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 890 momentaneamente non rendicontabile positivo PN-6079</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-CausaForzaMaggiore_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG015`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG001B` e verifica tipo DOC `23L`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN001C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_48] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 890 momentaneamente non rendicontabile positivo PN-6079</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario persona fisica e taxID `LVLDAA85T50G702B` e physicalAddress `via@OK-NonRendicontabile_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG013`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG001B` e verifica tipo DOC `23L`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN001C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_49] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza_AR- PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `via@OK-Giacenza_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN003B` e verifica tipo DOC `AR`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN003C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_50] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza_AR PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@FAIL-Giacenza_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN011`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN004B` e verifica tipo DOC `Plico`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN004C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_51] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@FAIL-CompiutaGiacenza_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN011`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN005B` e verifica tipo DOC `Plico`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_52] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica con taxID `LVLDAA85T50G702B` e physicalAddress `Via@FAIL-Giacenza-lte10_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG011A`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG007B` e verifica tipo DOC `Plico`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECAG007C` e verifica data delay più `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_53] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_AR_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@FAIL-DiscoveryIrreperibile_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080` tentativo "ATTEMPT_0"
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN002E` e verifica tipo DOC `Plico` tentativo `ATTEMPT_0`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN002E` e verifica tipo DOC `Indagine` tentativo `ATTEMPT_0`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN002F` e deliveryFailureCause `M01` tentativo `ATTEMPT_0`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080` tentativo "ATTEMPT_1"
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN002E` e verifica tipo DOC `Plico` tentativo `ATTEMPT_1`
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN002F` e deliveryFailureCause `M03` tentativo `ATTEMPT_1`
10. vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_54] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@FAIL-CompiutaGiacenza_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN011`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN005B` e verifica tipo DOC `Plico`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN005C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_55] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza-gt10_AR PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@OK-Giacenza-gt10_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN011`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNRN012`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN003B` e verifica tipo DOC `AR`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN003C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_56] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza-gt10_AR PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@FAIL-Giacenza-gt10_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN011`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNRN012`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN004B` e verifica tipo DOC `Plico`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN004B`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_57] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@FAIL-CompiutaGiacenza-gt10_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECRN002C`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_58] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_890_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@FAIL-Giacenza-gt10_AR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080` tentativo `ATTEMPT_0`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG003E` e verifica tipo DOC `Plico` tentativo `ATTEMPT_0`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECAG003F` e verifica tipo DOC `M03` tentativo `ATTEMPT_0`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080` tentativo `ATTEMPT_1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG003E` e verifica tipo DOC `Plico` tentativo `ATTEMPT_1`
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `RECAG003F`  e deliveryFailureCause `M03` tentativo `ATTEMPT_1`
9. vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_59] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza-gt10-23L_890 PN-5927</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `via@FAIL-Giacenza-gt10-23L_890`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `CON080`
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG011A`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con deliveryDetailCode `PNAG012`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG007B` e verifica tipo DOC `23L` tentativo `ATTEMPT_0.IDX_3`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG007C`  e verifica data delay più `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_60] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESSdeliveryDetailCode "RECRI001" scenario positivo PN-6634</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@ok_RIR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRI001`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_61] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS_deliveryDetailCode "RECRI002" scenario positivo PN-6634</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario `Mario Gherkin` con physicalAddress `Via@fail_RIR`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRI002`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>


##### Costo notifica con workflow analogico per persona fisica 890

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_1] Invio notifica verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 399` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_2] Invio notifica verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_3] Invio notifica con allegato verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `DELIVERY_MODE` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 521` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_4] Invio notifica con allegato e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `FLAT_RATE` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `DELIVERY_MODE` e `physicalAddress_zip 16121`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 391` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `FLAT_RATE` e `physicalAddress_zip 16121`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_7] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `DELIVERY_MODE`, `physicalAddress_zip 16121` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 521` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_8] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, physicalCommunication `REGISTERED_LETTER_890 `, physicalAddress `Via@ok_890`, feePolicy `FLAT_RATE`, `physicalAddress_zip 16121` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogico890.feature)

</details>


##### Costo notifica con workflow analogico per persona fisica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 400` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |
| payment_pagoPaForm           | NULL           |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 565` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |
| payment_pagoPaForm           | NULL           |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy DELIVERY_MODE` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 533` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy FLAT_RATE` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| payment_pagoPaForm           | SI             |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 798` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| payment_pagoPaForm           | SI             |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy DELIVERY_MODE` e `physicalAddress_zip 38121`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 374` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy FLAT_RATE` e `physicalAddress_zip 38121`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value      |
| ---------------------------- | ---------- |
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | 75007      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |
| payment_pagoPaForm           | NULL       |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 511` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value      |
| ---------------------------- | ---------- |
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | 75007      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |
| payment_pagoPaForm           | NULL       |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy DELIVERY_MODE`, `physicalAddress_zip 38121` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 497` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `physicalAddress_address Via@ok_AR`, `feePolicy FLAT_RATE`, `physicalAddress_zip 38121` e `payment_pagoPaForm SI`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value      |
| ---------------------------- | ---------- |
| payment_pagoPaForm           | SI         |
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | 75007      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 700` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value      |
| ---------------------------- | ---------- |
| payment_pagoPaForm           | SI         |
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | 75007      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |

1. Viene generata una nuova notifica con destinatario persona fisica, `physicalCommunication AR_REGISTERED_LETTER`, `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoAR.feature)

</details>

##### Costo notifica con workflow analogico per persona fisica RIS

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 233` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value        |
| ---------------------------- | ------------ |
| physicalAddress_State        | FRANCIA      |
| physicalAddress_municipality | Parigi       |
| physicalAddress_zip          | 75007        |
| physicalAddress_province     | Paris        |
| digitalDomicile_address      | test@fail.it |
| physicalAddress_address      | Via@ok_RIS   |
| payment_pagoPaForm           | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 233` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value        |
| ---------------------------- | ------------ |
| physicalAddress_State        | FRANCIA      |
| physicalAddress_municipality | Parigi       |
| physicalAddress_zip          | 75007        |
| physicalAddress_province     | Paris        |
| digitalDomicile_address      | test@fail.it |
| physicalAddress_address      | Via@ok_RIS   |
| payment_pagoPaForm           | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 233` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value        |
| ---------------------------- | ------------ |
| payment_pagoPaForm           | SI           |
| physicalAddress_State        | FRANCIA      |
| physicalAddress_municipality | Parigi       |
| physicalAddress_zip          | 75007        |
| physicalAddress_province     | Paris        |
| digitalDomicile_address      | test@fail.it |
| physicalAddress_address      | Via@ok_RIS   |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 233` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value        |
| ---------------------------- | ------------ |
| payment_pagoPaForm           | SI           |
| physicalAddress_State        | FRANCIA      |
| physicalAddress_municipality | Parigi       |
| physicalAddress_zip          | 75007        |
| physicalAddress_province     | Paris        |
| digitalDomicile_address      | test@fail.it |
| physicalAddress_address      | Via@ok_RIS   |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| physicalAddress_zip     | 39100        |
| payment_pagoPaForm      | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 212` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| physicalAddress_zip     | 39100        |
| payment_pagoPaForm      | NULL         |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| digitalDomicile_address      | test@fail.it   |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIS     |
| payment_pagoPaForm           | NULL           |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 302` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| digitalDomicile_address      | test@fail.it   |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIS     |
| payment_pagoPaForm           | NULL           |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| payment_pagoPaForm      | SI           |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| physicalAddress_zip     | 39100        |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 212` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter               | value        |
| ----------------------- | ------------ |
| payment_pagoPaForm      | SI           |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| physicalAddress_zip     | 39100        |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| payment_pagoPaForm           | SI             |
| digitalDomicile_address      | test@fail.it   |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIS     |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 302` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

Dati destinatario

| parameter                    | value          |
| ---------------------------- | -------------- |
| payment_pagoPaForm           | SI             |
| digitalDomicile_address      | test@fail.it   |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIS     |

1. Viene generata una nuova notifica con destinatario persona fisica e `feePolicy FLAT_RATE`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il `costo = 0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il `costo = 0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFCostoAnalogicoRS.feature)

</details>


#### Persona giuridica

##### Avanzamento b2b persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_8] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa` con digitalAddress `test@fail.it`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `PREPARE_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_9] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa` con digitalAddress `test@fail.it`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa` con digitalAddress `test@fail.it`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_11] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_12] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa` con digitalAddress `test@fail.it`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_13] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa` con digitalAddress `test@fail.it`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_14] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campo deliveryDetailCode positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `OK`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `OK` sia presente il campo deliveryDetailCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_15] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Gherkin spa` con digitalAddress `test@gmail.it`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `KO`
4. Viene verificato che nell'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `KO` sia presenti i campi deliveryDetailCode e deliveryFailureCause

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG-CF_1] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG-CF_2] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG-CF_3] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPG.feature)

</details>

##### Avanzamento b2b notifica multi destinatario persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_8] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Mario Cucumber`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG-CF_1] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG-CF_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG-CF_3] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica multi destinatario a `Gherkin spa` e `Cucumber Society`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGMulti.feature)

</details>

##### Avanzamento b2b persona giuridica pagamento

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber srl`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Il destinatario legge la notifica ricevuta
4. Vengono verificati costo e data di perfezionamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_2] Invio notifica e verifica amount</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber srl`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_3] Invio notifica FLAT e verifica amount</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_4] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber srl`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_5] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Il modello f24 viene pagato correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_6] Invio notifica e verifica amount</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber srl`
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_7] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario
   - **recipientType**: PG
   - **taxId**: 70472431207
   - **denomination**: VeryBadCaligola Snc
   - **payment_pagoPaForm**: SI
   - **payment_f24flatRate**: NULL
   - **payment_f24standard**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_8] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario
   - **recipientType**: PG
   - **taxId**: CCRMCT06A03A433H
   - **payment_creditorTaxId**: 77777777777
   - **payment_pagoPaForm**: SI
   - **payment_f24flatRate**: NULL
   - **payment_f24standard**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_9] Invio notifica e verifica amount</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario
   - **recipientType**: PG
   - **taxId**: LELPTR04A01C352E
   - **denomination**: Le Epistolae srl
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_10] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario
   - **recipientType**: PG
   - **taxId**: LELPTR04A01C352E
   - **denomination**: Le Epistolae srl
   - **payment_pagoPaForm**: SI
   - **payment_f24flatRate**: NULL
   - **payment_f24standard**: NULL
   - **payment_creditorTaxId**: 77777777777
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_11] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario
   - **recipientType**: PG
   - **taxId**: LELPTR04A01C352E
   - **denomination**: Le Epistolae srl
   - **payment_pagoPaForm**: SI
   - **payment_f24flatRate**: NULL
   - **payment_f24standard**: NULL
   - **payment_creditorTaxId**: 77777777777
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_12] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario
   - **recipientType**: PG
   - **taxId**: 70412331207
   - **payment_pagoPaForm**: SI
   - **payment_f24flatRate**: NULL
   - **payment_f24standard**: NULL
   - **payment_creditorTaxId**: 77777777777
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende il corretto pagamento della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotificheB2bPGPagamento.feature)

</details>

##### Avanzamento notifiche analogico persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok-Retry_RS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@fail_RS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RIS_1] Invio notifica digitale ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RIS_2] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@fail_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_RIR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_6] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail_RIR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_7] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail-Discovery_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_8] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail-Discovery_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_9] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_DOMICILE`
4. Viene verificato che il campo serviceLevel dell'evento di timeline `SEND_ANALOG_DOMICILE` sia valorizzato con `AR_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_10] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_DOMICILE`
4. Viene verificato che il campo serviceLevel dell'evento di timeline `SEND_ANALOG_DOMICILE` sia valorizzato con `REGISTERED_LETTER_890`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_11] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `PREPARE_ANALOG_DOMICILE`
4. Viene verificato che il campo serviceLevel dell'evento di timeline `PREPARE_ANALOG_DOMICILE` sia valorizzato con `AR_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_12] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `PREPARE_ANALOG_DOMICILE`
4. Viene verificato che il campo serviceLevel dell'evento di timeline `PREPARE_ANALOG_DOMICILE` sia valorizzato con `REGISTERED_LETTER_890`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_13] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_FEEDBACK`
4. Viene verificato che il campo serviceLevel dell'evento di timeline `SEND_ANALOG_FEEDBACK` sia valorizzato con `AR_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_14] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `SEND_ANALOG_FEEDBACK`
4. Viene verificato che il campo serviceLevel dell'evento di timeline `SEND_ANALOG_FEEDBACK` sia valorizzato con `REGISTERED_LETTER_890`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_15] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_16] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_17] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@fail_RIR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino allo stato della notifica `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_18] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. L'avviso pagopa viene pagato correttamente
4. Si attende la corretta sospensione dell'invio cartaceo

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGAnalogico.feature)

</details>

##### Costo notifica con workflow analogico per persona giuridica 890


<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_1] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
   - **physicalAddress_zip**: 00010
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `399` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
   - **physicalAddress_zip**: 00010
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_3] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `521` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_4] Invio notifica con allegato e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_3] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
   - **physicalAddress_zip**: 16121
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `391` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_4] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
   - **physicalAddress_zip**: 64010
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_7] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_zip**: 16121
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `516` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_8] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_zip**: 16121
   - **physicalAddress_address**: Via@ok_890
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogico890.feature)

</details>

##### Costo notifica con workflow analogico per persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
   - **physicalAddress_zip**: 18025
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `400` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
   - **physicalAddress_zip**: 18025
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 88010
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIR
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `565` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 88010
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIR
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE`
5. Viene verificato il costo = `0` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_5] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
   - **physicalAddress_zip**: 60121
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE`
4. Viene verificato il costo = `381` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_6] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
   - **physicalAddress_zip**: 60121
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_7] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Paris
   - **physicalAddress_address**: Via@ok_RIR
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE`
4. Viene verificato il costo = `856` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_8] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Paris
   - **physicalAddress_address**: Via@ok_RIR
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>

##### Costo notifica con workflow analogico per persona giuridica RS

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
4. Viene verificato il costo = `323` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
   - **physicalAddress_zip**: 01100
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: ZONE_1
   - **physicalAddress_province**: Paris
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RIS
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
4. Viene verificato il costo = `691` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**


1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: ZONE_1
   - **physicalAddress_province**: Paris
   - **physicalAddress_address**: Via@ok_RIS
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_5] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
   - **physicalAddress_zip**: 70122
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
4. Viene verificato il costo = `262` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_6] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**


1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
   - **physicalAddress_zip**: 70122
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_7] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 60012
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIS
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
4. Viene verificato il costo = `798` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_8] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**


1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 60012
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>

#### Persona fisica e giuridica

##### Avanzamento notifiche b2b multi destinatario con persona fisica e giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_01] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Destinatario `Gherkin spa`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_02] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Destinatario `Gherkin spa`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_03] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Destinatario `Gherkin spa`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_04] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Destinatario `Gherkin spa`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_05] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Destinatario `Gherkin spa`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PF_06] Invio notifica multidestinatario con pagamento destinatario 0 e 1 scenario  positivo</summary>

**Descrizione**

1. Destinatario con taxID `LVLDAA85T50G702B`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. l'avviso pagopa viene pagato correttamente dall'utente `0`
6. si attende il corretto pagamento della notifica dell'utente `0`
7. vengono letti gli eventi fino all'elemento di timeline della notifica `PAYMENT` per l'utente `0`
8. l'avviso pagopa viene pagato correttamente dall'utente `1`
9. si attende il corretto pagamento della notifica dell'utente `1`
10. vengono letti gli eventi fino all'elemento di timeline della notifica `PAYMENT` per l'utente `1`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PF_07] Invio notifica multidestinatario con pagamento destinatario 0 e non del destinatario 1 scenario  positivo</summary>

**Descrizione**

1. Destinatario con taxID `LVLDAA85T50G702B`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. l'avviso pagopa viene pagato correttamente dall'utente `0`
6. si attende il corretto pagamento della notifica dell'utente `0`
7. vengono letti gli eventi fino all'elemento di timeline della notifica `PAYMENT` per l'utente `0`
8. non vengono letti gli eventi fino all'elemento di timeline della notifica `PAYMENT` per l'utente `1`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PF_08] Invio notifica multidestinatario con pagamento destinatario 1 e non del destinatario 0 scenario  positivo</summary>

**Descrizione**

1. Destinatario con taxID `LVLDAA85T50G702B`
2. Destinatario `Mario Cucumber`
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. l'avviso pagopa viene pagato correttamente dall'utente `1`
6. si attende il corretto pagamento della notifica dell'utente `1`
7. vengono letti gli eventi fino all'elemento di timeline della notifica `PAYMENT` per l'utente `1`
8. non vengono letti gli eventi fino all'elemento di timeline della notifica `PAYMENT` per l'utente `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PF_09] Invio notifica multidestinatario da pagare destinatario 0 e destinatario 1 scenario  positivo</summary>

**Descrizione**

:warning: _Ignored_

1.  Destinatario con
- **denomination**: Ada
- **taxId**: LVLDAA85T50G702B
- **digitalDomicile**: NULL
- **physicalAddress_zip**: 16121
- **physicalAddress_address**:  Via@ok_890
2. Destinatario con
   - **denomination**: Polo
   - **taxId**: PLOMRC01P30L736Y
   - **digitalDomicile**: NULL
   - **physicalAddress_zip**: 16121
   - **physicalAddress_address**:  Via@ok_890
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. viene verificato il costo = `100` della notifica per l'utente `0`
6. viene verificato il costo = `100` della notifica per l'utente `1`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PF_10] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Destinatario con
   - **denomination**: Ada
   - **taxId**: LVLDAA85T50G702B
   - **digitalDomicile**: NULL
   - **physicalAddress_zip**: 16121
   - **physicalAddress_address**:  Via@ok_890
2. Destinatario con
   - **denomination**: Polo
   - **taxId**: PLOMRC01P30L736Y
   - **digitalDomicile**: NULL
   - **physicalAddress_zip**: 16121
   - **physicalAddress_address**:  Via@ok_890
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. viene verificato il costo = `100` della notifica per l'utente `0`
6. viene verificato il costo = `100` della notifica per l'utente `1`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMulti.feature)

</details>


##### Avanzamento b2b notifica multi destinatario analogico

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok-Retry_RS
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@fail_RS
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RIS_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RIS
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RIS_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@fail_RIS
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@ok_AR
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@ok_890
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_3] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@ok_RIR
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@fail_AR
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente `0`
8. Vengono letti gli eventi e verificho che l'utente `1` non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@fail_890
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_6] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@fail_RIR
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `1`
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente `0`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_7] Invio notifica e atteso stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario con
   - **taxId**: PRVMNL80A01F205M
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@fail_RIR
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente `0`
6. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_8] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@fail-Discovery_AR
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_9] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario  positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin con
   - **digitalDomicile_address**: NULL
   - **physicalAddress_address**: Via@fail-Discovery_AR
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>

##### Costo notifica con workflow analogico per multi destinatario 890

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_1] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                    | value      |
|-----------------------------|------------|
| digitalDomicile             | NULL       |
| physicalAddress_address     | Via@ok_890 |
| physicalAddress_address_zip | 00010      |
| payment_pagoPaForm          | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `842` della notifica per l'utente 0
9.  viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value      |
|------------------------------| ---------- |
| digitalDomicile              | NULL       |
| physicalAddress_address      | Via@ok_890 |
| physicalAddress_address_zip  | 00010      |
| payment_pagoPaForm           | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1



[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_3] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 64010      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `887` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_4] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 64010      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8.  viene verificato il costo = `0` della notifica per l'utente 0
9.  viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>

##### Costo notifica con workflow analogico per multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- |------------|
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 18025      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `508` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 18025      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value          |
| ---------------------------- | -------------- |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |
| payment_pagoPaForm           | NULL           |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `963` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value          |
| ---------------------------- | -------------- |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 88010          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |
| payment_pagoPaForm           | NULL           |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_5] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 60121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `381` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_6] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 60121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_7] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value      |
| ---------------------------- | ---------- |
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | 75007      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |
| payment_pagoPaForm           | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `856` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_8] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value      |
| ---------------------------- | ---------- |
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | 75007      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |
| payment_pagoPaForm           | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>

##### Costo notifica con workflow analogico per multi destinatario RS

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- |--------------|
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |
| physicalAddress_zip     | 01100        |


1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `323` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |
| physicalAddress_zip     | 01100        |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value      |
| ---------------------------- |------------|
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | ZONE_1     |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |
| payment_pagoPaForm           | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `691` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value      |
| ---------------------------- | ---------- |
| digitalDomicile              | NULL       |
| physicalAddress_State        | FRANCIA    |
| physicalAddress_municipality | Parigi     |
| physicalAddress_zip          | ZONE_1      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |
| payment_pagoPaForm           | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_5] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |
| physicalAddress_zip     | 70122        |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `262` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_6] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |
| physicalAddress_zip     | 70122        |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_7] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value          |
| ---------------------------- | -------------- |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 60012          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |
| payment_pagoPaForm           | NULL           |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `798` della notifica per l'utente 0
9. viene verificato il costo = `100` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_8] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                     | value          |
| ---------------------------- | -------------- |
| digitalDomicile              | NULL           |
| physicalAddress_State        | BRASILE        |
| physicalAddress_municipality | Florianopolis  |
| physicalAddress_zip          | 60012          |
| physicalAddress_province     | Santa Catarina |
| physicalAddress_address      | Via@ok_RIR     |
| payment_pagoPaForm           | NULL           |

1. viene generata nuova notifica
2. destinatario Mario Gherkin
3. destinatario Cucumber Society
4. la notifica viene inviata tramite api b2b dal `Comune_Multi`
5. si attende che lo stato diventi `ACCEPTED`
6. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. viene verificato il costo = `0` della notifica per l'utente 0
9. viene verificato il costo = `0` della notifica per l'utente 1


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>


### Download

#### Persona fisica

##### Download legalFact

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
6. La PA richiede il download dell'attestazione opponibile `SENDER_ACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
6. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
6. La PA richiede il download dell'attestazione opponibile `PEC_RECEIPT`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`
6. La PA richiede il download dell'attestazione opponibile `RECIPIENT_ACCESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
6. Viene richiesto tramite appIO il download dell'attestazione opponibile `SENDER_ACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
6. Viene richiesto tramite appIO il download dell'attestazione opponibile `DIGITAL_DELIVERY`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
6. Viene richiesto tramite appIO il download dell'attestazione opponibile`PEC_RECEIPT`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`
6. Viene richiesto tramite appIO il download dell'attestazione opponibile `RECIPIENT_ACCESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
6. `Mario Gherkin` richiede il download dell'attestazione opponibile `SENDER_ACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
6. `Mario Gherkin` richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
6. `Mario Gherkin` richiede il download dell'attestazione opponibile `PEC_RECEIPT`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`
6. `Mario Gherkin` richiede il download dell'attestazione opponibile `RECIPIENT_ACCESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
6. Viene verificato che la chiave dell'attestazione opponibile `SENDER_ACK` è `PN_LEGAL_FACTS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
6. Viene verificato che la chiave dell'attestazione opponibile `DIGITAL_DELIVERY` è `PN_LEGAL_FACTS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
6. Viene verificato che la chiave dell'attestazione opponibile `PEC_RECEIPT` è `PN_EXTERNAL_LEGAL_FACTS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`
6. Viene verificato che la chiave dell'attestazione opponibile `DIGITAL_RECIPIENT_ACCESSDELIVERY` è `PN_LEGAL_FACTS`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_5] Invio notifica e download atto opponibile SENDER_ACK_scenario senza legalFactType positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi ACCEPTED
4. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
5. la PA richiede il download dell'attestazione opponibile `SENDER_ACK` senza legalFactType


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_6] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario senza legalFactType positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi ACCEPTED
4. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
5. la PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY` senza legalFactType

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_7] Invio notifica e download atto opponibile PEC_RECEIPT_scenario senza legalFactType positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi ACCEPTED
4. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
5. la PA richiede il download dell'attestazione opponibile `PEC_RECEIPT` senza legalFactType


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_8] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario senza legalFactType positivo</summary>

**Descrizione**

1. Viene generata la notifica
2. Destinatario Mario Gherkin
3. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi ACCEPTED
4. `Mario Gherkin` legge la notifica ricevuta
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`
6. la PA richiede il download dell'attestazione opponibile `RECIPIENT_ACCESS` senza legalFactType


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature)

</details>

##### Download legalFact analogico


<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_1] Invio notifica con @fail_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@fail_RS  |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW`
5. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY_FAILURE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_2] Invio notifica con @ok_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

| paramter                | value         |
| ----------------------- |---------------|
| digitalDomicile_address | test@fail.it  |
| physicalAddress_address | Via@ok_RS     |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW`
5. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY_FAILURE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_3] Invio notifica con @fail_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value        |
| ----------------------- |--------------|
| digitalDomicile_address | NULL         |
| physicalAddress_address | Via@fail_AR  |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRN002B`
5. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN002B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
<summary>[B2B_PA_ANALOGICO_LEGALFACT_4] Invio notifica con @ok_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value      |
| ----------------------- |------------|
| digitalDomicile_address | NULL       |
| physicalAddress_address | Via@ok_890 |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECAG001B`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG001B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_5] Invio notifica con @fail_RIS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

| paramter                | value         |
| ----------------------- |---------------|
| digitalDomicile_address | test@fail.it  |
| physicalAddress_address | Via@fail_RIS  |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW`
5. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY_FAILURE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_6] Invio notifica con @ok_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RIR |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRI003B`
5. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRI003B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_7] Invio notifica con @fail_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value         |
| ----------------------- |---------------|
| digitalDomicile_address | NULL          |
| physicalAddress_address | Via@fail_RIR  |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRI004B`
5. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRI004B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_8] Invio notifica con @fail_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@fail_890 |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECAG003B`
4. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG003B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_9_TEST] Invio notifica con @FAIL-Discovery_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value                   |
| ----------------------- |-------------------------|
| digitalDomicile_address | NULL                    |
| physicalAddress_address | Via@FAIL-Discovery_AR   |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRN002E`
5. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN002E`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_10_TEST] Invio notifica con @FAIL-Discovery_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value                   |
| ----------------------- |-------------------------|
| digitalDomicile_address | NULL                    |
| physicalAddress_address | Via@FAIL-Discovery_890  |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECAG003E`
   La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG003E`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_11_TEST] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR negativo</summary>

**Descrizione**

| paramter                | value                                       |
| ----------------------- |---------------------------------------------|
| digitalDomicile_address | NULL                                        |
| physicalAddress_address | Via NationalRegistries @fail-Irreperibil_AR |
| denomination            | Test AR Fail 2                              |
| taxId                   | DVNLRD52D15M059P                            |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`
5. La PA richiede il download dell'attestazione opponibile `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_12_TEST] Invio notifica presenza allegato in corrispondenza dello stato "Aggiornamento sull'invio cartaceo" e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo PN-6090</summary>

**Descrizione**

| paramter                | value               |
| ----------------------- |---------------------|
| digitalDomicile_address | NULL                |
| physicalAddress_address | Via@OK-Giacenza_AR  |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRN003B`
5. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN003B`


[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>


##### Download legalFact multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-MULTI-RECIPIENT_LEGALFACT_1] Invio notifica multi destinatario_scenario positivo</summary>

**Descrizione**

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Mario Cucumber
4. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
5. `Mario Gherkin` legge la notifica ricevuta
6. `Mario Cucumber` legge la notifica ricevuta
7. Sono presenti 2 attestazioni opponibili `RECIPIENT_ACCESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactMulti.feature)

</details>

#### Persona giuridica

##### Download legalFact per la persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_PG_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. Viene generata nuova notifica
2. Destinatario Gherkin spa
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
6. La PA richiede il download dell'attestazione opponibile `SENDER_ACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pg/AvanzamentoNotifichePGLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_PG_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata nuova notifica
2. Destinatario Gherkin spa
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
6. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pg/AvanzamentoNotifichePGLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_PG_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. Viene generata nuova notifica
2. Destinatario Gherkin spa
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
6. La PA richiede il download dell'attestazione opponibile `PEC_RECEIPT`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pg/AvanzamentoNotifichePGLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_PG_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. viene generata nuova notifica
2. destinatario Gherkin spa
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. si attende che lo stato diventi `ACCEPTED`
5. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
6. la PA richiede il download dell'attestazione opponibile `SENDER_ACK` è `PN_LEGAL_FACTS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pg/AvanzamentoNotifichePGLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_PG_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata nuova notifica
2. Destinatario Gherkin spa
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
6. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY` è `PN_LEGAL_FACTS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pg/AvanzamentoNotifichePGLegalFact.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_PG_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. viene generata nuova notifica
2. destinatario Gherkin spa
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. si attende che lo stato diventi `ACCEPTED`
5. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
6. la PA richiede il download dell'attestazione opponibile `PEC_RECEIPT` è `PN_EXTERNAL_LEGAL_FACTS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pg/AvanzamentoNotifichePGLegalFact.feature)

</details>

### Webhook

#### Persona fisica

##### Avanzamento notifiche webhook b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_STATUS_1] Creazione stream notifica</summary>

**Descrizione**

1. Si predispone `1` nuovo stream denominato `stream-test` con eventType `STATUS`
2. Si crea il nuovo stream per il `Comune_1`
3. Lo stream è stato creato e viene correttamente recuperato dal sistema tramite `stream id`
4. Si cancella lo stream creato
5. Viene verificata la corretta cancellazione

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_1] Creazione stream notifica</summary>

**Descrizione**

1. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
2. Si crea il nuovo stream per il `Comune_1`
3. Lo stream è stato creato e viene correttamente recuperato dal sistema tramite `stream id`
4. Si cancella lo stream creato
5. Viene verificata la corretta cancellazione

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_2] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERING`
6. La persona fisica legge la notifica
7. Si verifica nello stream del `Comune_1` che la notifica abbia lo stato `VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_10] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERING`
6. La persona fisica legge la notifica
7. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_11] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_12] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERED`
6. La persona fisica legge la notifica
7. Si verifica nello stream del `Comune_1` che la notifica abbia lo stato `VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_13] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERED`
6. La persona fisica legge la notifica
7. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_14] Creazione multi stream notifica</summary>

**Descrizione**

1. Si predispone `6` nuovo stream denominato `stream-test` con eventType `STATUS`
2. Si crea il nuovo stream per il `Comune_1`
3. L'ultima creazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_15] Creazione multi stream notifica</summary>

**Descrizione**

1. Si predispone `6` nuovo stream denominato `stream-test` con eventType `TIMELINE`
2. Si crea il nuovo stream per il `Comune_1`
3. L'ultima creazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `DIGITAL_FAILURE_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOT_HANDLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_23] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b senza preload allegato dal `Comune_1` e si attende che lo stato diventi `REFUSED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`
6. Si verifica che la notifica non viene accettata causa `ALLEGATO`
7. Vengono letti gli eventi dello stream del `Comune_1` con la verifica di Allegato non trovato

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>

##### Avanzamento notifiche webhook b2b multi

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_1] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_2] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_3] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_4] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_5] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_1] Invio notifica digitale multi PG ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_2] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari giuridica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_3] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari giuridica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_4] Invio notifica digitale multi PG ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con `2` destinatari giuridica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `DIGITAL_FAILURE_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_5] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_6] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>

#### Persona giuridica

##### Avanzamento notifiche webhook b2b per persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino allo stato `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino allo stato `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino allo stato `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_8] Invio notifica digitale ed attesa elemento di timeline PREPARE_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `PREPARE_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_9] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_11] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_12] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_13] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b senza preload allegato dal `Comune_2` e si attende che lo stato diventi `REFUSED`
5. Si verifica che la notifica non viene accettata causa `ALLEGATO`
6. Then vengono letti gli eventi dello stream del `Comune_2` con la verifica di Allegato non trovato

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>

## Allegati

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_15] verifica retention time dei documenti pre-caricati</summary>

**Descrizione**

1. viene effettuato il pre-caricamento di un documento
2. viene effettuato un controllo sulla durata della retention di `ATTO OPPONIBILE` precaricato

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_16] verifica retention time pagopaForm pre-caricato</summary>

**Descrizione**

1. viene effettuato il pre-caricamento di un documento
2. viene effettuato un controllo sulla durata della retention di `PAGOPA` precaricato

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_13] verifica retention time dei documenti per la notifica inviata PG - CF</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona giuridica(CF)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene effettuato un controllo sulla durata della retention di `ATTO OPPONIBILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_14] verifica retention time pagopaForm per la notifica inviata PG - CF</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona giuridica(CF)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene effettuato un controllo sulla durata della retention di `PAGOPA`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_13] verifica retention time dei documenti per la notifica inviata PF</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene effettuato un controllo sulla durata della retention di `ATTO OPPONIBILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_14] verifica retention time pagopaForm per la notifica inviata PF</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene effettuato un controllo sulla durata della retention di `PAGOPA`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_13] verifica retention time dei documenti per la notifica inviata PG - P.Iva</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene effettuato un controllo sulla durata della retention di `ATTO OPPONIBILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_14] verifica retention time pagopaForm per la notifica inviata PG - P.Iva</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica(P.Iva)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene effettuato un controllo sulla durata della retention di `PAGOPA`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## Api Key Manager

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_1] Lettura apiKey generate_scenario positivo</summary>

**Descrizione**

1. vengono lette le apiKey esistenti
2. la lettura è avvenuta correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_2] generazione e cancellazione ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. viene modificato lo stato dell'apiKey in `BLOCK`
5. l'apiKey viene cancellata
6. vengono lette le apiKey esistenti
7. l'apiKey non è più presente

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_3] generazione e cancellazione ApiKey_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. si tenta la cancellazione dell'apiKey
5. l'operazione ha sollevato un errore con status code `409`
6. viene modificato lo stato dell'apiKey in `BLOCK`
7. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_4] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. viene modificato lo stato dell'apiKey in `BLOCK`
5. vengono lette le apiKey esistenti
6. si verifica lo stato dell'apikey `BLOCKED`
7. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_5] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. si verifica lo stato dell'apikey `ENABLED`
5. viene modificato lo stato dell'apiKey in `BLOCK`
6. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_6] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. viene modificato lo stato dell'apiKey in `ROTATE`
5. vengono lette le apiKey esistenti
6. si verifica lo stato dell'apikey `ROTATED`
7. viene modificato lo stato dell'apiKey in `BLOCK`
8. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_7] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. si verifica lo stato dell'apikey `ENABLED`
5. viene modificato lo stato dell'apiKey in `BLOCK`
6. vengono lette le apiKey esistenti
7. si verifica lo stato dell'apikey `BLOCKED`
8. viene modificato lo stato dell'apiKey in `ENABLE`
9. vengono lette le apiKey esistenti
10. si verifica lo stato dell'apikey `ENABLED`
11. viene modificato lo stato dell'apiKey in `BLOCK`
12. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_8] generazione e test ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. si verifica lo stato dell'apikey `ENABLED`
5. viene impostata l'apikey appena generata
6. viene generata una nuova notifica
7. destinatario persona fisica
8. la notifica viene inviata dal `Comune_1`
9. l'invio della notifica non ha prodotto errori
10. viene modificato lo stato dell'apiKey in `BLOCK`
11. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_9] generazione e test ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. viene modificato lo stato dell'apiKey in `BLOCK`
5. viene impostata l'apikey appena generata
6. viene generata una nuova notifica
7. destinatario persona fisica
8. la notifica viene inviata dal `Comune_1`
9. l'invio della notifica ha sollevato un errore di autenticazione `403`
10. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_10] generazione con gruppo e cancellazione ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con il primo gruppo disponibile
2. vengono lette le apiKey esistenti
3. l'apiKey creata è presente tra quelle lette
4. viene modificato lo stato dell'apiKey in `BLOCK`
5. l'apiKey viene cancellata
6. vengono lette le apiKey esistenti
7. l'apiKey non è più presente

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_11] generazione senza gruppo e invio notifica senza gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` senza gruppo
2. viene impostata l'apikey appena generata
3. viene generata una nuova notificacon `group NULL`
4. viene settato il taxId della notifica con quello dell'apikey
5. destinatario persona fisica
6. la notifica viene inviata tramite api b2b
7. l'invio della notifica non ha prodotto errori
8. viene modificato lo stato dell'apiKey in `BLOCK`
9. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_12] generazione senza gruppo e invio notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` senza gruppo
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica con group `NULL`
4. viene settato il taxId della notifica con quello dell'apikey
5. viene settato per la notifica corrente il primo gruppo valido del comune `Comune_1`
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b
8. l'invio della notifica non ha prodotto errori
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_13] generazione con gruppo e invio notifica senza gruppo ApiKey_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con il primo gruppo disponibile
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica con group `NULL`
4. viene settato il taxId della notifica con quello dell'apikey
5. destinatario persona fisica
6. la notifica viene inviata tramite api b2b
7. l'invio della notifica ha sollevato un errore `400`
8. viene modificato lo stato dell'apiKey in `BLOCK`
9. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_14] generazione con gruppo e invio notifica con lo stesso gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con il primo gruppo disponibile
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica
4. viene settato il gruppo della notifica con quello dell'apikey
5. viene settato il taxId della notifica con quello dell'apikey
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b
8. l'invio della notifica non ha prodotto errori
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_15] generazione con gruppo e invio notifica con un gruppo differente ApiKey_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con il primo gruppo disponibile
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica
4. viene settato il taxId della notifica con quello dell'apikey
5. viene settato un gruppo differente da quello utilizzato nell'apikey per il comune `Comune_1`
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b
8. l'invio della notifica ha sollevato un errore `400`
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_16] generazione con gruppo e invio notifica con gruppo e lettura notifica senza gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con il primo gruppo disponibile
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica
4. viene settato il gruppo della notifica con quello dell'apikey
5. viene settato il taxId della notifica con quello dell'apikey
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
8. si verifica la corretta acquisizione della notifica
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata
11. Viene creata una nuova apiKey per il comune `Comune_1` senza gruppo
12. viene impostata l'apikey appena generata
13. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
14. viene modificato lo stato dell'apiKey in `BLOCK`
15. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_17] generazione con gruppo e invio notifica con gruppo e lettura notifica con gruppo diverso ApiKey_scenario netagivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con il primo gruppo disponibile
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica
4. viene settato il gruppo della notifica con quello dell'apikey
5. viene settato il taxId della notifica con quello dell'apikey
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
8. si verifica la corretta acquisizione della notifica
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata
11. Viene creata una nuova apiKey per il comune `Comune_1` con gruppo differente del invio notifica
12. viene impostata l'apikey appena generata
13. si tenta il recupero dal sistema tramite codice IUN
14. il recupero della notifica ha sollevato un errore `404`
15. viene modificato lo stato dell'apiKey in `BLOCK`
16. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_18] generazione senza gruppo e invio notifica senza gruppo e lettura notifica senza gruppo  ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` senza gruppo
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica con group `NULL`
4. viene settato il taxId della notifica con quello dell'apikey
5. destinatario persona fisica
6. la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
7. si verifica la corretta acquisizione della notifica
8. viene modificato lo stato dell'apiKey in `BLOCK`
9. l'apiKey viene cancellata
10. Viene creata una nuova apiKey per il comune `Comune_1` senza gruppo
11. viene impostata l'apikey appena generata
12. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
13. viene modificato lo stato dell'apiKey in `BLOCK`
14. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_19] generazione con gruppo e invio notifica con gruppo e lettura notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con il primo gruppo disponibile
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica
4. viene settato il gruppo della notifica con quello dell'apikey
5. viene settato il taxId della notifica con quello dell'apikey
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
8. si verifica la corretta acquisizione della notifica
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata
11. Viene creata una nuova apiKey per il comune `Comune_1` con gruppo uguale del invio notifica
12. viene impostata l'apikey appena generata
13. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
14. viene modificato lo stato dell'apiKey in `BLOCK`
15. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_20] generazione senza gruppo e invio notifica con gruppo e lettura notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` senza gruppo
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica con group `NULL`
4. viene settato il taxId della notifica con quello dell'apikey
5. viene settato per la notifica corrente il primo gruppo valido del comune `Comune_1`
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
8. si verifica la corretta acquisizione della notifica
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata
11. Viene creata una nuova apiKey per il comune `Comune_1` con gruppo uguale del invio notifica
12. viene impostata l'apikey appena generata
13. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
14. viene modificato lo stato dell'apiKey in `BLOCK`
15. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_21] generazione con gruppo non valido ApiKey_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova apiKey con il gruppo `AAAAAAAAAA`
2. l'operazione ha sollevato un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_22] generazione con multi gruppi e invio notifica senza gruppo ApiKey_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con due gruppi
2. viene impostata l'apikey appena generata
3. viene generata una nuova notificacon group `NULL`
4. viene settato il taxId della notifica con quello dell'apikey
5. destinatario persona fisica
6. la notifica viene inviata tramite api b2b
7. l'invio della notifica ha sollevato un errore `400`
8. viene modificato lo stato dell'apiKey in `BLOCK`
9. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_23] generazione con multi gruppi e invio notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con due gruppi
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica
4. viene settato per la notifica corrente il primo gruppo valido del comune `Comune_1`
5. viene settato il taxId della notifica con quello dell'apikey
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b
8. l'invio della notifica non ha prodotto errori
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_24] generazione con multi gruppi e invio notifica con gruppo e lettura notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova apiKey per il comune `Comune_1` con due gruppi
2. viene impostata l'apikey appena generata
3. viene generata una nuova notifica
4. viene settato il taxId della notifica con quello dell'apikey
5. viene settato per la notifica corrente il primo gruppo valido del comune `Comune_1`
6. destinatario persona fisica
7. la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
8. si verifica la corretta acquisizione della notifica
9. viene modificato lo stato dell'apiKey in `BLOCK`
10. l'apiKey viene cancellata
11. Viene creata una nuova apiKey per il comune `Comune_1` con gruppo uguale del invio notifica
12. viene impostata l'apikey appena generata
13. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
14. viene modificato lo stato dell'apiKey in `BLOCK`
15. l'apiKey viene cancellata

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## Downtime logs

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[DOWNTIME-LOGS_1] lettura downtime-logs</summary>

**Descrizione**

1. Vengono letti gli eventi di disservizio degli ultimi 30 giorni relativi alla `creazione notifiche`
2. Viene individuato se presente l'evento più recente
3. Viene scaricata la relativa attestazione opponibile
4. L'attestazione opponibile è stata correttamente scaricata

[Feature link](src/test/resources/it/pagopa/pn/cucumber/DowntimeLogs.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[DOWNTIME-LOGS_2] lettura downtime-logs</summary>

**Descrizione**

1. Vengono letti gli eventi di disservizio degli ultimi 30 giorni relativi alla `visualizzazione notifiche`
2. Viene individuato se presente l'evento più recente
3. Viene scaricata la relativa attestazione opponibile
4. L'attestazione opponibile è stata correttamente scaricata

[Feature link](src/test/resources/it/pagopa/pn/cucumber/DowntimeLogs.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[DOWNTIME-LOGS_3] lettura downtime-logs</summary>

**Descrizione**

1. Vengono letti gli eventi di disservizio degli ultimi 30 giorni relativi alla `workflow notifiche`
2. Viene individuato se presente l'evento più recente
3. Viene scaricata la relativa attestazione opponibile
4. L'attestazione opponibile è stata correttamente scaricata

[Feature link](src/test/resources/it/pagopa/pn/cucumber/DowntimeLogs.feature)

</details>

## User Attributes

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-TOS_1] Viene recuperato il consenso TOS e verificato che sia `accepted` TOS_scenario positivo</summary>

**Descrizione**

1. Viene richiesto l'ultimo consenso di tipo `TOS`
2. Il recupero del consenso non ha prodotto errori
3. Il consenso è accettato

[Feature link](src/test/resources/it/pagopa/pn/cucumber/UserAttributes.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[USER-ATTR_2] inserimento pec errato</summary>

**Descrizione**

:warning: _Ignored_

1. Si predispone addressbook per l'utente `Mario Cucumber`
2. Viene richiesto l'inserimento della pec `test@test@fail.@`
3. L'inserimento ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/UserAttributes.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[USER-ATTR_3] inserimento telefono errato</summary>

**Descrizione**

:warning: _Ignored_

1. Si predispone addressbook per l'utente `Mario Cucumber`
2. Viene richiesto l'inserimento del numero di telefono `+0013894516888`
3. L'inserimento ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/UserAttributes.feature)

</details>

## Test di integrazione della pubblica amministrazione

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Cucumber`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_2] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Cucumber`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
5. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
6. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_3] download documento notificato_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Cucumber`
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene richiesto il download del documento `NOTIFICA`
5. Il download si conclude correttamente

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_5] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Cucumber` e
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **payment_f24flatRate**: NULL
   - **payment_f24standard**: NULL
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_6] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Cucumber` e
   - **payment_creditorTaxId**: 77777777777
2. Viene inviata dal `Comune_1`
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_7] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Cucumber`
2. Viene inviata dal `Comune_1`
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_0.1] Creazione stream notifica</summary>

**Descrizione**

1. Si predispone 1 nuovo stream denominato `stream-test` con eventType `STATUS`
2. Si crea il nuovo stream per il `Comune_1`
3. Lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
4. Si cancella lo stream creato
5. Viene verificata la corretta cancellazione

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_0.2] Creazione stream notifica</summary>

**Descrizione**

1. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
2. Si crea il nuovo stream per il `Comune_1`
3. Lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
4. Si cancella lo stream creato
5. Viene verificata la corretta cancellazione

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `ACCEPTED`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `REQUEST_ACCEPTED`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `AAR_GENERATION`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `GET_ADDRESS`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERING`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. Si predispone 1 nuovo stream denominato `stream-test` con eventType `TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERED`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC_PA_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
4. La PA richiede il download dell'attestazione opponibile `SENDER_ACK`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC_PA_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
4. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC_PA_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
4. La PA richiede il download dell'attestazione opponibile `PEC_RECEIPT`

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-PAY_1] Invio notifica e verifica amount</summary>

**Descrizione**

1. Viene generata una nuova notifica mono destinatario con destinatario `Mario Gherkin`
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Viene verificato il costo = `100` della notifica

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>

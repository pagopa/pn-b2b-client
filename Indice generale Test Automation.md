# Questo elenco riporta i Test Automatici che sono attualmente implementati
## Table of Contents

1. [Invio Notifica](#invio-notifica)
   1. [Invio](#invio)
   2. [Download](#download)
   3. [Validation](#validation)
2. [Visualizzazione notifica](#visualizzazione-notifica)
   1. [Deleghe](#deleghe)
   2. [Destinatario persona fisica](#destinatario-persona-fisica)
3. [Workflow notifica](#workflow-notifica)
   1. [B2B](#b2b)
   2. [Download](#notifica)
   3. [Webhook](#webhook)
4. [Allegati](#allegati)
5. [Api Key Manager](#api-key-manager)
6. [Downtime logs](#downtime-logs)
7. [User Attributes](#user-attributes)
8. [Test di integrazione della pubblica amministrazione](#test-di-integrazione-della-pubblica-amministrazione)


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
5. Si attende che la notifica passi in stato `ACCEPTED`
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
5. L'operazione va in errore con codice 409

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_4] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_1` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene generata una nuova notifica con uguale codice fiscale del creditore ma diverso codice avviso
5. Si attende che la notifica passi in stato `ACCEPTED`
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
5. Si attende che la notifica passi in stato `ACCEPTED`
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
  <summary>[B2B-PA-SEND_21] Invio notifica digitale mono destinatario con noticeCode ripetuto prima notifica rifiutata</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `REFUSED`
3. Viene generata una nuova notifica valida con uguale codice fiscale del creditore e uguale codice avviso
4. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
5. Si recupera la notifica tramite IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_21] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario senza pagamento
2. Viene inviata dal `Comune_Multi`
3. Si verifica la corretta acquisizione della richiesta di invio notifica, controllando la presenza del requestId e dello stato della richiesta

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_22] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario senza pagamento, ma con il campo amount valorizzato
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Si recupera la notifica tramite IUN
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

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
5. L'operazione va in errore con codice 409

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
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `REFUSED`
3. Si verifica che la notifica non viene accettata causa taxId errato

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pf/InvioNotificheB2bPF2.feature)

</details>

- [B2B-PA-SEND_36] Invio notifica  mono destinatario con allegato Injection scenario negativo
- [B2B-PA-SEND_37] Invio notifica  mono destinatario con allegato OverSize scenario negativo
- [B2B-PA-SEND_38] Invio notifica  mono destinatario con allegato OverSize scenario negativo

##### Invio notifiche b2b con altre PA, multi-destinatario e senza pagamento

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-MULTI-PA-SEND_1] Invio notifica digitale_scenario negativo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario
2. Viene inviata tramite api b2b dal `Comune_2` e si aspetta che lo stato passi in `ACCEPTED`
3. Si tenta il recupero tramite `Comune_1`
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
2. Quando la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_2] Invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. La notifica viene inviata tramite api b2b
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
6. Quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`
7. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_3] invio notifiche digitali mono destinatario (p.giuridica con P.Iva)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e idempotenceToken `AME2E3626070001.1`
2. La notifica viene inviata tramite api b2b
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
2. La notifica viene inviata tramite api b2b
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
6. Quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`
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
6. Quando la notifica viene inviata tramite api b2b (NON aspetta che passi in stato `ACCEPTED`)
7. L'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_9] invio notifiche digitali mono destinatario senza physicalAddress (p.giuridica)_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e physical address `NULL`
2. Quando la notifica viene inviata
3. Si verifica la corretta acquisizione della notifica
4. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. Quando la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Si tenta il recupero della notifica dal sistema tramite codice IUN
5. L'operazione ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_11] Invio notifica digitale mono destinatario Flat_rate_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e con PagoPA payment form fee policy `FLAT_RATE`
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e con PagoPA payment form fee policy `DELIVERY_MODE`
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_15] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) senza taxonomy code
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_16] Invio notifica digitale mono destinatario con taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con taxonomy code
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. Viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_17] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva), creditorTaxId e senza PagoPA payment form
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_18] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva), creditorTaxId
2. La notifica viene inviata
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_19] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e senza pagamento
2. La notifica viene inviata
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_20] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con importo(amount) 2550
2. La notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN
5. L'importo della notifica è 2550

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_21] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con physicalCommunication `REGISTERED_LETTER_890`
2. La notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_22] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con physicalCommunication `AR_REGISTERED_LETTER`
2. La notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_23] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con idempotenceToken `AME2E3626070001.3`
2. La notifica viene inviata
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
5. La notifica viene inviata dal `Comune_1`
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
2. La notifica viene inviata dal `Comune_1`
3. Viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>

##### Invio notifiche b2b per la persona giuridica con codice fiscale (società semplice)

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_1] Invio notifica digitale mono destinatario persona giuridica lettura tramite codice IUN (p.giuridica con CF)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario codice fiscale(Cucumber Society)
2. La notifica viene inviata tramite api b2b
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_2] Invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
6. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
7. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_3] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
6. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
7. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_4] invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e creditorTaxId
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
6. Quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`
7. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_5] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e creditorTaxId
2. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. Aspetta che la notifica passi in stato `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. Viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
6. Quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_9] invio notifiche digitali mono destinatario senza physicalAddress (p.giuridica)_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e physical address `NULL`
2. La notifica viene inviata
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

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e con PagoPA payment form fee policy FLAT_RATE
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e con PagoPA payment form fee policy DELIVERY_MODE
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_15] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e senza taxonomy code
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
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

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_17] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale), creditorTaxId e senza PagoPA payment form
2. La notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_18] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale), creditorTaxId
2. La notifica viene inviata
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_19] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e senza pagamento
2. La notifica viene inviata
3. Si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_20] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con importo(amount) 2550
2. La notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN
5. L'importo della notifica è 2550

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_21] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con physicalCommunication REGISTERED_LETTER_890
2. La notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_22] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con physicalCommunication AR_REGISTERED_LETTER
2. La notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. Si verifica la corretta acquisizione della notifica
4. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_23] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con idempotenceToken `AME2E3626070001.3`
2. La notifica viene inviata
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
2. La notifica viene inviata
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
3. La notifica viene inviata dal `Comune_1`
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
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. L'invio ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPGMultiPA.feature)

</details>

### Download

#### Persona fisica

##### Download da persona fisica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_1] download documento notificato_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber`
2. Viene generata la notifica
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

1. Con destinatario `Mario Cucumber`
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene effettuato il pagamento F24 forfettario
7. Viene richiesto il download del documento `PAGOPA`
8. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_3] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber`
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene effettuato il pagamento F24 standard
7. Viene richiesto il download del documento `PAGOPA`
8. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>

#### Persona giuridica

##### Download da persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_1] download documento notificato_scenario positivo</summary>

**Descrizione**

1. Con destinatario Gherkin spa(CF)
2. Viene generata la notifica
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

1. Con destinatario Gherkin spa(CF)
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene effettuato il pagamento F24 forfettario
7. Viene richiesto il download del documento `PAGOPA`
8. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_3] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. Con destinatario Gherkin spa(CF)
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene effettuato il pagamento F24 standard
7. Viene richiesto il download del documento `PAGOPA`
8. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_4] download documento notificato_scenario positivo</summary>

**Descrizione**

1. Con destinatario Cucumber Society(P.IVA)
2. Viene generata la notifica
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

1. Con destinatario Cucumber Society(P.IVA)
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene effettuato il pagamento F24 forfettario
7. Viene richiesto il download del documento `PAGOPA`
8. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_6] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. Con destinatario Cucumber Society(P.IVA)
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_1`
4. Lo stato diventa `ACCEPTED`
5. Si verifica la corretta acquisizione della notifica
6. Viene effettuato il pagamento F24 standard
7. Viene richiesto il download del documento `PAGOPA`
8. Il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>

### Validation

#### Persona fisica

##### Validazione campi invio notifiche b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Con destinatario `Mario Cucumber`
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_1_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber`
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

1. Con destinatario `Mario Cucumber`
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_2_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber`
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

1. Con destinatario avente oggetto con caretteri speciali
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

1. Con destinatario avente senderTaxId errato
2. La notifica viene inviata tramite api b2b dal `Comune_1`
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

1. Con destinatario `Mario Cucumber`
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_9_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber`
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

1. Con destinatario `Mario Cucumber`
2. La notifica viene inviata tramite api b2b dal `Comune_1`
3. Si attende che lo stato diventi `ACCEPTED`
4. Si verifica la corretta acquisizione della notifica
5. La notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_10_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. Con destinatario `Mario Cucumber`
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

1. Con destinatario avente noticeCode e noticeCodeAlternative uguali
2. Viene configurato noticeCodeAlternative uguale a noticeCode
3. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_16] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. Con destinatario avente noticeCode e noticeCodeAlternative diversi
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

1. Con destinatario avente noticeCode e noticeCodeAlternative uguali
2. Viene configurato noticeCodeAlternative uguale a noticeCode
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. L'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_18] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. Con destinatario avente noticeCode e noticeCodeAlternative diversi
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
5. La notifica può essere correttamente letta da `Mario Gherkin`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Il documento notificato può essere correttamente recuperato da `Mario Gherkin`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `Mario Gherkin`

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
5. L'allegato `F24` può essere correttamente recuperato da `Mario Gherkin`

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
5. L'allegato `F24` può essere correttamente recuperato da `Mario Gherkin`

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
5. La notifica può essere correttamente letta da `Mario Gherkin`
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
6. La notifica può essere correttamente letta da `Mario Gherkin`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Gherkin`
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
  <summary>[WEB-PF-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `Mario Gherkin` viene delegato da `Mario Cucumber`
2. `Mario Gherkin` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatari `Mario Cucumber`e `Mario Gherkin`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `Mario Cucumber`
6. La notifica può essere correttamente letta da `Mario Gherkin`

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
5. La notifica può essere correttamente letta da `CucumberSpa`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Il documento notificato può essere correttamente recuperato da `CucumberSpa`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `CucumberSpa`

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
5. L'allegato `F24` può essere correttamente recuperato da `CucumberSpa`

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
5. L'allegato `F24` può essere correttamente recuperato da `CucumberSpa`

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
6. La lettura della notifica da parte di `CucumberSpa` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` rifiuta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La lettura della notifica da parte di `CucumberSpa` produce un errore con stato `404`

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
5. La notifica può essere correttamente letta da `CucumberSpa`
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
6. La notifica può essere correttamente letta da `CucumberSpa`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PG-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `CucumberSpa` viene delegato da `GherkinSrl`
2. `CucumberSpa` accetta la delega `GherkinSrl`
3. Viene generata una nuova notifica con destinatario `GherkinSrl`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `CucumberSpa`
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
6. L'elemento di timeline della lettura non riporta i dati di `CucumberSpa`

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
6. La notifica può essere correttamente letta da `CucumberSpa`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pg/RicezioneNotifichePGWebDeleghe.feature)

</details>

- [WEB-PG-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
- [WEB-PG-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
- [WEB-PG-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
- [WEB-PG-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo
- [WEB-PG-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo
- [WEB-PG-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
- [WEB-PG-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
- [WEB-PG-MANDATE_8] Delega a se stesso _scenario negativo
- [WEB-PG-MANDATE_9] delega duplicata_scenario negativo
- [WEB-PG-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo
- [WEB-PG-MANDATE_11] Invio notifica digitale altro destinatario e recupero_scenario positivo
- [WEB-PG-MANDATE_12] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
- [WEB-PG-MANDATE_13] Invio notifica digitale delega e verifica elemento timeline_scenario positivo
- [WEB-PG-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
- [WEB-PG-MANDATE_14] Invio notifica digitale con delega senza gruppo e assegnazione di un gruppo alla delega da parte del PG amministratore  positivo PN-5962


#### Persona fisica e giuridica

##### Ricezione notifiche destinate al delegante

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Il documento notificato può essere correttamente recuperato da `GherkinSrl`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. L'allegato `PAGOPA` può essere correttamente recuperato da `GherkinSrl`

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
5. L'allegato `F24` può essere correttamente recuperato da `GherkinSrl`

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
5. L'allegato `F24` può essere correttamente recuperato da `GherkinSrl`

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
6. La lettura della notifica da parte di `GherkinSrl` produce un errore con stato `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` rifiuta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La lettura della notifica da parte di `GherkinSrl` produce un errore con stato `404`

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
5. La notifica può essere correttamente letta da `GherkinSrl`
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
6. La notifica può essere correttamente letta da `GherkinSrl`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/RicezioneNotifichePFPGWebDeleghe.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PFPG-MANDATE_11] Invio notifica digitale delega e verifica elemento timeline_scenario positivo</summary>

**Descrizione**

1. `GherkinSrl` viene delegato da `Mario Cucumber`
2. `GherkinSrl` accetta la delega `Mario Cucumber`
3. Viene generata una nuova notifica con destinatario `Mario Cucumber`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. La notifica può essere correttamente letta da `GherkinSrl`
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
6. L'elemento di timeline della lettura non riporta i dati di `GherkinSrl`

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
6. La notifica può essere correttamente letta da `GherkinSrl`

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
  [B2B-PA-APP-IO_2] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO
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

1. Con destinatario Mario Cucumber
2. Viene generata la notifica
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Lo stato diventa `ACCEPTED`
5. La notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_7] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Con destinatario Mario Gherkin
3. Viene generata la notifica
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Lo stato diventa `ACCEPTED`
6. La notifica può essere recuperata tramite AppIO
7. Viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.2`
8. La notifica viene inviata tramite api b2b dal `Comune_Multi`
9. Lo stato diventa `ACCEPTED`
10. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_8] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Con destinatario Mario Gherkin
3. Viene generata la notifica
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Lo stato diventa `ACCEPTED`
6. La notifica può essere recuperata tramite AppIO
7. Viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
8. La notifica viene inviata tramite api b2b dal `Comune_Multi`
9. Lo stato diventa `ACCEPTED`
10. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_9] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Con destinatario Mario Gherkin
3. Viene generata la notifica
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Lo stato diventa `ACCEPTED`
6. `Mario Cucumber` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_10] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Con destinatario Mario Gherkin
3. Viene generata la notifica
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Lo stato diventa `ACCEPTED`
6. `Mario Gherkin` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_11] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Con destinatario Mario Gherkin
3. Viene generata la notifica
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Lo stato diventa `ACCEPTED`
6. `Mario Cucumber` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_12] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. Con destinatario Mario Cucumber
2. Con destinatario Mario Gherkin
3. Viene generata la notifica
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Lo stato diventa `ACCEPTED`
6. `Mario Gherkin` recupera la notifica tramite AppIO

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

1. Viene generata una nuova notifica con destinatario persona fisica e physicalAddress `Via minzoni`
2. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_DOMICILE` verifica numero pagine AAR 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotifichePFAnalogico.feature)

</details>

- [B2B_TIMELINE_ANALOG_33] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECAG011A positivo PN-5783
- [B2B_TIMELINE_ANALOG_34] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECAG005C positivo PN-6093
- [B2B_TIMELINE_ANALOG_35] Attesa elemento di timeline SEND_ANALOG_DOMICILE_scenario positivo PN-5283 Presente
- [B2B_TIMELINE_ANALOG_36] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_37] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_38] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_39] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_40] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_41] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_42] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_43] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_44] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_45] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 AR momentaneamente non rendicontabile positivo PN-6079
- [B2B_TIMELINE_ANALOG_46] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 AR momentaneamente non rendicontabile positivo PN-6079
- [B2B_TIMELINE_ANALOG_47] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 890 momentaneamente non rendicontabile positivo PN-6079
- [B2B_TIMELINE_ANALOG_48] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 890 momentaneamente non rendicontabile positivo PN-6079
- [B2B_TIMELINE_ANALOG_49] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza_AR- PN-5927
- [B2B_TIMELINE_ANALOG_50] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza_AR PN-5927
- [B2B_TIMELINE_ANALOG_51] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
- [B2B_TIMELINE_ANALOG_52] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
- [B2B_TIMELINE_ANALOG_53] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_AR_scenario positivo
- [B2B_TIMELINE_ANALOG_54] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
- [B2B_TIMELINE_ANALOG_55] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
- [B2B_TIMELINE_ANALOG_56] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
- [B2B_TIMELINE_ANALOG_57] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
- [B2B_TIMELINE_ANALOG_58] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_890_scenario positivo
- [B2B_TIMELINE_ANALOG_59] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza-gt10-23L_890 PN-5927
- [B2B_TIMELINE_ANALOG_60] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESSdeliveryDetailCode "RECRI001" scenario positivo PN-6634
- [B2B_TIMELINE_ANALOG_61] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS_deliveryDetailCode "RECRI002" scenario positivo PN-6634


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

- [B2B_COSTO_ANALOG_PF_890_1_AAR] Invio notifica verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_890_3_AAR] Invio notifica con allegato verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_890_5_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_890_7_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo

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

- [B2B_COSTO_ANALOG_PF_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_1_AAR] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_3_AAR] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_7_AAR] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo

##### Costo notifica con workflow analogico per persona fisica RIS

- [B2B_COSTO_ANALOG_PF_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RIS_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RIS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RS_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RIS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PF_RS_1_AAR] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_3_AAR] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RS_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_7_AAR] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RS_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RS_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PF_RIS_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo


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

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
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
  <summary>[B2B_COSTO_ANALOG_PG_890_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

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
  <summary>[B2B_COSTO_ANALOG_PG_890_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: REGISTERED_LETTER_890
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_890
   - **physicalAddress_zip**: 16121
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
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `533` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 88010
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `798` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 88010
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
   - **physicalAddress_zip**: 38121
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `374` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile**: NULL
   - **physicalAddress_address**: Via@ok_AR
   - **physicalAddress_zip**: 38121
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

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
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `511` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

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
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_zip**: 38121
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `497` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_zip**: 38121
   - **physicalAddress_address**: Via@ok_AR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Paris
   - **physicalAddress_address**: Via@ok_RIR
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW`
5. Viene verificato il costo = `700` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **physicalCommunication**: AR_REGISTERED_LETTER
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile**: NULL
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Paris
   - **physicalAddress_address**: Via@ok_RIR
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
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `233` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
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
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Paris
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RIS
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `223` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: 75007
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
  <summary>[B2B_COSTO_ANALOG_PG_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `233` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Paris
   - **physicalAddress_address**: Via@ok_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `223` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: FRANCIA
   - **physicalAddress_municipality**: Parigi
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Paris
   - **physicalAddress_address**: Via@ok_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
   - **physicalAddress_zip**: 39100
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `212` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_address**: Via@ok_RS
   - **physicalAddress_zip**: 39100
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIS
   - **payment_pagoPaForm**: NULL
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `302` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_zip**: 39100
   - **physicalAddress_address**: Via@ok_RS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `212` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_zip**: 39100
   - **physicalAddress_address**: Via@ok_RS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: DELIVERY_MODE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `100` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `302` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

:warning: _Ignored_

1. Viene creata una nuova notifica mono destinatario con destinatario `Cucumber Analogic` e
   - **feePolicy**: FLAT_RATE
   - **payment_pagoPaForm**: SI
   - **digitalDomicile_address**: test@fail.it
   - **physicalAddress_State**: BRASILE
   - **physicalAddress_municipality**: Florianopolis
   - **physicalAddress_zip**: 75007
   - **physicalAddress_province**: Santa Catarina
   - **physicalAddress_address**: Via@ok_RIS
2. Viene inviata tramite api b2b dal `Comune_Multi` e si aspetta che lo stato passi in `ACCEPTED`
3. Viene verificato il costo = `0` della notifica
4. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`
5. Viene verificato il costo = `0` della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pg/AvanzamentoNotifichePGCostoAnalogicoRS.feature)

</details>

- [B2B_COSTO_ANALOG_PG_890_1] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_890_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_890_3] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_890_4] Invio notifica con allegato e verifica costo con FSU + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_890_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_890_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_890_7] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_890_8] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_890_1_AAR] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_890_3_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_890_5_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_890_7_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo

##### Costo notifica con workflow analogico per persona giuridica

- [B2B_COSTO_ANALOG_PG_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_1_AAR] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_3_AAR] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_7_AAR] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo

##### Costo notifica con workflow analogico per persona giuridica RS

- [B2B_COSTO_ANALOG_PG_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RIS_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RIS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RIS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RIS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RS_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RIS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RIS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RS_1_AAR] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_2_AAR] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_PG_RIS_3_AAR] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RIS_7_AAR] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RIS_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RS_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_PG_RIS_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo


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

[B2B_TIMELINE_MULTI_PF_PF_06] Invio notifica multidestinatario con pagamento destinatario 0 e 1 scenario  positivo
[B2B_TIMELINE_MULTI_PF_PF_07] Invio notifica multidestinatario con pagamento destinatario 0 e non del destinatario 1 scenario  positivo
[B2B_TIMELINE_MULTI_PF_PF_08] Invio notifica multidestinatario con pagamento destinatario 1 e non del destinatario 0 scenario  positivo

##### Avanzamento b2b notifica multi destinatario analogico

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RIS_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RIS_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_3] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `ANALOG_SUCCESS_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_6] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
7. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_FEEDBACK` con responseStatus `KO` per l'utente 0
8. Vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento `SEND_ANALOG_FEEDBACK` con responseStatus `KO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_7] Invio notifica e atteso stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
6. Vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_8] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo</summary>

**Descrizione**

1. Viene generata notifica
2. Destinatario Mario Gherkin
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
2. Destinatario Mario Gherkin
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

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `399` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_3] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `521` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_4] Invio notifica con allegato e verifica costo con FCU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 16121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `391` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 16121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_7] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | SI         |
| physicalAddress_zip     | 16121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `516` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_8] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | SI         |
| physicalAddress_zip     | 16121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogico890.feature)

</details>

##### Costo notifica con workflow analogico per multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `400` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

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

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

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
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `565` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

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
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `533` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

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
| payment_pagoPaForm           | SI             |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `798` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

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
| payment_pagoPaForm           | SI             |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 16121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `374` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value      |
| ----------------------- | ---------- |
| digitalDomicile         | NULL       |
| physicalAddress_address | Via@ok_890 |
| payment_pagoPaForm      | NULL       |
| physicalAddress_zip     | 16121      |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

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
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `511` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

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
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value     |
| ----------------------- | --------- |
| payment_pagoPaForm      | SI        |
| digitalDomicile         | NULL      |
| physicalAddress_zip     | 38121     |
| physicalAddress_address | Via@ok_AR |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `497` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value     |
| ----------------------- | --------- |
| payment_pagoPaForm      | SI        |
| digitalDomicile         | NULL      |
| physicalAddress_zip     | 38121     |
| physicalAddress_address | Via@ok_AR |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

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
| payment_pagoPaForm           | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `700` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

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
| payment_pagoPaForm           | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `ANALOG_SUCCESS_WORKFLOW` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoAR.feature)

</details>

##### Costo notifica con workflow analogico per multi destinatario RS

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `233` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

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

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

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
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `223` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

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
| physicalAddress_zip          | 75007      |
| physicalAddress_province     | Paris      |
| physicalAddress_address      | Via@ok_RIR |
| payment_pagoPaForm           | NULL       |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | SI           |

11. viene generata nuova notifica
12. destinatario Mario Gherkin
13. destinatario Cucumber Society
14. la notifica viene inviata tramite api b2b dal `Comune_Multi`
15. si attende che lo stato diventi `ACCEPTED`
16. viene verificato il costo = `100` della notifica per l'utente 0
17. viene verificato il costo = `100` della notifica per l'utente 1
18. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
19. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
20. viene verificato il costo = `233` della notifica per l'utente 0
21. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | SI           |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

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
| payment_pagoPaForm           | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `233` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

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
| payment_pagoPaForm           | SI         |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |
| physicalAddress_zip     | 39100        |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `212` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | NULL         |
| physicalAddress_zip     | 39100        |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

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
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `302` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

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

11. viene generata nuova notifica
12. destinatario Mario Gherkin
13. destinatario Cucumber Society
14. la notifica viene inviata tramite api b2b dal `Comune_Multi`
15. si attende che lo stato diventi `ACCEPTED`
16. viene verificato il costo = `0` della notifica per l'utente 0
17. viene verificato il costo = `0` della notifica per l'utente 1
18. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
19. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
20. viene verificato il costo = `0` della notifica per l'utente 0
21. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | SI           |
| physicalAddress_zip     | 39100        |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `212` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

Dati Destinatario

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |
| payment_pagoPaForm      | SI           |
| physicalAddress_zip     | 39100        |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

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
| payment_pagoPaForm           | SI             |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `100` della notifica per l'utente 0
7. Viene verificato il costo = `100` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `302` della notifica per l'utente 0
11. viene verificato il costo = `100` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

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
| payment_pagoPaForm           | SI             |

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Cucumber Society
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. Viene verificato il costo = `0` della notifica per l'utente 0
7. Viene verificato il costo = `0` della notifica per l'utente 1
8. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW` per l'utente 1
9. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER` per l'utente 0
10. viene verificato il costo = `0` della notifica per l'utente 0
11. viene verificato il costo = `0` della notifica per l'utente 1

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheB2bPFPGMultiCostoAnalogicoRS.feature)

</details>

- [B2B_COSTO_ANALOG_890_MULTI_1] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_890_MULTI_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_890_MULTI_3] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_890_MULTI_4] Invio notifica con allegato e verifica costo con FCU + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_890_MULTI_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_890_MULTI_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_890_MULTI_7] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_890_MULTI_8] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_890_MULTI_1_AAR] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_890_MULTI_3_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_890_MULTI_5_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_890_MULTI_7_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo

##### Costo notifica con workflow analogico per multi destinatario

- [B2B_COSTO_ANALOG_MULTI_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_MULTI_1_AAR] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_3_AAR] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_7_AAR] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_MULTI_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo

##### Costo notifica con workflow analogico per multi destinatario RS

- [B2B_COSTO_ANALOG_RS_MULTI_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_1_AAR] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_3_AAR] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_7_AAR] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RS_MULTI_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
- [B2B_COSTO_ANALOG_RIS_MULTI_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo


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


[B2B_PA_LEGALFACT_5] Invio notifica e download atto opponibile SENDER_ACK_scenario senza legalFactType positivo
[B2B_PA_LEGALFACT_6] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario senza legalFactType positivo
[B2B_PA_LEGALFACT_7] Invio notifica e download atto opponibile PEC_RECEIPT_scenario senza legalFactType positivo
[B2B_PA_LEGALFACT_8] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario senza legalFactType positivo

##### Download legalFact analogico


<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_1] Invio notifica con @fail_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW`
6. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY_FAILURE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_2] Invio notifica con @ok_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW`
6. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY_FAILURE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_3] Invio notifica con @fail_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRN002B`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN002B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
<summary>[B2B_PA_ANALOGICO_LEGALFACT_4] Invio notifica con @ok_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECAG001B`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG001B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_5] Invio notifica con @fail_RIS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

| paramter                | value        |
| ----------------------- | ------------ |
| digitalDomicile_address | test@fail.it |
| physicalAddress_address | Via@ok_RS    |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_FAILURE_WORKFLOW`
6. La PA richiede il download dell'attestazione opponibile `DIGITAL_DELIVERY_FAILURE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_6] Invio notifica con @ok_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRI003B`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRI003B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_7] Invio notifica con @fail_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRI004B`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRI004B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_8] Invio notifica con @fail_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECAG003B`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG003B`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_9_TEST] Invio notifica con @FAIL-Discovery_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECRN002E`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECRN002E`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_10_TEST] Invio notifica con @FAIL-Discovery_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

| paramter                | value     |
| ----------------------- | --------- |
| digitalDomicile_address | NULL      |
| physicalAddress_address | Via@ok_RS |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_ANALOG_PROGRESS` con deliveryDetailCode`RECAG003E`
6. La PA richiede il download dell'attestazione opponibile `SEND_ANALOG_PROGRESS` con deliveryDetailCode `RECAG003E`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_11_TEST] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR negativo</summary>

**Descrizione**

| paramter                | value                             |
| ----------------------- | --------------------------------- |
| digitalDomicile_address | NULL                              |
| physicalAddress_address | Via NationalRegistries @fail_AR 5 |
| denomination            | Test AR Fail 2                    |
| taxId                   | MNTMRA03M71C615V                  |

1. Viene generata nuova notifica
2. Destinatario Cucumber Analogic
3. La notifica viene inviata tramite api b2b dal `Comune_Multi`
4. Si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi fino all'elemento di timeline della notifica `COMPLETELY_UNREACHABLE`
6. La PA richiede il download dell'attestazione opponibile `COMPLETELY_UNREACHABLE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFactAnalogico.feature)

</details>

- [B2B_PA_ANALOGICO_LEGALFACT_1] Invio notifica con @fail_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
- [B2B_PA_ANALOGICO_LEGALFACT_2] Invio notifica con @ok_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
- [B2B_PA_ANALOGICO_LEGALFACT_3] Invio notifica con @fail_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
  [B2B_PA_ANALOGICO_LEGALFACT_4] Invio notifica con @ok_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
- [B2B_PA_ANALOGICO_LEGALFACT_5] Invio notifica con @fail_RIS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
- [B2B_PA_ANALOGICO_LEGALFACT_6] Invio notifica con @ok_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
- [B2B_PA_ANALOGICO_LEGALFACT_7] Invio notifica con @fail_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
- [B2B_PA_ANALOGICO_LEGALFACT_8] Invio notifica con @fail_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
- [B2B_PA_ANALOGICO_LEGALFACT_9_TEST] Invio notifica con @FAIL-Discovery_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
- [B2B_PA_ANALOGICO_LEGALFACT_10_TEST] Invio notifica con @FAIL-Discovery_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
- [B2B_PA_ANALOGICO_LEGALFACT_11_TEST] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR negativo
- [B2B_PA_ANALOGICO_LEGALFACT_12_TEST] Invio notifica presenza allegato in corrispondenza dello stato "Aggiornamento sull'invio cartaceo" e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo PN-6090


##### Download legalFact multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-MULTI-RECIPIENT_LEGALFACT_1] Invio notifica multi destinatario_scenario positivo</summary>

**Descrizione**

1. Viene generata nuova notifica
2. Destinatario Mario Gherkin
3. Destinatario Mario Cucumber
4. La notifica viene inviata tramite api b2b dal `Comune_Multi`
5. Si attende che lo stato diventi `ACCEPTED`
6. `Mario Gherkin` legge la notifica ricevuta
7. `Mario Cucumber` legge la notifica ricevuta
8. Sono presenti 2 attestazioni opponibili `RECIPIENT_ACCESS`

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

11. viene generata nuova notifica
12. destinatario Gherkin spa
13. la notifica viene inviata tramite api b2b dal `Comune_1`
14. si attende che lo stato diventi `ACCEPTED`
15. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`
16. la PA richiede il download dell'attestazione opponibile `SENDER_ACK` è `PN_LEGAL_FACTS`

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

11. viene generata nuova notifica
12. destinatario Gherkin spa
13. la notifica viene inviata tramite api b2b dal `Comune_1`
14. si attende che lo stato diventi `ACCEPTED`
15. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`
16. la PA richiede il download dell'attestazione opponibile `PEC_RECEIPT` è `PN_EXTERNAL_LEGAL_FACTS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/download/pg/AvanzamentoNotifichePGLegalFact.feature)

</details>

### Webhook

#### Persona fisica

##### Avanzamento notifiche webhook b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_STATUS_1] Creazione stream notifica</summary>

**Descrizione**

1. Si predispone `1` nuovo stream denominato `stream-test` con `eventType STATUS`
2. Si crea il nuovo stream per il `Comune_1`
3. Lo stream è stato creato e viene correttamente recuperato dal sistema tramite `stream id`
4. Si cancella lo stream creato
5. Viene verificata la corretta cancellazione

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_1] Creazione stream notifica</summary>

**Descrizione**

1. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino allo stato `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_12] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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

1. Si predispone `6` nuovo stream denominato `stream-test` con `eventType STATUS`
2. Si crea il nuovo stream per il `Comune_1`
3. L'ultima creazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_15] Creazione multi stream notifica</summary>

**Descrizione**

1. Si predispone `6` nuovo stream denominato `stream-test` con `eventType TIMELINE`
2. Si crea il nuovo stream per il `Comune_1`
3. L'ultima creazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `DIGITAL_FAILURE_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `NOT_HANDLED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_1`
4. La notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_1` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_23] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_2] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_3] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_4] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_5] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona fisica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_1] Invio notifica digitale multi PG ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_2] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari giuridica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_3] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari giuridica, entrambi con `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `DIGITAL_FAILURE_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_5] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_Multi`
4. La notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_Multi` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pf/AvanzamentoNotificheWebhookB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_6] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con `2` destinatari persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino allo stato `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino allo stato `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino allo stato `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_8] Invio notifica digitale ed attesa elemento di timeline PREPARE_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `PREPARE_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_9] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_11] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_12] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica e `digitalDomicile_address test@fail.it`
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
3. Si crea il nuovo stream per il `Comune_2`
4. La notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
5. Vengono letti gli eventi dello stream del `Comune_2` fino all'elemento di timeline `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/webhook/pg/AvanzamentoNotificheWebhookB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_13] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo</summary>

**Descrizione**

1. Viene generata una nuova notifica con destinatario persona giuridica
2. Si predispone `1` nuovo stream denominato `stream-test` con `eventType TIMELINE`
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
3. viene generata una nuova notificacon `group NULL`
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
3. viene generata una nuova notificacon `group NULL`
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
3. viene generata una nuova notificacon `group NULL`
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
3. viene generata una nuova notificacon `group NULL`
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
3. viene generata una nuova notificacon `group NULL`
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
2. Viene richiesto l'inserimento del numero di telefono `+0013894516888v
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
3. Viene verificato il costo = "100" della notifica

[Feature link](src/test/resources/PaIntegration/PaIntegrationTest.feature)

</details>

# Questo elenco riporta i Test Automatici che sono attualmente implementati

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
  <summary>[WEB_PA-SEND_3] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

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

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. quando la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_2] Invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. la notifica viene inviata tramite api b2b
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
6. quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`
7. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_3] invio notifiche digitali mono destinatario (p.giuridica con P.Iva)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e idempotenceToken `AME2E3626070001.1`
2. la notifica viene inviata tramite api b2b
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.1`
6. quando la notifica viene inviata tramite api b2b (NON aspetta che passi in stato `ACCEPTED`
7. l'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_4] invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e creditorTaxId
2. la notifica viene inviata tramite api b2b
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
6. quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`
7. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_5] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e creditorTaxId
2. la notifica viene inviata tramite api b2b
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
6. quando la notifica viene inviata tramite api b2b (NON aspetta che passi in stato `ACCEPTED`)
7. l'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_9] invio notifiche digitali mono destinatario senza physicalAddress (p.giuridica)_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e physical address `NULL`
2. quando la notifica viene inviata
3. si verifica la corretta acquisizione della notifica
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. quando la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. si tenta il recupero della notifica dal sistema tramite codice IUN
5. l'operazione ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_11] Invio notifica digitale mono destinatario Flat_rate_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e con PagoPA payment form fee policy `FLAT_RATE`
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e con PagoPA payment form fee policy `DELIVERY_MODE`
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_15] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) senza taxonomy code
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_16] Invio notifica digitale mono destinatario con taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con taxonomy code
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene controllata la presenza del taxonomyCode

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_17] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva), creditorTaxId e senza PagoPA payment form
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_18] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva), creditorTaxId
2. la notifica viene inviata
3. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_19] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e senza pagamento
2. la notifica viene inviata
3. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_20] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con importo(amount) 2550
2. la notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
5. l'importo della notifica è 2550

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_21] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con physicalCommunication `REGISTERED_LETTER_890`
2. la notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_22] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con physicalCommunication `AR_REGISTERED_LETTER`
2. la notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_23] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con idempotenceToken `AME2E3626070001.3`
2. la notifica viene inviata
3. viene verificato lo stato di accettazione con requestID

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_24] Invio notifica digitale mono destinatario e controllo paProtocolNumber con diverse pa_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene generata una nuova notifica con uguale paProtocolNumber
5. la notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_25] Invio notifica digitale mono destinatario e controllo paProtocolNumber con uguale pa_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene generata una nuova notifica con uguale paProtocolNumber
5. la notifica viene inviata dal `Comune_1`
6. l'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_26] invio notifiche digitali e controllo paProtocolNumber e idempotenceToken con diversa pa_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) con idempotenceToken `AME2E3626070001.3`
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.1`
5. la notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_27] Invio notifica mono destinatario con documenti pre-caricati non trovati su safestorage  scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con recipientType PG e taxId `CCRMCT06A03A433H`
2. la notifica viene inviata tramite api b2b senza preload allegato dal `Comune_Multi` e si attende che lo stato diventi `REFUSED`
3. si verifica che la notifica non viene accettata causa `ALLEGATO`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_28] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Gherkin spa(P.Iva) e idempotenceToken `AME2E3626070001.3`
2. la notifica viene inviata dal `Comune_1`
3. viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG2.feature)

</details>

##### Invio notifiche b2b per la persona giuridica con codice fiscale (società semplice)

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_1] Invio notifica digitale mono destinatario persona giuridica lettura tramite codice IUN (p.giuridica con CF)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario codice fiscale(Cucumber Society)
2. la notifica viene inviata tramite api b2b
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_2] Invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
6. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
7. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_3] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken
6. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
7. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_4] invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e creditorTaxId
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
6. quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`
7. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_5] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e creditorTaxId
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. aspetta che la notifica passi in stato `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
6. quando la notifica viene inviata tramite api b2b aspetta che la notifica passi in stato `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_9] invio notifiche digitali mono destinatario senza physicalAddress (p.giuridica)_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e physical address `NULL`
2. la notifica viene inviata
3. l'operazione ha prodotto un errore con status code `400`

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

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e con PagoPA payment form fee policy FLAT_RATE
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e con PagoPA payment form fee policy DELIVERY_MODE
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_15] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e senza taxonomy code
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene controllata la presenza del taxonomyCode

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

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale), creditorTaxId e senza PagoPA payment form
2. la notifica viene inviata tramite api b2b e aspetta che la notifica passi in stato `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_18] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale), creditorTaxId
2. la notifica viene inviata
3. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_19] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) e senza pagamento
2. la notifica viene inviata
3. si verifica la corretta acquisizione della richiesta di invio notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_20] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con importo(amount) 2550
2. la notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
5. l'importo della notifica è 2550

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_21] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con physicalCommunication REGISTERED_LETTER_890
2. la notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_22] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con physicalCommunication AR_REGISTERED_LETTER
2. la notifica viene inviata tramite api b2b e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_23] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con idempotenceToken `AME2E3626070001.3`
2. la notifica viene inviata
3. viene verificato lo stato di accettazione con requestID

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_24] Invio notifica digitale mono destinatario e controllo paProtocolNumber con diverse pa_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene generata una nuova notifica con uguale paProtocolNumber
5. la notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_25] Invio notifica digitale mono destinatario e controllo paProtocolNumber con uguale pa_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale)
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene generata una nuova notifica con uguale paProtocolNumber
5. la notifica viene inviata dal `Comune_1`
6. l'operazione ha prodotto un errore con status code `409`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_26] invio notifiche digitali e controllo paProtocolNumber e idempotenceToken con diversa pa_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con idempotenceToken `AME2E3626070001.3`
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. si verifica la corretta acquisizione della notifica
4. viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken `AME2E3626070001.1`
5. la notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
6. si verifica la corretta acquisizione della notifica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_27] Invio notifica digitale mono destinatario e verifica stato_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario Cucumber Society(codice fiscale) con idempotenceToken `AME2E3626070001.3`
2. la notifica viene inviata
3. viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/invio/pg/InvioNotificheB2bPG_CF.feature)

</details>

##### Invio notifiche b2b con altre PA, multi-destinatario e senza pagamento per persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_01] Invio notifica digitale_scenario negativo</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva)
2. la notifica viene inviata tramite api b2b dal `Comune_2` e si attende che lo stato diventi `ACCEPTED`
3. si tenta il recupero dal sistema tramite codice IUN dalla PA `Comune_1`
4. l'operazione ha generato un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_02] Invio notifica digitale senza pagamento_scenario positivo</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) con payment `NULL`
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_03] Invio notifica multi destinatario senza pagamento_scenario positivo</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e `Mario Cucumber` entrambi con payment `NULL`
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_04] Invio notifica multi destinatario con pagamento_scenario positivo</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e `Mario Cucumber` con pagamento
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_05] Invio notifica multi destinatario PA non abilitata_scenario negativa</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e `Mario Cucumber` con senderDenomination `Comune di milano`
2. la notifica viene inviata dal `Comune_1`
3. Then l'invio ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_06] Invio notifica multi destinatario uguale codice avviso_scenario positivo</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva)
2. e `Mario Cucumber` con uguale codice avviso del destinatario numero 1
3. la notifica viene inviata dal `Comune_1`
4. Then l'invio ha prodotto un errore con status code `500`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_07] Invio notifica multi destinatario senza pagamento_scenario positivo</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e Cucumber srl(P.Iva) entrambi con payment `NULL`
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_08] Invio notifica multi destinatario con pagamento_scenario positivo</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e Cucumber srl(P.Iva) entrambi con payment
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PG-MULTI-PA_09] Invio notifica multi destinatario PA non abilitata_scenario negativa</summary>

**Descrizione**

1. viene creata una nuova notifica multi destinatario con destinatario Gherkin spa(P.Iva) e Cucumber srl(P.Iva) con senderDenomination `Comune di milano`
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. l'invio ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

### Download

#### Persona fisica

##### Download da persona fisica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_1] download documento notificato_scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Cucumber`
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene richiesto il download del documento `NOTIFICA`
7. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_2] download documento pagopa_scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Cucumber`
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene effettuato il pagamento F24 forfettario
7. viene richiesto il download del documento `PAGOPA`
8. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PF_3] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Cucumber`
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene effettuato il pagamento F24 standard
7. viene richiesto il download del documento `PAGOPA`
8. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pf/DownloadAllegatoNotifichePF.feature)

</details>

#### Persona giuridica

##### Download da persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_1] download documento notificato_scenario positivo</summary>

**Descrizione**

1. con destinatario Gherkin spa(CF)
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene richiesto il download del documento `NOTIFICA`
7. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_2] download documento pagopa_scenario positivo</summary>

**Descrizione**

1. con destinatario Gherkin spa(CF)
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene effettuato il pagamento F24 forfettario
7. viene richiesto il download del documento `PAGOPA`
8. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_3] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. con destinatario Gherkin spa(CF)
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene effettuato il pagamento F24 standard
7. viene richiesto il download del documento `PAGOPA`
8. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_4] download documento notificato_scenario positivo</summary>

**Descrizione**

1. con destinatario Cucumber Society(P.IVA)
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene richiesto il download del documento `NOTIFICA`
7. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_5] download documento pagopa_scenario positivo</summary>

**Descrizione**

1. con destinatario Cucumber Society(P.IVA)
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene effettuato il pagamento F24 forfettario
7. viene richiesto il download del documento `PAGOPA`
8. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-DOWN-PG_6] download documento f24_standard_scenario positivo</summary>

**Descrizione**

1. con destinatario Cucumber Society(P.IVA)
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. viene effettuato il pagamento F24 standard
7. viene richiesto il download del documento `PAGOPA`
8. il download si conclude correttamente

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/download/pg/DownloadAllegatoNotifichePG.feature)

</details>

### Validation

#### Persona fisica

##### Validazione campi invio notifiche b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_1_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_2] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_2_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_3] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

1. con destinatario avente denominazione con caretteri speciali
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_3_LITE] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

1. con destinatario avente denominazione con caretteri speciali
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
6. [Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_4] invio notifica con oggetto contenente caratteri speciali_scenario positivo</summary>

**Descrizione**

1. con destinatario avente oggetto con caretteri speciali
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_5] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario positivo</summary>

**Descrizione**

1. con destinatario avente tax_id errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_6] invio notifiche digitali mono destinatario con parametri creditorTaxId errati_scenario negativo</summary>

**Descrizione**

1. con destinatario avente creditorTaxId errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_7] invio notifiche digitali mono destinatario con parametri senderTaxId errati_scenario negativo</summary>

**Descrizione**

1. con destinatario avente senderTaxId errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_8] invio notifiche digitali mono destinatario con parametri subject errati_scenario negativo</summary>

**Descrizione**

1. con destinatario avente subject errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation1.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_9] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_9_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_10_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_11] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario negativo</summary>

**Descrizione**

1. con destinatario avente tax_id errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_12] invio notifiche digitali mono destinatario con parametri denomination errati_scenario negativo</summary>

**Descrizione**

1. con destinatario avente denomination errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_13] invio notifiche digitali mono destinatario con parametri senderDenomination errati_scenario negativo</summary>

**Descrizione**

1. con destinatario avente senderDenomination errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_14] invio notifiche digitali mono destinatario con parametri abstract errati_scenario negativo</summary>

**Descrizione**

1. con destinatario avente abstract errato
2. la notifica viene inviata tramite api b2b dal `Comune_1`
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_15] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo</summary>

**Descrizione**

1. con destinatario avente noticeCode e noticeCodeAlternative uguali
2. viene configurato noticeCodeAlternative uguale a noticeCode
3. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_16] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. con destinatario avente noticeCode e noticeCodeAlternative diversi
2. viene configurato noticeCodeAlternative diversi a noticeCode
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. si attende che lo stato diventi `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_17] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo</summary>

**Descrizione**

1. con destinatario avente noticeCode e noticeCodeAlternative uguali
2. viene configurato noticeCodeAlternative uguale a noticeCode
3. la notifica viene inviata tramite api b2b dal `Comune_Multi`
4. l'operazione ha prodotto un errore con status code `400`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_18] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo</summary>

**Descrizione**

1. con destinatario avente noticeCode e noticeCodeAlternative diversi
2. viene configurato noticeCodeAlternative diversi a noticeCode
3. la notifica viene inviata tramite api b2b dal `Comune_Multi`
4. si attende che lo stato diventi `ACCEPTED`
5. si verifica la corretta acquisizione della notifica
6. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_19] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Gherkin` avente physicalAddress_zip corretto
2. la notifica viene inviata tramite api b2b dal `Comune_Multi`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN

[Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_20] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo</summary>

**Descrizione**

1. con destinatario `Mario Gherkin` avente physicalAddress_zip non corretto
2. la notifica viene inviata dal `Comune_Multi`
3. l'operazione ha prodotto un errore con status code `400`
   [Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_21] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo</summary>

**Descrizione**

1. con destinatario `Mario Gherkin` avente physicalAddress_zip corretto
2. la notifica viene inviata tramite api b2b dal `Comune_Multi`
3. si attende che lo stato diventi `ACCEPTED`
4. si verifica la corretta acquisizione della notifica
5. la notifica può essere correttamente recuperata dal sistema tramite codice IUN
   [Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_22] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo</summary>

**Descrizione**

1. con destinatario `Mario Gherkin` avente physicalAddress_zip non corretto
2. la notifica viene inviata dal `Comune_Multi`
3. l'operazione ha prodotto un errore con status code `400`
   [Feature link](src/test/resources/it/pagopa/pn/cucumber/invioNotifica/validation/pf/InvioNotificheB2bPFValidation2.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_VALID_23] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. con destinatario `Mario Gherkin` avente physicalAddress_zip non corretto
2. la notifica viene inviata dal `Comune_Multi`
3. l'operazione ha prodotto un errore con status code `400`
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

1. con destinatario Mario Cucumber
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>
  [B2B-PA-APP-IO_2] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_3] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. la notifica può essere recuperata tramite AppIO
6. viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
7. la notifica viene inviata tramite api b2b dal `Comune_1`
8. lo stato diventa `ACCEPTED`
9. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_4] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_5] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_1`
4. lo stato diventa `ACCEPTED`
5. `Mario Cucumber` tenta il recupero della notifica tramite AppIO
6. il tentativo di recupero con appIO ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature)

</details>

#### Recupero notifiche tramite api AppIO b2b multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_6] Invio notifica con api b2b e recupero tramite AppIO</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. viene generata la notifica
3. la notifica viene inviata tramite api b2b dal `Comune_Multi`
4. lo stato diventa `ACCEPTED`
5. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_7] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. con destinatario Mario Gherkin
3. viene generata la notifica
4. la notifica viene inviata tramite api b2b dal `Comune_Multi`
5. lo stato diventa `ACCEPTED`
6. la notifica può essere recuperata tramite AppIO
7. viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
8. la notifica viene inviata tramite api b2b dal `Comune_Multi`
9. lo stato diventa `ACCEPTED`
10. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_8] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. con destinatario Mario Gherkin
3. viene generata la notifica
4. la notifica viene inviata tramite api b2b dal `Comune_Multi`
5. lo stato diventa `ACCEPTED`
6. la notifica può essere recuperata tramite AppIO
7. viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
8. la notifica viene inviata tramite api b2b dal `Comune_Multi`
9. lo stato diventa `ACCEPTED`
10. la notifica può essere recuperata tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_9] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. con destinatario Mario Gherkin
3. viene generata la notifica
4. la notifica viene inviata tramite api b2b dal `Comune_Multi`
5. lo stato diventa `ACCEPTED`
6. `Mario Cucumber` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_10] Invio notifica con api b2b e recupero documento notificato con AppIO</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. con destinatario Mario Gherkin
3. viene generata la notifica
4. la notifica viene inviata tramite api b2b dal `Comune_Multi`
5. lo stato diventa `ACCEPTED`
6. `Mario Gherkin` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_11] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. con destinatario Mario Gherkin
3. viene generata la notifica
4. la notifica viene inviata tramite api b2b dal `Comune_Multi`
5. lo stato diventa `ACCEPTED`
6. `Mario Cucumber` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-APP-IO_12] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo</summary>

**Descrizione**

1. con destinatario Mario Cucumber
2. con destinatario Mario Gherkin
3. viene generata la notifica
4. la notifica viene inviata tramite api b2b dal `Comune_Multi`
5. lo stato diventa `ACCEPTED`
6. `Mario Gherkin` recupera la notifica tramite AppIO

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIOMulti.feature)

</details>

#### Ricezione notifiche api web con invio tramite api B2B

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. il documento notificato può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. l'allegato `PAGOPA` può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. l'allegato `F24` può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate NULL e payment_f24standard SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. l'allegato `F24` può essere correttamente recuperato dalla persona fisica

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_6] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica con payment_pagoPaForm SI e payment_f24flatRate NULL e payment_f24standard NULL
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. la persona fisica tenta il recupero dell'allegato `F24`
4. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_7] Invio notifica digitale altro destinatario e recupero tramite codice IUN API WEB_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Cucumber`
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero della notifica
4. il recupero ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_8] Invio notifica digitale altro destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`
4. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_9] Invio notifica digitale altro destinatario e recupero allegato F24_FLAT_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`
4. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_10] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `PAGOPA`
4. il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_11] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_12] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` con `subjectRegExp cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_13] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` con `subjectRegExp cucumber` e `startDate 01/01/2022`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-PF-RECIPIENT_14] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario `Mario Gherkin` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_1` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` con `subjectRegExp cucumber`, `startDate 01/01/2022`,
   `endDate 01/10/2030` e `iunMatch ACTUAL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFWeb.feature)

</details>

#### Ricezione notifiche api web con invio tramite api B2B multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_1] Invio notifica digitale multi destinatario e recupero tramite codice IUN API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi  `ACCEPTED`
3. la notifica può essere correttamente recuperata da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_2] Invio notifica digitale multi destinatario e recupero documento notificato_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. il documento notificato può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_3] Invio notifica digitale multi destinatario e recupero allegato pagopa_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi ACCEPTED
3. l'allegato `PAGOPA` può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_4] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI e payment_f24flatRate SI
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi  `ACCEPTED`
3. l'allegato `F24` può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_5] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI e payment_f24standard SI
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi  `ACCEPTED`
3. l'allegato `F24` può essere correttamente recuperato da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_6] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e`Mario Cucumber`con payment_pagoPaForm SI, payment_f24standard NULL e payment_f24flatRate NULL
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi  `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`
4. `Mario Cucumber` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_7] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

:warning: _Ignored_
**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e `Mario Cucumber` con payment_pagoPaForm SI, payment_f24standard NULL e payment_f24flatRate NULL
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi  `ACCEPTED`
3. `Mario Gherkin` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`
4. `Mario Cucumber` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_8] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. `Mario Gherkin` con payment_pagoPaForm NULL, payment_f24flatRate SI e payment_f24standard NULL
3. `Mario Cucumber` con payment_pagoPaForm SI, payment_f24flatRate NULL e payment_f24standard SI
4. `Mario Gherkin` tenta il recupero dell'allegato `PAGOPA`, ma il download ha prodotto un errore con status code `404`
5. l'allegato `PAGOPA` può essere correttamente recuperato da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_9] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. `Mario Gherkin` con payment_pagoPaForm SI, payment_f24flatRate NULL e payment_f24standard NULL
3. `Mario Cucumber` con payment_pagoPaForm SI, payment_f24flatRate SI e payment_f24standard NULL
4. `Mario Gherkin` tenta il recupero dell'allegato `F24`, ma il download ha prodotto un errore con status code `404`
5. l'allegato `F24` può essere correttamente recuperato da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_10] Invio notifica digitale multi destinatario e recupero allegato pagopa_scenario negativo</summary>

**Descrizione**

:warning: _Ignored_

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. `Mario Gherkin` con payment_pagoPaForm NULL, payment_f24flatRate SI e payment_f24standard NULL
3. `Mario Cucumber` con payment_pagoPaForm SI, payment_f24flatRate SI e payment_f24standard NULL
4. `Mario Gherkin` tenta il recupero dell'allegato `PAGOPA`, ma il download ha prodotto un errore con status code `404`
5. l'allegato `PAGOPA` può essere correttamente recuperato da `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_11] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi  `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_12] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi  `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_13] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber` e `startDate 01/01/2023`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_14] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber`, 
`startDate 01/01/2023`, `endDate 01/10/2030` e `iunMatch ACTUAL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[WEB-MULTI-PF-RECIPIENT_15] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario negativo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatari `Mario Gherkin` e Mario Cucumber
2. la notifica viene inviata tramite api b2b dal `Comune_Multi` e si attende che lo stato diventi `ACCEPTED`
3. la notifica NON può essere correttamente recuperata con una ricerca da `Mario Gherkin` e `Mario Cucumber` con `subjectRegExp cucumber`,
   `startDate 01/01/2030` e `endDate 01/10/2033`

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## Workflow notifica

### B2B

#### Persona fisica

##### Avanzamento notifiche b2b persona fisica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_1] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_
1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERING`
4. `Mario Cucumber` legge la notifica ricevuta
5. si verifica che la notifica abbia lo stato `VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_10] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERED`
4. `Mario Gherkin` legge la notifica ricevuta
5. si verifica che la notifica abbia lo stato `VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_11] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERED`
4. `Mario Gherkin` legge la notifica ricevuta
5. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_12] Invio notifica digitale ed attesa elemento di timeline PREPARE_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_
1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `PREPARE_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_13] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo</summary>

**Descrizione**

:warning: _Ignored_
1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_SIMPLE_REGISTERED_LETTER`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_14] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_15] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_PROGRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `PUBLIC_REGISTRY_CALL`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `PUBLIC_REGISTRY_RESPONSE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_18] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campo deliveryDetailCode positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `OK` sia presente il campo `deliveryDetailCode`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_FEEDBACK` con responseStatus `OK` sia presente il campo `deliveryDetailCode` e `deliveryFailureCause`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `DIGITAL_SUCCESS_WORKFLOW`
4. viene verificato che nell'elemento di timeline della notifica `SEND_DIGITAL_DOMICILE` è presente il campo Digital Address di piattaforma
5. "#PLOMRC01P30L736Y" ha un indirizzo digitale configurato non valido su NR serve un CF con indirizzo digitale in piattaforma

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `PREPARE_DIGITAL_DOMICILE`
4. viene verificato che nell'elemento di timeline della notifica `PREPARE_DIGITAL_DOMICILE` sia presente il campo Digital Address

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. viene generata una nuova notifica con destinatario persona fisica
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi  `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SCHEDULE_DIGITAL_WORKFLOW`
4. viene verificato che nell'elemento di timeline della notifica `SCHEDULE_DIGITAL_WORKFLOW` sia presente il campo Digital Address

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature)

</details>

##### Avanzamento notifiche b2b multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_1] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario 
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERED` dalla PA `Comune_Multi`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_2] Invio notifica multi destinatario_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. `Mario Gherkin` legge la notifica ricevuta
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_3] Invio notifica multi destinatario_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. `Mario Cucumber` legge la notifica ricevuta
4. vengono letti gli eventi fino all'elemento di timeline della notifica `NOTIFICATION_VIEWED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-TIMELINE_MULTI_4] Invio notifica multi destinatario SCHEDULE_ANALOG_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SCHEDULE_ANALOG_WORKFLOW`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_5] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_6] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `REQUEST_ACCEPTED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_7] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `AAR_GENERATION`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_8] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `GET_ADDRESS`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_9] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERING`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino all'elemento di timeline della notifica `SEND_DIGITAL_DOMICILE`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_11] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. viene generata una nuova notifica multi destinatario
2. la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi `ACCEPTED`
3. vengono letti gli eventi fino allo stato della notifica `DELIVERED`

[Feature link](src/test/resources/it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFMulti.feature)

</details>

##### Avanzamento notifiche b2b persona fisica pagamento

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_2] Invio notifica e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_3] Invio notifica FLAT e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_4] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_5] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_6] Invio notifica e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PAY_7] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Avanzamento notifiche b2b con workflow cartaceo

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RIS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_RIS_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_4] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_AR_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_5] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_890_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_6] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_RIR_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_7] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_fail_890_NR_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_8] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_fail_AR_NR_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_9] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_10] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_11] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_12] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_13] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_14] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_15] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_16] Attesa elemento di timeline SEND_ANALOG_FEEDBACK e verifica campo SEND_ANALOG_FEEDBACK positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_17] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_18] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_19] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_20] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_21] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_22] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_23] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_24] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_25] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_26] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890 negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_27] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_28] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_29] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_RIR_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_30] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_NR negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_31] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_NR negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_ANALOG_32] Invio notifica digitale senza allegato ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo numero pagine AAR</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per persona fisica 890

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_1] Invio notifica verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_2] Invio notifica verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_3] Invio notifica con allegato verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_4] Invio notifica con allegato e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_7] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_890_8] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per persona fisica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per persona fisica RIS

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PF_RIS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

#### Persona giuridica

##### Avanzamento b2b persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_1] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_8] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_9] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_11] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_12] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_13] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_14] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campo deliveryDetailCode positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_15] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG-CF_1] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG-CF_2] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG-CF_3] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Avanzamento b2b notifica multi destinatario persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_1] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG_8] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG-CF_1] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG-CF_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PG-CF_3] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Avanzamento b2b persona giuridica pagamento

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_2] Invio notifica e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_3] Invio notifica FLAT e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_4] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_5] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_6] Invio notifica e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_7] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_8] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_9] Invio notifica e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_10] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_11] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-PG-PAY_12] Invio e visualizzazione notifica e verifica amount e effectiveDate</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Avanzamento notifiche analogico persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RIS_1] Invio notifica digitale ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_RIS_2] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_6] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_7] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_8] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_9] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_10] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_11] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_12] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_13] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_14] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_15] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_16] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_17] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_PG_ANALOG_18] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per persona giuridica 890

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_1] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_3] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_4] Invio notifica con allegato e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_7] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_890_8] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per persona giuridica RS

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_PG_RIS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

#### Persona fisica e giuridica

##### Avanzamento notifiche b2b multi destinatario con persona fisica e giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_01] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_02] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_03] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_04] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_PF_PG_05] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Avanzamento b2b notifica multi destinatario analogico

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RIS_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_RIS_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_3] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_6] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_7] Invio notifica e atteso stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_8] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_TIMELINE_MULTI_ANALOG_9] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario  positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per multi destinatario 890

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_1] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_3] Invio notifica con allegato e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_4] Invio notifica con allegato e verifica costo con FCU + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_7] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_890_MULTI_8] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_9] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_10] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIR + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIR + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Costo notifica con workflow analogico per multi destinatario RS

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_7] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RS_MULTI_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_COSTO_ANALOG_RIS_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

### Download

#### Persona fisica

##### Download legalFact

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_IO_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-RECIPIENT_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Download legalFact analogico

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_1] Invio notifica con @fail_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_2] Invio notifica con @ok_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_3] Invio notifica con @fail_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
  [B2B_PA_ANALOGICO_LEGALFACT_4] Invio notifica con @ok_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_5] Invio notifica con @fail_RIS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_6] Invio notifica con @ok_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_7] Invio notifica con @fail_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_8] Invio notifica con @fail_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_9_TEST] Invio notifica con @FAIL-Discovery_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_10_TEST] Invio notifica con @FAIL-Discovery_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_ANALOGICO_LEGALFACT_11_TEST] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Download legalFact multi destinatario

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_WEB-MULTI-RECIPIENT_LEGALFACT_1] Invio notifica multi destinatario_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

#### Persona giuridica

##### Download legalFact per la persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_PG_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_PG_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_PG_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_PG_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_PG_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B_PA_LEGALFACT_KEY_PG_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

### Webhook

#### Persona fisica

##### Avanzamento notifiche webhook b2b

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_STATUS_1] Creazione stream notifica</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_1] Creazione stream notifica</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_2] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_10] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_11] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_12] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_13] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_14] Creazione multi stream notifica</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_15] Creazione multi stream notifica</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_23] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

##### Avanzamento notifiche webhook b2b multi

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_1] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_2] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_3] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_4] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_5] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_1] Invio notifica digitale multi PG ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_2] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_3] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_4] Invio notifica digitale multi PG ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_5] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM-TIMELINE_MULTI_PG_6] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

#### Persona giuridica

##### Avanzamento notifiche webhook b2b per persona giuridica

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_1] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_8] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_9] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_11] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_12] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-STREAM_TIMELINE_PG_13] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## Allegati

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_15] verifica retention time dei documenti pre-caricati</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_16] verifica retention time  pagopaForm pre-caricato</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_13] verifica retention time dei documenti per la notifica inviata</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG-CF_14] verifica retention time pagopaForm per la notifica inviata</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_13] verifica retention time dei documenti per la notifica inviata</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_14] verifica retention time pagopaForm per la notifica inviata</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_13] verifica retention time dei documenti per la notifica inviata</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PA-SEND_PG_14] verifica retention time pagopaForm per la notifica inviata</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## Api Key Manager

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_1] Lettura apiKey generate_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_2] generazione e cancellazione ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_3] generazione e cancellazione ApiKey_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_4] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_5] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_6] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_7] generazione e verifica stato ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_8] generazione e test ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_9] generazione e test ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_10] generazione con gruppo e cancellazione ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_11] generazione senza gruppo e invio notifica senza gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_12] generazione senza gruppo e invio notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_13] generazione con gruppo e invio notifica senza gruppo ApiKey_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_14] generazione con gruppo e invio notifica con lo stesso gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_15] generazione con gruppo e invio notifica con un gruppo differente ApiKey_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_16] generazione con gruppo e invio notifica con gruppo e lettura notifica senza gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_17] generazione con gruppo e invio notifica con gruppo e lettura notifica con gruppo diverso ApiKey_scenario netagivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_18] generazione senza gruppo e invio notifica senza gruppo e lettura notifica senza gruppo  ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_19] generazione con gruppo e invio notifica con gruppo e lettura notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_20] generazione senza gruppo e invio notifica con gruppo e lettura notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_21] generazione con gruppo non valido ApiKey_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_22] generazione con multi gruppi e invio notifica senza gruppo ApiKey_scenario negativo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_23] generazione con multi gruppi e invio notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[API-KEY_24] generazione con multi gruppi e invio notifica con gruppo e lettura notifica con gruppo ApiKey_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## Downtime logs

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[DOWNTIME-LOGS_1] lettura downtime-logs</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[DOWNTIME-LOGS_2] lettura downtime-logs</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[DOWNTIME-LOGS_3] lettura downtime-logs</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## User Attributes

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[B2B-PF-TOS_1] Viene recuperato il consenso TOS e verificato che sia `accepted` TOS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[USER-ATTR_2] inserimento pec errato</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[USER-ATTR_3] inserimento telefono errato</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

## Test di integrazione della pubblica amministrazione

<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_2] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_3] download documento notificato_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_5] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_6] Invio notifica digitale mono destinatario con pagamento</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-SEND_7] Invio notifica digitale mono destinatario senza pagamento</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_0.1] Creazione stream notifica</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_0.2] Creazione stream notifica</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_1] Invio notifica digitale ed attesa stato `ACCEPTED`_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC_PA_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC_PA_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC_PA_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>
<details style="border:1px solid; border-radius: 5px; padding: 10px; margin-bottom: 20px">
  <summary>[TC-PA-PAY_1] Invio notifica e verifica amount</summary>

**Descrizione**

1. scrivere step di esecuzione del test

[Feature link](src/test/resources/it/pagopa/pn/cucumber)

</details>

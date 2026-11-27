# 20261127 Report Analisi Esiti (RAE) ProducerKeychainSuiteTest QA

| Campo | Valore |
| --- | --- |
| Release | [Interop] Core 2.25.0 |
| Scope | PROMO |
| Issue Jira | https://pagopa.atlassian.net/browse/QA-17417 |
| Data e ora | 2026-11-27T15:37:25Z |
| Durata | 00:08:06 |
| Numero KO | 3 |
| Run GitHub Actions | https://github.com/pagopa/interop-qa-tests/actions/runs/46026150730/attempts/1 |
| Suite | ProducerKeychainSuiteTest |
| Ambiente | qa |
| Executor | Vincenzo-Massaro |
| Source branch | feature/QA-17417-eservice-producerKeychain-link |

### Guida alla compilazione dei campi

- **Result** *(obbligatorio)*: descrivere il motivo del KO e l’esito dell’analisi in modo chiaro e sintetico, senza ripercorrere i singoli passaggi dell’indagine.
- **Procedure** *(obbligatorio)*: documentare passo per passo le verifiche svolte per arrivare al risultato. Se è già stato effettuato un intervento, includere il link alla PR o al ticket relativo.
- **BUG SUITE**: indicare il ticket Jira relativo a un difetto della suite di test. Obbligatorio se non è presente un BUG PRODOTTO, salvo i casi motivati nella nota.
- **BUG PRODOTTO**: indicare il ticket Jira relativo a un difetto del prodotto. Se il problema riguarda componenti esterni (ad esempio SelfCare) e non esiste un ticket, spiegare il motivo nel campo Result. Obbligatorio se non è presente un BUG SUITE, salvo i casi motivati nella nota.

> **Nota:** in assenza di ticket specifici, l’analisi si basa esclusivamente sulle informazioni riportate in **Result** e **Procedure**.

## 0 - KO DOVUTI A BUG DI PRODOTTO NOTI

<details>
<summary>Mostra/nascondi 0 KO</summary>

Nessun fallimento.

</details>

## 0 - KO DOVUTI A BUG DI SUITE NOTI

<details>
<summary>Mostra/nascondi 0 KO</summary>

Nessun fallimento.

</details>

## 3 - KO DA ANALIZZARE

<details>
<summary>Mostra/nascondi 3 KO</summary>

### 1. [M2M_PRODUCER_KEYCHAIN_LINK_3] - Associazione del producer-keychain a un e-service in stato illegittimo [cucumber-report-producer-keychain-m2mv2.json]

**RESULT**

Il codice di errore restituito è diverso da quello atteso. Poiché nella piattaforma Interop è prassi restituire il codice 409 quando si tenta di eseguire un'operazione incoerente con lo stato delle risorse coinvolte, la difformità dello status code è stata considerata un bug.

**PROCEDURE**

Dall'analisi dei log è emerso che l'operazione fallisce correttamente, restituendo tuttavia un codice di errore diverso da quello atteso. È stata effettuata una prova in locale, impostando come codice atteso quello effettivamente restituito, per sbloccare il test, portarlo a termine e verificare che non emergessero ulteriori errori. La prova ha confermato che l'unica anomalia riguardava lo status code.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11724

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: expected: <409> but was: <403>
	at it.pagopa.pn.interop.cucumber.steps.authorization.ClientCommonSteps.verifyHttpStatusCode(ClientCommonSteps.java:118)
	at ✽.si ottiene http status code 409(classpath:it/pagopa/pn/cucumber/m2mgateway/producer-keychain-link.feature:49)

````


---

### 2. [BFF_PRODUCER_KEYCHAIN_LINK_3] - Associazione del producer-keychain a un e-service in stato illegittimo [cucumber-report-producer-keychain-m2mv2.json]

**RESULT**

Il codice di errore restituito è diverso da quello atteso. Poiché nella piattaforma Interop è prassi restituire il codice 409 quando si tenta di eseguire un'operazione incoerente con lo stato delle risorse coinvolte, la difformità dello status code è stata considerata un bug.

**PROCEDURE**

Dall'analisi dei log è emerso che l'operazione fallisce correttamente, restituendo tuttavia un codice di errore diverso da quello atteso. È stata effettuata una prova in locale, impostando come codice atteso quello effettivamente restituito, per sbloccare il test, portarlo a termine e verificare che non emergessero ulteriori errori. La prova ha confermato che l'unica anomalia riguardava lo status code.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11722

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: expected: <409> but was: <403>
	at it.pagopa.pn.interop.cucumber.steps.authorization.ClientCommonSteps.verifyStatusCode(ClientCommonSteps.java:107)
	at ✽.si ottiene status code 409(classpath:it/pagopa/pn/cucumber/producer-keychain/producer-keychain-link.feature:47)

````


---

### 3. [M2M_PRODUCER_KEYCHAIN_LINK_3] - Associazione del producer-keychain a un e-service in stato illegittimo [cucumber-report-producer-keychain-m2mv3.json]

**RESULT**

Il codice di errore restituito è diverso da quello atteso. Poiché nella piattaforma Interop è prassi restituire il codice 409 quando si tenta di eseguire un'operazione incoerente con lo stato delle risorse coinvolte, la difformità dello status code è stata considerata un bug.

**PROCEDURE**

Dall'analisi dei log è emerso che l'operazione fallisce correttamente, restituendo tuttavia un codice di errore diverso da quello atteso. È stata effettuata una prova in locale, impostando come codice atteso quello effettivamente restituito, per sbloccare il test, portarlo a termine e verificare che non emergessero ulteriori errori. La prova ha confermato che l'unica anomalia riguardava lo status code.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11724

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: expected: <409> but was: <403>
	at it.pagopa.pn.interop.cucumber.steps.authorization.ClientCommonSteps.verifyHttpStatusCode(ClientCommonSteps.java:118)
	at ✽.si ottiene http status code 409(classpath:it/pagopa/pn/cucumber/m2mgateway/producer-keychain-link.feature:49)

````

</details>

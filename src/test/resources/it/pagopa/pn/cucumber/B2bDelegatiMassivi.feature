Feature: Pn-mandate api b2b per intermediari massivi

  @useB2B @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-SUCCESS] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    And si verifica che la delega a nome di "Mario Cucumber" è stata creata con stato pending

  @useB2B @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-COMPLETE_FLOW_SUCCESS] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    And la delega a nome di "Mario Cucumber" viene accettata da "CucumberSpa" senza associare nessun gruppo
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "Mario Cucumber" per comune "Comune_1"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "Mario Cucumber" per comune "Comune_1"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | size          |    -1      |
    And si verifica che lo status code sia: 400
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "Mario Cucumber" per comune "Comune_1"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | status        | ACCEPTED   |
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "Mario Cucumber" per comune "Comune_1"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | subjectRegExp | adsdasdasdasdasdasdasdasdasdasdasdas   |
    And Si verifica che il numero di notifiche restituite nella pagina sia 0
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "Mario Cucumber" per comune "Comune_1"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | iunMatch      |  VDKD-YVDR-XXXX-202409-X-9  |
    And Si verifica che il numero di notifiche restituite nella pagina sia 0
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "Mario Cucumber" per comune "Comune_1"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then "CucumberSpa" richiede il download dell'attestazione opponibile "SENDER_ACK"
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega
    Then il documento notificato può essere correttamente recuperato da "CucumberSpa" con delega

  @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-1] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale vuoto
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | EMPTY_FISCAL_CODE |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-2] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale non valido
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | INVALID_FISCAL_CODE |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-3] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega formalmente non valida
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | INVALID_FORMAT |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-4] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega antecedente alla data odierna
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | PAST_DATE |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-5] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega vuota
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | EMPTY_DATE |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-6] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con campo nome e cognome vuoti
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | EMPTY_NAME |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi @deleghe1
  Scenario Outline: [B2B-REVERSE-MANDATE-FAILURE-7] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con campo nome e cognome con lunghezza maggiore di 80 caratteri
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | <delegator> |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then si verifica che la chiamata sia fallita con status code: 400
    Examples:
      | delegator |
      | FIRST_NAME_NOT_VALID |
      | LAST_NAME_NOT_VALID  |

  @useB2B @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-ACCEPT-MANDATE-1] Viene invocata l'API di accettazione di una delega da parte di un delegato persona giuridica verso se stesso a nome del delegante persona giuridica, senza associarla ad un gruppo
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | GherkinSrl |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    When la delega a nome di "GherkinSrl" viene accettata da "CucumberSpa" senza associare nessun gruppo
    And si verifica che la delega è stata creata senza un gruppo associato

  @useB2B @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-ACCEPT-MANDATE-2] Viene invocata l'API di accettazione di una delega da parte di un delegato persona giuridica inserita verso se stesso a nome del delegante persona giuridica, associandola ad un gruppo
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | GherkinSrl |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then viene recuperato il primo gruppo disponibile attivo
    And la delega a nome di "GherkinSrl" viene accettata da "CucumberSpa" associando un gruppo
    And si verifica che la delega è stata creata con un gruppo associato

  @useB2B @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-ACCEPT-MANDATE-3] Viene invocata l'API di visualizzazione dell’elenco delle notifiche per le quali il destinatario risulta essere delegato senza aver accettato la delega
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | GherkinSrl |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica non può essere recuperata da "CucumberSpa"

  @useB2B @b2bIntermediariMassivi @deleghe1
  Scenario: [B2B-ACCEPT-MANDATE-4] Viene invocata l'API di accettazione di una delega da parte di un delegato persona giuridica verso se stesso a nome del delegante persona giuridica e successivamente invocare l’API di rifiuto della delega
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | GherkinSrl |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    And la delega a nome di "GherkinSrl" viene accettata da "CucumberSpa" senza associare nessun gruppo
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And la notifica non può essere recuperata da "CucumberSpa"
Feature: Pn-mandate api b2b per intermediari massivi

  @useB2B @b2bIntermediariMassivi
  Scenario: [B2B-REVERSE-MANDATE-SUCCESS] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    And si verifica che la delega a nome di "Mario Cucumber" è stata creata con stato pending

  @b2bIntermediariMassivi
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-1] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale vuoto
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | EMPTY_FISCAL_CODE |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-2] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale non valido
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | INVALID_FISCAL_CODE |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-3] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega formalmente non valida
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | INVALID_FORMAT |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-4] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega antecedente alla data odierna
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | PAST_DATE |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-5] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega vuota
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | Mario Cucumber |
      | dateFrom | TODAY |
      | dateTo  | EMPTY_DATE |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi
  Scenario: [B2B-REVERSE-MANDATE-FAILURE-6] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con campo nome e cognome vuoti
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | EMPTY_NAME |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    Then si verifica che la chiamata sia fallita con status code: 400

  @b2bIntermediariMassivi
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

  @useB2B @b2bIntermediariMassivi
  Scenario: [B2B-ACCEPT-MANDATE-1] Viene invocata l'API di accettazione di una delega da parte di un delegato persona giuridica verso se stesso a nome del delegante persona giuridica, senza associarla ad un gruppo
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    When viene effettuata una richiesta di creazione delega con i seguenti parametri:
      | delegate  | CucumberSpa |
      | delegator | GherkinSrl |
      | dateFrom | TODAY |
      | dateTo  | TOMORROW |
    When la delega a nome di "GherkinSrl" viene accettata da "CucumberSpa" senza associare nessun gruppo
    And si verifica che la delega è stata creata senza un gruppo associato

  @useB2B @b2bIntermediariMassivi
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

  @useB2B @b2bIntermediariMassivi
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

  @useB2B @b2bIntermediariMassivi
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
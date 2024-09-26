Feature: Pn-mandate api b2b per intermediari massivi

  @useB2B
  Scenario: [B2B-REVERSE-MANDATE-SUCCESS] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "Mario Cucumber"
    Then si verifica che la risposta contenga status code: 201
    And si verifica che la delega è stata creata con stato pending

  Scenario: [B2B-REVERSE-MANDATE-FAILURE-1] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale vuoto
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "EMPTY_FISCAL_CODE"
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [B2B-REVERSE-MANDATE-FAILURE-2] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale non valido
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "INVALID_FISCAL_CODE"
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [B2B-REVERSE-MANDATE-FAILURE-3] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega formalmente non valida
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "INVALID_FORMAT"
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [B2B-REVERSE-MANDATE-FAILURE-4] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega antecedente alla data odierna
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "PAST_DATE"
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [B2B-REVERSE-MANDATE-FAILURE-5] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega vuota
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "EMPTY_DATE"
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [B2B-REVERSE-MANDATE-FAILURE-6] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con campo nome e cognome vuoti
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "EMPTY_NAME"
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario Outline: [B2B-REVERSE-MANDATE-FAILURE-7] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con campo nome e cognome con lunghezza maggiore di 80 caratteri
    When l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di "<delegator>"
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata
    Examples:
      | delegator |
      | FIRST_NAME_NOT_VALID |
      | LAST_NAME_NOT_VALID  |

  @useB2B
  Scenario: [B2B-ACCEPT-MANDATE-1] Viene invocata l'API di accettazione di una delega da parte di un delegato persona giuridica verso se stesso a nome del delegante persona giuridica, senza associarla ad un gruppo
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" crea una delega verso se stesso a nome di "CucumberSpa"
    When la delega verso "CucumberSpa" viene accettata dal delegato "GherkinSrl" senza associare nessun gruppo
    Then si verifica che la delega è stata accettata e la risposta contenga status code: 204
    And si verifica che la delega è stata creata senza un gruppo associato

  @useB2B
  Scenario: [B2B-ACCEPT-MANDATE-2] Viene invocata l'API di accettazione di una delega da parte di un delegato persona giuridica inserita verso se stesso a nome del delegante persona giuridica, associandola ad un gruppo
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" crea una delega verso se stesso a nome di "CucumberSpa"
    Then si verifica che la risposta contenga status code: 201
    Then viene recuperato il primo gruppo disponibile attivo per il delegato "CucumberSpa"
    And la delega verso "CucumberSpa" viene accettata dal delegato "GherkinSrl" associando un gruppo
    And si verifica che la delega è stata accettata e la risposta contenga status code: 204
    And si verifica che la delega è stata creata senza un gruppo associato

  @useB2B
  Scenario: [B2B-ACCEPT-MANDATE-3] Viene invocata l'API di visualizzazione dell’elenco delle notifiche per le quali il destinatario risulta essere delegato senza aver accettato la delega
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" crea una delega verso se stesso a nome di "CucumberSpa"
    Then si verifica che la risposta contenga status code: 201
    And la notifica non può essere recuperata da "GherkinSrl"

  @useB2B
  Scenario: [B2B-ACCEPT-MANDATE-4] Viene invocata l'API di accettazione di una delega da parte di un delegato persona giuridica verso se stesso a nome del delegante persona giuridica e successivamente invocare l’API di rifiuto della delega
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" crea una delega verso se stesso a nome di "CucumberSpa"
    Then si verifica che la risposta contenga status code: 201
    And la delega verso "CucumberSpa" viene accettata dal delegato "GherkinSrl" senza associare nessun gruppo
    And "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And la notifica non può essere recuperata da "GherkinSrl"
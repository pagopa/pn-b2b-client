Feature: Allegati notifica

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_15] verifica retention time dei documenti pre-caricati
    Given viene effettuato il pre-caricamento di un documento
    Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_16] verifica retention time  pagopaForm pre-caricato
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention di "PAGOPA" precaricato

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_PG-CF_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_PG-CF_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_PG_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  @SmokeTest @testLite
  Scenario: [B2B-PA-SEND_PG_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

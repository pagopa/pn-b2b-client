Feature: verifica compatibilità verioni

  @version
  Scenario: [B2B-PA-SEND_1_V1] Invio notifica digitale mono destinatario V2.1 e recupero tramite codice IUN V1.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1

  @version
  Scenario: [B2B-PA-SEND_1_V2] Invio notifica digitale mono destinatario V2.1 e recupero tramite codice IUN V2.0 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20



Feature: verifica compatibilità tra v1 a v2

  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_15] Invio notifica V2 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V2 positivo
    Given viene generata una nuova notifica V2
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20



  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_17] Invio notifica V2 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V1.1 positivo
    Given viene generata una nuova notifica V2
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" V1

  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_18] Invio notifica digitale mono destinatario V2 e recupero tramite codice IUN V1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica V2
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then si verifica la corretta acquisizione della notifica V2
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1
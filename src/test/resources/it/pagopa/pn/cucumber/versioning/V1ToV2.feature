Feature: verifica compatibilità tra v1 a v2

  @version
  Scenario: [B2B-PA-SEND_VERSION_15] Invio notifica V2 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V2 positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @version
  Scenario: [B2B-PA-SEND_VERSION_17] Invio notifica V2 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V1.1 positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_18] Invio notifica digitale mono destinatario V2 e recupero tramite codice IUN V1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_19] Recupero notifica V1 non esistente su Send V2.0
    When si tenta il recupero della notifica dal sistema tramite codice IUN "UGYD-XHEZ-KLRM-202208-X-0"
    Then l'operazione ha prodotto un errore con status code "404"
    And si tenta il recupero della notifica dal sistema tramite codice IUN "UGYD-XHEZ-KLRM-202208-X-0" con la V1
    And l'operazione ha prodotto un errore con status code "404"

  @version
  Scenario: [B2B-PA-SEND_VERSION_20] Invio notifica digitale mono destinatario V2 e controllo che V1 non abbia l'evento "NOTIFICATION_CANCELLED"
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1
    And vengono letti gli eventi della timeline e si controlla che non abbia lo stato "NOTIFICATION_CANCELLED" con la V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_21] Controlle se presente lo stato ACCEPTED nella versione V1
    Given viene generata una nuova notifica V1
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED V1
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED" V1
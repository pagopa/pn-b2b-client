Feature: verifica compatibilità tra verioni

  @version
  Scenario: [B2B-PA-SEND_VERSION_1] Invio notifica digitale mono destinatario V2.1 e recupero tramite codice IUN V1.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_2] Invio notifica digitale mono destinatario V2.1 e recupero tramite codice IUN V2.0 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20

  @version
  Scenario: [B2B-PA-SEND_VERSION_3] Invio notifica digitale mono destinatario V1.1 e recupero tramite codice IUN V2.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then si verifica la corretta acquisizione della notifica V1
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @version
  Scenario: [B2B-PA-SEND_VERSION_4] Invio notifica digitale mono destinatario V2.1 Ccon annullamento e recupero tramite codice IUN V1.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_5]  Invio notifica digitale mono destinatario V1.1 e recupero visualizzazione notifica e verifica amount e effectiveDate V2.1
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then si verifica la corretta acquisizione della notifica V1
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono verificati costo = "100" e data di perfezionamento della notifica

  @version
  Scenario: [B2B-PA-SEND_VERSION_6] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario V2.1 e recupero con V1.1 positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_7] Invio notifica digitale V2.1 ed attesa stato ACCEPTED_scenario V1.1 positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED" V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_8] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario da V1.1 a V2.1 positivo
    Given viene generata una nuova notifica V1
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin V1 e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED V1
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_9] Invio notifica ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario da V1.1 a V2.1 positivo
    Given viene generata una nuova notifica V1
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" V1

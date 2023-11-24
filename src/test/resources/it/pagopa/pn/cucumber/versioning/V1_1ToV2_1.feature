Feature: verifica compatibilità tra v1.1 a v2.1

  #ok_AR -> successo raccomandata a/r
  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW da V1 in ambiente con versione superiore
    Given viene generata una nuova notifica V1
      | subject | notifica analogica con cucumber v1 |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin V1 e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED V1
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_2] Invio e visualizzazione notifica e verifica amount e effectiveDate da V1 in ambiente con versione superiore
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario V1
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then l'avviso pagopa viene pagato correttamente dall'utente 0 V1
    And si attende il corretto pagamento della notifica V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_3] Invio notifica digitale mono destinatario V1.1 con annullamento V2.1 e recupero tramite codice IUN V1.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber V1
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    When la notifica può essere annullata dal sistema tramite codice IUN
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_4] Invio notifica ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario da V1 in ambiente con versione superiore
    Given viene generata una nuova notifica V1
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_5] Invio notifica V1.1 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V2.1 positivo
    Given viene generata una nuova notifica V1
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_6] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V1.1 a V2.1
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario V1
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica



  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_7] Invio notifica digitale mono destinatario e mono pagamento V2.1 e recupero tramite codice IUN V1.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1



  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_8] Invio notifica digitale mono destinatario e mono pagamento V1.1 e recupero tramite codice IUN V2.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then si verifica la corretta acquisizione della notifica V1
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_9] Invio notifica digitale mono destinatario e mono pagamento V2.1 Ccon annullamento e recupero tramite codice IUN V1.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_10] Invio notifica digitale mono destinatario e mono pagamento V2.1 con annullamento e recupero tramite codice IUN V1.1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    And vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "NOTIFICATION_CANCELLATION_REQUEST" V1
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED" V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_11]  Invio notifica digitale mono destinatario e mono pagamento V1.1 e recupero visualizzazione notifica e verifica amount e effectiveDate V2.1
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
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_12] Invio notifica digitale mono destinatario e mono pagamento con payment senza PagopaForm_scenario V2.1 e recupero con V1.1 positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And si tenta il recupero dal sistema tramite codice IUN con api v1
    And l'operazione ha generato un errore con status code "400"

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_13] Invio notifica digitale V2.1 ed attesa stato ACCEPTED_scenario V1.1 positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED" V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_14] Invio notifica digitale mono destinatario e mono pagamento con PagopaForm_scenario V2.1 e recupero con V1.1 positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1




  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_15] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V1.1 a V2.1
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin V1 e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then vengono verificati costo = "100" e data di perfezionamento della notifica "V1"


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_16] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V2.1 a V1.1
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0 V1
    And si attende il corretto pagamento della notifica V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_17] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V1.1 a V2.1 senza pagoPaIntMode PN-8843
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | pagoPaIntMode | NULL |
    And destinatario Mario Gherkin V1 e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then vengono verificati costo = "100" e data di perfezionamento della notifica "V1"


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_18] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V1.1 a V2.1 e recupero con V1.1 senza payment_pagoPaForm PN-8842
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin V1 e:
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V21_19] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V1.1 a V2.1 e recupero con V2.1 senza payment_pagoPaForm PN-8842
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin V1 e:
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN

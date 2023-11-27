Feature: verifica compatibilità tra v1 a v2

  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_1] Invio notifica V2 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V2 positivo
    Given viene generata una nuova notifica V2
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20



  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_2] Invio notifica V2 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V1.1 positivo
    Given viene generata una nuova notifica V2
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" V1

  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_3] Invio notifica digitale mono destinatario V2 e recupero tramite codice IUN V1 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica V2
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then si verifica la corretta acquisizione della notifica V2
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_4] Recupero notifica non esistente su Send V2.0 e V1
    When si tenta il recupero della notifica dal sistema tramite codice IUN "UGYD-XHEZ-KLRM-202208-X-0" con la V2
    Then l'operazione ha prodotto un errore con status code "404"
    And si tenta il recupero della notifica dal sistema tramite codice IUN "UGYD-XHEZ-KLRM-202208-X-0" con la V1
    And l'operazione ha prodotto un errore con status code "404"

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_5] Invio notifica digitale mono destinatario V2 e controllo che V1 non abbia l'evento "NOTIFICATION_CANCELLED"
    Given viene generata una nuova notifica V2
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata V2
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED" V2
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1
    And vengono letti gli eventi della timeline e si controlla che l'evento di timeline "NOTIFICATION_CANCELLED" non esista con la V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_6] Controlle se presente lo stato ACCEPTED nella versione V1
    Given viene generata una nuova notifica V1
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED V1
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED" V1


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_7] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V2.0 a V1.1
    Given viene generata una nuova notifica V2
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario V2
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then l'avviso pagopa viene pagato correttamente dall'utente 0 V1
    And si attende il corretto pagamento della notifica V1

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_8] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V1.1 a V2.0
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario V1
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then l'avviso pagopa viene pagato correttamente dall'utente 0 V2
    And si attende il corretto pagamento della notifica V2

   #Da chiedere se è corretto che sia voluto che restituisca un 403
  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_9]  Invio notifica digitale mono destinatario e mono pagamento V2.0 e fallimento visualizzazione notifica
    Given viene generata una nuova notifica V2
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then si verifica la corretta acquisizione della notifica V2
    And "Mario Cucumber" legge la notifica ricevuta "V2"
    Then l'operazione ha prodotto un errore con status code "403"

    #Da chiedere se è corretto che sia voluto che restituisca un 403
  @version @ignore
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_10]  Invio notifica digitale mono destinatario e mono pagamento V1.1 e fallimento visualizzazione notifica
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then si verifica la corretta acquisizione della notifica V1
    And "Mario Cucumber" legge la notifica ricevuta "V1"
    Then l'operazione ha prodotto un errore con status code "403"

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_11] Invio notifica da V1.1 e recupero con V2 senza payment_pagoPaForm PN-8842
    Given viene generata una nuova notifica V1
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin V1 e:
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_12] Invio e visualizzazione notifica e verifica amount e effectiveDate da  V2.0 senza pagoPaIntMode PN-8843
    Given viene generata una nuova notifica V2
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | pagoPaIntMode | NULL |
    And destinatario Mario Gherkin V2 e:
      | payment_pagoPaForm         | SI   |
      | payment_noticeCodeOptional | NULL |
      | payment_f24flatRate        | NULL |
      | payment_f24standard        | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then vengono verificati costo = "100" e data di perfezionamento della notifica "V2"


  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_13] Notifica creata con GA1.1 con due pagamenti (primario e secondario)
    Given viene generata una nuova notifica V1
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin V1 e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED V1
    Then vengono verificati costo = "100" e data di perfezionamento della notifica "V1"

  @version
  Scenario: [B2B-PA-SEND_VERSION_V1_V2_14] Notifica creata con GA2.0 con due pagamenti (primario e secondario)
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin V2 e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then vengono verificati costo = "100" e data di perfezionamento della notifica "V2"
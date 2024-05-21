Feature: verifica compatibilità tra v2 a v2.1

  @version @mockPec
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_1] Invio notifica V2.1 ed attesa elemento di timeline DIGITAL_SUCCESS_WORKFLOW_scenario V2 positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" V2


  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_2] Invio notifica digitale mono destinatario e mono pagamento V2.1 e recupero tramite codice IUN V2.0 (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20


  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_3] Invio notifica digitale mono destinatario e mono pagamento con PagopaForm_scenario V2.1 e recupero con V1.1 positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI          |
      | payment_multy_number  | 2           |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20


  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_5] Invio e recupero notifica V2.0 e recupero con V2.0 senza payment_pagoPaForm PN-8842
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin V2 e:
      | payment_pagoPaForm | NULL |
      | payment_f24        | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED "V2"
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20
    And viene richiesto il download del documento "PAGOPA"
    And l'operazione ha prodotto un errore con status code "404"


  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_6] Invio e visualizzazione notifica da  V2.0 e recupero con V2.1 senza payment_pagoPaForm PN-8842
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin V2 e:
      | payment_pagoPaForm | NULL |
      | payment_f24        | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED "V2"
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_7] Invio e pagamento notifica da  V2.1 a V2.0
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0 V2
    And si attende il corretto pagamento della notifica V2

  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_8]  Invio notifica digitale mono destinatario e mono pagamento V2 e recupero visualizzazione notifica e verifica amount e effectiveDate V2.1
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED "V2"
    Then si verifica la corretta acquisizione della notifica V2
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono verificati costo = "100" e data di perfezionamento della notifica


  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_9] Invio notifica V2.0 con taxId errato
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario V2
      | taxID | LNALNI80A01H501X |
    When la notifica viene inviata dal "Comune_1" dalla "V2"
    Then l'operazione ha prodotto un errore con status code "400"

  @version
  Scenario: [B2B-PA-SEND_VERSION_V2_V21_10]  Invio notifica V2.0 con noticeCode esistente PN-8913
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin V2 e:
      | payment_noticeCode | 355312817721270543 |
    When la notifica viene inviata dal "Comune_1" dalla "V2"
    Then l'operazione ha prodotto un errore con status code "409"

  @version
  Scenario Outline: [B2B-PA-SEND_VERSION_V2_V21_11] Invio notifica digitale V2.0 physicalAddress_address non valido PN-8913
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin V2 e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata dal "Comune_1" dalla "V2"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                                                |
      | ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |
Feature: verifica validazione asincrona

  @dev
  Scenario: [B2B-PA-ASYNC_VALIDATION_1] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage  scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    Then la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @validation @realNormalizzatore @asyncValidation @refused
  Scenario Outline: [B2B-PA-ASYNC_VALIDATION_2] invio notifiche digitali mono destinatario con  con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province errati scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_zip          | <zip_code>     |
      | physicalAddress_province     | <province>     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Examples:
      | municipality | zip_code | province |
      | Palermo      | 20019    | MI       |
      | Milano       | 90121    | PA       |
      | Milano       | 90121    | MI       |
      | Milano       | 90121    | RM       |




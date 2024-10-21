Feature: verifica funzionamento api rest

  @restApiValidation
  Scenario: [REST_VALIDATION_1] Invio notifica digitale mono destinatario e verifica stato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | idempotenceToken | AME2E3626070001.3  |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then viene verificato lo stato di accettazione con requestID


  @authFleet
  Scenario: [REST_VALIDATION_2] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo
    Given si tenta il recupero della notifica dal sistema tramite codice IUN "UGYD-XHEZ-KLRM-202208-X-0"
    Then l'operazione ha prodotto un errore con status code "404"

    #TODO: da spostare ?
  @restApiValidation
  Scenario: [REST_VALIDATION_3] Invio notifica digitale_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica multi cucumber |
      | senderDenomination | Comune di verona |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si tenta il recupero dal sistema tramite codice IUN dalla PA "Comune_1"
    And l'operazione ha generato un errore con status code "404"

  @restApiValidation
  Scenario: [REST_VALIDATION_4] Invio notifica digitale_scenario senza destinatario
    Given viene generata una nuova notifica
      | subject            | invio notifica multi cucumber |
      | senderDenomination | Comune di palermo             |
    And senza destinatario
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
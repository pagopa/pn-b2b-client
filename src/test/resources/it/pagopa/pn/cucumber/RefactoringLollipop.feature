Feature: 15371 Refactoring Lollipop

  @lollipopAuthorizer
  Scenario Outline: [LOLLIPOP_LAMBDA_AUTHORIZER_GET]
    When viene invocata l'api per testare la lambda authorizer di lollipop con metodo "GET" passando headers <headersLollipop>
    Examples:
      | headersLollipop                      |
      | tutti validi                         |
      | xPagopaLollipopOriginalUrl_errato    |
      | xPagopaLollipopOriginalMethod_errato |
      | xPagopaLollipopPublicKey_errato      |
      | xPagopaLollipopAssertionRef_errato   |
      | xPagopaLollipopAssertionType_errato  |
      | xPagopaLollipopAuthJwt_errato        |
      | xPagopaLollipopUserId_errato         |
      | signatureInput_errato                |
      | signature_errato                     |

  @lollipopAuthorizer
  Scenario Outline: [LOLLIPOP_LAMBDA_AUTHORIZER_POST]
    When viene invocata l'api per testare la lambda authorizer di lollipop con metodo "POST" passando headers <headersLollipop>
    Examples:
      | headersLollipop                      |
      | tutti validi                         |
      | xPagopaLollipopOriginalUrl_errato    |
      | xPagopaLollipopOriginalMethod_errato |
      | xPagopaLollipopPublicKey_errato      |
      | xPagopaLollipopAssertionRef_errato   |
      | xPagopaLollipopAssertionType_errato  |
      | xPagopaLollipopAuthJwt_errato        |
      | xPagopaLollipopUserId_errato         |
      | signatureInput_errato                |
      | signature_errato                     |


  @lollipopLambdaFilterOn
  Scenario: [LOLLIPOP_HEADER_VALIDATION_LAMBDA_FILTER_ON_OK] Creazione con successo di una delega temporanea passando header lollipop validi quando la lambda è attiva
#    Given viene generata una nuova notifica
#      | subject            | invio notifica delega temporanea |
#      | senderDenomination | comune di Palermo                |
#    And destinatario Mario Cucumber
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Given imposto lo iun di SharedSteps a "VAGE-NYRY-EHTP-202603-Q-1" e la pa a "Comune_Multi"
    When "TODO_CF_UAT" viene temporaneamente delegato da "Mario Cucumber" passando headers lollipop tutti validi
    Then la delega temporanea è stata correttamente creata

  @lollipopLambdaFilterOn
  Scenario Outline: [LOLLIPOP_HEADER_VALIDATION_LAMBDA_FILTER_ON_KO] Creazione senza successo di una delega temporanea passando header lollipop errati quando la lambda è attiva
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When "TODO_CF_UAT" viene temporaneamente delegato da "Mario Cucumber" passando headers lollipop <headersLollipopErrorType>
    Then l'operazione restituisce codice 403
    Examples:
      | headersLollipopErrorType             |
      | xPagopaLollipopOriginalUrl_errato    |
      | xPagopaLollipopOriginalMethod_errato |
      | xPagopaLollipopPublicKey_errato      |
      | xPagopaLollipopAssertionRef_errato   |
      | xPagopaLollipopAssertionType_errato  |
      | xPagopaLollipopAuthJwt_errato        |
      | xPagopaLollipopUserId_errato         |
      | signatureInput_errato                |
      | signature_errato                     |

  @lollipopLambdaFilterOff
  Scenario: [LOLLIPOP_HEADER_VALIDATION_LAMBDA_FILTER_OFF_OK] Creazione con successo di una delega temporanea passando header lollipop validi quando la lambda è disattiva
#    Given viene generata una nuova notifica
#      | subject            | invio notifica delega temporanea |
#      | senderDenomination | comune di Palermo                |
#    And destinatario Mario Cucumber
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Given imposto lo iun di SharedSteps a "VAGE-NYRY-EHTP-202603-Q-1" e la pa a "Comune_Multi"
    When "TODO_CF_UAT" viene temporaneamente delegato da "Mario Cucumber" passando headers lollipop tutti validi
    Then la delega temporanea è stata correttamente creata

  @lollipopLambdaFilterOff
  Scenario Outline: [LOLLIPOP_HEADER_VALIDATION_LAMBDA_FILTER_OFF_KO] Creazione senza successo di una delega temporanea passando header lollipop errati quando la lambda è disattiva
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When "TODO_CF_UAT" viene temporaneamente delegato da "Mario Cucumber" passando headers lollipop <headersLollipopErrorType>
    Then l'operazione restituisce codice 400
    Examples:
      | headersLollipopErrorType             |
      | xPagopaLollipopOriginalUrl_errato    |
      | xPagopaLollipopOriginalMethod_errato |
      | xPagopaLollipopPublicKey_errato      |
      | xPagopaLollipopAssertionRef_errato   |
      | xPagopaLollipopAssertionType_errato  |
      | xPagopaLollipopAuthJwt_errato        |
      | xPagopaLollipopUserId_errato         |
      | signatureInput_errato                |
      | signature_errato                     |

  Scenario: TOOL HEADER
    Given genero la curl a partire dai log lambdaAuthorizer
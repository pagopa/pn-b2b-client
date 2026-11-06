# Fonte: QA-18839 - Validation del bug PN-21731 ([pn-ec] Disallineamento OpenAPI)
Feature: Validazione della richiesta di postalizzazione su external-channel

  @richiestaCartaceaRequestPaId
  Scenario: [EC-PAPER-REQUESTPAID-1] Rifiuto di una richiesta di postalizzazione senza identificativo della PA mittente
    When il client "pn-cons-000" invia a external-channel una richiesta di postalizzazione senza requestPaId
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "requestPaId"

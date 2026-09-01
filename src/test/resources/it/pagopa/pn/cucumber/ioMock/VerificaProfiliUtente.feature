@ioMock @profiles
Feature: Verifica e Routing Profili Utente su POST /profiles (Mock IO e Lambda Router)
  Verifica della raggiungibilità e instradamento su POST /profiles tramite Lambda Router e Mock microservice basato su parametri SSM (Deny-list MapIoConnectorMockSenderNotAllowed, Whitelist MapIoConnectorMockRealTaxIdsWhitelist, Standard, 404 e 400)

  @MOCK_IO_ROUTER_PROFILE_01_1_A
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_A] Verifica profilo con codice fiscale in Deny-list SSM MapIoConnectorMockSenderNotAllowed (Mock)
    Given preparo una richiesta di verifica profilo con codice fiscale "DENYLIST_CF_001"
    When invoco endpoint "POST /profiles"
    Then verifico che lo status code della risposta sia 200
    And verifico che il body della risposta contenga "sender_allowed" impostato a false

  @MOCK_IO_ROUTER_PROFILE_01_1_B
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_B] Routing trasparente a IO reale per codice fiscale in Whitelist SSM MapIoConnectorMockRealTaxIdsWhitelist
    Given preparo una richiesta di verifica profilo con codice fiscale "WHITELIST_CF_001"
    When invoco endpoint "POST /profiles"
    Then verifico che lo status code della risposta sia 200
    And verifico che la richiesta sia stata inoltrata in modo trasparente a IO reale

  @MOCK_IO_ROUTER_PROFILE_01_1_C
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_C] Verifica profilo per codice fiscale standard non presente in lista (Mock)
    Given preparo una richiesta di verifica profilo con codice fiscale "STANDAR_CF_00001"
    When invoco endpoint "POST /profiles"
    Then verifico che lo status code della risposta sia 200
    And verifico che il body della risposta contenga "sender_allowed" impostato a true

  @MOCK_IO_ROUTER_PROFILE_01_1_D
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_D] Risposta 404 Not Found per destinatario non registrato su App IO
    Given preparo una richiesta di verifica profilo con codice fiscale "NOT_REGISTERED_CF_001"
    When invoco endpoint "POST /profiles"
    Then verifico che lo status code della risposta sia 404

  @MOCK_IO_ROUTER_PROFILE_01_2_A
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_2_A] Errore 400 Bad Request per richiesta priva del campo obbligatorio fiscal_code
    Given preparo una richiesta di verifica profilo senza il campo "fiscal_code"
    When invoco endpoint "POST /profiles"
    Then verifico che lo status code della risposta sia 400

  @MOCK_IO_ROUTER_PROFILE_01_2_B
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_2_B] Errore 400 Bad Request per richiesta con campi non previsti da OpenAPI
    Given preparo una richiesta di verifica profilo contenente campi non definiti nelle specifiche OpenAPI
    When invoco endpoint "POST /profiles"
    Then verifico che lo status code della risposta sia 400

  @MOCK_IO_ROUTER_PROFILE_01_2_C
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_2_C] Errore 400 Bad Request per richiesta con fiscal_code malformato o non valido
    Given preparo una richiesta di verifica profilo con codice fiscale ""
    When invoco endpoint "POST /profiles"
    Then verifico che lo status code della risposta sia 400

@ioMock @profiles
Feature: Verifica e Routing dei Profili Utente App IO
  Come connettore IO di SEND
  Voglio verificare la raggiungibilità dei destinatari su App IO
  Per instradare correttamente le comunicazioni verso il Mock o l'ambiente reale

  @MOCK_IO_ROUTER_PROFILE_01_1_A
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_A] Destinatario non abilitato alla ricezione su IO (Mock)
    Given un destinatario con codice fiscale in blacklist "DENYLIST_CF_001"
    When viene richiesta la verifica del profilo utente
    Then il profilo risulta non abilitato alla ricezione dei messaggi

  @MOCK_IO_ROUTER_PROFILE_01_1_B
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_B] Destinatario abilitato per inoltro trasparente verso IO reale
    Given un destinatario abilitato al routing reale "WHITELIST_CF_001"
    When viene richiesta la verifica del profilo utente
    Then la richiesta viene instradata con successo verso l'ambiente reale di IO

  @MOCK_IO_ROUTER_PROFILE_01_1_C
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_C] Destinatario standard abilitato alla ricezione su IO (Mock)
    Given un destinatario con codice fiscale ordinario "STANDAR_CF_00001"
    When viene richiesta la verifica del profilo utente
    Then il profilo risulta abilitato alla ricezione dei messaggi

  @MOCK_IO_ROUTER_PROFILE_01_1_D
  Scenario: [MOCK_IO_ROUTER_PROFILE_01_1_D] Destinatario non registrato su App IO
    Given un destinatario non registrato ad App IO "NOT_REGISTERED_CF_001"
    When viene richiesta la verifica del profilo utente
    Then il profilo utente risulta non registrato

  @MOCK_IO_ROUTER_PROFILE_01_2_A
  Scenario Outline: [MOCK_IO_ROUTER_PROFILE_01_2_A] Rifiuto verifica profilo per anomalia o formato non conforme
    Given una richiesta di verifica profilo con payload non conforme "<tipo_anomalia>"
    When viene richiesta la verifica del profilo utente
    Then la richiesta di verifica profilo viene rifiutata per errore di validazione formale

    Examples:
      | tipo_anomalia      |
      | SENZA_FISCAL_CODE  |
      | CAMPI_NON_PREVISTI |
      | FISCAL_CODE_VUOTO  |

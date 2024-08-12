Feature: interop probing test

  @interopProbing
  Scenario: [INTEROP-PROBING_1] interop probing SUCCESS
    When viene chiamato il servizio di probing per interop
    Then la chiamata al servizio di probing per interop restituisce 200

  @interopProbing
  Scenario: [INTEROP-PROBING_2] interop probing ERROR
    When viene chiamato il servizio di probing per interop
    Then la chiamata al servizio di probing per interop restituisce 500


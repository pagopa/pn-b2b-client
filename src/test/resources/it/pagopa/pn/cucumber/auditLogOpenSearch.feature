Feature: verifica audit-log su openSearch

  Scenario: [AUDIT_OPEN_SEARCH_0] verifica audit log prova
    Given viene effettuato una prova di chiamata ad openSearch


  Scenario Outline: [AUDIT_OPEN_SEARCH_1] verifica audit log 10y
    Then viene verificato che esiste un audit log "<audit-log>" in "10y" non più vecchio di 2 giorni
    And viene verificato che non esiste un audit log "<audit-log>" in "5y"
    Examples:
      | audit-log          |
      | AUD_NT_PRELOAD     |
      | AUD_ACC_LOGIN      |
